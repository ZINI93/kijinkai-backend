package com.kijinkai.domain.campaign.adapter.out.entity;


import com.kijinkai.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "campaign_images")
@Entity
public class CampaignImageJpaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_image_id")
    @Comment("캠페인 이미지 고유 식별자 (PK)")
    private Long campaignImageId;

    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @Comment("이미지 외부 노출용 UUID")
    private UUID campaignImageUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    @Comment("연관된 캠페인 ID (FK)")
    private CampaignJpaEntity campaign;

    @Column(nullable = false, length = 500)
    @Comment("이미지 저장 경로 (URL)")
    private String imageUrl;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Comment("이미지 유형 (예: THUMBNAIL, BANNER, CONTENT)")
    private String imageType;

    @Column(nullable = false)
    @Comment("이미지 노출 순서")
    private int displayOrder;

    @Column(length = 200)
    @Comment("이미지 대체 텍스트 (웹 접근성용)")
    private String altText;

    /**
     * 신규 이미지 생성 시 UUID 자동 생성
     */
    @PrePersist
    public void prePersist() {
        if (this.campaignImageUuid == null) {
            this.campaignImageUuid = UUID.randomUUID();
        }
    }
}
