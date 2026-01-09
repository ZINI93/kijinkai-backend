package com.kijinkai.domain.user.application.port.in;

import com.kijinkai.domain.user.application.dto.request.UserSignUpRequestDto;import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import com.kijinkai.domain.user.application.dto.response.UserSignUpResponse;

public interface SignUpUserUseCase {
    UserSignUpResponse signUp(UserSignUpRequestDto userSignUpRequestDto);

}
