package com.kijinkai.domain.platform.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PlatformResponseDto {

    @Schema(description = "플렛폼 고유 식별자",example = "xxxx-xxxx")
    private UUID platformUuid;

    @Schema(description = "유저 고유 식별자",example = "xxxx-xxxx")
    private UUID userUuid;

    @Schema(description = "플렛폼 주소", example = "www.merukari.co.jp")
    private String baseUrl;

    @Builder
    public PlatformResponseDto(UUID platformUuid, UUID userUuid, String baseUrl) {
        this.platformUuid = platformUuid;
        this.userUuid = userUuid;
        this.baseUrl = baseUrl;
    }
}
