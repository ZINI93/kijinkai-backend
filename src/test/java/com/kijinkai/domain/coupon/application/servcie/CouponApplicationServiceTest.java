package com.kijinkai.domain.coupon.application.servcie;

import com.kijinkai.domain.coupon.application.dto.request.CouponCreateRequestDto;
import com.kijinkai.domain.coupon.application.dto.response.CouponResponseDto;
import com.kijinkai.domain.coupon.application.mapper.CouponMapper;
import com.kijinkai.domain.coupon.application.port.out.CouponPersistencePort;
import com.kijinkai.domain.coupon.application.servcie.issuance.IssuanceTransactionManager;
import com.kijinkai.domain.coupon.domain.exception.CouponException;
import com.kijinkai.domain.coupon.domain.exception.CouponNotFoundException;
import com.kijinkai.domain.coupon.domain.exception.CouponValidateException;
import com.kijinkai.domain.coupon.domain.factory.CouponFactory;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponApplicationServiceTest {

    @InjectMocks
    private CouponApplicationService couponApplicationService;

    @Mock private CouponPersistencePort couponPersistencePort;
    @Mock private CouponFactory couponFactory;
    @Mock private CouponMapper couponMapper;
    @Mock private RedissonClient redissonClient;
    @Mock private UserPersistencePort userPersistencePort;
    @Mock private IssuanceTransactionManager issuanceTransactionManager;
    @Mock private RLock rLock;

    private UUID adminUuid;
    private UUID couponUuid;
    private User adminUser;

    @BeforeEach
    void setUp() {
        adminUuid = UUID.randomUUID();
        couponUuid = UUID.randomUUID();
        adminUser = Mockito.mock(User.class); // 내부 메서드 validateAdminRole 호출을 위해 모킹
    }

    // -------------------------------------------------------------------------
    // 1. 쿠폰 생성 테스트 (Create)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("성공: 유효한 관리자가 쿠폰 생성 요청 시 쿠폰이 저장되고 응답을 반환한다.")
    void createCoupon_Success() {
        // given

        String couponCode = "1234-1234-1234";

        CouponCreateRequestDto requestDto = CouponCreateRequestDto.builder()
                .campaignUuid(UUID.randomUUID())
                .discountValue(new BigDecimal("1000"))
                // ... 필요한 필드만 명시적으로 세팅
                .build();

        Coupon mockCoupon = Mockito.mock(Coupon.class);

        CouponResponseDto expectedResponse = CouponResponseDto.builder()
                .couponUuid(UUID.randomUUID())
                .couponCode(couponCode)
                .isActive(true)
                .build();
        when(userPersistencePort.findByUserUuid(adminUuid)).thenReturn(Optional.of(adminUser));
        when(couponPersistencePort.existsByCouponCode(anyString())).thenReturn(false);
        when(couponFactory.createCoupon(any(), any(), any())).thenReturn(mockCoupon);
        when(couponPersistencePort.saveCoupon(any())).thenReturn(mockCoupon);
        when(couponMapper.toCreateResponse(any())).thenReturn(expectedResponse);

        // when
        CouponResponseDto result = couponApplicationService.createCoupon(adminUuid, requestDto);

        // then
        assertThat(result).isNotNull();
        verify(adminUser).validateAdminRole(); // 권한 검증 호출 확인
        verify(couponPersistencePort).saveCoupon(any());
    }

    @Test
    @DisplayName("예외: 관리자가 아닌 유저가 쿠폰 생성 시 예외가 발생한다.")
    void createCoupon_Fail_NotAdmin() {
        // given

        CouponCreateRequestDto requestDto = CouponCreateRequestDto.builder()
                .campaignUuid(UUID.randomUUID())
                .discountValue(new BigDecimal("1000"))
                // ... 필요한 필드만 명시적으로 세팅
                .build();

        when(userPersistencePort.findByUserUuid(adminUuid)).thenReturn(Optional.of(adminUser));
        doThrow(new AccessDeniedException("권한이 없습니다.")).when(adminUser).validateAdminRole();

        // when & then
        assertThatThrownBy(() -> couponApplicationService.createCoupon(adminUuid, requestDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    // -------------------------------------------------------------------------
    // 2. 쿠폰 조회 테스트 (Read)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("예외: 존재하지 않는 쿠폰 코드로 조회 시 CouponNotFoundException 발생")
    void getCouponInfo_Fail_NotFound() {
        // given
        String invalidCode = "INVALID-CODE";
        when(couponPersistencePort.findByCouponCode(invalidCode)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponApplicationService.getCouponInfo(invalidCode))
                .isInstanceOf(CouponNotFoundException.class)
                .hasMessageContaining("쿠폰을 찾을수 없습니다.");
    }

    // -------------------------------------------------------------------------
    // 3. 쿠폰 발급 테스트 (Distributed Lock)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("성공: 분산 락을 획득하여 쿠폰 발급 프로세스를 실행한다.")
    void issueCoupon_Success() throws InterruptedException {
        // given
        UUID userUuid = UUID.randomUUID();
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        // when
        couponApplicationService.issueCoupon(userUuid, couponUuid);

        // then
        verify(issuanceTransactionManager).processIssuance(userUuid, couponUuid);
        verify(rLock).unlock(); // 락 해제 확인
    }

    @Test
    @DisplayName("예외: 락 획득 실패 시 CouponException 발생")
    void issueCoupon_Fail_LockAcquisition() throws InterruptedException {
        // given
        when(redissonClient.getLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> couponApplicationService.issueCoupon(UUID.randomUUID(), couponUuid))
                .isInstanceOf(CouponException.class)
                .hasMessageContaining("현재 요청이 많아 지연되고 있습니다.");
    }

    // -------------------------------------------------------------------------
    // 4. 쿠폰 삭제 테스트 (Delete & Boundary Analysis)
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("예외: 활성화 상태인 쿠폰은 삭제할 수 없다.")
    void deleteCoupon_Fail_IfActive() {
        // given
        Coupon activeCoupon = Mockito.mock(Coupon.class);
        when(userPersistencePort.findByUserUuid(adminUuid)).thenReturn(Optional.of(adminUser));
        when(couponPersistencePort.findByCouponUuid(couponUuid)).thenReturn(Optional.of(activeCoupon));
        when(activeCoupon.isActive()).thenReturn(true); // 경계값: 활성 상태

        // when & then
        assertThatThrownBy(() -> couponApplicationService.deleteCoupon(adminUuid, couponUuid))
                .isInstanceOf(CouponValidateException.class)
                .hasMessageContaining("활성화 중인 쿠폰은 삭제할수 없습니다.");
    }

    // -------------------------------------------------------------------------
    // 5. 헬퍼 메서드/경계값 테스트
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("예외: 유저 UUID가 null인 경우 UserNotFoundException 발생 (방어적 테스트)")
    void findUser_Fail_NullUuid() {
        // given
        when(userPersistencePort.findByUserUuid(null)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponApplicationService.deleteCoupon(null, couponUuid))
                .isInstanceOf(UserNotFoundException.class);
    }
}