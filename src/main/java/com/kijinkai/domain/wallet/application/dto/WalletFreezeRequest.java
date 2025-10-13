package com.kijinkai.domain.wallet.application.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WalletFreezeRequest {


    @Schema(description = "월렛을 정지 사유", example = "버그를 사용해서 돈을 충전했음.")
    @NotBlank(message = "정지 사유는 필수 입니다.")
    private String reason;


    public WalletFreezeRequest(String reason) {
        this.reason = reason;
    }
}
