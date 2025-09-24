package com.kijinkai.domain.mail.service;




import com.kijinkai.domain.mail.EmailRepository;
import com.kijinkai.domain.mail.EmailVerification;
import com.kijinkai.domain.mail.exception.InvalidVerificationCodeException;
import com.kijinkai.util.EmailRandomCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Transactional
@Service
@AllArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final EmailRandomCode emailRandomCode;
    private final EmailRepository emailRepository;


    public void sendEmail(String toEmail, String title, String content) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject(title);
        helper.setText(content, true);
        helper.setReplyTo("imgforestmail@gmail.com");
        try{
            emailSender.send(message);
        } catch (MailException e){
            log.error("Failed to send email to {}", toEmail, e);
            throw new IllegalStateException("Unable to send email", e);
        }
    }

    public SimpleMailMessage createFrom(String toEmail, String title, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);
        return message;
    }


     public void sendVerificationCode(String email) {

        emailRepository.deleteByEmail(email);

        String code = emailRandomCode.generateVerificationCode();

        // 이메일 내용 구성
        String title = "KIJINKAI 이메일 인증번호";
        String content = "<html>"
                + "<body>"
                + "<h1>이메일 인증번호</h1>"
                + "<p>아래 인증번호를 입력하여 이메일 인증을 완료해주세요.</p>"
                + "<h3>" + code + "</h3>"
                + "<p>인증번호는 5분간 유효합니다.</p>"
                + "</body>"
                + "</html>";

        // 데이터베이스에 인증 정보 저장
        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .verificationCode(code)
                .expiresAt(LocalDateTime.now().plusMinutes(10)) // 10분 후 만료 설정
                .build();
        emailRepository.save(verification);

        try {
            sendEmail(email, title, content);
            log.info("인증 이메일 발송 성공: {}", email);
        } catch (MessagingException e) {
            log.error("인증 이메일 전송 실패: {}", email, e);
            emailRepository.deleteByEmail(email);  // 실패된 이메일 삭제
            throw new IllegalStateException("인증 이메일 전송에 실패했습니다.", e);
        }
    }


    @Transactional
    public boolean verifyCode(String email, String code) {
        return emailRepository.findValidVerification(email, code, LocalDateTime.now())
                .map(verification -> {
                    // 사용된 코드 표시하여 재사용 방지
                    verification.markAsUsed();
                    emailRepository.save(verification);
                    return true;
                })
                .orElseThrow(() -> new InvalidVerificationCodeException("유효하지 않은 인증코드입니다"));
    }

    @Transactional
    @Scheduled(cron = "0 0 12 * * ?") // 매일 정오(12:00)에 실행
    public void deleteExpiredVerificationCode(){
        emailRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}


