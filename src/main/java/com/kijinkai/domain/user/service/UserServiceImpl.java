package com.kijinkai.domain.user.service;

import com.kijinkai.domain.user.dto.UserRequestDto;
import com.kijinkai.domain.user.dto.UserResponseDto;
import com.kijinkai.domain.user.dto.UserUpdateDto;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.exception.UserNotFoundException;
import com.kijinkai.domain.user.factory.UserFactory;
import com.kijinkai.domain.user.mapper.UserMapper;
import com.kijinkai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserFactory factory;

    @Override
    @Transactional
    public UserResponseDto createUserWithValidate(UserRequestDto requestDto) {
        log.info("Creating user for user email:{}", requestDto.getEmail());

        User user = factory.createUser(requestDto);
        User savedUser = userRepository.save(user);

        log.info("Created user for user email:{}", savedUser.getEmail());
        return mapper.toResponse(savedUser);
    }

    private void updateUser(User user, UserUpdateDto updateDto) {
        String encodedUpdatePassword = passwordEncoder.encode(updateDto.getPassword());
        user.updateUser(
                encodedUpdatePassword,
                updateDto.getNickname());
    }

    @Override
    @Transactional
    public UserResponseDto updateUserWithValidate(String userUuid, UserUpdateDto updateDto) {
        User user = findUserByUserUuidInUser(userUuid);
        log.info("Updating user for user email:{}", user.getEmail());

        updateUser(user, updateDto);
        log.info("Updated user for user email:{}", user.getEmail());
        return mapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(String userUuid) {
        log.info("Deleting user fro userUuid:{}", userUuid);
        User user = findUserByUserUuidInUser(userUuid);
        userRepository.delete(user);
    }

    @Override
    public UserResponseDto getUserInfo(String userUuid) {
        User user = findUserByUserUuidInUser(userUuid);
        return mapper.toResponse(user);
    }

    private User findUserByUserUuidInUser(String userUuid) {
        return userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException("User uuid: user Not found"));
    }
}
