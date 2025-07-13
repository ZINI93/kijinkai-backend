package com.kijinkai.domain.customer.dto;

import com.kijinkai.domain.customer.entity.CustomerTier;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

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

    public CustomerRequestDto(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}
