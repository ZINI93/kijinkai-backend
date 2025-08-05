package com.kijinkai.domain.payment.domain.util;

import java.math.BigDecimal;

public class PaymentContents {
    public static final BigDecimal DEPOSIT_FEE = new BigDecimal("200.00");
    public static final BigDecimal WITHDRAWAL_FEE = new BigDecimal("300.00");
    public static  final int MAX_RETRY_COUNT = 3;

}
