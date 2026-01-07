package stnw.utils;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Utility class cho việc gửi email
 * Sử dụng Gmail SMTP server
 */
public class EmailUtils {
    
    // Cấu hình Gmail
    private static final String FROM_EMAIL = "hoang.anhe173@gmail.com";
    private static final String PASSWORD = "yyyt yhku glio vazj"; // App Password
    
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

            // Tạo Session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });

            // Tạo nội dung Email
            MimeMessage msg = new MimeMessage(session);
            
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            try {
                msg.setFrom(new InternetAddress(FROM_EMAIL, "AloTra Support"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
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

