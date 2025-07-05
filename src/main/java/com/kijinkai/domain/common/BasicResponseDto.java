package com.kijinkai.domain.common;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Default Response Dto")
public class BasicResponseDto<T> {


    @Schema(description = "Successful request processing", example = "true")
    private Boolean success;

    @Schema(description = "Response message", example = "success")
    private String message;


    @Schema(description = "Response data", example = "data")
    private T data;

    // 성공 응답 (데이터 포함)
    public static <T> BasicResponseDto<T> success(T data) {
        return BasicResponseDto.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    // 성공 응답 (메시지 및 데이터 포함)
    public static <T> BasicResponseDto<T> success(String message, T data) {
        return BasicResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // 성공 응답 (데이터 없이 메시지만)
    public static BasicResponseDto<Void> success(String message) {
        return BasicResponseDto.<Void>builder()
                .success(true)
                .message(message)
                .data(null)
                .build();
    }

    // 실패 응답 (메시지 포함)
    public static BasicResponseDto<Void> fail(String message) {
        return BasicResponseDto.<Void>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }

    // 실패 응답 (메시지 및 에러 데이터 포함 - 예를 들어, 유효성 검사 오류 목록)
    public static <T> BasicResponseDto<T> fail(String message, T errorData) {
        return BasicResponseDto.<T>builder()
                .success(false)
                .message(message)
                .data(errorData)
                .build();
    }
}
