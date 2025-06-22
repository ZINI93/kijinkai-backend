package com.kijinkai.domain.platform.service;

import com.kijinkai.domain.platform.dto.PlatformRequestDto;
import com.kijinkai.domain.platform.dto.PlatformResponseDto;
import com.kijinkai.domain.platform.dto.PlatformUpdateDto;
import com.kijinkai.domain.platform.entity.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlatformService {

    PlatformResponseDto createPlatformWithValidate(String userUuid, PlatformRequestDto requestDto);
    PlatformResponseDto updatePlatformWithValidate(String userUuid, String platformUuid, PlatformUpdateDto updateDto);
    void deletePlatform(String userUuid, String platformUuid);
    Page<PlatformResponseDto> getPlatforms(Pageable pageable);
    PlatformResponseDto getPlatformInfo(String userUuid, String platformUuid);
}
