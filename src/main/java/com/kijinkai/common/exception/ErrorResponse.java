package com.kijinkai.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ErrorResponse {

    private final String code;
    private final String message;
}
