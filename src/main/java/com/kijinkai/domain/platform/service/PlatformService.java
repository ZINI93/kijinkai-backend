package com.kijinkai.domain.platform.service;

import com.kijinkai.domain.platform.dto.PlatformRequestDto;
import com.kijinkai.domain.platform.dto.PlatformResponseDto;
import com.kijinkai.domain.platform.dto.PlatformUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PlatformService {

    PlatformResponseDto createPlatformWithValidate(UUID userUuid, PlatformRequestDto requestDto);
    PlatformResponseDto updatePlatformWithValidate(UUID userUuid, UUID platformUuid, PlatformUpdateDto updateDto);
    void deletePlatform(UUID userUuid, UUID platformUuid);
    Page<PlatformResponseDto> getPlatforms(Pageable pageable);
    PlatformResponseDto getPlatformInfo(UUID platformUuid);
}
