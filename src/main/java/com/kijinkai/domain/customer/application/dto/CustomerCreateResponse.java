package com.kijinkai.domain.customer.application.dto;


import com.kijinkai.domain.customer.domain.model.CustomerTier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
@Schema(description = "구매자 등록 정보 응답")
public class CustomerCreateResponse {


    @Schema(description = "구매자 고유 식별자", example = "xxx-xxx")
    UUID customerUuid;
    @Schema(description = "사용자 고유 식별자", example = "xxxx-xxxx")
    UUID userUuid;
    @Schema(description = "구매자 이름", example = "JInhee")
    String firstName;
    @Schema(description = "구매자 성", example = "Park")
    String lastName;
    @Schema(description = "휴대폰 번호", example = "080-1234-1234")
    String phoneNumber;
    @Schema(description = "구매자 등급", example = "BRONZE",
            allowableValues = {"BRONZE", "SILVER", "GOLD"})
    CustomerTier customerTier;


    // -- Address

    @Schema(description = "주소 고유 식별자", example = "1111-1111")
    UUID addressUuid;

    @Schema(description = "수취인", example = "Park Jinhee")
    String recipientName;

    @Schema(description = "수취인 전화번호", example = "010-1111-1234")
    String recipientPhoneNumber;

    @Schema(description = "도착지 국가", example = "Korea")
    String country;

    @Schema(description = "우편번호", example = "111-111")
    String zipcode;

    @Schema(description = "주소1", example = "대구광역시")
    String state;

    @Schema(description = "주소2", example = "동구")
    String city;

    @Schema(description = "주소3", example = "신암동 111-11")
    String street;


}
