package com.kijinkai.domain.wallet.domain.model;

import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.payment.domain.exception.PaymentAmountException;
import com.kijinkai.domain.wallet.adapter.out.persistence.entity.WalletStatus;
import com.kijinkai.domain.wallet.application.dto.WalletFreezeRequest;
import com.kijinkai.domain.wallet.domain.exception.InsufficientBalanceException;
import com.kijinkai.domain.wallet.domain.exception.WalletStatusException;
import com.kijinkai.domain.wallet.domain.exception.WalletUpdateFailedException;
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


    //검증

    public void validateActive(){
        if (this.walletStatus != WalletStatus.ACTIVE){
            throw new WalletStatusException("활성화 상태에서만 결제가 가능합니다.");
        }
    }


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


    public void increaseBalance(BigDecimal amount){

        // 음수 값 거절
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0){
            throw new WalletUpdateFailedException("증가시킬 금액은 0 보다 커야 합니다.");
        }

        // 잔액의 null일 경우
        BigDecimal currentBalance = (this.balance == null) ? BigDecimal.ZERO : this.balance;

        // 환불금액 증가
        this.balance = this.getBalance().add(amount);
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
