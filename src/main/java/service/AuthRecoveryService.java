package service;

public interface AuthRecoveryService {
    /**
     * Tạo token reset nếu email hợp lệ và active. Trả về token (nullable) để controller gửi mail/log.
     */
    String createResetTokenIfEligible(String email, String resetBaseUrl);

    /**
     * Kiểm tra token còn hiệu lực.
     */
    boolean isValidToken(String token);

    /**
     * Đặt lại mật khẩu bằng token, trả về true nếu thành công.
     */
    boolean resetPassword(String token, String newPassword);
}

