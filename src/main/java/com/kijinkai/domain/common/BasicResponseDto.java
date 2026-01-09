package com.kijinkai.domain.common;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "API 공틍 응답 형식")
@JsonPropertyOrder({"success", "message", "data"})
public class BasicResponseDto<T> {


    @Schema(description = "성공 여부", example = "true")
    private Boolean success;

    @Schema(description = "응답 메세지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "응답 데이터",
    anyOf = {UserResponseDto.class, Object.class})
    private T data;

    /**
     * 성공 응답 - 기본 메시지와 데이터
     */
    public static <T> BasicResponseDto<T> success(T data) {
        return BasicResponseDto.<T>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(data)
                .build();
    }

    /**
     * 성공 응답 - 커스텀 메시지와 데이터
     */
    public static <T> BasicResponseDto<T> success(String message, T data) {
        return BasicResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 성공 응답 - 메시지만 (데이터 없음)
     */
    public static BasicResponseDto<Void> successWithMessage(String message) {  // 메서드명 명확화
        return BasicResponseDto.<Void>builder()
                .success(true)
                .message(message)
                .data(null)
                .build();
    }

    /**
     * 성공 응답 - 기본 메시지만
     */
    public static BasicResponseDto<Void> success() {  // 새로 추가
        return BasicResponseDto.<Void>builder()
                .success(true)
                .message("요청이 성공적으로 처리되었습니다.")
                .data(null)
                .build();
    }

    // === 실패 응답 메서드들 ===

    /**
     * 실패 응답 - 에러 메시지만
     */
    public static BasicResponseDto<Void> failure(String message) {  // fail → failure (명확성)
        return BasicResponseDto.<Void>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }

    /**
     * 실패 응답 - 에러 메시지와 에러 데이터
     */
    public static <T> BasicResponseDto<T> failure(String message, T errorData) {
        return BasicResponseDto.<T>builder()
                .success(false)
                .message(message)
                .data(errorData)
                .build();
    }
}