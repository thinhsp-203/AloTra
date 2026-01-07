package stnw.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class cho việc tạo token ngẫu nhiên
 * Sử dụng cho reset password, verify email, etc.
 */
public class TokenUtils {
    
    private static final SecureRandom RNG = new SecureRandom();
    
    /**
     * Tạo token ngẫu nhiên cho URL (dùng cho reset password, verify email, etc.)
     * @return Token string an toàn
     */
    public static String generateUrlToken() {
        byte[] buf = new byte[32];
        RNG.nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }
    
    /**
     * Tạo token ngẫu nhiên cho URL (alias cho generateUrlToken để tương thích ngược)
     * @deprecated Sử dụng generateUrlToken() thay thế
     */
    @Deprecated
    public static String newUrlToken() {
        return generateUrlToken();
    }
}

