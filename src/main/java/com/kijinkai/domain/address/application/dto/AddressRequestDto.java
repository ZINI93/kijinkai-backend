package com.kijinkai.domain.address.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AddressRequestDto {

    @Schema(description = "수취인", example = "Park Jinhee")
    private String recipientName;

    @Schema(description = "수취인 전화번호", example = "010-1111-1234")
    private String recipientPhoneNumber;

    @Schema(description = "우편번호", example = "111-111")
    private String zipcode;

    @Schema(description = "주소", example = "대구광역시")
    private String streetAddress;

    @Schema(description = "상세주소", example = "동구")
    private String detailAddress;

    @Schema(description = "개인 통관고유번호", example = "신암동 111-11")
    private String pcc;


}
