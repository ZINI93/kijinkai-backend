package com.kijinkai.domain.address.dto;

import com.kijinkai.domain.customer.entity.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AddressResponseDto {

    @Schema(description = "주소 고유 식별자", example = "1111-1111")
    private UUID addressUuid;

    @Schema(description = "고객 고유 식별자", example = "1111-1111")
    private UUID customerUuid;

    @Schema(description = "수취인", example = "Park Jinhee")
    private String recipientName;

    @Schema(description = "수취인 전화번호", example = "010-1111-1234")
    private String recipientPhoneNumber;

    @Schema(description = "도착지 국가", example = "Korea")
    private String country;

    @Schema(description = "우편번호", example = "111-111")
    private String zipcode;

    @Schema(description = "주소1", example = "대구광역시")
    private String state;

    @Schema(description = "주소2", example = "동구")
    private String city;

    @Schema(description = "주소3", example = "신암동 111-11")
    private String street;

    @Builder
    public AddressResponseDto(UUID customerUuid, String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street, UUID addressUuid) {
        this.customerUuid = customerUuid;
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.country = country;
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
        this.addressUuid = addressUuid;
    }
}
