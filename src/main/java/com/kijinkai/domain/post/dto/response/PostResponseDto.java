package com.kijinkai.domain.post.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "포스트 응답")
public class PostResponseDto {

    private UUID postUuid;
    private UUID postImageUuid;
    private String nickname;
    private String imageUrl;

    private String title;
    private String content;
    private Integer viewCount;
    private LocalDate createAt;
    private boolean pinned;
    private boolean isOwner;
}
