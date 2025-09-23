package com.kijinkai.domain.mail;

import com.kijinkai.domain.mail.config.EmailConfig;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<EmailVerification, Long> {

    // 이메일 주소를 기반으로 인증 정보를 찾는 메서드
    Optional<EmailVerification> findByEmail(String email);

    // 이메일과 인증 코드를 모두 사용하여 인증 정보를 찾는 메서드
    Optional<EmailVerification> findByEmailAndVerificationCode(String email, String verificationCode);

    // 만료된 인증 코드를 삭제하는 메서드 (스케줄링용)
    void deleteByExpiresAtBefore(LocalDateTime now);

}
