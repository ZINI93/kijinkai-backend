package com.kijinkai.domain.campaign.adapter.out.entity;


import com.kijinkai.domain.campaign.domain.modal.RewardType;
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
@Table(name = "campaign_participants")
@Entity
public class CampaignParticipantJpaEntity extends BaseEntity {


    @Comment("참여 내역 고유 식별자 (PK)")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_participant_id")
    private Long campaignParticipantId;

    @Comment("참여 내역 외부 확인용 UUID")
    @Column(name = "campaign_participant_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID campaignParticipantUuid;

    @Comment("참여한 캠페인 ID (FK)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id", nullable = false)
    private CampaignJpaEntity campaign;

    @Comment("참여한 사용자의 식별자 (외부 회원 UUID)")
    @Column(name = "user_uuid", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userUuid;

    @Comment("참여 일시")
    @Column(name = "participated_at", nullable = false)
    private LocalDateTime participatedAt;

    @Comment("보상 지급 여부")
    @Column(name = "reward_given", nullable = false)
    private Boolean rewardGiven;


    @Comment("보상 유형 (예: POINT, COUPON, BADGE)")
    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", length = 50)
    private RewardType rewardType;

    /**
     * 영속화 전 데이터 초기화
     */
    @PrePersist
    public void prePersist() {
        if (this.campaignParticipantUuid == null) {
            this.campaignParticipantUuid = UUID.randomUUID();
        }
        if (this.participatedAt == null) {
            this.participatedAt = LocalDateTime.now();
        }
        if (this.rewardGiven == null) {
            this.rewardGiven = false;
        }
    }

    /**
     * 비즈니스 로직: 보상 지급 처리
     */
    public void markRewardAsGiven(RewardType rewardType) {
        this.rewardGiven = true;
        this.rewardType = rewardType;
    }
}
