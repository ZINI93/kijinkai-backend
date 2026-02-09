package com.kijinkai.domain.campaign.adapter.out.entity;


import com.kijinkai.domain.campaign.domain.modal.CampaignImageType;
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

    @Comment("캠페인 이미지 고유 식별자 (PK)")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_image_id")
    private Long campaignImageId;

    @Comment("이미지 외부 노출용 UUID")
    @Column(name = "campaign_image_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID campaignImageUuid;

    @Comment("생성한 관리자")
    @Column(name = "created_admin_uuid", nullable = false ,columnDefinition = "BINARY(16)", updatable = false)
    private UUID createdAdminUuid;


    @Comment("연관된 캠페인 ID (FK)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private CampaignJpaEntity campaign;

    @Comment("이미지 저장 경로 (URL)")
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;


    @Comment("이미지 유형")
    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", nullable = false, length = 50)
    private CampaignImageType imageType;

    @Comment("이미지 노출 순서")
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Comment("이미지 대체 텍스트 (웹 접근성용)")
    @Column(name = "alt_text", length = 200)
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
