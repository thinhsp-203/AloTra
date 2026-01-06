package stnw.enums;

/**
 * Enum định nghĩa các trạng thái thanh toán
 * Lưu vào DB dưới dạng String (EnumType.STRING)
 */
public enum PaymentStatus {
    CHUA_THANH_TOAN("Chưa thanh toán"),
    DA_THANH_TOAN("Đã thanh toán"),
    HOAN_TIEN("Đã hoàn tiền"),
    THAT_BAI("Thất bại");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Chuyển đổi từ string cũ sang enum mới (để migration data)
     */
    public static PaymentStatus fromOldString(String oldStatus) {
        if (oldStatus == null) return CHUA_THANH_TOAN;
        
        String normalized = oldStatus.trim();
        switch (normalized) {
            case "Chưa thanh toán":
                return CHUA_THANH_TOAN;
            case "Chờ thanh toán":
                // Map "Chờ thanh toán" cũ sang "Chưa thanh toán"
                return CHUA_THANH_TOAN;
            case "Đã thanh toán":
                return DA_THANH_TOAN;
            case "Đã hoàn tiền":
                return HOAN_TIEN;
            case "Thất bại":
                return THAT_BAI;
            default:
                return CHUA_THANH_TOAN;
        }
    }
}

