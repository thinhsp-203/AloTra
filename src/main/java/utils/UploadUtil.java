package utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Utility class để xử lý upload file ảnh
 * Chuẩn hóa: lưu file với UUID tên, validate extension và size
 */
public class UploadUtil {
    
    private static final long MAX_FILE_SIZE = Constant.MAX_FILE_SIZE; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".webp"};
    
    /**
     * Upload và lưu file ảnh
     * @param part Part từ request (có thể null)
     * @param type Loại upload (BANNERS, PRODUCTS, PROMOTIONS, USERS, CATEGORIES)
     * @param servletContext ServletContext để lấy real path
     * @return Relative path: "uploads/<type>/<filename>" hoặc null nếu không có file
     * @throws IllegalArgumentException nếu file không hợp lệ (extension, size)
     * @throws RuntimeException nếu có lỗi khi lưu file
     */
    public static String save(Part part, UploadType type, ServletContext servletContext) {
        // Nếu part null hoặc size = 0 -> return null
        if (part == null || part.getSize() == 0) {
            return null;
        }
        
        String submittedFileName = part.getSubmittedFileName();
        if (submittedFileName == null || submittedFileName.trim().isEmpty()) {
            return null;
        }
        
        // Validate extension
        String originalFileName = Paths.get(submittedFileName).getFileName().toString();
        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex).toLowerCase();
        }
        
        if (!isValidExtension(extension)) {
            throw new IllegalArgumentException("File không hợp lệ! Chỉ chấp nhận: jpg, jpeg, png, webp");
        }
        
        // Validate size
        if (part.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File quá lớn! Kích thước tối đa: " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB");
        }
        
        try {
            // Lấy đường dẫn upload
            String uploadRoot = servletContext.getRealPath("/uploads/" + type.getFolderName());
            if (uploadRoot == null) {
                throw new RuntimeException("Không thể lấy đường dẫn upload directory");
            }
            
            // Tạo folder nếu chưa có
            File uploadDir = new File(uploadRoot);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Tạo tên file: UUID + extension
            String filename = UUID.randomUUID().toString() + extension;
            
            // Lưu file
            File fileToSave = new File(uploadDir, filename);
            try (InputStream input = part.getInputStream()) {
                Files.copy(input, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Trả về relative path: "uploads/<type>/<filename>"
            return "uploads/" + type.getFolderName() + "/" + filename;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xóa file ảnh cũ
     * @param imagePath Relative path: "uploads/<type>/<filename>" hoặc absolute path
     * @param servletContext ServletContext để lấy real path
     * @return true nếu xóa thành công hoặc file không tồn tại, false nếu có lỗi
     */
    public static boolean deleteOldImage(String imagePath, ServletContext servletContext) {
        if (imagePath == null || imagePath.trim().isEmpty() || imagePath.startsWith("http")) {
            return true; // Không xóa URL external
        }
        
        try {
            File fileToDelete;
            
            // Nếu là relative path (uploads/...)
            if (imagePath.startsWith("uploads/")) {
                String realPath = servletContext.getRealPath("/" + imagePath);
                if (realPath != null) {
                    fileToDelete = new File(realPath);
                } else {
                    return false;
                }
            } else {
                // Nếu là absolute path (tương thích ngược)
                fileToDelete = new File(imagePath);
            }
            
            if (fileToDelete.exists() && fileToDelete.isFile()) {
                return fileToDelete.delete();
            }
            
            return true;
            
        } catch (Exception e) {
            // Log error nhưng không throw (ignore để không ảnh hưởng flow chính)
            System.err.println("Lỗi khi xóa file cũ: " + imagePath + " - " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Kiểm tra extension có hợp lệ không
     */
    private static boolean isValidExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExt)) {
                return true;
            }
        }
        return false;
    }
}

