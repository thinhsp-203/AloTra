package utils;

/**
 * Enum định nghĩa các loại upload ảnh trong hệ thống
 */
public enum UploadType {
    BANNERS("banners"),
    GIFTS("gifts"),
    PRODUCTS("products"),
    PROMOTIONS("promotions"),
    USERS("users"),
    CATEGORIES("categories"); // Thêm categories cho icon category
    
    private final String folderName;
    
    UploadType(String folderName) {
        this.folderName = folderName;
    }
    
    public String getFolderName() {
        return folderName;
    }
}

