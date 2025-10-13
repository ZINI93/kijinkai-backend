package com.kijinkai.domain.wallet.domain.model;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.exception.PaymentAmountException;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletStatus;
import com.kijinkai.domain.wallet.application.dto.WalletFreezeRequest;
import com.kijinkai.domain.wallet.domain.exception.InsufficientBalanceException;
import com.kijinkai.domain.wallet.domain.exception.WalletStatusException;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {

    private Long walletId;
    private UUID walletUuid;
    private UUID customerUuid;
    private BigDecimal balance;
    private Currency currency;
    private WalletStatus walletStatus;
    private String freezeReason;
    private Long version;


    public void freeze(WalletFreezeRequest request) {
        this.freezeReason = request.getReason();
        this.walletStatus = WalletStatus.FROZEN;

    }
    public void unfreeze() {
        this.walletStatus = WalletStatus.ACTIVE;
    }


    public boolean isActive() {
        return this.walletStatus == WalletStatus.ACTIVE;
    }

    public boolean isFrozen() {
        return this.walletStatus == WalletStatus.FROZEN;
    }

    public void requireActiveStatus() {
        if (this.walletStatus != WalletStatus.ACTIVE) {
            throw new WalletStatusException("Wallet must be in active status to proceed");
        }
    }

    /**
     * 잔액 부족 검증
     *
     * @param totalAmount
     */
    public void requireSufficientBalance(BigDecimal totalAmount) {
        if (this.balance.compareTo(totalAmount) <= 0) {
            throw new PaymentAmountException("Amount must be a positive value");
        }
    }


    /**
     * 환정 가능 최소 금액
     */
    public void validateMinimumExchangeAmount() {
        if (this.balance.compareTo(new BigDecimal(8000.00)) < 0) {
            throw new InsufficientBalanceException("The minimum exchange amount is 8,000 yen");
        }
    }

}
