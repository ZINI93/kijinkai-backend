package com.kijinkai.domain.payment.infrastructure.adapter.in.messaging;


import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PaymentMassage {

    public static final String DEPOSIT_CREATE_SUCCESS = "payment.deposit.create.success";
    public static final String DEPOSIT_APPROVE_SUCCESS = "payment.deposit.approve.success";
    public static final String DEPOSIT_RETRIEVED_SUCCESS = "payment.deposit.retrieved.success";

    public static final String WITHDRAW_CREATE_SUCCESS = "payment.withdraw.create.success";
    public static final String WITHDRAW_APPROVE_SUCCESS = "payment.withdraw.approve.success";
    public static final String WITHDRAW_RETRIEVED_SUCCESS = "payment.withdraw.retrieved.success";

    public static final String REFUND_CREATE_SUCCESS = "payment.refund.create.success";
    public static final String REFUND_APPROVE_SUCCESS = "payment.refund.approve.success";
    public static final String REFUND_RETRIEVED_SUCCESS = "payment.refund.retrieved.success";

    public static final String ORDER_PAYMENT_CREATE_SUCCESS = "payment.orderPayment.create.success";
    public static final String ORDER_PAYMENT_COMPLETE_SUCCESS = "payment.orderPayment.approve.success";
    public static final String ORDER_PAYMENT_RETRIEVED_SUCCESS = "payment.orderPayment.retrieved.success";



}
