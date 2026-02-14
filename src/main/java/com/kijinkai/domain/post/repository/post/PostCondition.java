package com.kijinkai.domain.post.repository.post;


import com.kijinkai.domain.post.entity.PostCategory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class PostCondition {

    // 유저
    private String title;
    private String content;

    //관리자
    private UUID authorUuid;
    private PostCategory postCategory;
    private LocalDate startDate;
    private LocalDate endDate;

}
