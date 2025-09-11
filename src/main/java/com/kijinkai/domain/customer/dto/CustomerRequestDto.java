package com.kijinkai.domain.customer.dto;

import com.kijinkai.domain.customer.entity.CustomerTier;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@Schema(description = "구매자 요청")
public class CustomerRequestDto {

    @Schema(description = "구매자 이름", example = "Jinhee")
    @NotBlank(message = "이름은 필수 입니다.")
    private String firstName;

    @Schema(description = "구매자 성", example = "Park")
    @NotBlank(message = "성은 필수 입니다.")
    private String lastName;

    @Schema(description = "휴대폰 번호", example = "080-1234-1234")
    @NotBlank(message = "전화 번호는 필수 입니다.")
    private String phoneNumber;

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


    public CustomerRequestDto(String firstName, String lastName, String phoneNumber, String recipientName, String recipientPhoneNumber, String country, String zipcode, String state, String city, String street) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.country = country;
        this.zipcode = zipcode;
        this.state = state;
        this.city = city;
        this.street = street;
    }
}
