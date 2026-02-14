package com.kijinkai.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "포스트 생성 요청")
public class PostRequestDto {

    @Getter
    public static class BaseInfo{

        @NotBlank(message = "리뷰 제목을 입력해주세요.")
        @Size(max = 200)
        @Schema(description = "리뷰 제목")
        private String title;

        @NotBlank(message = "리뷰 내용을 입력해주세요.")
        @Schema(description = "리뷰 내용")
        private String content;
    }

    @Getter
    public static class NoticeCreate{
        private BaseInfo baseInfo;

        @Schema(description = "상단 고정 여부")
        private boolean pinned;

        @Schema(description = "비밀글 여부")
        private boolean secret;
    }


    @Getter
    public static class ReviewCreate{
        private BaseInfo baseInfo;

        @NotBlank(message = "주문 코드는 필수입니다.")
        @Schema(description = "연관 주문 코드", example = "ORD-20260210-A123")
        private String orderCode;
    }


}
