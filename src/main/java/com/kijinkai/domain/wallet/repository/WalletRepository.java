package com.kijinkai.domain.wallet.repository;

import com.kijinkai.domain.wallet.dto.WalletResponseDto;
import com.kijinkai.domain.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByCustomerCustomerId(Long customerId);
}