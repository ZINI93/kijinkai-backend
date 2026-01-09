package com.kijinkai.domain.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String allowedOrigins;

    public EmailService(JavaMailSender mailSender, @Value("${cors.allowed-origins}")String allowedOrigins) {
        this.mailSender = mailSender;
        this.allowedOrigins = allowedOrigins;
    }

    // 비밀번호 리셋 메일
    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String resetLink = allowedOrigins+ "/reset-password?token=" + token;
        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText("<h1>Reset Your Password</h1><p>Click the link below to reset your password:</p><a href='" + resetLink + "'>Reset Password</a><p>This link expires in 15 minutes.</p>", true);
        mailSender.send(message);
    }
}
