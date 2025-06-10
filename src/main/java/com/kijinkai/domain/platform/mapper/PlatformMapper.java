package com.kijinkai.domain.platform.mapper;

import com.kijinkai.domain.platform.dto.PlatformResponseDto;
import com.kijinkai.domain.platform.entity.Platform;
import org.springframework.stereotype.Component;


@Component
public class PlatformMapper {

    public PlatformResponseDto toResponse(Platform platform){

        return PlatformResponseDto.builder()
                .platformUuid(platform.getPlatformUuid())
                .userUuid(platform.getUser().getUserUuid())
                .baseUrl(platform.getBaseUrl())
                .build();
    }
}
