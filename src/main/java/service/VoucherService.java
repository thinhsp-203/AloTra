package service;

import model.CartItem;
import model.Voucher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VoucherService {
    record ApplyResult(boolean ok, String message, BigDecimal discount, BigDecimal newTotal, Optional<Voucher> voucher) {}
    
    record AvailableVoucherInfo(
        Voucher voucher,
        boolean canUse, // Có thể sử dụng với giá trị giỏ hàng hiện tại không
        int remainingUses, // Số lần còn lại có thể sử dụng
        String discountDisplay // Hiển thị giá trị giảm giá (ví dụ: "10%" hoặc "10.000₫")
    ) {}

    ApplyResult applyVoucher(String code, List<CartItem> cartItems);
    
    /**
     * Lấy danh sách voucher khả dụng với thông tin chi tiết
     * @param cartItems Giỏ hàng để kiểm tra điều kiện min_order_value
     * @return Danh sách voucher với thông tin chi tiết
     */
    List<AvailableVoucherInfo> getAvailableVouchers(List<CartItem> cartItems);
}

