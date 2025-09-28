//package com.kijinkai.domain.mail;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@Repository
//public interface EmailRepository extends JpaRepository<EmailVerification, Long> {
//
//    // 이메일 주소를 기반으로 인증 정보를 찾는 메서드
//    Optional<EmailVerification> findByEmail(String email);
//
//    // 이메일과 인증 코드를 모두 사용하여 인증 정보를 찾는 메서드
//    Optional<EmailVerification> findByEmailAndVerificationCode(String email, String verificationCode);
//
//    // 만료된 인증 코드를 삭제하는 메서드 (스케줄링용)
//    void deleteByExpiresAtBefore(LocalDateTime now);
//
//    void deleteByEmailAndIsVerifiedFalse(String email);
//
//    @Query("SELECT e FROM EmailVerification e WHERE e.email = :email " +
//            "AND e.verificationCode = :code " +
//            "AND e.expiresAt > :now")
//    Optional<EmailVerification> findValidVerification(
//            @Param("email") String email,
//            @Param("code") String code,
//            @Param("now") LocalDateTime now
//    );
//
//    @Modifying
//    @Transactional
//    @Query("DELETE FROM EmailVerification e WHERE e.email = :email")
//    void deleteByEmail(@Param("email") String email);
//
//}
