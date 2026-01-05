package stnw.utils;

/**
 * Enum định nghĩa các trạng thái đơn hàng
 * Lưu vào DB dưới dạng String (EnumType.STRING)
 */
public enum OrderStatus {
    CHO_XAC_NHAN("Chờ xác nhận"),
    DANG_CHUAN_BI("Đang chuẩn bị"),
    DANG_GIAO("Đang giao"),
    HOAN_THANH("Hoàn thành"),
    HUY_BOI_KHACH("Hủy bởi khách"),
    HUY_BOI_SHOP("Hủy bởi shop"),
    TU_CHOI("Từ chối");
    
    private final String displayName;
    
    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Chuyển đổi từ string cũ sang enum mới (để migration data)
     */
    public static OrderStatus fromOldString(String oldStatus) {
        if (oldStatus == null) return null;
        
        String normalized = oldStatus.trim();
        switch (normalized) {
            case "Chờ xác nhận":
                return CHO_XAC_NHAN;
            case "Đang chuẩn bị":
            case "Đang xử lý":
                return DANG_CHUAN_BI;
            case "Đang giao":
                return DANG_GIAO;
            case "Hoàn thành":
                return HOAN_THANH;
            case "Hủy bởi shop":
                return HUY_BOI_SHOP;
            case "Hủy bởi khách":
                return HUY_BOI_KHACH;
            case "Hủy Đơn":
                // Phân biệt dựa trên logic nghiệp vụ:
                // Nếu đơn đã được xác nhận trước đó thì là HUY_BOI_SHOP
                // Nếu chưa được xác nhận thì là HUY_BOI_KHACH
                // Mặc định: HUY_BOI_KHACH (vì thường user hủy trước khi admin xác nhận)
                return HUY_BOI_KHACH;
            case "Từ chối":
                return TU_CHOI;
            default:
                // Fallback: nếu không khớp, mặc định là CHO_XAC_NHAN
                return CHO_XAC_NHAN;
        }
    }
    
    /**
     * Kiểm tra xem trạng thái có phải là final state không (không thể chuyển đổi nữa)
     */
    public boolean isFinalState() {
        return this == HOAN_THANH || 
               this == HUY_BOI_KHACH || 
               this == HUY_BOI_SHOP || 
               this == TU_CHOI;
    }
    
    /**
     * Kiểm tra xem khách hàng có thể hủy đơn ở trạng thái này không
     */
    public boolean canCustomerCancel() {
        return this == CHO_XAC_NHAN;
    }
    
    /**
     * Kiểm tra xem admin có thể xác nhận/từ chối đơn ở trạng thái này không
     */
    public boolean canAdminConfirmOrReject() {
        return this == CHO_XAC_NHAN;
    }
    
    /**
     * Kiểm tra xem admin có thể cập nhật trạng thái từ trạng thái này không
     */
    public boolean canAdminUpdateStatus() {
        return this == DANG_CHUAN_BI && !isFinalState();
    }
}

