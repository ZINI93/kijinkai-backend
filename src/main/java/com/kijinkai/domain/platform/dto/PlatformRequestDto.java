package com.kijinkai.domain.platform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlatformRequestDto {

    @Schema(description = "플렛폼 주소", example = "www.merukari.co.jp")
    private String baseUrl;

    @Builder
    public PlatformRequestDto(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
