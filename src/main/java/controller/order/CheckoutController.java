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
import service.impl.VNPayService;
@WebServlet(urlPatterns = {"/checkout", "/checkout/*"})
public class CheckoutController extends HttpServlet {

  @SuppressWarnings("unchecked")
  private List<CartItem> cart(HttpSession session){
    var list = (List<CartItem>) session.getAttribute("CART");
    return list != null ? list : new ArrayList<>();
  }

  @Override 
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
          throws ServletException, IOException {
    HttpSession session = req.getSession();
    User currentUser = (User) session.getAttribute("currentUser");

    if (currentUser == null) {
        session.setAttribute("redirectAfterLogin", req.getContextPath() + "/checkout");
        resp.sendRedirect(req.getContextPath() + "/login");
        return;
    }

    if (cart(session).isEmpty()) {
        resp.sendRedirect(req.getContextPath() + "/products");
        return;
    }

    req.getRequestDispatcher("/views/order/checkout.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
          throws IOException, ServletException {
      HttpSession session = req.getSession();
      User currentUser = (User) session.getAttribute("currentUser");

      if (currentUser == null) {
          session.setAttribute("redirectAfterLogin", req.getContextPath() + "/checkout");
          resp.sendRedirect(req.getContextPath() + "/login");
          return;
      }

      var items = cart(session);
      if (items.isEmpty()) {
          resp.sendRedirect(req.getContextPath() + "/products");
          return;
      }

      String fullname = req.getParameter("fullname");
      String phone    = req.getParameter("phone");
      String address  = req.getParameter("address");
      String note     = req.getParameter("note");
      String voucherCode = req.getParameter("voucher");
      String payment  = Optional.ofNullable(req.getParameter("payment")).orElse("COD");

      EntityManager em = JpaUtil.em();
      try {
          em.getTransaction().begin();

          BigDecimal total = items.stream()
                                  .map(CartItem::getLineTotal)
                                  .reduce(BigDecimal.ZERO, BigDecimal::add);

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
                      em.merge(v);
                  }
              } else {
                  em.getTransaction().rollback();
                  req.getSession().setAttribute("checkoutError", "Mã giảm giá không hợp lệ hoặc đã hết hạn!");
                  resp.sendRedirect(req.getContextPath() + "/checkout");
                  return;
              }
          }
          
          BigDecimal grandTotal = total.subtract(discountAmount);
          grandTotal = grandTotal.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : grandTotal;

          User managedUser = em.find(User.class, currentUser.getId());
          OrderRepository repo = new OrderRepository(em);
          
          // Set payment and order status based on payment method
          String paymentStatus = "COD".equals(payment) ? "Chưa thanh toán" : "Chờ thanh toán";
          String orderStatus = "Chờ xác nhận";
          
          Orders order = repo.createOrder(managedUser, fullname, phone, address, note,
                            grandTotal, payment, paymentStatus, orderStatus, items);

          em.getTransaction().commit();

          // Handle payment method
          if (!"COD".equals(payment)) {
              // Build return URL
              String returnUrl = buildReturnUrl(req);
              String paymentUrl = null;
              
              switch (payment) {
              case "VNPAY":
                  VNPayService vnpayService = new VNPayService();
                  paymentUrl = vnpayService.createPaymentUrl(order, payment, returnUrl);
                  break;
              
              case "MOMO":
                  // TODO: Implement MoMo payment
                  System.err.println("MoMo payment not implemented yet");
                  paymentUrl = null;
                  break;
              
              case "ZALOPAY":
                  // TODO: Implement ZaloPay payment
                  System.err.println("ZaloPay payment not implemented yet");
                  paymentUrl = null;
                  break;
              
              case "SHOPEEPAY":
                  // TODO: Implement ShopeePay payment
                  System.err.println("ShopeePay payment not implemented yet");
                  paymentUrl = null;
                  break;
               
              default:
                  System.err.println("Unsupported payment method: " + payment);
                  paymentUrl = null;
          }
              
              if (paymentUrl != null) {
                  // Clear cart before redirecting to payment gateway
                  session.removeAttribute("CART");
                  resp.sendRedirect(paymentUrl);
                  return;
              } else {
                  // Rollback if payment URL creation failed
                  EntityManager rollbackEm = JpaUtil.em();
                  try {
                      rollbackEm.getTransaction().begin();
                      Orders rollbackOrder = rollbackEm.find(Orders.class, order.getOrder_id());
                      if (rollbackOrder != null) {
                          rollbackOrder.setOrder_status("Đã hủy");
                          rollbackOrder.setPayment_status("Thất bại");
                          rollbackEm.merge(rollbackOrder);
                      }
                      rollbackEm.getTransaction().commit();
                  } catch (Exception e) {
                      if (rollbackEm.getTransaction().isActive()) {
                          rollbackEm.getTransaction().rollback();
                      }
                  } finally {
                      rollbackEm.close();
                  }
                  
                  req.getSession().setAttribute("checkoutError", 
                      "Không thể tạo link thanh toán " + payment + ". Vui lòng thử phương thức khác hoặc liên hệ hỗ trợ.");
                  resp.sendRedirect(req.getContextPath() + "/checkout");
                  return;
              }
          }

          // COD payment - success
          session.removeAttribute("CART");
          session.setAttribute("orderSuccess", 
              "Đơn hàng #" + order.getOrder_id() + " đã được đặt thành công! Bạn sẽ thanh toán khi nhận hàng.");
          resp.sendRedirect(req.getContextPath() + "/user/orders");

      } catch (Exception e) {
          if (em.getTransaction().isActive()) {
              em.getTransaction().rollback();
          }
          e.printStackTrace();
          req.getSession().setAttribute("checkoutError", 
              "Lỗi khi tạo đơn hàng. Vui lòng thử lại hoặc liên hệ hỗ trợ.");
          resp.sendRedirect(req.getContextPath() + "/checkout");
      } finally {
          if (em.isOpen()) {
              em.close();
          }
      }
  }
  
  /**
   * Build return URL for payment gateways
   */
  private String buildReturnUrl(HttpServletRequest req) {
      String scheme = req.getScheme();
      String serverName = req.getServerName();
      int serverPort = req.getServerPort();
      String contextPath = req.getContextPath();
      
      StringBuilder returnUrl = new StringBuilder();
      returnUrl.append(scheme).append("://").append(serverName);
      
      // Add port if not standard
      if ((scheme.equals("http") && serverPort != 80) || 
          (scheme.equals("https") && serverPort != 443)) {
          returnUrl.append(":").append(serverPort);
      }
      
      returnUrl.append(contextPath).append("/payment/vnpay-return");
      
      return returnUrl.toString();
  }
}