package com.kijinkai.domain.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AddressUpdateDto {

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
    public AddressUpdateDto(String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street) {
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.country = country;
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
    }
}
