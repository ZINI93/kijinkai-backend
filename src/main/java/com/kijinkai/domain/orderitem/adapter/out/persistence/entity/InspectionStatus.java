package com.kijinkai.domain.orderitem.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InspectionStatus {

    NONE("비검수대상"),
    READY("검수대기"),
    COMPLETED("이메일 발송"),
    FAILED("발송 실패");


    private final String displayName;


}
