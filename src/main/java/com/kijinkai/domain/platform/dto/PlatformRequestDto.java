package com.kijinkai.domain.platform.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlatformRequestDto {

    private String baseUrl;

    @Builder
    public PlatformRequestDto(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
