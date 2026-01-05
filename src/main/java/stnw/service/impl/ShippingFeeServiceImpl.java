package stnw.service.impl;

import stnw.service.ShippingFeeService;
import java.math.BigDecimal;

/**
 * Implementation của ShippingFeeService
 * Chỉ có 2 loại ship:
 * - Ship tiêu chuẩn: 15,000 VND
 * - Ship ưu tiên: 30,000 VND
 */
public class ShippingFeeServiceImpl implements ShippingFeeService {
    
    private static final BigDecimal STANDARD_SHIPPING_FEE = new BigDecimal("15000");
    private static final BigDecimal PRIORITY_SHIPPING_FEE = new BigDecimal("30000");
    
    @Override
    public BigDecimal calculateShippingFee(String shippingType) {
        if (shippingType == null || shippingType.isBlank()) {
            return STANDARD_SHIPPING_FEE; // Mặc định là ship tiêu chuẩn
        }
        
        if ("PRIORITY".equalsIgnoreCase(shippingType)) {
            return PRIORITY_SHIPPING_FEE;
        }
        
        // Mặc định là ship tiêu chuẩn
        return STANDARD_SHIPPING_FEE;
    }
    
    @Override
    public BigDecimal getStandardShippingFee() {
        return STANDARD_SHIPPING_FEE;
    }
    
    @Override
    public BigDecimal getPriorityShippingFee() {
        return PRIORITY_SHIPPING_FEE;
    }
}

