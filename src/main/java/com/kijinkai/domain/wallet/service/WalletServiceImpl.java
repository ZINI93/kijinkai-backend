package com.kijinkai.domain.wallet.service;


import com.kijinkai.domain.customer.entity.Customer;
import com.kijinkai.domain.customer.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.repository.CustomerRepository;
import com.kijinkai.domain.user.entity.User;
import com.kijinkai.domain.user.exception.UserNotFoundException;
import com.kijinkai.domain.user.repository.UserRepository;
import com.kijinkai.domain.user.validator.UserValidator;
import com.kijinkai.domain.wallet.dto.WalletFreezeRequest;
import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import com.kijinkai.domain.wallet.entity.WalletStatus;
import com.kijinkai.domain.wallet.exception.InsufficientBalanceException;
import com.kijinkai.domain.wallet.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.fectory.WalletFactory;
import com.kijinkai.domain.wallet.mapper.WalletMapper;
import com.kijinkai.domain.wallet.repository.WalletRepository;
import com.kijinkai.domain.wallet.validator.WalletValidator;
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
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final WalletMapper walletMapper;

    private final WalletFactory walletFactory;

    private final WalletValidator walletValidator;
    private final UserValidator userValidator;

    /**
     * 지갑 생성
     *
     * @param customer
     * @return
     */
    @Override
    public WalletResponseDto createWalletWithValidate(Customer customer) {

        Wallet wallet = walletFactory.createWallet(customer);
        Wallet savedWallet = walletRepository.save(wallet);

        return walletMapper.toResponse(savedWallet);
    }


    /**
     * 입금 프로세스
     *
     * @param customerUuid
     * @param walletUuid
     * @param amount
     * @return
     */
    @Override
    @Transactional
    public WalletResponseDto deposit(UUID customerUuid, UUID walletUuid, BigDecimal amount) {
        log.info("Depositing amount: {} to wallet: {}", amount, walletUuid);

        Wallet wallet = findWalletByCustomerUuidAndWalletUuid(customerUuid, walletUuid);
        walletValidator.requireActiveStatus(wallet);

        int updateRows = walletRepository.increaseBalanceAtomic(wallet.getWalletUuid(), amount);
        if (updateRows == 0) {
            throw new InsufficientBalanceException("지갑 잔액 충전에 실패했습니다.");
        }

        Wallet updateWallet = findWalletByWalletUuid(wallet.getWalletUuid());

        log.info("Successfully deposited {} to wallet: {}. New balance: {}", amount, wallet.getWalletUuid(), updateWallet.getBalance());

        return walletMapper.toResponse(updateWallet);

    }


    /**
     * 출금 프로세스
     *
     * @param customerUuid
     * @param walletUuid
     * @param amount
     * @return
     */
    @Override
    @Transactional
    public WalletResponseDto withdrawal(UUID customerUuid, UUID walletUuid, BigDecimal amount) {
        log.info("Withdrawal amount: {} to wallet: {}", amount, walletUuid);

        Wallet wallet = findWalletByCustomerUuidAndWalletUuid(customerUuid, walletUuid);
        walletValidator.requireActiveStatus(wallet);
        walletValidator.validateMinimumExchangeAmount(wallet);

        int updateRows = walletRepository.decreaseBalanceAtomic(wallet.getWalletUuid(), amount);
        if (updateRows == 0) {
            throw new InsufficientBalanceException("지갑 잔액 출금에 실패했습니다.");
        }

        Wallet updateWallet = findWalletByWalletUuid(wallet.getWalletUuid());
        log.info("Successfully Withdrawal {} to wallet: {}. New balance: {}", amount, wallet.getWalletUuid(), updateWallet.getBalance());

        return walletMapper.toResponse(updateWallet);
    }


//    /**
//     * 출금 가능 여부 검증
//     *
//     * @param walletId
//     * @param amount
//     * @param minBalance
//     * @return
//     */
//    @Transactional
//    public boolean canWithdrawal(String walletUuid, BigDecimal amount, BigDecimal minBalance) {
//        Wallet wallet = findWalletByWalletUuid(UUID.fromString(walletUuid));
//        walletValidator.requireActiveStatus(wallet);
//
//        BigDecimal balanceAfterWithdrawal = wallet.getBalance().subtract(amount);
//        return balanceAfterWithdrawal.compareTo(minBalance) >= 0;
//    }


    /**
     * 고객의 지갑 조회
     *
     * @param userUuid
     * @return
     */
    @Override
    public WalletResponseDto getWalletBalance(UUID userUuid) {

        Customer customer = findCustomerByUserUuid(userUuid);
        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());

        return walletMapper.toResponse(wallet);
    }


    /**
     * 관리자가 유저 월렛 조회
     * @param userUuid
     * @param walletUuid
     * @return
     */
    @Override
    public WalletResponseDto getCustomerWalletBalanceByAdmin(UUID userUuid, String walletUuid) {

        User user = findUserByUserUUid(userUuid);
        userValidator.requireAdminRole(user);

        Wallet wallet = findWalletByWalletUuid(UUID.fromString(walletUuid));

        return walletMapper.toResponse(wallet);
    }

    /**
     * 관리자가 규약 위반한 유저의 지갑 동결
     * @param adminUUid
     * @param walletUuid
     * @param request
     * @return
     */
    @Override
    @Transactional
    public WalletResponseDto freezeWallet(UUID adminUUid, String walletUuid, WalletFreezeRequest request) {
        log.info("Freezing wallet: {} for reason: {}", walletUuid, request);

        User user = findUserByUserUUid(adminUUid);
        userValidator.requireAdminRole(user);

        Wallet wallet = findWalletByWalletUuid(UUID.fromString(walletUuid));
        Wallet freezeWallet = wallet.freeze(request);
        Wallet saevedWallet = walletRepository.save(freezeWallet);

        return walletMapper.toResponse(saevedWallet);
    }


    /**
     * 관리자가 규약 위반한 유저의 지갑 동결 해제
     * 객페 업데이트
     * @param userUuid
     * @param walletUuid
     * @return
     */
    @Transactional
    public WalletResponseDto unFreezeWallet(UUID userUuid, String walletUuid) {
        log.info("UnFreezing wallet: {}", walletUuid);

        User user = findUserByUserUUid(userUuid);
        userValidator.requireAdminRole(user);

        Wallet wallet = findWalletByWalletUuid(UUID.fromString(walletUuid));
        Wallet freezeWallet = wallet.unfreeze();
        Wallet saevedWallet = walletRepository.save(freezeWallet);

        return walletMapper.toResponse(saevedWallet);
    }

    @Override
    public Wallet findByCustomerUuid(UUID customerUuid) {
        Wallet wallet = findWalletByCustomerUuid(customerUuid);

        return wallet;
    }


    //helper method

    private User findUserByUserUUid(UUID userUuid) {
        return userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user uuid: %s", userUuid)));
    }
    private Wallet findWalletByWalletUuid(UUID walletUuid) {
        return walletRepository.findByWalletUuid(walletUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found for wallet uuid: %s", walletUuid)));
    }

    private Wallet findWalletByCustomerUuidAndWalletUuid(UUID customerUuid, UUID walletUuid) {
        return walletRepository.findByCustomerCustomerUuidAndWalletUuid(customerUuid, walletUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("User not found for wallet uuid: %s", walletUuid)));
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerRepository.findByUserUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid: %s", userUuid)));
    }

    private Wallet findWalletByCustomerUuid(UUID cusotmerUuid) {
        return walletRepository.findByCustomerCustomerUuid(cusotmerUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("Wallet not found for customer uuid: %s", cusotmerUuid)));
    }
}


