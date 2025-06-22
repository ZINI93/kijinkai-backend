package com.kijinkai.domain.platform.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PlatformResponseDto {

    private UUID platformUuid;
    private String userUuid;
    private String baseUrl;

    @Builder
    public PlatformResponseDto(UUID platformUuid, String userUuid, String baseUrl) {
        this.platformUuid = platformUuid;
        this.userUuid = userUuid;
        this.baseUrl = baseUrl;
    }
}
