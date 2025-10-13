package com.kijinkai.domain.payment.domain.validator;

import com.kijinkai.domain.payment.application.dto.PaymentDepositRequestDto;
import com.kijinkai.domain.payment.application.dto.WithdrawalRequestDto;
import com.kijinkai.domain.payment.domain.exception.PaymentAmountException;
import com.kijinkai.domain.payment.domain.repository.DepositRequestRepository;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletJpaEntity;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletStatus;
import com.kijinkai.domain.wallet.domain.exception.WalletNotActiveException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class PaymentValidator {

    private DepositRequestRepository depositRequestRepository;


    public void validateAmount(PaymentDepositRequestDto requestDto) {
        if (requestDto.getAmountOriginal().compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentAmountException("Amount must be a positive value");
        }
    }

    public void validateAmountByWithdrawal(WithdrawalRequestDto requestDto) {
        if (requestDto.getAmountOriginal().compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentAmountException("Amount must be a positive value");
        }
    }



    public void validateDepositEligibility(BigDecimal originalAmount, WalletJpaEntity wallet) {

        if (!wallet.getWalletStatus().equals(WalletStatus.ACTIVE)){
            throw new WalletNotActiveException("Inactive wallets can not be deposited");
        }

        if (originalAmount.compareTo(new BigDecimal("1000")) < 0) {
            throw new PaymentAmountException("The minimum deposit amount is 1,000 en");
        }

        if (originalAmount.compareTo(new BigDecimal("1000000")) > 0) {
            throw new PaymentAmountException("The maximum deposit amount is 1,000,000 en");
        }

    }

    public void validateWithdrawEligibility(BigDecimal originalAmount) {

        BigDecimal minimumAmount = new BigDecimal("20000");
        if (originalAmount.compareTo(minimumAmount) < 0) {
            throw new PaymentAmountException("The minimum withdraw is 20,000 en");
        }
    }

}
