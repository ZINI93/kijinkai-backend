package com.kijinkai.domain.common.service;


import com.kijinkai.domain.common.dto.MyPageResponseDto;
import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import com.kijinkai.domain.user.application.port.in.GetUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.UUID;


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MyPageService {


    private final GetUserUseCase getUserUseCase;

    public MyPageResponseDto myPage(UUID userUuid){

        UserResponseDto userInfo = getUserUseCase.getUserInfo(userUuid);

        //미구현은 나중에 구현예정

        return MyPageResponseDto.builder()
                //유저 정보관련
                .nickname(userInfo.getNickname())

                //지갑관련
                .depositBalance(BigDecimal.ZERO)
                .availableBalance(BigDecimal.ZERO)
                .outstandingBalance(BigDecimal.ZERO)

                //배송 출고 관련
                .undispatchedOrders(0)
                .failedOrders(0)
                .purchaseRequestOrders(0)
                .purchaseApprovedOrders(0)
                .firstPaymentCompletedOrders(0)
                .localDeliveryCompletedOrders(0)
                .combinedProcessingOrders(0)
                .secondPaymentRequestedOrders(0)
                .secondPaymentCompletedOrders(0)
                .internationalShippingOrders(0)
                .deliveredOrders(0)
                .build();

    }
}
