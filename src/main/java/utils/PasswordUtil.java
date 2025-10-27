package utils;

import org.mindrot.jbcrypt.BCrypt;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
  private static final int BCRYPT_ROUNDS = 10;
  private static final SecureRandom RNG = new SecureRandom();

  public static String hash(String plain) {
    return BCrypt.hashpw(plain, BCrypt.gensalt(BCRYPT_ROUNDS));
  }
  public static boolean verify(String plain, String hashed) {
    if (plain == null || hashed == null) return false;
    return BCrypt.checkpw(plain, hashed);
  }

  // For production, consider a more robust token generation mechanism (e.g., JWT)
  public static String newUrlToken() {
    byte[] buf = new byte[32];
    RNG.nextBytes(buf);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
  }
}