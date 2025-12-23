package service.impl;

import config.JpaUtil;
import dao.VoucherRepository;
import dao.impl.VoucherRepositoryImpl;
import jakarta.persistence.EntityManager;
import model.CartItem;
import model.Voucher;
import service.VoucherService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository = new VoucherRepositoryImpl();

    @Override
    public ApplyResult applyVoucher(String code, List<CartItem> cartItems) {
        if (code == null || code.isBlank()) {
            return new ApplyResult(false, "Vui lòng nhập mã voucher.", BigDecimal.ZERO, total(cartItems), Optional.empty());
        }
        BigDecimal total = total(cartItems);

        EntityManager em = JpaUtil.em();
        try {
            Optional<Voucher> vopt = voucherRepository.findActiveByCode(code.trim(), em);
            if (vopt.isEmpty()) {
                return new ApplyResult(false, "Mã giảm giá không hợp lệ hoặc đã hết hạn.", BigDecimal.ZERO, total, Optional.empty());
            }
            Voucher v = vopt.get();

            if (v.getMin_order_value() != null && total.compareTo(v.getMin_order_value()) < 0) {
                return new ApplyResult(false, "Đơn hàng chưa đủ điều kiện áp dụng mã.", BigDecimal.ZERO, total, Optional.of(v));
            }

            BigDecimal discountAmount;
            if ("Percent".equalsIgnoreCase(v.getDiscount_type())) {
                discountAmount = total.multiply(v.getDiscount_value().divide(BigDecimal.valueOf(100)));
            } else {
                discountAmount = v.getDiscount_value();
            }

            if (v.getMax_discount() != null && discountAmount.compareTo(v.getMax_discount()) > 0) {
                discountAmount = v.getMax_discount();
            }

            BigDecimal newTotal = total.subtract(discountAmount);
            newTotal = newTotal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : newTotal;

            return new ApplyResult(true, formatSuccess(discountAmount, newTotal), discountAmount, newTotal, Optional.of(v));
        } finally {
            em.close();
        }
    }

    private BigDecimal total(List<CartItem> cartItems) {
        if (cartItems == null) return BigDecimal.ZERO;
        return cartItems.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatSuccess(BigDecimal discount, BigDecimal newTotal) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return String.format("Áp dụng mã thành công! Giảm %s, còn %s",
                currencyFormatter.format(discount),
                currencyFormatter.format(newTotal));
    }
}

