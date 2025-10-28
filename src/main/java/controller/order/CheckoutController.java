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

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
      String path = req.getPathInfo();
      if (path != null && !"/".equals(path)) {
          resp.sendError(HttpServletResponse.SC_NOT_FOUND);
          return;
      }

      HttpSession session = req.getSession();
      User currentUser = (User) session.getAttribute("currentUser");

      // 1. Kiểm tra đăng nhập trước khi làm bất cứ điều gì
      if (currentUser == null) {
          session.setAttribute("redirectAfterLogin", req.getContextPath() + "/checkout");
          resp.sendRedirect(req.getContextPath() + "/login");
          return;
      }

      var items = cart(session);
      if (items.isEmpty()) {
          resp.sendRedirect(req.getContextPath() + "/cart/view");
          return;
      }

      // Lấy thông tin từ form
      String fullname = req.getParameter("fullname");
      String phone    = req.getParameter("phone");
      String address  = req.getParameter("address");
      String note     = req.getParameter("note");
      String voucherCode = req.getParameter("voucher");
      String payment  = Optional.ofNullable(req.getParameter("payment")).orElse("COD");

      EntityManager em = JpaUtil.em();
      try {
          // 2. Bắt đầu một giao dịch duy nhất cho toàn bộ quá trình
          em.getTransaction().begin();

          BigDecimal total = items.stream()
                                  .map(CartItem::getLineTotal)
                                  .reduce(BigDecimal.ZERO, BigDecimal::add);

          // Xử lý voucher (nếu có)
          BigDecimal discountAmount = BigDecimal.ZERO;
          if (voucherCode != null && !voucherCode.isBlank()) {
              var vopt = new VoucherRepository(em).findActiveByCode(voucherCode.trim());
              if (vopt.isPresent()) {
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
                      em.merge(v); // Thay đổi sẽ được commit cùng với đơn hàng
                  }
              }
          }
          
          BigDecimal grandTotal = total.subtract(discountAmount);
          grandTotal = grandTotal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : grandTotal;

          // Lấy lại entity User đang được quản lý bởi EntityManager
          User managedUser = em.find(User.class, currentUser.getId());

          // Tạo đơn hàng (bên trong cùng transaction)
          OrderRepository repo = new OrderRepository(em);
          Orders order = repo.createOrder(managedUser, fullname, phone, address, note,
                            grandTotal, payment, "Chưa thanh toán", "Chờ xác nhận", items);

          // 3. Commit giao dịch sau khi mọi thứ thành công
          em.getTransaction().commit();

          // Xóa giỏ hàng và chuyển hướng
          session.removeAttribute("CART");
          resp.sendRedirect(req.getContextPath() + "/checkout/pay?oid=" + order.getOrder_id());

      } catch (Exception e) {
          // 4. Nếu có lỗi, rollback toàn bộ giao dịch
          if (em.getTransaction().isActive()) {
              em.getTransaction().rollback();
          }
          // Có thể ghi log lỗi ở đây và chuyển hướng đến trang lỗi
          e.printStackTrace(); // In lỗi ra console server
          resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi tạo đơn hàng.");
      } finally {
          if (em.isOpen()) {
              em.close();
          }
      }
  }
}
