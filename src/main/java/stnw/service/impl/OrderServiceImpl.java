package stnw.service.impl;

import stnw.config.JpaUtil;
import stnw.dao.OrderRepository;
import stnw.dao.VoucherRepository;
import stnw.dao.impl.OrderRepositoryImpl;
import stnw.dao.impl.VoucherRepositoryImpl;
import jakarta.persistence.EntityManager;
import stnw.model.CartItem;
import stnw.model.Orders;
import stnw.model.User;
import stnw.model.Voucher;
import stnw.service.NotificationService;
import stnw.service.OrderService;
import stnw.service.ShippingFeeService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository = new OrderRepositoryImpl();
    private final VoucherRepository voucherRepository = new VoucherRepositoryImpl();
    private final NotificationService notificationService = new NotificationServiceImpl();
    private final ShippingFeeService shippingFeeService = new ShippingFeeServiceImpl();

    @Override
    public VoucherResult applyVoucher(String code, List<CartItem> cartItems) {
        if (code == null || code.isBlank()) {
            return new VoucherResult(false, "Vui lòng nhập mã voucher.", BigDecimal.ZERO, total(cartItems));
        }
        BigDecimal total = total(cartItems);

        EntityManager em = JpaUtil.em();
        try {
            Optional<Voucher> vopt = voucherRepository.findActiveByCode(code.trim(), em);
            if (vopt.isEmpty()) {
                return new VoucherResult(false, "Mã giảm giá không hợp l�?hoặc đã hết hạn.", BigDecimal.ZERO, total);
            }
            Voucher v = vopt.get();
            if (v.getMin_order_value() != null && total.compareTo(v.getMin_order_value()) < 0) {
                return new VoucherResult(false, "Đơn hàng chưa đ�?điều kiện áp dụng mã.", BigDecimal.ZERO, total);
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
            return new VoucherResult(true, "Áp dụng mã thành công", discountAmount, newTotal);
        } finally {
            em.close();
        }
    }

    @Override
    public Orders placeOrder(User user, List<CartItem> items, String fullname, String phone, String address,
                             String note, String voucherCode, String paymentMethod, String shippingType) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();

            BigDecimal total = total(items);
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (voucherCode != null && !voucherCode.isBlank()) {
                Optional<Voucher> vopt = voucherRepository.findActiveByCode(voucherCode.trim(), em);
                if (vopt.isEmpty()) {
                    throw new IllegalArgumentException("Mã giảm giá không hợp l�?hoặc đã hết hạn!");
                }
                Voucher v = vopt.get();
                if (v.getMin_order_value() == null || total.compareTo(v.getMin_order_value()) >= 0) {
                    if ("Percent".equalsIgnoreCase(v.getDiscount_type())) {
                        discountAmount = total.multiply(v.getDiscount_value().divide(BigDecimal.valueOf(100)));
                    } else {
                        discountAmount = v.getDiscount_value();
                    }
                    if (v.getMax_discount() != null && discountAmount.compareTo(v.getMax_discount()) > 0) {
                        discountAmount = v.getMax_discount();
                    }
                    v.setUsed_count((v.getUsed_count() == null ? 0 : v.getUsed_count()) + 1);
                    em.merge(v);
                }
            }
            BigDecimal grandTotal = total.subtract(discountAmount);
            if (grandTotal.compareTo(BigDecimal.ZERO) < 0) grandTotal = BigDecimal.ZERO;
            
            // Tính phí giao hàng theo loại ship được chọn
            if (shippingType == null || shippingType.isBlank()) {
                shippingType = "STANDARD"; // Mặc định là ship tiêu chuẩn
            }
            BigDecimal shippingFee = shippingFeeService.calculateShippingFee(shippingType);
            grandTotal = grandTotal.add(shippingFee);

            User managedUser = em.find(User.class, user.getId());
            // Tất c�?đơn hàng mới đều bắt đầu với "Chưa thanh toán"
            // (COD: chưa thanh toán, Online: s�?cập nhật sau khi thanh toán thành công)
            // Nếu phương thức thanh toán không phải COD thì mặc định là "Đã thanh toán"
            // (COD: chưa thanh toán, VNPAY/ATM/MOMO: đã thanh toán)
            String paymentStatus;
            if ("COD".equalsIgnoreCase(paymentMethod)) {
                paymentStatus = stnw.utils.PaymentStatus.CHUA_THANH_TOAN.getDisplayName();
            } else {
                paymentStatus = stnw.utils.PaymentStatus.DA_THANH_TOAN.getDisplayName();
            }
            String orderStatus = stnw.utils.OrderStatus.CHO_XAC_NHAN.getDisplayName();

            Orders order = orderRepository.createOrder(managedUser, fullname, phone, address, note,
                    grandTotal, paymentMethod, paymentStatus, orderStatus, items, em);

            em.getTransaction().commit();
            
            // Tạo notification cho user v�?đơn hàng mới
            try {
                String message = "Đơn hàng #" + order.getOrder_id() + " của bạn đã được đặt thành công!";
                String link = "/user/orders";
                notificationService.createNotification(user.getId(), message, link);
            } catch (Exception e) {
                // Log lỗi nhưng không ảnh hưởng đến đơn hàng
                System.err.println("Lỗi khi tạo notification cho đơn hàng #" + order.getOrder_id() + ": " + e.getMessage());
            }
            
            return order;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Orders> findById(int orderId) {
        EntityManager em = JpaUtil.em();
        try {
            return Optional.ofNullable(em.find(Orders.class, orderId));
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
}

