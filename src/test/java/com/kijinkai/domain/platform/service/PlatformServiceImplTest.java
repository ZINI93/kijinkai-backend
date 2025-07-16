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
import com.kijinkai.domain.user.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PlatformServiceImplTest {

    @Mock PlatformRepository platformRepository;
    @Mock UserRepository userRepository;
    @Mock UserValidator userValidator;
    @Mock PlatformMapper mapper;
    @Mock PlatformFactory factory;
    @InjectMocks PlatformServiceImpl platformService;


    UUID userUuid;
    UUID platfomUuid;

    User user;
    Platform platform;
    PlatformRequestDto requestDto;
    PlatformResponseDto responseDto;

    @BeforeEach
    void setUp(){

        userUuid = UUID.randomUUID();
        platfomUuid = UUID.randomUUID();

    }

    private User createMockUser(UUID userUuid, UserRole userRole) {
        return User.builder().userUuid(userUuid).userRole(userRole).build();
    }
    private Platform createMockPlatform(UUID platfomUuid, User  user, String baseUrl){
        return Platform.builder().platformUuid(platfomUuid).user(user).baseUrl(baseUrl).build();

    }

    @Test
    @DisplayName("플렛폼 생성")
    void createPlatformWithValidate() {
        //given
        String baseUrl = "www.xxxx.xxx";

        User user = createMockUser(userUuid, UserRole.ADMIN);
        Platform platform = createMockPlatform(platfomUuid, user, baseUrl);
        PlatformResponseDto response = PlatformResponseDto.builder().baseUrl(platform.getBaseUrl()).platformUuid(platform.getPlatformUuid()).build();

        when(userRepository.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));
        when(platformRepository.save(any(Platform.class))).thenReturn(platform);
        when(factory.createPlatform(user,requestDto)).thenReturn(platform);
        when(mapper.toResponse(platform)).thenReturn(response);

        //when
        PlatformResponseDto result = platformService.createPlatformWithValidate(user.getUserUuid(), requestDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getBaseUrl()).isEqualTo(baseUrl);

        verify(userRepository,times(1)).findByUserUuid(user.getUserUuid());
        verify(platformRepository,times(1)).save(any(Platform.class));
        verify(mapper,times(1)).toResponse(platform);
        verify(userValidator).requireAdminRole(user);
    }

    @Test
    @DisplayName("플렛폼 업데이트")
    void updatePlatformWithValidate() {

        //given
        String baseUrl = "www.xxxx.xxx";

        User user = createMockUser(userUuid, UserRole.ADMIN);
        Platform platform = createMockPlatform(platfomUuid, user, baseUrl);

        PlatformUpdateDto updateDto = new PlatformUpdateDto("www.yahoo.co.jp");
        PlatformResponseDto updateResponse = new PlatformResponseDto(platfomUuid, user.getUserUuid(), updateDto.getBaseUrl());

        when(userRepository.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));
        when(platformRepository.findByPlatformUuid(platfomUuid)).thenReturn(Optional.ofNullable(platform));
        PlatformResponseDto response = PlatformResponseDto.builder().platformUuid(platfomUuid).baseUrl(updateResponse.getBaseUrl()).build();
        when(mapper.toResponse(platform)).thenReturn(response);


        //when
        PlatformResponseDto result = platformService.updatePlatformWithValidate(user.getUserUuid(), platform.getPlatformUuid(), updateDto);

        //then

        assertThat(result).isNotNull();
        assertThat(result.getBaseUrl()).isEqualTo(updateDto.getBaseUrl());

        verify(platformRepository, times(1)).findByPlatformUuid(platform.getPlatformUuid());
        verify(mapper,times(1)).toResponse(platform);
        verify(userValidator).requireAdminRole(user);


    }

    @Test
    @DisplayName("플렛폼 삭제")
    void deletePlatform() {
        //given
        String baseUrl = "www.xxxx.xxx";

        User user = createMockUser(userUuid, UserRole.ADMIN);
        Platform platform = createMockPlatform(platfomUuid, user, baseUrl);

        when(userRepository.findByUserUuid(user.getUserUuid())).thenReturn(Optional.ofNullable(user));
        when(platformRepository.findByPlatformUuid(platfomUuid)).thenReturn(Optional.ofNullable(platform));


        //when
        platformService.deletePlatform(user.getUserUuid(),platform.getPlatformUuid());

        //then

    }

    @Test
    @DisplayName("플렛폼정보 조회")
    void getPlatform() {

        //given

        String baseUrl = "www.xxxx.xxx";

        User user = createMockUser(userUuid, UserRole.ADMIN);
        Platform platform = createMockPlatform(platfomUuid, user, baseUrl);
        PlatformResponseDto response = PlatformResponseDto.builder().baseUrl(platform.getBaseUrl()).platformUuid(platform.getPlatformUuid()).build();

        when(platformRepository.findByPlatformUuid(platform.getPlatformUuid())).thenReturn(Optional.ofNullable(platform));
        when(mapper.toResponse(platform)).thenReturn(response);

        //when
        PlatformResponseDto result = platformService.getPlatformInfo(platform.getPlatformUuid());

        //then
        assertThat(result).isNotNull();
        assertThat(result.getBaseUrl()).isEqualTo(baseUrl);

        verify(platformRepository, times(1)).findByPlatformUuid(platform.getPlatformUuid());
        verify(mapper,times(1)).toResponse(platform);
    }
}