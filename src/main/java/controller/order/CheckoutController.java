package controller.order;

import jakarta.servlet.*; 
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import config.JpaUtil;
import jakarta.persistence.EntityManager;
import dao.jpa.OrderRepository; 
import dao.jpa.VoucherRepository;
import model.*;

@WebServlet(urlPatterns = {"/checkout", "/checkout/*"})
public class CheckoutController extends HttpServlet {

  @SuppressWarnings("unchecked")
  private List<CartItem> cart(HttpSession session){
    var list = (List<CartItem>) session.getAttribute("CART");
    return list != null ? list : new ArrayList<>();
  }

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getPathInfo(); // /, /pay, /result
    if (path == null || "/".equals(path)){
      req.getRequestDispatcher("/views/order/checkout.jsp").forward(req, resp);
    } else if ("/pay".equals(path)){
      // Giả lập chuyển hướng sang "ngân hàng" -> quay lại /checkout/result?status=success
      resp.sendRedirect(req.getContextPath() + "/checkout/result?status=success");
    } else if ("/result".equals(path)){
      String status = req.getParameter("status");
      req.setAttribute("payStatus", status);
      req.getRequestDispatcher("/views/order/result.jsp").forward(req, resp);
    } else {
      resp.sendError(404);
    }
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String path = req.getPathInfo();
    if (path == null || "/".equals(path)){
      // Đặt hàng: áp voucher -> lưu Orders/OrderDetail
      String fullname = req.getParameter("fullname");
      String phone    = req.getParameter("phone");
      String address  = req.getParameter("address");
      String note     = req.getParameter("note");
      String voucher  = req.getParameter("voucher");
      String payment  = Optional.ofNullable(req.getParameter("payment")).orElse("COD"); // COD/Bank/MoMo/VNPay (ảo)
      var items = cart(req.getSession());
      if (items.isEmpty()){ resp.sendRedirect(req.getContextPath()+"/cart/view"); return; }

      BigDecimal total = BigDecimal.ZERO;
      for (CartItem ci : items){
        total = total.add(ci.getLineTotal());
      }

      EntityManager em = JpaUtil.em();
      try {
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (voucher != null && !voucher.isBlank()){
          var vopt = new VoucherRepository(em).findActiveByCode(voucher.trim());
          if (vopt.isPresent()){
            Voucher v = vopt.get();
            // Min order check
            if (v.getMin_order_value()==null || total.compareTo(v.getMin_order_value()) >= 0){
              if ("Percent".equalsIgnoreCase(v.getDiscount_type())){
                discountAmount = total.multiply(v.getDiscount_value().divide(BigDecimal.valueOf(100)));
              } else {
                discountAmount = v.getDiscount_value();
              }
              if (v.getMax_discount()!=null && discountAmount.compareTo(v.getMax_discount()) > 0){
                discountAmount = v.getMax_discount();
              }
              // Update used_count (đơn giản)
              em.getTransaction().begin();
              v.setUsed_count( (v.getUsed_count()==null?0:v.getUsed_count()) + 1 );
              em.merge(v);
              em.getTransaction().commit();
            }
          }
        }
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) discountAmount = BigDecimal.ZERO;
        BigDecimal grand = total.subtract(discountAmount);
        if (grand.compareTo(BigDecimal.ZERO) < 0) grand = BigDecimal.ZERO;

        // Giả user đăng nhập (nếu đã có session user -> dùng luôn)
        User current = (User) req.getSession().getAttribute("currentUser");
        if (current == null){
          // fallback: đơn vãng lai -> cần user id; ở đây demo lấy user id=3 "Khách hàng"
          current = em.find(User.class, 3); // KHÁCH HÀNG mẫu trong seed của bạn
        }

        var repo = new OrderRepository(em);
        Orders order = repo.createOrder(current, fullname, phone, address, note,
                          grand, payment, "Chưa thanh toán", "Chờ xác nhận", items);

        // Lưu xong -> clear cart và chuyển sang "ngân hàng ảo"
        req.getSession().removeAttribute("CART");
        resp.sendRedirect(req.getContextPath() + "/checkout/pay?oid=" + order.getOrder_id());
      } finally { em.close(); }

    } else {
      resp.sendError(404);
    }
  }
}
