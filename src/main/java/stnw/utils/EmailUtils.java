package stnw.utils;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Utility class cho việc gửi email
 * Sử dụng Gmail SMTP server
 * Jakarta Mail (jakarta.mail.*) - phù hợp Tomcat 10+ / Jakarta EE
 */
public class EmailUtils {

    // Cấu hình Gmail
    private static final String FROM_EMAIL = "hoang.anhe173@gmail.com";
    private static final String PASSWORD = ""; // App Password

    // Bật debug nếu cần xem log SMTP
    private static final boolean SMTP_DEBUG = false;

    /**
     * Gửi email
     * @param toEmail Email người nhận
     * @param subject Tiêu đề email
     * @param body Nội dung email (HTML)
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public static boolean sendEmail(String toEmail, String subject, String body) {
        try {
            // Cấu hình SMTP Server của Google
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            // (Khuyến nghị) ép dùng TLS 1.2/1.3 để tránh lỗi handshake ở một số môi trường
            props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");

            // Timeout để tránh treo
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");

            // Tạo Session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });

            session.setDebug(SMTP_DEBUG);

            // Tạo nội dung Email
            MimeMessage msg = new MimeMessage(session);

            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            try {
                msg.setFrom(new InternetAddress(FROM_EMAIL, "AloTra Support", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // fallback nếu lỗi encoding tên hiển thị
                msg.setFrom(new InternetAddress(FROM_EMAIL));
            }

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(body, "text/html; charset=UTF-8");

            // Gửi Email
            Transport.send(msg);
            System.out.println("Gửi email thành công đến: " + toEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
