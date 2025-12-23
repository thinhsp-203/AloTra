package service;

import model.CartItem;
import model.Voucher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VoucherService {
    record ApplyResult(boolean ok, String message, BigDecimal discount, BigDecimal newTotal, Optional<Voucher> voucher) {}

    ApplyResult applyVoucher(String code, List<CartItem> cartItems);
}

