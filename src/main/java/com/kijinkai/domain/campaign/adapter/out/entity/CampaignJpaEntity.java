package com.kijinkai.domain.campaign.adapter.out.entity;


import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import com.kijinkai.domain.campaign.domain.modal.CampaignType;
import com.kijinkai.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "campaigns")
@Entity
public class CampaignJpaEntity extends BaseEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_id")
    @Comment("캠페인 고유 식별자 (PK)")
    private Long campaignId;

    @Comment("캠페인 외부 노출용 UUID")
    @Column(name = "campaign_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID campaignUuid;

    @Comment("생성한 관리자")
    @Column(name = "created_admin_uuid", nullable = false ,columnDefinition = "BINARY(16)", updatable = false)
    private UUID createdAdminUuid;


    @Comment("캠페인 제목")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Comment("캠페인 요약 설명")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Comment("캠페인 상세 내용")
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Comment("캠페인 유형")
    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_type", nullable = false, length = 50)
    private CampaignType campaignType;

    @Comment("캠페인 상태")
    @Enumerated(EnumType.STRING)
    @Column(name = "campaign_status", nullable = false, length = 50)
    private CampaignStatus campaignStatus;

    @Comment("캠페인 시작 일시")
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Comment("캠페인 종료 일시")
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Comment("메인 노출 여부")
    @Column(name = "featured", nullable = false)
    private boolean featured;

    @Comment("노출 순서")
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Comment("현재 참여자 수")
    @Column(name = "participant_count", nullable = false)
    private int participantCount;

    @Comment("상시 캠페인 여부")
    @Column(name = "always_on", nullable = false)
    private boolean alwaysOn;

    @PrePersist
    public void prePersist() {
        if (this.campaignUuid == null) {
            this.campaignUuid = UUID.randomUUID();
        }
        if (this.participantCount < 0) {
            this.participantCount = 0;
        }
    }

}
