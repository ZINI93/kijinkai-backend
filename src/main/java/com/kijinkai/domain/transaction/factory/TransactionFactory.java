package com.kijinkai.domain.transaction.factory;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.exchange.doamin.Currency;
import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.wallet.entity.Wallet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class TransactionFactory {

    public Transaction createTransaction(Customer customer, Wallet wallet, Order order, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, TransactionStatus transactionStatus){

        return Transaction.builder()
                .transactionUuid(UUID.randomUUID())
                .customer(customer)
                .wallet(wallet)
                .order(order)
                .transactionType(transactionType)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .currency(Currency.JPY)
                .transactionStatus(transactionStatus)
                .memo(null)
                .build();
    }
}
