package com.kijinkai.domain.post.entity;


import com.kijinkai.domain.common.BaseEntity;
import com.kijinkai.domain.post.dto.request.PostUpdateDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts")
@Entity
public class PostJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    @Comment("포스트 고유 ID")
    private Long postId;

    @Comment("포스트 외부 식별자")
    @Column(name = "post_uuid", nullable = false, updatable = false, unique = true)
    private UUID postUuid;

    @Comment("주문번호")
    @Column(name = "order_code")
    private String orderCode;

    @Comment("작성자 외부 식별자")
    @Column(name = "author_uuid", nullable = false)
    private UUID authorUuid;

    @Comment("포스트 카테고리")
    @Enumerated(EnumType.STRING)
    @Column(name = "post_category", nullable = false)
    private PostCategory postCategory;

    @Comment("제목")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Comment("내용")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Comment("조회수")
    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Comment("상단 고정 여부")
    @Builder.Default
    @Column(name = "is_pinned", nullable = false)
    private boolean pinned = false;

    @Comment("비밀글 여부")
    @Builder.Default
    @Column(name = "is_secret", nullable = false)
    private boolean secret = false;

    @Comment("포스트 상태 (PUBLISHED, DRAFT, DELETED 등)")
    @Enumerated(EnumType.STRING)
    @Column(name = "post_status", nullable = false)
    private PostStatus postStatus;


    public void updateNotice(PostUpdateDto.NoticeUpdate updateDto) {

        this.title = updateDto.getBaseInfo().getTitle();
        this.content = updateDto.getBaseInfo().getContent();
        this.pinned = updateDto.isPinned();
        this.secret = updateDto.isSecret();
        this.postStatus = updateDto.getPostStatus();
    }

    public void updateReview(PostUpdateDto.ReviewUpdate updateDto) {
        this.title = updateDto.getBaseInfo().getTitle();
        this.content = updateDto.getBaseInfo().getContent();
    }

}
