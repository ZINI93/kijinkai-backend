package com.kijinkai.domain.transaction.service;

import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.order.entity.Order;
import com.kijinkai.domain.transaction.dto.TransactionResponseDto;
import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.transaction.exception.TransactionNotFoundException;
import com.kijinkai.domain.transaction.factory.TransactionFactory;
import com.kijinkai.domain.transaction.mapper.TransactionMapper;
import com.kijinkai.domain.transaction.repository.TransactionRepository;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    private final TransactionMapper transactionMapper;
    private final TransactionFactory transactionFactory;

    private final UserValidator userValidator;

    /**
     * 주문 완료시 거래 내역저장
     * @param userUuid
     * @param wallet
     * @param order
     * @param transactionType
     * @param amount
     * @param balanceBefore
     * @param balanceAfter
     * @param transactionStatus
     * @return
     */
    @Override
    public Transaction createTransactionWithValidate(UUID userUuid, Wallet wallet, Order order, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, TransactionStatus transactionStatus) {
        Customer customer = findCustomerByUserUuid(userUuid);

        Transaction transaction = transactionFactory.createTransaction(customer, wallet, order, transactionType, amount, balanceBefore, balanceAfter, transactionStatus);

        return transactionRepository.save(transaction);
    }

    // 좀 더 확인 후 구현
    @Override
    public TransactionResponseDto updateTransactionWithValidate(UUID userUuid, UUID transactionUuid) {
        return null;
    }

    /**
     * 유저가 거래정보 확인
     * @param userUuid
     * @param transactionUuid
     * @return
     */
    @Override
    public TransactionResponseDto getTransactionInfo(UUID userUuid, UUID transactionUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Transaction transaction = findTransactionByCustomerAndTransactionUuid(customer, transactionUuid);

        return transactionMapper.toResponse(transaction);
    }

    /**
     * 관리자가 거래정보 확인
     * @param userUuid
     * @param transactionUuid
     * @return
     */
    @Override
    public TransactionResponseDto getTransactionInfoByAdmin(UUID userUuid, UUID transactionUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        userValidator.requireAdminRole(customer.getUser());

        Transaction transaction = findTransactionByTransactionUuid(transactionUuid);

        return transactionMapper.toResponse(transaction);
    }


    private Transaction findTransactionByCustomerAndTransactionUuid(Customer customer,UUID transactionUuid) {
        return transactionRepository.findByCustomerCustomerUuidAndTransactionUuid(customer.getCustomerUuid(), transactionUuid)
                .orElseThrow(() -> new TransactionNotFoundException(String.format("Transaction not found for customer uuid: %s, transaction uuid: %s", customer.getCustomerUuid(), transactionUuid)));
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid: %s", userUuid)));
    }

    private Transaction findTransactionByTransactionUuid(UUID transactionUuid) {
        return transactionRepository.findByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new TransactionNotFoundException(String.format("Transaction not found for transaction uuid: %s", transactionUuid)));
    }
}
