//package com.kijinkai.domain.wallet.service;
//
//import com.kijinkai.domain.customer.adapter.out.persistence.entity.Customer;
//import com.kijinkai.domain.user.domain.model.UserRole;
//import com.kijinkai.domain.user.adapter.out.persistence.repository.UserRepository;
//import com.kijinkai.domain.user.adapter.in.web.validator.UserValidator;
//import com.kijinkai.domain.wallet.dto.WalletFreezeRequest;
//import com.kijinkai.domain.wallet.dto.WalletResponseDto;
//import com.kijinkai.domain.wallet.entity.WalletJpaEntity;
//import com.kijinkai.domain.wallet.entity.WalletStatus;
//import com.kijinkai.domain.wallet.fectory.WalletFactory;
//import com.kijinkai.domain.wallet.mapper.WalletMapper;
//import com.kijinkai.domain.wallet.repository.WalletRepository;
//import com.kijinkai.domain.wallet.validator.WalletValidator;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.lang.reflect.Field;
//import java.math.BigDecimal;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class WalletServiceImplTest {
//
//
//    @Mock
//    WalletRepository walletRepository;
//    @Mock
//    UserRepository userRepository;
//    @Mock
//    WalletMapper walletMapper;
//    @Mock
//    WalletFactory walletFactory;
//    @Mock
//    WalletValidator walletValidator;
//    @Mock
//    UserValidator userValidator;
//
//    @InjectMocks
//    WalletServiceImpl walletService;
//
//    private UUID userUuid;
//    private UUID customerUuid;
//    private UUID walletUuid;
//
//
//    private WalletResponseDto walletResponseDto;
//
//
//    private void setWalletId(WalletJpaEntity wallet, Long id) throws Exception {
//        Field field = wallet.getClass().getDeclaredField("walletId");
//        field.setAccessible(true);
//        field.set(wallet, id);
//    }
//
//    @BeforeEach
//    void setUp() {
//
//        userUuid = UUID.randomUUID();
//        customerUuid = UUID.randomUUID();
//        walletUuid = UUID.randomUUID();
//
//    }
//    // Helper method
//
//    private User createMockUser(UUID uuid, UserRole userRole) {
//        return User.builder().userUuid(uuid).userRole(userRole).build();
//    }
//
//
//    private Customer createMockCustomer(User user) {
//        return Customer.builder().user(user).build();
//    }
//
//    private WalletJpaEntity createMockWallet(UUID walletUuid, Customer customer, BigDecimal balance, WalletStatus status) {
//        return WalletJpaEntity.builder().walletUuid(walletUuid).customer(customer).balance(balance).walletStatus(status).build();
//    }
//
//
//    @Test
//    @DisplayName("유저의 지갑 생성")
//    void createWalletWithValidate() {
//
//        //Given
//        User user = createMockUser(userUuid, UserRole.USER);
//        Customer customer = createMockCustomer(user);
//        WalletJpaEntity wallet = createMockWallet(walletUuid, customer, BigDecimal.ZERO, WalletStatus.ACTIVE);
//
//        when(walletFactory.createWallet(customer)).thenReturn(wallet);
//        when(walletRepository.save(any(WalletJpaEntity.class))).thenReturn(wallet);
//        when(walletMapper.toResponse(wallet)).thenReturn(new WalletResponseDto());
//
//        //When
//        WalletResponseDto result = walletService.createWalletWithValidate(customer);
//
//        //Then
//        assertThat(result).isNotNull();
//        assertThat(walletUuid).isEqualTo(wallet.getWalletUuid());
//
//        verify(walletMapper, times(1)).toResponse(wallet);
//        verify(walletFactory, times(1)).createWallet(customer);
//        verify(walletRepository, times(1)).save(any(WalletJpaEntity.class));
//    }
//
//    @Test
//    @DisplayName("유저의 지갑에 금액 충전")
//    void deposit() {
//
//        //Given
//        User user = createMockUser(userUuid, UserRole.USER);
//        Customer customer = createMockCustomer(user);
//        WalletJpaEntity initialWallet = createMockWallet(walletUuid, customer, BigDecimal.ZERO, WalletStatus.ACTIVE);
//
//        BigDecimal depositAmount = new BigDecimal("50000.00");
//        BigDecimal expectedFinalBalance = BigDecimal.ZERO.add(depositAmount);
//
//        WalletJpaEntity updatedWallet = createMockWallet(walletUuid, customer, expectedFinalBalance, WalletStatus.ACTIVE);
//
//        when(walletRepository.findByCustomerUuidAndWalletUuid(eq(customerUuid), eq(walletUuid))).thenReturn(Optional.of(initialWallet));
//        when(walletRepository.increaseBalanceAtomic(walletUuid, depositAmount)).thenReturn(1);
//        when(walletRepository.findByWalletUuid(eq(walletUuid))).thenReturn(Optional.of(updatedWallet));
//
//        WalletResponseDto response = WalletResponseDto.builder().walletUuid(updatedWallet.getWalletUuid()).balance(updatedWallet.getBalance()).walletStatus(updatedWallet.getWalletStatus()).build();
//        when(walletMapper.toResponse(updatedWallet)).thenReturn(response);
//
//        //when
//        WalletResponseDto result = walletService.deposit(customerUuid, walletUuid, depositAmount);
//
//
//        //then
//        assertThat(result).isNotNull();
//        assertThat(result.getBalance()).isEqualTo(expectedFinalBalance);
//        verify(walletValidator).requireActiveStatus(initialWallet);
//        verify(walletRepository, times(1)).increaseBalanceAtomic(eq(walletUuid), eq(depositAmount));
//        verify(walletRepository, times(1)).findByWalletUuid(eq(walletUuid));
//    }
//
//    @Test
//    @DisplayName("유저의 지갑에서 출금")
//    void withdrawal() {
//        //Given
//        User user = createMockUser(userUuid, UserRole.USER);
//        Customer customer = createMockCustomer(user);
//        WalletJpaEntity initialWallet = createMockWallet(walletUuid, customer, new BigDecimal(1000000.00), WalletStatus.ACTIVE);
//
//        BigDecimal withdrawalAmount = new BigDecimal(100000.00);
//        BigDecimal expectedFinalBalance = initialWallet.getBalance().subtract(withdrawalAmount);
//
//        WalletJpaEntity updateWallet = createMockWallet(walletUuid, customer, expectedFinalBalance, WalletStatus.ACTIVE);
//
//        when(walletRepository.findByCustomerUuidAndWalletUuid(customerUuid, walletUuid)).thenReturn(Optional.ofNullable(initialWallet));
//        when(walletRepository.decreaseBalanceAtomic(walletUuid, withdrawalAmount)).thenReturn(1);
//        when(walletRepository.findByWalletUuid(walletUuid)).thenReturn(Optional.of(updateWallet));
//
//        WalletResponseDto response = WalletResponseDto.builder().walletUuid(updateWallet.getWalletUuid()).balance(updateWallet.getBalance()).walletStatus(updateWallet.getWalletStatus()).build();
//        when(walletMapper.toResponse(updateWallet)).thenReturn(response);
//
//        //When
//        WalletResponseDto result = walletService.withdrawal(customerUuid, walletUuid, withdrawalAmount);
//
//        assertThat(result).isNotNull();
//        assertThat(result.getBalance()).isEqualTo(expectedFinalBalance);
//
//        verify(walletValidator).requireActiveStatus(initialWallet);
//        verify(walletRepository, times(1)).decreaseBalanceAtomic(eq(walletUuid), eq(withdrawalAmount));
//        verify(walletRepository, times(1)).findByWalletUuid(eq(walletUuid));
//    }
//
////    @Test
////    @DisplayName("출금 가능 여부 검증 - 수정필요")
////    void canWithdrawal() {
////        //Given
////        String walletUuid = UUID.randomUUID().toString();
////
////        User user = createMockUser(userUuid, UserRole.USER);
////        Customer customer = createMockCustomer(user);
////        WalletJpaEntity wallet = createMockWallet(UUID.fromString(walletUuid), customer, new BigDecimal(1000000.00), WalletStatus.ACTIVE);
////        BigDecimal amount = new BigDecimal(10000.00);
////        WalletResponseDto response = WalletResponseDto.builder().walletUuid(wallet.getWalletUuid()).balance(wallet.getBalance()).build();
////
////
////        when(walletRepository.findByWalletUuid(UUID.fromString(walletUuid))).thenReturn(Optional.ofNullable(wallet));
////        when(walletMapper.toResponse(wallet)).thenReturn(response);
////
////        //When
////        boolean result = walletService.canWithdrawal(walletUuid, amount, wallet.getBalance());
////
////        //Then
////        assertThat(result).isEqualTo(false);
////
////    }
//
//    @Test
//    @DisplayName("월렛 조회")
//    void getWalletBalance() {
//        //Given
//        String walletUuid = UUID.randomUUID().toString();
//
//        User user = createMockUser(userUuid, UserRole.USER);
//        Customer customer = createMockCustomer(user);
//        WalletJpaEntity wallet = createMockWallet(UUID.fromString(walletUuid), customer, new BigDecimal(1000000.00), WalletStatus.ACTIVE);
//        WalletResponseDto response = WalletResponseDto.builder().walletUuid(wallet.getWalletUuid()).balance(wallet.getBalance()).build();
//
//        when(walletRepository.findByCustomerUuidAndWalletUuid(customerUuid, UUID.fromString(walletUuid))).thenReturn(Optional.ofNullable(wallet));
//        when(walletMapper.toResponse(wallet)).thenReturn(response);
//        //When
//
//        WalletResponseDto result = walletService.getWalletBalance(customerUuid);
//
//        //Then
//        assertThat(result).isNotNull();
//        assertThat(result.getBalance()).isEqualTo(new BigDecimal(1000000.00));
//
//        verify(walletRepository, times(1)).findByCustomerUuidAndWalletUuid(customerUuid, UUID.fromString(walletUuid));
//    }
//
//    @Test
//    void geCustomerWalletBalanceByAdmin() {
//        //Given
//        UUID adminUuid = UUID.randomUUID();
//        String walletUuid = UUID.randomUUID().toString();
//
//        User user = createMockUser(adminUuid, UserRole.ADMIN);
//        Customer customer = createMockCustomer(user);
//        WalletJpaEntity wallet = createMockWallet(UUID.fromString(walletUuid), customer, new BigDecimal(1000000.00), WalletStatus.ACTIVE);
//        WalletResponseDto response = WalletResponseDto.builder().walletUuid(wallet.getWalletUuid()).balance(wallet.getBalance()).build();
//
//        when(userRepository.findByUserUuid(adminUuid)).thenReturn(Optional.ofNullable(user));
//        when(walletRepository.findByWalletUuid(UUID.fromString(walletUuid))).thenReturn(Optional.ofNullable(wallet));
//        when(walletMapper.toResponse(wallet)).thenReturn(response);
//
//        //When
//        WalletResponseDto result = walletService.getCustomerWalletBalanceByAdmin(adminUuid, walletUuid);
//
//        //Then
//        assertThat(result).isNotNull();
//        assertThat(result.getBalance()).isEqualTo(new BigDecimal(1000000.00));
//
//        verify(userValidator).requireAdminRole(user);
//    }
//
//    @Test
//    @DisplayName("유저 지갑 비활성화")
//    void freezeWallet() {
//        //Given
//        UUID adminUuid = UUID.randomUUID();
//        String walletUuid = UUID.randomUUID().toString();
//        String reason = "규약을 위반했지요....";
//
//        User admin = createMockUser(adminUuid, UserRole.ADMIN);
//        Customer customer = createMockCustomer(admin);
//        WalletJpaEntity wallet = createMockWallet(UUID.fromString(walletUuid), customer, new BigDecimal(1000000.00), WalletStatus.CLOSED);
//        WalletResponseDto response = WalletResponseDto.builder().walletUuid(wallet.getWalletUuid()).balance(wallet.getBalance()).walletStatus(wallet.getWalletStatus()).build();
//        WalletFreezeRequest FreezeRequest = new WalletFreezeRequest("규약위반");
//
//        when(userRepository.findByUserUuid(adminUuid)).thenReturn(Optional.ofNullable(admin));
//        when(walletRepository.findByWalletUuid(UUID.fromString(walletUuid))).thenReturn(Optional.ofNullable(wallet));
//        when(walletRepository.save(any(WalletJpaEntity.class))).thenReturn(wallet);
//        when(walletMapper.toResponse(wallet)).thenReturn(response);
//
//        //When
//        WalletResponseDto result = walletService.freezeWallet(adminUuid, walletUuid, FreezeRequest);
//
//        //Then
//        assertThat(result).isNotNull();
//        assertThat(result.getWalletStatus()).isEqualTo(WalletStatus.CLOSED);
//        assertThat(admin).isNotEqualTo(admin);
//
//        verify(walletRepository, times(1)).save(any(WalletJpaEntity.class));
//        verify(userRepository, times(1)).findByUserUuid(adminUuid);
//        verify(walletRepository, times(1)).findByWalletUuid(UUID.fromString(walletUuid));
//        verify(userValidator).requireAdminRole(admin);
//    }
//
//    @Test
//    @DisplayName("유저 지갑 비활성화에서 활성화로 변경")
//    void unFreezeWallet() {
//        //Given
//        UUID adminUuid = UUID.randomUUID();
//        String walletUuid = UUID.randomUUID().toString();
//
//        User admin = createMockUser(adminUuid, UserRole.ADMIN);
//        Customer customer = createMockCustomer(admin);
//        WalletJpaEntity wallet = createMockWallet(UUID.fromString(walletUuid), customer, new BigDecimal(1000000.00), WalletStatus.ACTIVE);
//        WalletResponseDto response = WalletResponseDto.builder().walletUuid(wallet.getWalletUuid()).balance(wallet.getBalance()).walletStatus(wallet.getWalletStatus()).build();
//
//        when(userRepository.findByUserUuid(adminUuid)).thenReturn(Optional.ofNullable(admin));
//        when(walletRepository.findByWalletUuid(UUID.fromString(walletUuid))).thenReturn(Optional.ofNullable(wallet));
//        when(walletRepository.save(any(WalletJpaEntity.class))).thenReturn(wallet);
//        when(walletMapper.toResponse(wallet)).thenReturn(response);
//
//        //When
//        WalletResponseDto result = walletService.unFreezeWallet(adminUuid, walletUuid);
//
//        //Then
//        assertThat(result).isNotNull();
//        assertThat(result.getWalletStatus()).isEqualTo(WalletStatus.ACTIVE);
//        assertThat(admin).isNotEqualTo(admin);
//
//        verify(walletRepository, times(1)).save(any(WalletJpaEntity.class));
//        verify(userRepository, times(1)).findByUserUuid(adminUuid);
//        verify(walletRepository, times(1)).findByWalletUuid(UUID.fromString(walletUuid));
//        verify(userValidator).requireAdminRole(admin);
//    }
//}