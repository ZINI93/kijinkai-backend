package com.kijinkai.domain.campaign.domain.modal;

public enum CampaignStatus {
    DRAFT,      // 임시 저장 (운영자에게만 보이고 아직 활성화 전인 상태)
    READY,      // 대기 (시작 일시가 아직 되지 않아 공개 대기 중인 상태)
    PROGRESS,   // 진행 중 (현재 활발히 노출 및 참여가 가능한 상태)
    PAUSED,     // 일시 중지 (문제가 생기거나 재고 부족 등으로 잠시 멈춘 상태)
    FINISHED,   // 기간 만료 (설정된 종료 일시가 지나 자연스럽게 종료된 상태)
    CLOSED,     // 강제 종료 (기간은 남았지만 운영자가 수동으로 닫은 상태)
    DELETED     // 삭제 (DB에서 실제로 지우지 않고 논리적으로 삭제 처리한 상태)
}
