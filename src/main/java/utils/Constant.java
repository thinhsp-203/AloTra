package utils;

import jakarta.servlet.ServletContext;

public class Constant {
    public static final String UPLOAD_DIRECTORY = "uploads";
    
    /**
     * Lấy đường dẫn tuyệt đối của thư mục uploads
     * @param context ServletContext để lấy real path
     * @return Đường dẫn tuyệt đối
     */
    public static String getUploadPath(ServletContext context) {
        return context.getRealPath("/" + UPLOAD_DIRECTORY);
    }

    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_TYPES = {"image/jpeg", "image/png", "image/gif", "image/webp"};
}