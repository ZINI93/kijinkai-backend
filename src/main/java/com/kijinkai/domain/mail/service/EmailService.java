package com.kijinkai.domain.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String username) {
        String subject = "会員登録が完了しました";
        String content = "<h1>会員登録ありがとうござい！、, " + username + "様!</h1>"
                + "こちらのページからログインいただけます。";

        sendEmail(toEmail, subject, content);
    }

    /**
     * 이메일 인증 코드 발송
     */
    public void sendVerificationEmail(String toEmail, String verificationCode) {
        String subject = "【認証コード】会員登録の認証";
        String content = buildVerificationEmailContent(verificationCode);

        sendEmail(toEmail, subject, content);
    }

    /**
     * 이메일 인증 링크 발송 (대안)
     */
    public void sendVerificationLinkEmail(String toEmail, String nickname, String verificationLink) {
        String subject = "【認証必要】会員登録のメール認証";
        String content = buildVerificationLinkEmailContent(nickname, verificationLink);

        sendEmail(toEmail, subject, content);
    }

    public void sendPaymentCompletedEmail(String toEmail, String username, String orderUuid, BigDecimal price, String paymentMethod) {
        String subject = "ZINIショップのお支払い完了しました。";
        String content = "<h1>" + username + "様、お支払い完了しました</h1>"
                + "<p>注文番号: " + orderUuid + "</p>"
                + "<p>注文金額: " + price + "円</p>"
                + "<p>注文方法: " + paymentMethod + "</p>";

        sendEmail(toEmail, subject, content);
    }

    /**
     * 인증 코드 이메일 내용 생성
     */
    private String buildVerificationEmailContent(String verificationCode) {
        return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<h2 style='color: #333;'>メール認証</h2>" +
                "<p>会員登録いただきありがとうございます。</p>" +
                "<p>以下の認証コードを入力して、登録を完了してください：</p>" +
                "<div style='background: #f5f5f5; padding: 20px; text-align: center; margin: 20px 0; border-radius: 5px;'>" +
                "<h1 style='letter-spacing: 8px; color: #007bff; margin: 0;'>" + verificationCode + "</h1>" +
                "</div>" +
                "<p style='color: #666; font-size: 14px;'>※ この認証コードは10分間有効です。</p>" +
                "<p style='color: #666; font-size: 14px;'>※ 心当たりがない場合は、このメールを無視してください。</p>" +
                "</div>";
    }

    /**
     * 인증 링크 이메일 내용 생성
     */
    private String buildVerificationLinkEmailContent(String nickname, String verificationLink) {
        return "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
                "<h2 style='color: #333;'>メール認証</h2>" +
                "<p>" + nickname + "様、会員登録いただきありがとうございます。</p>" +
                "<p>以下のボタンをクリックして、メールアドレスを認証してください：</p>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + verificationLink + "' " +
                "style='background: #007bff; color: white; padding: 12px 30px; " +
                "text-decoration: none; border-radius: 5px; display: inline-block;'>" +
                "メールアドレスを認証する</a>" +
                "</div>" +
                "<p style='color: #666; font-size: 14px;'>または、以下のリンクをコピーしてブラウザに貼り付けてください：</p>" +
                "<p style='color: #007bff; font-size: 12px; word-break: break-all;'>" + verificationLink + "</p>" +
                "<hr style='margin: 30px 0; border: none; border-top: 1px solid #eee;'>" +
                "<p style='color: #666; font-size: 12px;'>※ このリンクは24時間有効です。</p>" +
                "<p style='color: #666; font-size: 12px;'>※ 心当たりがない場合は、このメールを無視してください。</p>" +
                "</div>";
    }

    private void sendEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);  // HTML 적용
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

