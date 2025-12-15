package dao;
import jakarta.persistence.EntityManager;
import model.*;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository {
    Orders createOrder(User user, String fullname, String phone, String address, String note,
                       BigDecimal totalAmount, String paymentMethod, String paymentStatus, String orderStatus,
                       List<CartItem> items, EntityManager em);

    boolean hasUserPurchasedProduct(Integer userId, Integer productId, EntityManager em);
}