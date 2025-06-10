package com.kijinkai.domain.platform.service;

import com.kijinkai.domain.platform.dto.PlatformRequestDto;
import com.kijinkai.domain.platform.dto.PlatformResponseDto;
import com.kijinkai.domain.platform.dto.PlatformUpdateDto;
import com.kijinkai.domain.platform.entity.Platform;
import com.kijinkai.domain.platform.factory.PlatformFactory;
import com.kijinkai.domain.platform.mapper.PlatformMapper;
import com.kijinkai.domain.platform.repository.PlatformRepository;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.entity.UserRole;
import com.kijinkai.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PlatformServiceImplTest {

    @Mock PlatformRepository platformRepository;
    @Mock UserRepository userRepository;
    @Mock PlatformMapper mapper;
    @Mock PlatformFactory factory;
    @InjectMocks PlatformServiceImpl platformService;

    User user;
    Platform platform;
    PlatformRequestDto requestDto;
    PlatformResponseDto responseDto;


    @BeforeEach
    void setUp(){

        user = User.builder().userUuid(UUID.randomUUID().toString()).userRole(UserRole.ADMIN).build();
        requestDto = new PlatformRequestDto("www.merukari.com");
        platform = new Platform(
                UUID.randomUUID().toString(),
                user,
                requestDto.getBaseUrl()
        );
        responseDto = PlatformResponseDto.builder().userUuid(platform.getUser().getUserUuid()).baseUrl(platform.getBaseUrl()).platformUuid(platform.getPlatformUuid()).build();

    }

    @Test
    void createPlatformWithValidate() {
        //given

        when(userRepository.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));
        when(platformRepository.save(any(Platform.class))).thenReturn(platform);
        when(factory.createPlatform(user,requestDto)).thenReturn(platform);
        when(mapper.toResponse(platform)).thenReturn(responseDto);

        //when
        PlatformResponseDto result = platformService.createPlatformWithValidate(user.getUserUuid(), requestDto);

        //then
        assertNotNull(result);
        assertEquals(platform.getPlatformUuid(), result.getPlatformUuid());
        assertEquals(platform.getUser().getUserUuid(), result.getUserUuid());

        verify(userRepository,times(1)).findByUserUuid(user.getUserUuid());
        verify(platformRepository,times(1)).save(any(Platform.class));
        verify(mapper,times(1)).toResponse(platform);
    }

    @Test
    void updatePlatformWithValidate() {

        //given
        PlatformUpdateDto updateDto = new PlatformUpdateDto("www.yahoo.co.jp");
        PlatformResponseDto updateResponse = new PlatformResponseDto(platform.getPlatformUuid(), user.getUserUuid(), updateDto.getBaseUrl());
        when(platformRepository.findByUserUserUuidAndPlatformUuid(user.getUserUuid(),platform.getPlatformUuid())).thenReturn(Optional.ofNullable(platform));
        when(mapper.toResponse(platform)).thenReturn(updateResponse);

        //when
        PlatformResponseDto result = platformService.updatePlatformWithValidate(user.getUserUuid(), platform.getPlatformUuid(), updateDto);

        //then
        verify(platformRepository, times(1)).findByUserUserUuidAndPlatformUuid(user.getUserUuid(),platform.getPlatformUuid());
        verify(mapper,times(1)).toResponse(platform);

    }

    @Test
    void deletePlatform() {
        //given
        when(platformRepository.findByUserUserUuidAndPlatformUuid(user.getUserUuid(),platform.getPlatformUuid())).thenReturn(Optional.ofNullable(platform));

        //when
        platformService.deletePlatform(user.getUserUuid(),platform.getPlatformUuid());

        //then

    }

    @Test
    void getPlatform() {

        //given
        when(platformRepository.findByUserUserUuidAndPlatformUuid(user.getUserUuid(),platform.getPlatformUuid())).thenReturn(Optional.ofNullable(platform));
        when(mapper.toResponse(platform)).thenReturn(responseDto);

        //when
        PlatformResponseDto result = platformService.getPlatformInfo(user.getUserUuid(), platform.getPlatformUuid());

        //then
        assertNotNull(result);
        assertEquals(platform.getBaseUrl(), result.getBaseUrl());

        verify(platformRepository, times(1)).findByUserUserUuidAndPlatformUuid(user.getUserUuid(),platform.getPlatformUuid());
        verify(mapper,times(1)).toResponse(platform);
    }
}