package dao.jpa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.*;
import java.math.BigDecimal;
import java.util.List;

public class OrderRepository {
  private final EntityManager em;
  public OrderRepository(EntityManager em){ this.em = em; }

  public Orders createOrder(User user, String fullname, String phone, String address, String note,
                            BigDecimal totalAmount, String paymentMethod, String paymentStatus, String orderStatus,
                            List<CartItem> items){
    // BỎ DÒNG: var tx = em.getTransaction(); tx.begin();
    try {
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

      for (CartItem ci : items){
        Product p = em.find(Product.class, ci.getProductId());
        OrderDetail d = new OrderDetail();
        d.setOrder(o);
        d.setProduct(p);
        d.setProduct_name(ci.getProductName());
        d.setSize_name(ci.getSizeName());
        d.setQuantity(ci.getQuantity());
        
        var unit = (ci.getUnitPrice()==null? BigDecimal.ZERO : ci.getUnitPrice())
                 .add(ci.getSizeAdj()==null? BigDecimal.ZERO : ci.getSizeAdj())
                 .add(ci.getToppingsCost()==null? BigDecimal.ZERO : ci.getToppingsCost());
        d.setPrice(unit);
        d.setToppings(ci.getToppingsCsv());
        em.persist(d);

        // Cập nhật tồn kho
        if (p != null && p.getStock() != null){
          p.setStock(Math.max(0, p.getStock() - ci.getQuantity()));
          em.merge(p); // Thêm merge để đảm bảo thay đổi được lưu
        }
      }

      // BỎ DÒNG: tx.commit();
      return o;
    } catch (Exception ex){
      // BỎ KHỐI: if (tx.isActive()) tx.rollback();
      throw ex; // Ném exception để Controller xử lý rollback
    }
  }
/**
 * Kiểm tra xem một user đã mua một sản phẩm cụ thể chưa VÀ đơn hàng đã hoàn thành.
 * @param userId ID của User
 * @param productId ID của Product
 * @return true nếu đã mua và hoàn thành, false ngược lại.
 */
public boolean hasUserPurchasedProduct(Integer userId, Integer productId) {
    String ql = "SELECT COUNT(o.order_id) FROM Orders o " +
                "JOIN o.orderDetails od " +
                "WHERE o.user.id = :userId AND od.product.product_id = :productId " +
                "AND o.order_status = 'Hoàn thành'";
    
    TypedQuery<Long> query = this.em.createQuery(ql, Long.class);
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