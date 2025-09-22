package com.kijinkai.domain.user.application.service;

import com.kijinkai.domain.mail.exception.InvalidVerificationCodeException;
import com.kijinkai.domain.mail.exception.VerificationCodeExpiredException;
import com.kijinkai.domain.mail.service.EmailService;
import com.kijinkai.domain.user.application.dto.EmailVerificationRequestDto;
import com.kijinkai.domain.user.application.port.in.*;
import com.kijinkai.domain.user.application.validator.UserValidator;
import com.kijinkai.domain.user.application.dto.UserRequestDto;
import com.kijinkai.domain.user.application.dto.UserResponseDto;
import com.kijinkai.domain.user.application.dto.UserUpdateDto;
import com.kijinkai.domain.user.application.mapper.UserMapper;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.event.UserCreatedEvent;
import com.kijinkai.domain.user.domain.factory.UserFactory;
import com.kijinkai.domain.user.domain.exception.*;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.util.EmailRandomCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserApplicationService implements CreateUserUseCase, GetUserUseCase, UpdateUserUseCase, DeleteUserUseCase, VerifyEmailUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoder passwordEncoder;

    private final UserFactory userFactory;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailRandomCode emailRandomCode;
    private final EmailService emailService;

    /**
     * 유저의 이메일을 통한 어카운트 생성
     *
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {
        log.info("Creating user for user email:{}", requestDto.getEmail());

        try {
            // 1. 검증
            userValidator.validateCreateUserRequest(requestDto);

            // 2. 사용자 생성
            String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
            User user = userFactory.createUser(requestDto, encodedPassword);
            User savedUser = userPersistencePort.saveUser(user);

            // 3. 이벤트 발생
            applicationEventPublisher.publishEvent(UserCreatedEvent.from(savedUser));

            log.info("Created user for user email:{}", savedUser.getEmail());
            return userMapper.toResponse(savedUser);
        } catch (DuplicateEmailException | InvalidUserDataException e) {
            log.error("User creation validation failed: {}", e.getMessage());
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("Email already exists");
        } catch (Exception e) {
            log.error("Failed to crate user for user email:{}", requestDto.getEmail(), e);
            throw new UserCreationException("Failed to create user", e);
        }
    }


    /**
     * 계정 단건 조회
     *
     * @param userUuid
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
     * @param name
     * @param pageable
     * @return
     */
    @Override
    public Page<UserResponseDto> findAllByEmailAndName(UUID userUuid, String email, String name, Pageable pageable) {

        User user = findUserByUserUuid(userUuid);
        user.validateAdminRole();

        Page<User> users = userPersistencePort.findAllByEmailAndName(email, name, pageable);

        return users.map(userMapper::toResponse);
    }

    /**
     * 유저 프로필 업데이트
     *
     * @param userUuid
     * @param updateDto
     * @return
     */
    @Override
    @Transactional
    public UserResponseDto updateUserProfile(UUID userUuid, UserUpdateDto updateDto) {
        try {

            userValidator.validateUpdateUserRequest(updateDto);
            User user = findUserByUserUuid(userUuid);

            userValidator.validateUserPassword(passwordEncoder, updateDto, user);
            String encodedPassword = passwordEncoder.encode(updateDto.getNewPassword());

            user.updateUser(updateDto.getNickname(), encodedPassword);
            User savedUser = userPersistencePort.saveUser(user);

            return userMapper.toResponse(savedUser);

        } catch (UserNotFoundException | InvalidUserDataException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating user with UUID: {}", userUuid, e);
            throw new UserUpdateException("Failed to update user due to internal error", e);
        }
    }

    @Override
    @Transactional
    public UserResponseDto updateUserPassword(UUID userUuid, UserUpdateDto updateDto) {
        try {
            User user = findUserByUserUuid(userUuid);

            userValidator.validateUserPassword(passwordEncoder, updateDto, user);
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
     *
     * @param userUuid
     */
    @Override
    @Transactional
    public void deleteUser(UUID userUuid) {

        User user = findUserByUserUuid(userUuid);
        userPersistencePort.deleteUser(user);
    }

    @Override
    @Transactional
    public void verifyEmail(EmailVerificationRequestDto requestDto) {
        //1. 유저를 조회
        User user = findUserByEmail(requestDto.getEmail());
        //2. 코드 조회
        String key = "email:verify: " + user.getUserUuid();
        String savedCode = redisTemplate.opsForValue().get(key);

        //3. 코드 검증
        if (savedCode == null){
            throw new VerificationCodeExpiredException("코드 인증이 만료되었습니다.");
        }
        if (!savedCode.equals(requestDto.getVerificationCode())){
            throw new InvalidVerificationCodeException("인증 코드가 일치하지 않습니다.");
        }

        //4. 완료 처리
        user.verifyEmail();
        userPersistencePort.saveUser(user);

        //5. 안중 코드 삭제
        redisTemplate.delete(key);

        log.info("Email verified successfully for user: {}", user.getEmail());
    }

    @Override
    public void resendCode(String email) {
        // 1. 유저 조회
        User user = findUserByEmail(email);

        // 2. 유저 활성화 체크
        if (user.isEmailVerified()){
            throw new AlreadyVerifiedException("이미 계정이 활성화 되어 있습니다.");
        }

        // 3. 재 인증코드 발급
        String verificationCode = emailRandomCode.generateVerificationCode();

        // 4.인증코드 덮어 쓰기
        String key = "email:verify:" + user.getUserUuid();
        redisTemplate.opsForValue().set(key, verificationCode, 10, TimeUnit.MINUTES);

        // 5. 이메일 코드 재발송
        emailService.sendVerificationEmail(email, verificationCode);
    }

    // helper
    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user uuid: %s", userUuid)));
    }
    private User findUserByEmail(String email) {
        return userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user email: %s", email)));
    }
}
