package com.kijinkai.domain.customer.dto;

import com.kijinkai.domain.customer.entity.CustomerTier;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Getter
@Schema(description = "구매자 정보 응답")
public class CustomerResponseDto {

    @Schema(description = "구매자 고유 식별자", example = "xxx-xxx")
    private UUID customerUuid;

    @Schema(description = "사용자 고유 식별자", example = "xxxx-xxxx")
    private UUID userUuid;

    @Schema(description = "구매자 이름", example = "JInhee")
    private String firstName;

    @Schema(description = "구매자 성", example = "Park")
    private String lastName;

    @Schema(description = "휴대폰 번호", example = "080-1234-1234")
    private String phoneNumber;

    @Schema(description = "구매자 등급", example = "BRONZE",
    allowableValues = {"BRONZE", "SILVER", "GOLD"})
    private CustomerTier customerTier;


    @Builder
    public CustomerResponseDto(UUID customerUuid, String firstName, String lastName, String phoneNumber, CustomerTier customerTier, UUID userUuid) {
        this.customerUuid = customerUuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.customerTier = customerTier;
        this.userUuid = userUuid;
    }

    @QueryProjection
    public CustomerResponseDto(String firstName, String lastName, String phoneNumber, CustomerTier customerTier) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.customerTier = customerTier;
    }
}
