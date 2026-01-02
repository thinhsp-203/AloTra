package dao.impl;

import dao.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.*;

import java.math.BigDecimal;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public Orders createOrder(User user, String fullname, String phone, String address, String note,
                              BigDecimal totalAmount, String paymentMethod, String paymentStatus, String orderStatus,
                              List<CartItem> items, EntityManager em) {
        Orders o = new Orders();
        o.setUser(user);
        o.setFullname(fullname);
        o.setPhone(phone);
        o.setAddress(address);
        o.setNote(note);
        o.setTotal_amount(totalAmount);
        o.setPayment_method(paymentMethod);
        o.setPayment_status(paymentStatus);
        o.setOrder_status(orderStatus);
        o.setCreatedDate(java.time.LocalDateTime.now());
        o.setUpdatedDate(java.time.LocalDateTime.now());

        em.persist(o);
        for (CartItem ci : items) {
            Product p = em.find(Product.class, ci.getProductId());
            OrderDetail d = new OrderDetail();
            d.setOrder(o);
            d.setProduct(p);
            d.setProduct_name(ci.getProductName());
            d.setSize_name(ci.getSizeName());
            d.setQuantity(ci.getQuantity());

            var unit = (ci.getUnitPrice() == null ? BigDecimal.ZERO : ci.getUnitPrice())
                    .add(ci.getSizeAdj() == null ? BigDecimal.ZERO : ci.getSizeAdj())
                    .add(ci.getToppingsCost() == null ? BigDecimal.ZERO : ci.getToppingsCost());
            d.setPrice(unit);
            d.setToppings(ci.getToppingsCsv());
            em.persist(d);
        }
        return o;
    }

    @Override
    public boolean hasUserPurchasedProduct(Integer userId, Integer productId, EntityManager em) {
        String ql = "SELECT COUNT(o.order_id) FROM Orders o " +
                "JOIN o.orderDetails od " +
                "WHERE o.user.id = :userId AND od.product.product_id = :productId " +
                "AND o.order_status = 'Hoàn thành'";
        TypedQuery<Long> query = em.createQuery(ql, Long.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);

        try {
            return query.getSingleResult() > 0;
        } catch (jakarta.persistence.NoResultException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

