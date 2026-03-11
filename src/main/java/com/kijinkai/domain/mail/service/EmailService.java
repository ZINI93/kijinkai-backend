package com.kijinkai.domain.mail.service;

import com.kijinkai.domain.orderitem.domain.model.OrderItem;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static jodd.io.FileNameUtil.getExtension;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String allowedOrigins;

    public EmailService(JavaMailSender mailSender, @Value("${cors.allowed-origins}") String allowedOrigins) {
        this.mailSender = mailSender;
        this.allowedOrigins = allowedOrigins;
    }

    // 비밀번호 리셋 메일
    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String resetLink = allowedOrigins + "/reset-password?token=" + token;
        helper.setTo(to);
        helper.setSubject("Password Reset Request");
        helper.setText("<h1>Reset Your Password</h1><p>Click the link below to reset your password:</p><a href='" + resetLink + "'>Reset Password</a><p>This link expires in 15 minutes.</p>", true);
        mailSender.send(message);
    }

    public void sendStoragePeriodExceededMail(String to, String orderItemCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        // 제목에 [자동취소]를 넣어 고객이 중요도를 인지하게 함
        helper.setSubject("⚠️ [COWCOW] 장기 보관 미출고 상품 자동 취소 안내 (" + orderItemCode + ")");

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<div style='font-family: Arial, sans-serif; max-width: 600px; border: 1px solid #eee; padding: 20px;'>");

        // 제목 섹션 (경고 느낌의 색상 사용)
        htmlContent.append("  <h2 style='color: #c0392b; border-bottom: 2px solid #c0392b; padding-bottom: 10px;'>장기 보관 상품 자동 취소 공지</h2>");
        htmlContent.append("  <p style='font-size: 16px; line-height: 1.6;'>안녕하세요, <strong>COWCOW</strong>입니다.</p>");

        htmlContent.append("  <p style='font-size: 14px; color: #555; line-height: 1.6;'>");
        htmlContent.append("    고객님의 상품이 현지 창고에 도착한 후 <strong>최대 보관 기간(30일)</strong>이 경과되었습니다.<br>");
        htmlContent.append("    당사 운영 정책에 따라 해당 상품은 <strong>자동 주문 취소</strong> 처리되었음을 안내드립니다.");
        htmlContent.append("  </p>");

        // 핵심 정보 박스
        htmlContent.append("  <div style='background-color: #fcf3f2; padding: 15px; border-radius: 5px; margin: 20px 0; border: 1px solid #f5c6cb;'>");
        htmlContent.append("    <p style='margin: 5px 0;'><strong>상품 코드:</strong> <span style='color: #c0392b;'>" + orderItemCode + "</span></p>");
        htmlContent.append("    <p style='margin: 5px 0;'><strong>처리 상태:</strong> 주문 취소 (보관 기간 초과)</p>");
        htmlContent.append("    <p style='margin: 5px 0;'><strong>취소 사유:</strong> 현지 창고 30일 보관 정책 미준수</p>");
        htmlContent.append("  </div>");

        htmlContent.append("  <p style='font-size: 14px; line-height: 1.6; color: #333;'>");
        htmlContent.append("    장기 보관으로 인해 발생한 취소 상품의 후속 처리(폐기 또는 반송)에 대해서는 <br>");
        htmlContent.append("    아래 고객센터를 통해 문의해 주시기 바랍니다.");
        htmlContent.append("  </p>");

        // 푸터 섹션
        htmlContent.append("  <hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>");
        htmlContent.append("  <p style='font-size: 12px; color: #888;'>궁금하신 점은 카카오톡 채널 <strong>'COWCOW'</strong> 또는 고객센터로 연락주세요.</p>");
        htmlContent.append("  <p style='font-size: 12px; color: #b3b3b3; margin-top: 10px;'>본 메일은 관련 법령 및 이용약관에 근거하여 발송된 자동 통지 메일입니다.</p>");
        htmlContent.append("</div>");

        helper.setText(htmlContent.toString(), true);

        mailSender.send(message);
    }

    public void sendBulkArrivalMail(String to, List<OrderItem> items) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        // 제목에 도착한 상품 개수를 표시하여 주목도를 높임
        helper.setSubject("📬 [COWCOW] 현지 창고 상품 도착 안내 (총 " + items.size() + "건)");

        // 1. 상품 리스트를 HTML 테이블 행(tr)으로 변환
        StringBuilder tableRows = new StringBuilder();
        for (OrderItem item : items) {
            tableRows.append("<tr style='border-bottom: 1px solid #eee;'>")
                    .append("  <td style='padding: 12px; text-align: left; font-size: 14px; color: #333;'>")
                    .append(item.getOrderItemCode()).append("</td>")
                    .append("  <td style='padding: 12px; text-align: center; font-size: 14px; color: #333;'>")
                    .append(item.getQuantity()).append(" 개</td>")
                    .append("  <td style='padding: 12px; text-align: right; font-size: 12px; color: #3498db;'>")
                    .append("<a href='").append(item.getProductLink()).append("' style='text-decoration: none; color: #3498db;'>상품보기</a>")
                    .append("  </td>")
                    .append("</tr>");
        }

        // 2. 전체 메일 템플릿 구성
        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<div style='font-family: Arial, sans-serif; max-width: 600px; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>");
        htmlContent.append("  <h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;'>상품 도착 알림</h2>");
        htmlContent.append("  <p style='font-size: 15px; color: #555; line-height: 1.6;'>안녕하세요, <strong>COWCOW</strong>입니다.</p>");
        htmlContent.append("  <p style='font-size: 14px; color: #555; line-height: 1.6;'>고객님의 소중한 상품들이 현지 물류 창고에 안전하게 도착했습니다.</p>");

        // 상품 리스트 테이블 시작
        htmlContent.append("<table style='width: 100%; border-collapse: collapse; margin: 20px 0;'>");
        htmlContent.append("  <thead>");
        htmlContent.append("    <tr style='background-color: #f8f9fa; border-top: 2px solid #3498db;'>");
        htmlContent.append("      <th style='padding: 12px; text-align: left; font-size: 13px;'>상품코드</th>");
        htmlContent.append("      <th style='padding: 12px; text-align: center; font-size: 13px;'>수량</th>");
        htmlContent.append("      <th style='padding: 12px; text-align: right; font-size: 13px;'>링크</th>");
        htmlContent.append("    </tr>");
        htmlContent.append("  </thead>");
        htmlContent.append("  <tbody>");
        htmlContent.append(tableRows.toString()); // 위에서 만든 행 삽입
        htmlContent.append("  </tbody>");
        htmlContent.append("</table>");

        // 정책 안내 박스 (24시간 자동 전환 강조)
        htmlContent.append("<div style='background-color: #fff9db; border: 1px solid #ffec99; padding: 15px; border-radius: 5px; margin-top: 20px;'>");
        htmlContent.append("  <p style='margin: 0; font-size: 13px; color: #856404; line-height: 1.5;'>");
        htmlContent.append("    <strong>🔔 안내사항</strong><br>");
        htmlContent.append("    창고관리가 어려워상품 도착 후 <strong>30일 이내</strong>에 별도의 요청이 없으실 경우, ");
        htmlContent.append("    자동으로 취소됩니다. 취소될 경우 별도로 문의해주시기 바랍니다.");
        htmlContent.append("  </p>");
        htmlContent.append("</div>");

        // 푸터
        htmlContent.append("  <p style='margin-top: 30px; font-size: 13px; color: #888;'>궁금하신 점은 카카오톡 <strong>'COWCOW'</strong> 채널로 문의주세요.</p>");
        htmlContent.append("  <p style='font-size: 12px; color: #bbb;'>본 메일은 시스템에 의해 자동 발송되었습니다.</p>");
        htmlContent.append("</div>");

        helper.setText(htmlContent.toString(), true); // HTML 설정

        mailSender.send(message);
    }

    /*
    검수 사진 전송 메일
     */
    public void sendInspectionMail(String to, String orderItemCode, List<MultipartFile> photos) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("[][검수완료] " + orderItemCode + " 상품의 검수 사진입니다.");

        String content = "안녕하세요. 요청하신 상품(" + orderItemCode + ")의 검수 사진을 보내드립니다.\n" +
                "첨부파일을 확인해 주세요.";
        helper.setText(content);

        // 사진첨부 로직
        if (photos != null && !photos.isEmpty()) {
            for (int i = 0; i < photos.size(); i++) {
                MultipartFile photo = photos.get(i);
                if (!photo.isEmpty()) {
                    // 첨부파일 이름 설정 (예: orderCode_1.jpg)
                    String fileName = orderItemCode + "_" + (i + 1) + getExtension(photo.getOriginalFilename());
                    helper.addAttachment(fileName, photo);
                }
            }
        }

        mailSender.send(message);
    }
}
