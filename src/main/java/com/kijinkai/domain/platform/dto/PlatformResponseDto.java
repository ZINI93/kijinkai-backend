package com.kijinkai.domain.platform.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlatformResponseDto {

    private String platformUuid;
    private String userUuid;
    private String baseUrl;

    @Builder
    public PlatformResponseDto(String platformUuid, String userUuid, String baseUrl) {
        this.platformUuid = platformUuid;
        this.userUuid = userUuid;
        this.baseUrl = baseUrl;
    }
}
