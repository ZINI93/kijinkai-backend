package com.kijinkai.domain.user.application.service;

import com.kijinkai.domain.user.application.port.in.*;
import com.kijinkai.domain.user.application.validator.UserValidator;
import com.kijinkai.domain.user.application.dto.UserRequestDto;
import com.kijinkai.domain.user.application.dto.UserResponseDto;
import com.kijinkai.domain.user.application.dto.UserUpdateDto;
import com.kijinkai.domain.user.application.mapper.UserMapper;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.factory.UserFactory;
import com.kijinkai.domain.user.domain.exception.*;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserApplicationService implements CreateUserUseCase, GetUserUseCase, UpdateUserUseCase, DeleteUserUseCase {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoder passwordEncoder;

    private final UserFactory userFactory;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    /**
     * 회원가입
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
    public Page<UserResponseDto> findAllByEmailAndNickName(UUID userUuid, String email, String nickName, Pageable pageable) {

        User user = findUserByUserUuid(userUuid);
        user.validateAdminRole();

        Page<User> users = userPersistencePort.findAllByEmailAndNickName(email, nickName, pageable);

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

    /**
     * 비밀번호 업데이트
     * @param userUuid
     * @param updateDto
     * @return
     */
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

    // helper
    private User findUserByUserUuid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user uuid: %s", userUuid)));
    }
}
