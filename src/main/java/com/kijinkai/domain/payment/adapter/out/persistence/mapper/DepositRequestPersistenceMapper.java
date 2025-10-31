package com.kijinkai.domain.payment.adapter.out.persistence.mapper;


import com.kijinkai.domain.payment.adapter.out.persistence.entity.DepositRequestJpaEntity;
import com.kijinkai.domain.payment.domain.model.DepositRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepositRequestPersistenceMapper {

    DepositRequest toDepositRequest(DepositRequestJpaEntity depositRequestJpaEntity);
    DepositRequestJpaEntity toDepositRequestJapEntity(DepositRequest depositRequest);

    List<DepositRequest> toDepositRequests(List<DepositRequestJpaEntity> depositRequestJpaEntities);
    List<DepositRequestJpaEntity> toDepositRequestsJpaEntity(List<DepositRequest> depositRequests);

}
