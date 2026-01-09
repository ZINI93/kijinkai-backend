package com.kijinkai.domain.user.application.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


@Schema(description = "사용자 회원가입 정보 전달")
@Getter
@Builder
public class UserSignUpResponse{

    // user
    private UUID UserUuid;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    // customer
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private UUID walletUuid;

}
