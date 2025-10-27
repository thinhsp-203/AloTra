package dao.jpa;
import jakarta.persistence.EntityManager;
import model.*;
import java.math.BigDecimal;
import java.util.List;

public class OrderRepository {
  private final EntityManager em;
  public OrderRepository(EntityManager em){ this.em = em; }

  public Orders createOrder(User user, String fullname, String phone, String address, String note,
                            BigDecimal totalAmount, String paymentMethod, String paymentStatus, String orderStatus,
                            List<CartItem> items){
    var tx = em.getTransaction(); tx.begin();
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
        // linePrice mỗi item (đã gồm size + topping) *KHÔNG nhân quantity ở đây*
        // vì OrderDetail.price là giá 1 đơn vị theo schema bạn đã định
        var unit = (ci.getUnitPrice()==null? BigDecimal.ZERO : ci.getUnitPrice())
                 .add(ci.getSizeAdj()==null? BigDecimal.ZERO : ci.getSizeAdj())
                 .add(ci.getToppingsCost()==null? BigDecimal.ZERO : ci.getToppingsCost());
        d.setPrice(unit);
        d.setToppings(ci.getToppingsCsv());
        em.persist(d);

        // trừ tồn kho đơn giản (nếu cần)
        if (p.getStock()!=null){
          p.setStock(Math.max(0, p.getStock() - ci.getQuantity()));
        }
      }

      tx.commit();
      return o;
    } catch (Exception ex){
      if (tx.isActive()) tx.rollback();
      throw ex;
    }
  }
}
