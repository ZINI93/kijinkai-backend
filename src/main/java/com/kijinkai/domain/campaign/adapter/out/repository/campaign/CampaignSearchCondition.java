package com.kijinkai.domain.campaign.adapter.out.repository.campaign;


import com.kijinkai.domain.campaign.domain.modal.CampaignStatus;
import com.kijinkai.domain.campaign.domain.modal.CampaignType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
public class CampaignSearchCondition {

    private String title;           // 제목 키워드
    private CampaignType type;      // 유형 (PROMOTION, NOTICE 등)
    private CampaignStatus status;  // 상태 (PROGRESS, FINISHED 등)

    private LocalDate searchStartDate; // 조회 기간 시작
    private LocalDate searchEndDate;   // 조회 기간 종료

    private Boolean featured;       // 메인 노출 여부

    private Integer minParticipants; // 최소 참여자 수 (필터)

}
