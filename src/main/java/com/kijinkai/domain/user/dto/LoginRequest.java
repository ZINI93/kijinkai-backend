package com.kijinkai.domain.user.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginRequest {

    String email;
    String password;

}
