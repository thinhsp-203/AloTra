package stnw.service.impl;

import stnw.config.JpaUtil;
import stnw.dao.VoucherRepository;
import stnw.dao.impl.VoucherRepositoryImpl;
import jakarta.persistence.EntityManager;
import stnw.model.CartItem;
import stnw.model.Voucher;
import stnw.service.VoucherService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
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
                return new ApplyResult(false, "Mã giảm giá không hợp l�?hoặc đã hết hạn.", BigDecimal.ZERO, total, Optional.empty());
            }
            Voucher v = vopt.get();

            if (v.getMin_order_value() != null && total.compareTo(v.getMin_order_value()) < 0) {
                return new ApplyResult(false, "Đơn hàng chưa đ�?điều kiện áp dụng mã.", BigDecimal.ZERO, total, Optional.of(v));
            }

            BigDecimal discountAmount;
            if ("Percent".equalsIgnoreCase(v.getDiscount_type()) || "PERCENT".equalsIgnoreCase(v.getDiscount_type())) {
                discountAmount = total.multiply(v.getDiscount_value().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
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
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        return String.format("Áp dụng mã thành công! Giảm %s, còn %s",
                currencyFormatter.format(discount),
                currencyFormatter.format(newTotal));
    }
    
    @Override
    public List<AvailableVoucherInfo> getAvailableVouchers(List<CartItem> cartItems) {
        BigDecimal cartTotal = total(cartItems);
        EntityManager em = JpaUtil.em();
        try {
            List<Voucher> vouchers = voucherRepository.findAvailableVouchers(em);
            List<AvailableVoucherInfo> result = new ArrayList<>();
            
            for (Voucher v : vouchers) {
                // Kiểm tra điều kiện min_order_value
                boolean canUse = v.getMin_order_value() == null || 
                                cartTotal.compareTo(v.getMin_order_value()) >= 0;
                
                // Tính s�?lần còn lại
                int remainingUses = v.getUsage_limit() == null 
                    ? Integer.MAX_VALUE 
                    : Math.max(0, v.getUsage_limit() - (v.getUsed_count() == null ? 0 : v.getUsed_count()));
                
                // Format giá tr�?giảm giá
                String discountDisplay = formatDiscountValue(v);
                
                result.add(new AvailableVoucherInfo(v, canUse, remainingUses, discountDisplay));
            }
            
            return result;
        } finally {
            em.close();
        }
    }
    
    private String formatDiscountValue(Voucher v) {
        if ("PERCENT".equalsIgnoreCase(v.getDiscount_type())) {
            return v.getDiscount_value().intValue() + "%";
        } else {
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
            return currencyFormatter.format(v.getDiscount_value());
        }
    }
}

