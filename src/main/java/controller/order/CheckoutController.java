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
import dao.jpa.VoucherRepository; // SỬA: Thêm import
import model.*;
import service.impl.VNPayService;

@WebServlet(urlPatterns = {"/checkout", "/checkout/*"})
public class CheckoutController extends HttpServlet {

    // SỬA 1: Khai báo repository ở đây (stateless)
    private OrderRepository orderRepo;
    private VoucherRepository voucherRepo;

    // SỬA 2: Thêm hàm init()
    @Override
    public void init() throws ServletException {
        orderRepo = new OrderRepository();
        voucherRepo = new VoucherRepository();
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> cart(HttpSession session) {
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
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String note = req.getParameter("note");
        String voucherCode = req.getParameter("voucher");
        String payment = Optional.ofNullable(req.getParameter("payment")).orElse("COD");

        EntityManager em = JpaUtil.em(); // Bắt đầu quản lý em
        try {
            em.getTransaction().begin();

            BigDecimal total = items.stream()
                    .map(CartItem::getLineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal discountAmount = BigDecimal.ZERO;
            if (voucherCode != null && !voucherCode.isBlank()) {
                
                // SỬA 3: Sử dụng voucherRepo (stateless) và truyền 'em'
                var vopt = voucherRepo.findActiveByCode(voucherCode.trim(), em); 

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
            
            // SỬA 4: Bỏ khởi tạo OrderRepository ở đây
            // OrderRepository repo = new OrderRepository(em); // BỎ DÒNG NÀY

            String paymentStatus = "COD".equals(payment) ? "Chưa thanh toán" : "Chờ thanh toán";
            String orderStatus = "Chờ xác nhận";

            // SỬA 5 (LỖI CHÍNH): Truyền 'em' vào làm tham số cuối cùng
            Orders order = orderRepo.createOrder(managedUser, fullname, phone, address, note,
                    grandTotal, payment, paymentStatus, orderStatus, items, em); // <-- THÊM 'em'

            em.getTransaction().commit();

            // Handle payment method
            if (!"COD".equals(payment)) {
                String returnUrl = buildReturnUrl(req);
                String paymentUrl = null;

                switch (payment) {
                    case "VNPAY":
                        VNPayService vnpayService = new VNPayService();
                        paymentUrl = vnpayService.createPaymentUrl(order, payment, returnUrl);
                        break;
                    // ... (các trường hợp khác)
                    default:
                         System.err.println("Unsupported payment method: " + payment);
                         paymentUrl = null;
                }
                
                if (paymentUrl != null) {
                    session.removeAttribute("CART");
                    resp.sendRedirect(paymentUrl);
                    return;
                } else {
                    // Rollback if payment URL creation failed (Logic này OK)
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

            // COD payment - success (Logic này OK)
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
                em.close(); // Đảm bảo em luôn đóng
            }
        }
    }

    /**
     * Build return URL for payment gateways (Logic này OK)
     */
    private String buildReturnUrl(HttpServletRequest req) {
        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int serverPort = req.getServerPort();
        String contextPath = req.getContextPath();

        StringBuilder returnUrl = new StringBuilder();
        returnUrl.append(scheme).append("://").append(serverName);

        if ((scheme.equals("http") && serverPort != 80) ||
                (scheme.equals("https") && serverPort != 443)) {
            returnUrl.append(":").append(serverPort);
        }

        returnUrl.append(contextPath).append("/payment/vnpay-return"); // Giả sử callback là vnpay-return

        return returnUrl.toString();
    }
}