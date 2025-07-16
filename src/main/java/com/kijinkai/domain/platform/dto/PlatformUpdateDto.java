package com.kijinkai.domain.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PlatformUpdateDto {

    @Schema(description = "플렛폼 주소", example = "www.merukari.co.jp")
    private String baseUrl;

    public PlatformUpdateDto(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
