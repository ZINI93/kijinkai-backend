package com.kijinkai.domain.mail;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String verificationCode;
    private LocalDateTime expiresAt;


    @Builder
    public EmailVerification(String email, String verificationCode, LocalDateTime expiresAt) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiresAt = expiresAt;
    }
}