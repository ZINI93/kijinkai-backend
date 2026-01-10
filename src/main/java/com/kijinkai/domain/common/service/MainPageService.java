package com.kijinkai.domain.common.service;

import com.kijinkai.domain.common.dto.MainPageResponseDto;
import com.kijinkai.domain.customer.application.dto.CustomerResponseDto;
import com.kijinkai.domain.customer.application.port.in.GetCustomerUseCase;
import com.kijinkai.domain.user.application.dto.response.UserResponseDto;
import com.kijinkai.domain.user.application.port.in.GetUserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MainPageService {


    private final GetUserUseCase getUserUseCase;
    private final GetCustomerUseCase getCustomerUseCase;


    public MainPageResponseDto mainPage(UUID userUuid)  {

        // 닉네임
        UserResponseDto userInfo = getUserUseCase.getUserInfo(userUuid);

        // 티어
        CustomerResponseDto customerInfo = getCustomerUseCase.getCustomerInfo(userUuid);

        //쿠폰 - 미구현


        //주문 - 미구현


        // 환률 - 미구현
        return MainPageResponseDto.builder()
                .nickname(userInfo.getNickname())
                .tier(customerInfo.getCustomerTier().getDisplayName())
                .coupons(0)
                .orders(0)
                .build();
    }

}
