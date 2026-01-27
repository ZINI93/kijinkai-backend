package com.kijinkai.domain.user.application.service;

import com.kijinkai.domain.address.application.port.in.UpdateAddressUseCase;
import com.kijinkai.domain.address.application.port.out.AddressPersistencePort;
import com.kijinkai.domain.customer.application.port.in.UpdateCustomerUseCase;
import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.jwt.service.JwtService;
import com.kijinkai.domain.mail.service.EmailService;
import com.kijinkai.domain.user.adapter.in.web.securiry.CustomUserDetails;
import com.kijinkai.domain.user.application.dto.CustomOAuth2User;
import com.kijinkai.domain.user.application.dto.response.UserEditInfoResponse;
import com.kijinkai.domain.user.application.port.in.*;
import com.kijinkai.domain.user.application.validator.UserValidator;
import com.kijinkai.domain.user.application.dto.request.UserRequestDto;
import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import com.kijinkai.domain.user.application.dto.request.UserUpdateDto;
import com.kijinkai.domain.user.application.mapper.UserMapper;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.factory.UserFactory;
import com.kijinkai.domain.user.domain.exception.*;
import com.kijinkai.domain.user.domain.model.SocialProviderType;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.user.domain.model.UserRole;
import com.kijinkai.domain.user.domain.model.UserStatus;
import com.kijinkai.filter.AuthPrincipal;
import com.kijinkai.util.JwtUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserApplicationService extends DefaultOAuth2UserService implements CreateUserUseCase, GetUserUseCase, UpdateUserUseCase, DeleteUserUseCase, UserDetailsService, PasswordUpdateUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    private final UserFactory userFactory;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final CustomerPersistencePort customerPersistencePort;

    private final UpdateAddressUseCase updateAddressUseCase;
    private final AddressPersistencePort addressPersistencePort;


    /**
     * 이메일 중복 확인
     *
     * @param userRequestDto return
     */
    @Override
    public Boolean existsByUser(UserRequestDto userRequestDto) {
        return userPersistencePort.existsByEmail(userRequestDto.getEmail());
    }

    /**
     * 회원가입
     *
     * @param email
     * @param password
     * @param nickname
     * @return
     */
    @Override
    @Transactional
    public User createUser(String email, String password, String nickname) {
        log.info("Creating user for user email:{}", email);

        // 1. email 중복 검증
        if (userPersistencePort.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exist");
        }

        try {
            // 2. 검증
            userValidator.validateCreateUserRequest(email, password, nickname);

            // 3. 사용자 생성
            String encodedPassword = passwordEncoder.encode(password);
            User user = userFactory.createUser(email, nickname, encodedPassword);
            User savedUser = userPersistencePort.saveUser(user);

            log.info("Created user for user email:{}", savedUser.getEmail());

            return savedUser;
        } catch (DuplicateEmailException | InvalidUserDataException e) {
            log.error("User creation validation failed: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Email already exists");
        } catch (Exception e) {
            log.error("Failed to crate user for user email:{}", email, e);
            throw new UserCreationException("Failed to create user", e);
        }
    }


//    /**
//     * 계정 단건 조회
//     *
//     * @return
//     */
//    @Override
//    public UserResponseDto getUserInfo(UUID userUuid) throws AccessDeniedException {
//
//        User user = findUserByUserUuid(userUuid);
//
//        boolean isOwner = user.getUserUuid().equals(userUuid);
//        boolean isAdmin = user.equals("Role_" + UserRole.ADMIN);
//
//        if (!isOwner && !isAdmin) {
//            throw new AccessDeniedException("Only admin, owner can read it ");
//        }
//
//        return userMapper.toResponse(user);
//    }

    /**
     * 계정 단건 조회
     *
     * @return
     */
    @Override
    public UserResponseDto getUserInfo(UUID userUuid) {

        User user = findUserByUserUuid(userUuid);

        return userMapper.toResponse(user);
    }

    /**
     * 고객 조회
     * 권한 - 관리자
     *
     * @param userUuid
     * @param email
     * @param nickName
     * @param pageable
     * @return
     */
    @Override
    public Page<UserResponseDto> findAllByEmailAndNickName(UUID userUuid, String email, String nickName, Pageable pageable) {

        User user = findUserByUserUuid(userUuid);
        user.validateAdminRole();

        Page<User> users = userPersistencePort.findAllByEmailAndNickName(email, nickName, pageable);

        return users.map(userMapper::toResponse);
    }


    /**
     * 유저정보 수정
     *
     * @param userUuid
     * @param updateDto
     * @return
     */
    @Override
    @Transactional
    public UserResponseDto updateUserInfo(UUID userUuid, UserUpdateDto updateDto) {

        // 유저 조회 활성화 검증
        User user = findUserByUserUuid(userUuid);
        user.validateActive();

        //변경 요청받은 비밀번호 인코딩


        String encodedPassword = null;

        if (updateDto.getNewPassword() != null){
            encodedPassword = passwordEncoder.encode(updateDto.getNewPassword());
        }


        // 업데이트
        user.updateUser(updateDto.getNickname(), encodedPassword);

        // 구매자 업데이트 후 저장
        UUID customer = updateCustomerUseCase.updateCustomer(
                userUuid,
                updateDto.getFirstName(),
                updateDto.getLastName(),
                updateDto.getPhoneNumber(),
                updateDto.getPcc(),
                updateDto.getBankType(),
                updateDto.getAccountHolder(),
                updateDto.getAccountNumber()
        );

        // 업데이트 된 유저정보 저장
        User savedUser = userPersistencePort.saveUser(user);

        return userMapper.updatedResponse(savedUser.getUserUuid(), customer);
    }

    /**
     * 비밀번호 업데이트 -- 체크
     *
     * @param userUuid
     * @param updateDto
     * @return
     */
    @Override
    @Transactional
    public UserResponseDto updateUserPassword(UUID userUuid, UserUpdateDto updateDto) {

        try {
            User user = findUserByUserUuid(userUuid);

            String newEncodedPassword = passwordEncoder.encode(updateDto.getNewPassword());
            user.updatePassword(newEncodedPassword);
            User savedUser = userPersistencePort.saveUser(user);

            return userMapper.toResponse(savedUser);
        } catch (UserNotFoundException | InvalidUserDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating user with UUID: {}", userUuid, e);
            throw new UserUpdateException("Failed to update user due to internal error", e);
        }
    }


    /**
     * 계정 삭제
     * 계정 주인, 관리자 이외에 계정을 삭제하지 못한다.
     *
     * @param userUuid
     * @throws AccessDeniedException
     */
    @Override
    @Transactional
    public void deleteUser(UUID userUuid) throws AccessDeniedException {

        User user = findUserByUserUuid(userUuid);

        boolean isOwner = user.getUserUuid().equals(userUuid);
        boolean isAdmin = user.equals("Role_" + UserRole.ADMIN);

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Only admin, owner can delete it ");
        }

        // 유저 제거
        userPersistencePort.deleteUser(user);

        //RefreshToken 제거
        jwtService.removeRefreshUser(user.getUserUuid());

    }

    // helper
    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user uuid: %s", userUuid)));
    }

    /**
     * 자체 로그인
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // user를 조회
        User user = userPersistencePort.findByEmailAndUserStatusAndIsSocial(username, UserStatus.ACTIVE, false)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found for username: %s", username)));

        // Uuid와 email을 한곳에 authPrincipal recode 담는다.
        AuthPrincipal authPrincipal = new AuthPrincipal(user.getUserUuid());

        //유저 권한 검증
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));


        return new CustomUserDetails(authPrincipal, user.getPassword(), authorities, true);
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 부모 메소드 호출
        OAuth2User oAuth2User = super.loadUser(userRequest);

        //데이터
        Map<String, Object> attributes;
        List<GrantedAuthority> authorities;


        String email;
        String role = UserRole.USER.name();
        String nickname;


        // Provider 제공자 별 데이터 흭득
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        if (registrationId.equals(SocialProviderType.NAVER.name())) {

            attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            email = attributes.get("email").toString();
            nickname = attributes.get("name").toString();

        } else if (registrationId.equals(SocialProviderType.GOOGLE.name())) {
            attributes = (Map<String, Object>) oAuth2User.getAttributes();
            email = attributes.get("email").toString();
            nickname = attributes.get("name").toString();
        } else {
            throw new OAuth2AuthenticationException("Unsupported social login");
        }

        // 데이터베이스 조회 -> 존재하면 업데이트, 없으면 신규 가입

        Optional<User> user = userPersistencePort.findByEmailAndIsSocial(email, true);

        User savedUser;
        if (user.isPresent()) {
            // role 조회
            role = user.get().getUserRole().name();

            //기존 유저 업데이트
            UserRequestDto userRequestDto = new UserRequestDto();
            userRequestDto.setNickname(nickname);
            user.get().oAuth2LoginUpdate(nickname);
            savedUser = userPersistencePort.saveUser(user.get());

        } else {

            // 신규 유저 추가
            User newOAuth2User = userFactory.createOAuth2User(email, nickname, SocialProviderType.valueOf(registrationId));
            savedUser = userPersistencePort.saveUser(newOAuth2User);
        }

        authorities = List.of(new SimpleGrantedAuthority(role));

        return new CustomOAuth2User(attributes, authorities, email, savedUser.getUserUuid());
    }

    // 비밀번호 변경

    /**
     * 비밀번호 리셋메일 전송
     *
     * @param requestDto
     * @throws MessagingException
     */
    @Override
    public void forgetPassword(UserRequestDto requestDto) throws MessagingException {

        // 사용자 확인
        User user = userPersistencePort.findByEmailAndUserStatusAndIsSocial(requestDto.getEmail(), UserStatus.ACTIVE, false)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user email: %s", requestDto.getEmail())));

        // jwt Token 생성
        String resetToken = jwtUtil.createPasswordRestJWT(user.getUserUuid(), user.getUserRole().name());

        // 토큰 포함해서 메일 전송
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }


    /**
     * 토큰발급
     *
     * @param token
     * @return
     */
    @Override
    public String verifyResetTokenAndIssueInterim(String token) {

        //토큰 검증
        if (!jwtUtil.isPasswordResetValid(token, "reset")) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        //토큰으로 부터 유저 추출
        UUID userUuid = jwtUtil.getUserUuid(token);
        User user = findUserByUserUuid(userUuid);

        //변경에 쓰일 토큰 발급
        return jwtUtil.createInterimJWT(user.getUserUuid(), user.getUserRole().name());
    }


    /**
     * 비밀번호 변경
     *
     * @param interimToken
     * @param requestDto
     */
    @Transactional
    @Override
    public void updatePasswordWithInterimToken(String interimToken, UserRequestDto requestDto) {

        // 토큰 검증
        if (!jwtUtil.isPasswordResetValid(interimToken, "interim")) {
            throw new IllegalArgumentException("비밀번호 변경 권한이 없거나 시간이 초과 되었습니다.");
        }

        // 토큰에서 정보 추출
        UUID userUuid = jwtUtil.getUserUuid(interimToken);
        User user = findUserByUserUuid(userUuid);

        // 비밀번호 변경
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        user.updatePassword(encodedPassword);
        userPersistencePort.saveUser(user);
    }


    // 조회.

    /**
     * 유저 회원정보 수정 조회
     *
     * @param userUuid
     * @return
     */
    @Override
    public UserEditInfoResponse userEditInfo(UUID userUuid) {

        // 유저 조회
        User user = findUserByUserUuid(userUuid);

        // 구매자 조회
        Customer customer = customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException("Not found customer"));


        return userMapper.toEditResponse(user, customer);
    }


    // helper


}

