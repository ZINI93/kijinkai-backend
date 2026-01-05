package com.kijinkai.domain.jwt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshRequestDto {

    @NotBlank
    private String refreshToken;
}
