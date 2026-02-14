package com.kijinkai.domain.post.dto.request;

import com.kijinkai.domain.post.entity.PostStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "포스트 업데이트 요청")
public class PostUpdateDto {


    @Getter
    public static class BaseInfo {
        private String title;
        private String content;
    }

    @Getter
    public static class NoticeUpdate{
        BaseInfo baseInfo;
        private boolean pinned;
        private boolean secret;
        private PostStatus postStatus;
    }

    @Getter
    public static class  ReviewUpdate{
        BaseInfo baseInfo;
        private boolean isImageDeleted;
    }
}
