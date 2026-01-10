package com.kijinkai.domain.common.dto;


import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class MainPageResponseDto{

    String nickname;
    String tier;
    int coupons;
    int orders;
}
