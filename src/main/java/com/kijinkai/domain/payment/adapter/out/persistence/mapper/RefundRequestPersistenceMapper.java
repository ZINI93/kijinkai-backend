package com.kijinkai.domain.payment.adapter.out.persistence.mapper;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.RefundRequestJpaEntity;
import com.kijinkai.domain.payment.domain.model.RefundRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RefundRequestPersistenceMapper {

    RefundRequest toRefundRequest(RefundRequestJpaEntity refundRequestJpaEntity);
    RefundRequestJpaEntity toRefundRequestJpaEntity(RefundRequest refundRequest);

    List<RefundRequest> toRefundRequests (List<RefundRequestJpaEntity> refundRequestJpaEntities);
    List<RefundRequestJpaEntity> toRefundRequestsJpaEntity(List<RefundRequest> refundRequest);

}
