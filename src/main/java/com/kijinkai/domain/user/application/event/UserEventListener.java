package com.kijinkai.domain.user.application.event;

import com.kijinkai.domain.mail.service.EmailService;
import com.kijinkai.domain.user.domain.event.UserCreatedEvent;
import com.kijinkai.util.EmailRandomCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventListener {


    private final EmailRandomCode emailRandomCode;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 사용자 생성 이벤트 처리 - 이메일 인증 발송
     *
     * 🟢 @Async: 비동기 처리로 메인 스레드 블로킹 방지
     * 🟢 @TransactionalEventListener: 트랜잭션 커밋 후에만 실행
     * 🟢 REQUIRES_NEW: 별도 트랜잭션으로 실행 (Redis 저장용)
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("taskExecutor")  // 비동기 실행
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleUserCreated(UserCreatedEvent event) {
        log.info("Processing email verification for user: {}", event.email());

        try {
            // 6자리 인증 코드 생성
            String verificationCode = emailRandomCode.generateVerificationCode();

            // Redis에 저장 (10분 TTL)
            String key = "email:verify:" + event.userUuid();
            redisTemplate.opsForValue().set(key, verificationCode, 10, TimeUnit.MINUTES);

            // 인증 이메일 발송
            emailService.sendVerificationEmail(event.email(), verificationCode);

            log.info("Successfully sent verification email to: {}", event.email());

        } catch (Exception e) {
            log.error("Failed to send verification email for user: {} - {}",
                    event.email(), e.getMessage(), e);

            // 🟢 이메일 발송 실패해도 사용자 생성은 성공으로 처리
            // 필요시 재시도 로직이나 관리자 알림 추가 가능

            // 예: 실패한 이벤트를 별도 큐에 저장하여 재시도
            // retryQueueService.addFailedEmailEvent(event, e.getMessage());
        }
    }
}

