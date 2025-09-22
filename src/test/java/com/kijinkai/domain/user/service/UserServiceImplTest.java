package com.kijinkai.domain.user.service;

import com.kijinkai.domain.mail.service.EmailService;
import com.kijinkai.domain.user.application.dto.UserRequestDto;
import com.kijinkai.domain.user.application.dto.UserResponseDto;
import com.kijinkai.domain.user.application.dto.UserUpdateDto;
import com.kijinkai.domain.user.domain.model.UserRole;
import com.kijinkai.domain.user.domain.model.UserStatus;
import com.kijinkai.domain.user.application.service.UserApplicationServiceImpl;
import com.kijinkai.domain.user.domain.factory.UserFactory;
import com.kijinkai.domain.user.application.mapper.UserMapper;
import com.kijinkai.domain.user.adapter.out.persistence.repository.UserRepository;
import com.kijinkai.util.EmailRandomCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UserMapper mapper;
    @Mock UserFactory factory;
    @Mock EmailService emailService;
    @Mock EmailRandomCode emailRandomCode;
    @Mock RedisTemplate<String, String> redisTemplate;
    @InjectMocks
    UserApplicationServiceImpl userService;

    UserRequestDto requestDto;
    UserResponseDto response;
    User user;

    @BeforeEach
    void setUp(){

        requestDto = UserRequestDto.builder().email("aaa@gmail.com").password("12341234").nickname("zinikun").build();
        user = new User(UUID.randomUUID(), requestDto.getEmail(), requestDto.getPassword(), requestDto.getNickname(), UserRole.USER, false , UserStatus.PENDING);


        response = new UserResponseDto(user.getUserUuid(), user.getEmail(), user.getNickname());
    }

    @Test
    void createUserWithValidate() {

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        String verificationCode = "123456";

        //given
        when(emailRandomCode.generateVerificationCode()).thenReturn(verificationCode);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(factory.createUser(requestDto, encodedPassword)).thenReturn(user);

        when(mapper.toResponse(user)).thenReturn(response);
        when(userRepository.save(any(User.class))).thenReturn(user);


        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        //when
        UserResponseDto result = userService.createUserWithValidate(requestDto);

        //then
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository,times(1)).save(any(User.class));
    }

    @Test
    void updateUserWithValidate() {

        //given
        when(userRepository.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));
        UserUpdateDto updateDto = new UserUpdateDto("12341234", "지니쨩");
        response = new UserResponseDto(user.getUserUuid(), user.getEmail(), updateDto.getNickname());
        when(passwordEncoder.encode(updateDto.getPassword())).thenReturn("password");
        when(mapper.toResponse(user)).thenReturn(response);


        //when
        UserResponseDto result = userService.updateUserWithValidate(user.getUserUuid(), updateDto);

        //then
        assertNotNull(result);
        assertEquals(updateDto.getNickname(),result.getNickname());

        verify(userRepository,times(1)).findByUserUuid(user.getUserUuid());
    }

    @Test
    void deleteUser() {

        //given
        when(userRepository.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));


        //when
        userService.deleteUser(user.getUserUuid());

        //then

    }

    @Test
    void getUserInfo() {

        //given
        when(userRepository.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));
        when(mapper.toResponse(user)).thenReturn(response);

        //when
        UserResponseDto result = userService.getUserInfo(user.getUserUuid());

        //then
        assertNotNull(result);
        assertEquals(user.getNickname(),result.getNickname());
        assertEquals(user.getUserUuid(),result.getUserUuid());

        verify(userRepository, times(1)).findByUserUuid(user.getUserUuid());
        verify(mapper,times(1)).toResponse(user);
    }
}