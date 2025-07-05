package com.kijinkai.domain.user.service;

import com.kijinkai.domain.mail.exception.InvalidVerificationCodeException;
import com.kijinkai.domain.mail.exception.VerificationCodeExpiredException;
import com.kijinkai.domain.mail.service.EmailService;
import com.kijinkai.domain.user.dto.UserRequestDto;
import com.kijinkai.domain.user.dto.UserResponseDto;
import com.kijinkai.domain.user.dto.UserUpdateDto;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.exception.DuplicateEmailException;
import com.kijinkai.domain.user.exception.UserCreationException;
import com.kijinkai.domain.user.exception.UserNotFoundException;
import com.kijinkai.domain.user.exception.UserUpdateException;
import com.kijinkai.domain.user.factory.UserFactory;
import com.kijinkai.domain.user.mapper.UserMapper;
import com.kijinkai.domain.user.repository.UserRepository;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.util.EmailRandomCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;
    private final UserFactory factory;
    private final UserValidator userValidator;

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailRandomCode emailRandomCode;
    private final EmailService emailService;

    /**
     * email, password, nickname을 받아서 계정생성 프로세스
     * @param requestDto
     * @return 생성 응답 DTO
     */
    @Override @Transactional
    public UserResponseDto createUserWithValidate(UserRequestDto requestDto) {
        log.info("Creating user for user email:{}", requestDto.getEmail());

        // 이메일 중복 확인
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일 입니다.");
        }

        try {
            String encodedPassword = passwordEncoding(requestDto.getPassword());
            User user = factory.createUser(requestDto, encodedPassword);
            User savedUser = userRepository.save(user);

            // 6자리 인증 코드 생성
            String verificationCode = emailRandomCode.generateVerificationCode();

            // Redis에 저장 (10분 만료)
            String key = "email:verify: " + user.getUserUuid();
            redisTemplate.opsForValue().set(key, verificationCode, 10, TimeUnit.MINUTES);

            // 이메일 발송
            emailService.sendVerificationEmail(user.getEmail(), verificationCode);

            log.info("Created user for user email:{}", savedUser.getEmail());
            return userMapper.toResponse(savedUser);
        }catch (Exception e){
            log.error("Failed to crate user for user email:{}", requestDto.getEmail(), e);
            throw new UserCreationException("Failed to create user", e);
        }
    }

    @Transactional
    public void verifyEmail(UUID userUuid, String code){
        // 1. Redis에서 인증 코드 확인
        String key = "email:verify:" + userUuid;
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null){
            throw new VerificationCodeExpiredException("인증 코드가 만료되었습니다.");
        }

        if (!savedCode.equals(code)){
            throw new InvalidVerificationCodeException("잘못된 인증 코드입니다.");
        }

        // 2. user 상태 업데이트

        User user = findUserByUserUuid(userUuid);

        user.isEmailVerified();
        user.isActive();

        // 3. Redis에서 코드 삭제
        redisTemplate.delete(key);
    }

    /**
     * 유저가 본인의 password, nickname을 업데이트 프로세트
     * @param userUuid
     * @param updateDto
     * @return 업데이트 응답 DTO
     */
    @Override @Transactional
    public UserResponseDto updateUserWithValidate(UUID userUuid, UserUpdateDto updateDto) {
        try {
            log.info("Updating user for user uuid:{}", userUuid);

            String encodedPassword = passwordEncoding(updateDto.getPassword());

            User user = findUserByUserUuid(userUuid);
            user.updateUser(encodedPassword, updateDto);

            log.info("Updated user for user email:{}", user.getEmail());
            return userMapper.toResponse(user);
        }catch (Exception e){
            log.error("Failed to update for user uuid: {}", userUuid, e);
            throw new UserUpdateException("Failed to update user", e);
        }
    }

    private String passwordEncoding(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * 본인 계정 정보 조회
     * @param userUuid
     * @return
     */
    @Override
    public UserResponseDto getUserInfo(UUID userUuid) {
        User user = findUserByUserUuid(userUuid);
        return userMapper.toResponse(user);
    }

    /**
     * 관리자가 유저 관리로 전체 리스트를 확인하는 프로세스 ( 이메일, 닉네임으로 검색 )
     * @param userUuid
     * @param email
     * @param nickname
     * @param pageable
     * @return userRepository
     */
    @Override
    public Page<UserResponseDto> findAllByUsers(UUID userUuid, String email, String nickname, Pageable pageable) {

        User user = findUserByUserUuid(userUuid);
        userValidator.requireAdminRole(user);

        return userRepository.findByNameAndNickname(email,nickname,pageable);
    }

    /**
     * 본인의 계정삭제
     * @param userUuid
     */
    @Override @Transactional
    public void deleteUser(UUID userUuid) {
        log.info("Deleting user fro userUuid:{}", userUuid);
        User user = findUserByUserUuid(userUuid);
        userRepository.delete(user);
    }

    private User findUserByUserUuid(UUID userUuid) {
        return userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user uuid: %s", userUuid)));
    }


}
