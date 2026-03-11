package com.kijinkai.domain.transaction.service;

import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.transaction.controller.TransactionSearchConditionDto;
import com.kijinkai.domain.transaction.dto.TransactionAdminSearchResponseDto;
import com.kijinkai.domain.transaction.dto.TransactionAdminSummaryDto;
import com.kijinkai.domain.transaction.dto.TransactionResponseDto;
import com.kijinkai.domain.transaction.entity.Transaction;
import com.kijinkai.domain.transaction.entity.TransactionStatus;
import com.kijinkai.domain.transaction.entity.TransactionType;
import com.kijinkai.domain.transaction.exception.TransactionNotFoundException;
import com.kijinkai.domain.transaction.factory.TransactionFactory;
import com.kijinkai.domain.transaction.mapper.TransactionMapper;
import com.kijinkai.domain.transaction.repository.TransactionRepository;
import com.kijinkai.domain.transaction.repository.TransactionSearchCondition;
import com.kijinkai.domain.user.adapter.in.web.validator.UserApplicationValidator;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.domain.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final CustomerPersistencePort customerPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final WalletPersistencePort walletPersistencePort;

    private final TransactionMapper transactionMapper;
    private final TransactionFactory transactionFactory;

    private final UserApplicationValidator userValidator;

    //외부


    // 압/출금 내역 저장


    // 생성.

    /**
     * 입출금 요청 생성
     *
     * @param customerUuid
     * @param walletUuid
     * @param transactionType
     * @param paymentCode
     * @param amount
     * @param transactionStatus
     * @return
     */
    @Override
    @Transactional
    public UUID createAccountHistory(UUID customerUuid, UUID walletUuid, TransactionType transactionType, String paymentCode, BigDecimal amount, TransactionStatus transactionStatus) {

        Transaction accountHistory = transactionFactory.createAccountHistory(
                customerUuid,
                walletUuid,
                transactionType,
                paymentCode,
                amount,
                transactionStatus
        );

        Transaction savedTransaction = transactionRepository.save(accountHistory);

        return savedTransaction.getTransactionUuid();
    }


    /**
     * 주문 완료시 거래 내역저장
     *
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
    public Transaction createTransactionWithValidate(UUID userUuid, UUID walletUuid, UUID orderUuid, TransactionType transactionType, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter, TransactionStatus transactionStatus) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Wallet wallet = walletPersistencePort.findByCustomerUuid(customer.getCustomerUuid())
                .orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found for walletUuid: %s", walletUuid)));


        Transaction transaction = transactionFactory.createTransaction(customer.getCustomerUuid(), walletUuid, orderUuid, transactionType, amount, balanceBefore, balanceAfter, transactionStatus);

        return transactionRepository.save(transaction);
    }

    // 좀 더 확인 후 구현
    @Override
    public TransactionResponseDto updateTransactionWithValidate(UUID userUuid, UUID transactionUuid) {
        return null;
    }

    /**
     * 유저가 거래정보 확인
     *
     * @param userUuid
     * @param transactionUuid
     * @return
     */
    @Override
    public TransactionResponseDto getTransactionInfo(UUID userUuid, UUID transactionUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Transaction transaction = findTransactionByCustomerAndTransactionUuid(customer, transactionUuid);

        return transactionMapper.toRecordResponse(transaction);
    }

    /**
     * 관리자가 거래정보 확인
     *
     * @param userUuid
     * @param transactionUuid
     * @return
     */
    @Override
    public TransactionResponseDto getTransactionInfoByAdmin(UUID userUuid, UUID transactionUuid) {

        User user = userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for userUuid: %s", userUuid)));
        userValidator.requireAdminRole(user);

        Transaction transaction = findTransactionByTransactionUuid(transactionUuid);

        return transactionMapper.toRecordResponse(transaction);
    }


    //  업데이트.

    // 거절

    /**
     * 완료
     *
     * @param customerUuid
     * @param paymentCode
     */
    @Override
    @Transactional
    public void failedPayment(UUID customerUuid, String paymentCode) {

        // 구매자, 코드를 받아서 조회
        Transaction transaction = transactionRepository.findByCustomerUuidAndPaymentCode(customerUuid, paymentCode)
                .orElseThrow(() -> new TransactionNotFoundException("Not found Transaction"));

        // 완료로 상태변경
        transaction.failedPayment();

        // 저장 변경감지
    }


    /**
     * 완료
     *
     * @param customerUuid
     * @param paymentCode
     */
    @Override
    @Transactional
    public void completedPayment(UUID customerUuid, String paymentCode) {

        // 구매자, 코드를 받아서 조회
        Transaction transaction = transactionRepository.findByCustomerUuidAndPaymentCode(customerUuid, paymentCode)
                .orElseThrow(() -> new TransactionNotFoundException("Not found Transaction"));

        // 완료로 상태변경
        transaction.completedPayment();

        // 저장 변경감지
    }


    // 조회 .


    /**
     * @param userUuid
     * @param pageable
     * @return
     */
    @Override
    public List<TransactionResponseDto> getRecentAccountHistoryTopFive(UUID userUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);

        List<Transaction> transactions = transactionRepository.findTop5ByCustomerUuidAndTransactionTypeInOrderByCreatedAtDesc(customer.getCustomerUuid(), List.of(TransactionType.DEPOSIT, TransactionType.WITHDRAWAL));

        return transactions.stream().map(transactionMapper::toRecordResponse).toList();
    }


    @Override
    public Page<TransactionResponseDto> getTransactionHistory(UUID userUuid, TransactionType type,
                                                              LocalDate startDate, LocalDate endDate, Pageable pageable) {

        Customer customer = findCustomerByUserUuid(userUuid);

        // 1. 검색 조건 설정
        TransactionSearchCondition condition = TransactionSearchCondition.builder()
                .type(type)
                .starDate(startDate)
                .endDate(endDate)
                .build();

        // 조회
        Page<Transaction> transactions = transactionRepository.search(condition, pageable);

        return transactions.map(transactionMapper::toRecordResponse);
    }

    @Override
    public Page<TransactionAdminSearchResponseDto> getSearchTransactionByAdmin(UUID userAdminUuid, TransactionSearchConditionDto conditionDto, Pageable pageable) {

        // 관리자 조회 및 검증
        User userAdmin = userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        userAdmin.validateAdminRole();

        // 조건 매칭
        TransactionSearchCondition condition = TransactionSearchCondition.builder()
                .name(conditionDto.name())
                .phoneNumber(conditionDto.phoneNo())
                .paymentCode(conditionDto.paymentCode())
                .transactionStatus(conditionDto.status())
                .type(conditionDto.type())
                .starDate(conditionDto.startDate())
                .endDate(conditionDto.endDate())
                .build();

        // 거래조회 및 고객 유유아이디 추출
        Page<Transaction> transactions = transactionRepository.searchByAdmin(condition, pageable);
        List<UUID> customerUuidsByTransaction = transactions.stream()
                .map(Transaction::getCustomerUuid)
                .distinct()
                .toList();

        // 고객조회 및 고객 이메일 아이디 추출
        List<Customer> customers = customerPersistencePort.findAllByCustomerUuidIn(customerUuidsByTransaction);


        Map<UUID, Customer> customerMap = customers.stream()
                .collect(Collectors.toMap(
                        Customer::getCustomerUuid,
                        c -> c,
                        (e, r) -> e
                ));

        List<UUID> userUuidsByCustomer = customers.stream()
                .map(Customer::getUserUuid)
                .distinct()
                .toList();


        Map<UUID, User> userMap = userPersistencePort.findAllByUserUuidIn(userUuidsByCustomer)
                .stream()
                .collect(Collectors.toMap(
                        User::getUserUuid,
                        u -> u,
                        (e, r) -> e
                ));


        return transactions.map(transaction -> {

            Customer customer = customerMap.get(transaction.getCustomerUuid());
            User user = (customer != null) ? userMap.get(customer.getUserUuid()) : null;

            return transactionMapper.toSearchResponse(transaction, user, customer);

        });
    }

    @Override
    public TransactionAdminSummaryDto summary(UUID userAdminUuid) {

        // 관리자 조회 및 검증
        User userAdmin = userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        userAdmin.validateAdminRole();

        LocalDate now = LocalDate.now();

        return transactionRepository.getTransactionSummary(now);
    }


    // helper

    private Transaction findTransactionByCustomerAndTransactionUuid(Customer customer, UUID transactionUuid) {
        return transactionRepository.findByCustomerUuidAndTransactionUuid(customer.getCustomerUuid(), transactionUuid)
                .orElseThrow(() -> new TransactionNotFoundException(String.format("Transaction not found for customer uuid: %s, transaction uuid: %s", customer.getCustomerUuid(), transactionUuid)));
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid: %s", userUuid)));
    }

    private Transaction findTransactionByTransactionUuid(UUID transactionUuid) {
        return transactionRepository.findByTransactionUuid(transactionUuid)
                .orElseThrow(() -> new TransactionNotFoundException(String.format("Transaction not found for transaction uuid: %s", transactionUuid)));
    }


}
