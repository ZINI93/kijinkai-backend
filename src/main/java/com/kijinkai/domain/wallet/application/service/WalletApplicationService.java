package com.kijinkai.domain.wallet.application.service;


import com.kijinkai.domain.customer.application.port.out.persistence.CustomerPersistencePort;
import com.kijinkai.domain.customer.domain.exception.CustomerNotFoundException;
import com.kijinkai.domain.customer.domain.model.Customer;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import com.kijinkai.domain.wallet.application.dto.WalletBalanceResponseDto;
import com.kijinkai.domain.wallet.application.dto.WalletFreezeRequest;
import com.kijinkai.domain.wallet.application.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.application.mapper.WalletMapper;
import com.kijinkai.domain.wallet.application.port.in.CreateWalletUseCase;
import com.kijinkai.domain.wallet.application.port.in.DeleteWalletUseCase;
import com.kijinkai.domain.wallet.application.port.in.GetWalletUseCase;
import com.kijinkai.domain.wallet.application.port.in.UpdateWalletUseCase;
import com.kijinkai.domain.wallet.application.port.out.WalletPersistencePort;
import com.kijinkai.domain.wallet.domain.exception.InsufficientBalanceException;
import com.kijinkai.domain.wallet.domain.exception.WalletNotFoundException;
import com.kijinkai.domain.wallet.domain.fectory.WalletFactory;
import com.kijinkai.domain.wallet.domain.model.Wallet;
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
public class WalletApplicationService implements CreateWalletUseCase, GetWalletUseCase, UpdateWalletUseCase, DeleteWalletUseCase {

    private final WalletPersistencePort walletPersistencePort;
    private final WalletMapper walletMapper;
    private final WalletFactory walletFactory;

    //outer
    private final UserPersistencePort userPersistencePort;
    private final CustomerPersistencePort customerPersistencePort;


    // 금액차감


    /**
     * 지갑 생성
     *
     * @param customerUuid
     * @return
     */
    @Override
    @Transactional
    public Wallet createWallet(UUID customerUuid) {
        Wallet wallet = walletFactory.createWallet(customerUuid);
        return walletPersistencePort.saveWallet(wallet);
    }

    /**
     * 고객의 지갑 조회
     *
     * @param userUuid
     * @return
     */
    @Override
    public WalletBalanceResponseDto getWalletBalance(UUID userUuid) {
        Customer customer = findCustomerByUserUuid(userUuid);
        Wallet wallet = findWalletByCustomerUuid(customer.getCustomerUuid());

        return walletMapper.balanceMapper(wallet.getBalance());
    }

    /**
     * 관리자가 유저 월렛 조회
     *
     * @param userUuid
     * @param walletUuid
     * @return
     */
    @Override
    public WalletResponseDto getCustomerWalletBalanceByAdmin(UUID userUuid, String walletUuid) {
        User user = findUserByUserUUid(userUuid);
        user.validateAdminRole();

        Wallet wallet = findWalletByWalletUuid(UUID.fromString(walletUuid));

        return walletMapper.toResponse(wallet);
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
        wallet.requireActiveStatus();

        int updateRows = walletPersistencePort.increaseBalanceAtomic(wallet.getWalletUuid(), amount);
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
     * @param amount
     * @return
     */
    @Override
    @Transactional
    public WalletResponseDto withdrawal(UUID customerUuid, BigDecimal amount) {
        log.info("Withdrawal customer: {} to amount: {}", customerUuid, amount);

        // 지갑 조회 및 검증
        Wallet wallet = findWalletByCustomerUuid(customerUuid);
        wallet.requireActiveStatus();  // 활성화 상태 검증
        wallet.validateMinimumExchangeAmount(); // 최소 금액 검증

        // 지갑에서 잔책 차감
        int updateRows = walletPersistencePort.decreaseBalanceAtomic(wallet.getWalletUuid(), amount);

        if (updateRows == 0) {
            throw new InsufficientBalanceException("지갑 잔액 출금에 실패했습니다.");
        }

        // 최산 지갑상태를 다시 조회
        Wallet updatedWallet = findWalletByCustomerUuid(customerUuid);

        log.info("Successfully Withdrawal {} to wallet: {}. New balance: {}", amount, wallet.getWalletUuid(), updatedWallet.getBalance());

        return walletMapper.toResponse(updatedWallet);
    }


    // 관리자
    @Override
    @Transactional
    public Wallet refund(UUID customerUuid, UUID walletUuid, BigDecimal amount) {

        // 지갑 조회 및 검증
        Wallet wallet = findWalletByCustomerUuidAndWalletUuid(customerUuid, walletUuid);
        wallet.requireActiveStatus();

        int updateRows = walletPersistencePort.increaseBalanceAtomic(wallet.getWalletUuid(), amount);

        if (updateRows == 0) {
            throw new InsufficientBalanceException("환불 처리 중 지갑을 찾을 수 없습니다.");
        }

        // 환불금액 증가
        wallet.increaseBalance(amount);

        log.info("Refund successful. Wallet: {}, Amount: {}", walletUuid, amount);

        return wallet;
    }

    /**
     * 관리자가 규약 위반한 유저의 지갑 동결
     *
     * @param adminUuid
     * @param walletUuid
     * @param request
     * @return
     */
    @Override
    @Transactional
    public WalletResponseDto freezeWallet(UUID adminUuid, UUID walletUuid, WalletFreezeRequest request) {
        log.info("Freezing wallet: {} for reason: {}", walletUuid, request);

        User user = findUserByUserUUid(adminUuid);
        user.validateAdminRole();

        Wallet wallet = findWalletByWalletUuid(walletUuid);
        wallet.freeze(request);
        Wallet saevedWallet = walletPersistencePort.saveWallet(wallet);

        return walletMapper.toResponse(saevedWallet);
    }

    /**
     * 관리자가 규약 위반한 유저의 지갑 동결 해제
     * 객페 업데이트
     *
     * @param userUuid
     * @param walletUuid
     * @return
     */
    @Override
    @Transactional
    public WalletResponseDto unFreezeWallet(UUID userUuid, UUID walletUuid) {
        log.info("UnFreezing wallet: {}", walletUuid);

        User user = findUserByUserUUid(userUuid);
        user.validateAdminRole();

        Wallet wallet = findWalletByWalletUuid(walletUuid);
        wallet.unfreeze();
        Wallet saevedWallet = walletPersistencePort.saveWallet(wallet);

        return walletMapper.toResponse(saevedWallet);
    }


    //helper method

    private User findUserByUserUUid(UUID userUuid) {
        return userPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new UserNotFoundException(String.format("User not found for user uuid: %s", userUuid)));
    }

    private Wallet findWalletByWalletUuid(UUID walletUuid) {
        return walletPersistencePort.findByWalletUuid(walletUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("WalletJpaEntity not found for wallet uuid: %s", walletUuid)));
    }

    private Wallet findWalletByCustomerUuidAndWalletUuid(UUID customerUuid, UUID walletUuid) {
        return walletPersistencePort.findByCustomerUuidAndWalletUuid(customerUuid, walletUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("User not found for wallet uuid: %s", walletUuid)));
    }

    private Customer findCustomerByUserUuid(UUID userUuid) {
        return customerPersistencePort.findByUserUuid(userUuid)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer not found for user uuid: %s", userUuid)));
    }

    private Wallet findWalletByCustomerUuid(UUID cusotmerUuid) {
        return walletPersistencePort.findByCustomerUuid(cusotmerUuid)
                .orElseThrow(() -> new WalletNotFoundException(String.format("WalletJpaEntity not found for customer uuid: %s", cusotmerUuid)));
    }
}
