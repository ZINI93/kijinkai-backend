package com.kijinkai.domain.payment.adapter.out.persistence.mapper;

import com.kijinkai.domain.payment.adapter.out.persistence.entity.WithdrawRequestJpaEntity;
import com.kijinkai.domain.payment.domain.model.WithdrawRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WithdrawRequestPersistenceMapper {

    WithdrawRequest toWithdrawRequest(WithdrawRequestJpaEntity withdrawRequestJpaEntity);
    WithdrawRequestJpaEntity toWithdrawRequestJpaEntity(WithdrawRequest withdrawRequest);

    List<WithdrawRequest> toWithdrawRequests(List<WithdrawRequestJpaEntity> withdrawRequestJpaEntity);
    List<WithdrawRequestJpaEntity> toWithdrawRequestsJpaEntity(List<WithdrawRequest> withdrawRequest);

}
