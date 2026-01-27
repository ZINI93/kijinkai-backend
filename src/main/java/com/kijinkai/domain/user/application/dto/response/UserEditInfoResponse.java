package com.kijinkai.domain.user.application.dto.response;

import com.kijinkai.domain.payment.domain.enums.BankType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@Schema(description = "회원정보 수정")
public class UserEditInfoResponse {

    String email;
    String nickName;
    String firstName;
    String lastName;

    String pcc;

    String phoneNumber;

    String recipientName;
    String recipientPhoneNumber;
    String zipCode;
    String streetAddress;
    String detailAddress;

    BankType bankType;
    String accountHolder;
    String accountNumber;


}
