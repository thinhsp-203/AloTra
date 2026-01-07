package stnw.service;

import stnw.model.CartItem;
import stnw.model.Orders;
import stnw.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    record VoucherResult(boolean ok, String message, BigDecimal discount, BigDecimal newTotal) {}

    VoucherResult applyVoucher(String code, List<CartItem> cartItems);

    /**
     * Tạo đơn hàng và trả về Orders đã persist (đã commit).
     */
    Orders placeOrder(User user, List<CartItem> items, String fullname, String phone, String address,
                      String note, String voucherCode, String paymentMethod, String shippingType);

    Optional<Orders> findById(int orderId);
}
