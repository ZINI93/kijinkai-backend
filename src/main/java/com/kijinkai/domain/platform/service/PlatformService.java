package com.kijinkai.domain.platform.service;

import com.kijinkai.domain.platform.dto.PlatformRequestDto;
import com.kijinkai.domain.platform.dto.PlatformResponseDto;
import com.kijinkai.domain.platform.dto.PlatformUpdateDto;

public interface PlatformService {

    PlatformResponseDto createPlatformWithValidate(String userUuid, PlatformRequestDto requestDto);
    PlatformResponseDto updatePlatformWithValidate(String userUuid, String platformUuid, PlatformUpdateDto updateDto);
    void deletePlatform(String userUuid, String platformUuid);
    PlatformResponseDto getPlatformInfo(String userUuid, String platformUuid);
}
