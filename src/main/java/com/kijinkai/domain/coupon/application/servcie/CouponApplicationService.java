package com.kijinkai.domain.coupon.application.servcie;

import com.kijinkai.domain.coupon.adapter.out.repository.coupon.CouponSearchCondition;
import com.kijinkai.domain.coupon.application.dto.request.CouponCreateRequestDto;
import com.kijinkai.domain.coupon.application.dto.request.CouponIssuanceRequestDto;
import com.kijinkai.domain.coupon.application.dto.request.CouponUpdateRequestDto;
import com.kijinkai.domain.coupon.application.dto.response.CouponResponseDto;
import com.kijinkai.domain.coupon.application.mapper.CouponMapper;
import com.kijinkai.domain.coupon.application.port.in.coupon.CreateCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.coupon.DeleteCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.coupon.GetCouponUseCase;
import com.kijinkai.domain.coupon.application.port.in.coupon.UpdateCouponUseCase;
import com.kijinkai.domain.coupon.application.port.out.CouponPersistencePort;
import com.kijinkai.domain.coupon.application.servcie.issuance.IssuanceTransactionManager;
import com.kijinkai.domain.coupon.application.util.CouponCodeGenerator;
import com.kijinkai.domain.coupon.domain.exception.*;
import com.kijinkai.domain.coupon.domain.factory.CouponFactory;
import com.kijinkai.domain.coupon.domain.modal.Coupon;
import com.kijinkai.domain.coupon.domain.modal.DiscountType;
import com.kijinkai.domain.user.application.port.out.persistence.UserPersistencePort;
import com.kijinkai.domain.user.domain.exception.UserNotFoundException;
import com.kijinkai.domain.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CouponApplicationService implements CreateCouponUseCase, GetCouponUseCase, UpdateCouponUseCase, DeleteCouponUseCase {


    private final CouponPersistencePort couponPersistencePort;
    private final CouponFactory couponFactory;
    private final CouponMapper couponMapper;


    // 외부
    private final RedissonClient redissonClient;
    private final UserPersistencePort userPersistencePort;
    private final IssuanceTransactionManager issuanceTransactionManager;

    // -- 생성 ..


    /**
     * 쿠폰 생성
     *
     * @param userAdminUuid
     * @param requestDto
     * @return
     */
    @Override
    @Transactional
    public CouponResponseDto createCoupon(UUID userAdminUuid, CouponCreateRequestDto requestDto) {

        // 생성하는 관리자 검증 및 조회
        User userAdmin = findUserByUserUuid(userAdminUuid);
        userAdmin.validateAdminRole();

        // 생성시 캠페인 존재여부 검증
        if (requestDto.getCampaignUuid() != null) {
            couponPersistencePort.existsByCampaignUuid(requestDto.getCampaignUuid());
        }

        // 쿠폰번호 생성
        String couponCode = generateUniqueCouponCode();

        // 쿠폰 생성
        Coupon coupon = couponFactory.createCoupon(
                userAdminUuid,
                requestDto,
                couponCode
        );

        // 쿠폰 저장
        Coupon savedCoupon = couponPersistencePort.saveCoupon(coupon);

        return couponMapper.toCreateResponse(savedCoupon);
    }


    // --- 조회.

    // 쿠폰 조회 -> 유저가 아닌사람 들 모든 사람이 볼수 있게 해야함 -> 간단히 couponCode로 조회
    @Override
    public CouponResponseDto getCouponInfo(String couponCode) {

        Coupon coupon = couponPersistencePort.findByCouponCode(couponCode)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을수 없습니다."));

        return couponMapper.toResponse(coupon);
    }

    /**
     * 조건별 조회
     */
    @Override
    public Page<CouponResponseDto> getCoupons(
            UUID campaignUuid, String couponCode, DiscountType type, Integer minTotalQuantity, Integer maxTotalQuantity,
            LocalDate validFrom, LocalDate validUntil, Boolean active, Pageable pageable) {

        CouponSearchCondition condition = CouponSearchCondition.builder()
                .campaignUuid(campaignUuid)
                .couponCode(couponCode)
                .discountType(type)
                .minTotalQuantity(minTotalQuantity)
                .maxTotalQuantity(maxTotalQuantity)
                .validFrom(validFrom)
                .validUntil(validUntil)
                .active(active)
                .build();

        Page<Coupon> coupons = couponPersistencePort.searchCoupon(condition, pageable);

        return coupons.map(couponMapper::toResponse);
    }

    // --- 업데이트.



    /*
    캠페인에 쿠폰 등록
     */
    @Override
    @Transactional
    public void addCampaignUuid(UUID couponUuid, UUID campaignUuid){

        //쿠폰 조회
        Coupon coupon = findCouponByCouponUuid(couponUuid);

        //동륵
        coupon.addCampaignUuid(campaignUuid);

        //저장
        couponPersistencePort.saveCoupon(coupon);

    }


    /**
     * 발급
     *
     * @param userUuid
     * @param couponUuid
     */
    @Override
    @Transactional(readOnly = false)
    public void issueCoupon(UUID userUuid, UUID couponUuid) {

        String lockKey = "LOCK:COUPON" + couponUuid;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 락 흭득 시도 (최대 5초 대기, 락 흭득 후 2초간 유지)
            boolean available = lock.tryLock(5, 2, TimeUnit.SECONDS);

            if (!available) {
                throw new CouponException("현재 요청이 많아 지연되고 있습니다. 잠시 후 다시 시도해주세요.");
            }

            // 비즈니스 로직 수행 및 검증
            issuanceTransactionManager.processIssuance(userUuid, couponUuid);


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CouponException("시스템 오류가 발생했습니다.");
        } finally {
            // 락을 흭득한 경우에만 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // 쿠폰 번호로 발급
    @Override
    @Transactional(readOnly = false)
    public void issueByCouponCode(UUID userUuid, CouponIssuanceRequestDto requestDto) {

        String lockKey = "LOCK:COUPON" + requestDto.getCouponCode();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean available = lock.tryLock(5, 2, TimeUnit.SECONDS);

            if (!available) {
                throw new CouponException("현재 요청이 많아 지연되고 있습니다. 잠시 후 다시 시도해주세요.");
            }

            issuanceTransactionManager.processIssuanceByCouponCode(userUuid, requestDto);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CouponException("시스템 오류가 발생했습니다.");
        }finally {
            // 락을 흭득한 경우에만 해제
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }


    }


    // 비활성화 쿠폰 업데이트

    @Override
    @Transactional
    public CouponResponseDto updateCoupon(UUID userAdminUuid, UUID couponUuid, CouponUpdateRequestDto requestDto) {

        // 관리자 조회 및 검증
        User userAdmin = findUserByUserUuid(userAdminUuid);
        userAdmin.validateAdminRole();

        //쿠폰 조회
        Coupon coupon = findCouponByCouponUuid(couponUuid);

        //업데이트
        coupon.updateCoupon(requestDto);

        //저장
        Coupon savedCoupon = couponPersistencePort.saveCoupon(coupon);

        return couponMapper.toResponse(savedCoupon);

    }


    // 쿠폰 활성화
    @Override
    @Transactional
    public String activeCoupon(UUID userAdminUuid, UUID couponUuid) {

        // 관리자 조회 및 검증
        User userAdmin = findUserByUserUuid(userAdminUuid);
        userAdmin.validateAdminRole();

        //쿠폰 조회
        Coupon coupon = findCouponByCouponUuid(couponUuid);

        //활성화
        coupon.active();

        //저장
        Coupon savedCoupon = couponPersistencePort.saveCoupon(coupon);

        return savedCoupon.getCouponCode();
    }

    // --- 삭제

    @Override
    @Transactional
    public void deleteCoupon(UUID userAdminUuid, UUID couponUuid) {

        // 관리자 조회 및 검증
        User userAdmin = findUserByUserUuid(userAdminUuid);
        userAdmin.validateAdminRole();

        //쿠폰 조회
        Coupon coupon = findCouponByCouponUuid(couponUuid);

        if (coupon.isActive()) {
            throw new CouponValidateException(CouponErrorCode.CANNOT_DELETE_ACTIVE_COUPON);
        }


        //삭제
        couponPersistencePort.deleteCoupon(coupon);
    }


    // helper

    // 쿠폰 중복 생성 방지
    private String generateUniqueCouponCode() {
        String code;
        int retryCount = 0;
        do {
            code = CouponCodeGenerator.generateDefault();
            retryCount++;
            if (retryCount > 5) {
                throw new CouponCodeGenerateException("쿠폰 번호 생성에 실패 했습니다. / 다시 시도해 주세요");
            }
        } while (couponPersistencePort.existsByCouponCode(code));
        return code;
    }

    // 유저 조회
    private User findUserByUserUuid(UUID userAdminUuid) {
        return userPersistencePort.findByUserUuid(userAdminUuid)
                .orElseThrow(() -> new UserNotFoundException("Not found user"));
    }

    // 쿠폰 조회
    private Coupon findCouponByCouponUuid(UUID couponUuid) {
        return couponPersistencePort.findByCouponUuid(couponUuid)
                .orElseThrow(() -> new CouponNotFoundException("쿠폰을 찾을 수 없습니다."));
    }


}
