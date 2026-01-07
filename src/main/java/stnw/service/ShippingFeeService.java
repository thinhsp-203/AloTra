package stnw.service;

import java.math.BigDecimal;

/**
 * Service tính phí giao hàng
 * Chỉ có 2 loại: Tiêu chuẩn và Ưu tiên
 */
public interface ShippingFeeService {
    
    /**
     * Tính phí giao hàng theo loại ship
     * @param shippingType Loại ship: "STANDARD" hoặc "PRIORITY"
     * @return Phí giao hàng
     */
    BigDecimal calculateShippingFee(String shippingType);
    
    /**
     * Lấy mức phí ship tiêu chuẩn
     * @return Phí ship tiêu chuẩn
     */
    BigDecimal getStandardShippingFee();
    
    /**
     * Lấy mức phí ship ưu tiên
     * @return Phí ship ưu tiên
     */
    BigDecimal getPriorityShippingFee();
}

