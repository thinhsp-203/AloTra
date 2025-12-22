package utils;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    // ================= CẤU HÌNH GMAIL ================= //
    // 1. Email của bạn (dùng để gửi đi)
    private static final String FROM_EMAIL = "jknguyen522@gmail.com"; 
    
    // 2. Mật khẩu ứng dụng (APP PASSWORD) - KHÔNG PHẢI MẬT KHẨU ĐĂNG NHẬP
    // Hướng dẫn lấy: Tài khoản Google -> Bảo mật -> Xác minh 2 bước (Bật) -> Mật khẩu ứng dụng -> Tạo mới
    private static final String PASSWORD = "xxx xxx xxx xxx"; 

    public static boolean sendEmail(String toEmail, String subject, String body) {
        try {
            // 1. Cấu hình SMTP Server của Google
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); // SMTP Host
            props.put("mail.smtp.port", "587"); // TLS Port
            props.put("mail.smtp.auth", "true"); // Phải đăng nhập
            props.put("mail.smtp.starttls.enable", "true"); // Kích hoạt bảo mật TLS

            // 2. Tạo Session (Phiên làm việc)
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });

            // 3. Tạo nội dung Email
            MimeMessage msg = new MimeMessage(session);
            
            // Tiêu đề Email (Có hỗ trợ tiếng Việt UTF-8)
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            // Người gửi (Kèm tên hiển thị đẹp)
            try {
				msg.setFrom(new InternetAddress(FROM_EMAIL, "AloTra Support"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            // Người nhận
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

            // Tiêu đề
            msg.setSubject(subject, "UTF-8");

            // Nội dung (Chấp nhận HTML)
            msg.setContent(body, "text/html; charset=UTF-8");

            // 4. Gửi Email
            Transport.send(msg);
            System.out.println("Gửi email thành công đến: " + toEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Hàm main để test thử chạy độc lập xem gửi được không
    public static void main(String[] args) {
        sendEmail("thinhit555@gmail.com", "Test Email AloTra", "<h1>Xin chào!</h1><p>Đây là email test từ hệ thống.</p>");
    }
}
