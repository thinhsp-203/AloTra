package stnw.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class cho các hàm xử lý mật khẩu
 * Sử dụng BCrypt để hash và verify mật khẩu
 */
public class PasswordUtils {
    
    private static final int BCRYPT_ROUNDS = 10;
    
    /**
     * Hash mật khẩu bằng BCrypt
     * @param plain Mật khẩu plain text
     * @return Mật khẩu đã hash
     */
    public static String hashPassword(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    /**
     * Verify mật khẩu
     * @param plain Mật khẩu plain text
     * @param hashed Mật khẩu đã hash
     * @return true nếu khớp, false nếu không khớp
     */
    public static boolean verifyPassword(String plain, String hashed) {
        if (plain == null || hashed == null) return false;
        return BCrypt.checkpw(plain, hashed);
    }
}

