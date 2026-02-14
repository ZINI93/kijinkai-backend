package com.kijinkai.domain.post.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_images")
@Entity
public class PostImageJpaEntity {

    @Comment("포스트 이미지 고유 ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_id", nullable = false)
    private Long postImageId;

    @Comment("이미지 외부 식별자")
    @Column(name = "post_image_uuid", nullable = false, updatable = false, unique = true)
    private UUID postImageUuid;

    @Comment("연관 포스트 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostJpaEntity post;

    @Comment("사용자가 업로드한 실제 파일명")
    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Comment("서버에 저장된 고유 파일명")
    @Column(name = "stored_file_name", nullable = false, length = 255)
    private String storedFileName;

    @Comment("파일 저장 경로 또는 URL")
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Comment("이미지 노출 순서 (0부터 시작)")
    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Comment("파일 크기 (Byte)")
    @Column(name = "file_size")
    private Long fileSize;

    @Comment("콘텐츠 타입 (MIME type)")
    @Column(name = "content_type", length = 50)
    private String contentType;


    public void updateImage(String originalName, String storedFileName, String imageUrl){

        this.originalFileName = originalName;
        this.storedFileName = storedFileName;
        this.imageUrl = imageUrl;

    }
}
