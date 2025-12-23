package service.impl;

import config.JpaUtil;
import dao.OrderRepository;
import dao.VoucherRepository;
import dao.impl.OrderRepositoryImpl;
import dao.impl.VoucherRepositoryImpl;
import jakarta.persistence.EntityManager;
import model.CartItem;
import model.Orders;
import model.User;
import model.Voucher;
import service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository = new OrderRepositoryImpl();
    private final VoucherRepository voucherRepository = new VoucherRepositoryImpl();

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
                return new VoucherResult(false, "Mã giảm giá không hợp lệ hoặc đã hết hạn.", BigDecimal.ZERO, total);
            }
            Voucher v = vopt.get();
            if (v.getMin_order_value() != null && total.compareTo(v.getMin_order_value()) < 0) {
                return new VoucherResult(false, "Đơn hàng chưa đủ điều kiện áp dụng mã.", BigDecimal.ZERO, total);
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
                             String note, String voucherCode, String paymentMethod) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();

            BigDecimal total = total(items);
            BigDecimal discountAmount = BigDecimal.ZERO;
            if (voucherCode != null && !voucherCode.isBlank()) {
                Optional<Voucher> vopt = voucherRepository.findActiveByCode(voucherCode.trim(), em);
                if (vopt.isEmpty()) {
                    throw new IllegalArgumentException("Mã giảm giá không hợp lệ hoặc đã hết hạn!");
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

            User managedUser = em.find(User.class, user.getId());
            String paymentStatus = "COD".equalsIgnoreCase(paymentMethod) ? "Chưa thanh toán" : "Chờ thanh toán";
            String orderStatus = "Chờ xác nhận";

            Orders order = orderRepository.createOrder(managedUser, fullname, phone, address, note,
                    grandTotal, paymentMethod, paymentStatus, orderStatus, items, em);

            em.getTransaction().commit();
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
    public void markOrderPaid(int orderId, String paymentStatus, String orderStatus) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            Orders order = em.find(Orders.class, orderId);
            if (order != null) {
                order.setPayment_status(paymentStatus);
                order.setOrder_status(orderStatus);
                em.merge(order);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
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

