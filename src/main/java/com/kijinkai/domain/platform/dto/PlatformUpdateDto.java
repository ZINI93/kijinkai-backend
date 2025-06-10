package com.kijinkai.domain.platform.dto;

import lombok.Getter;

@Getter
public class PlatformUpdateDto {

    private String baseUrl;

    public PlatformUpdateDto(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
