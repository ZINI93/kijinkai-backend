package com.kijinkai.domain.user.service;

import com.kijinkai.domain.user.dto.UserRequestDto;
import com.kijinkai.domain.user.dto.UserResponseDto;
import com.kijinkai.domain.user.dto.UserUpdateDto;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.entity.UserRole;
import com.kijinkai.domain.user.factory.UserFactory;
import com.kijinkai.domain.user.mapper.UserMapper;
import com.kijinkai.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    @InjectMocks UserServiceImpl userService;

    UserRequestDto requestDto;
    UserResponseDto response;
    User user;

    @BeforeEach
    void setUp(){

        requestDto = UserRequestDto.builder().email("aaa@gmail.com").password("12341234").nickname("zinikun").build();
        user = new User(UUID.randomUUID().toString(), requestDto.getEmail(), requestDto.getPassword(), requestDto.getNickname(), UserRole.USER);


        response = new UserResponseDto(user.getUserUuid(), user.getEmail(), user.getNickname());
    }

    @Test
    void createUserWithValidate() {
        //given
        when(mapper.toResponse(user)).thenReturn(response);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(factory.createUser(requestDto)).thenReturn(user);

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