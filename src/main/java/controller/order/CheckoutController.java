package controller.order;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;
import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.*;
import service.OrderService;
import service.VoucherService;
import service.impl.OrderServiceImpl;
import service.impl.VNPayService;
import service.impl.VoucherServiceImpl;

@WebServlet(urlPatterns = {"/checkout", "/checkout/*"})
public class CheckoutController extends HttpServlet {

    // SỬA 1: Khai báo repository ở đây (stateless)
    private OrderService orderService;
    private VoucherService voucherService;

    // SỬA 2: Thêm hàm init()
    @Override
    public void init() throws ServletException {
        orderService = new OrderServiceImpl();
        voucherService = new VoucherServiceImpl();
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

        var cartItems = cart(session);
        if (cartItems.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }

        // Load danh sách voucher khả dụng
        try {
            var availableVouchers = voucherService.getAvailableVouchers(cartItems);
            req.setAttribute("availableVouchers", availableVouchers);
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu lỗi khi load voucher, vẫn tiếp tục với danh sách rỗng
            req.setAttribute("availableVouchers", new ArrayList<>());
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

        Orders order;
        try {
            order = orderService.placeOrder(currentUser, items, fullname, phone, address, note, voucherCode, payment);

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
                            rollbackOrder.setOrder_status("Hủy Đơn");
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

            // COD payment - success (không tích điểm ở đây, sẽ tích khi đơn hàng hoàn thành)
            session.setAttribute("orderSuccess",
                    "Đơn hàng #" + order.getOrder_id() + " đã được đặt thành công! Bạn sẽ thanh toán khi nhận hàng.");
            
            session.removeAttribute("CART");
            resp.sendRedirect(req.getContextPath() + "/user/orders");

        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("checkoutError",
                    "Lỗi khi tạo đơn hàng. Vui lòng thử lại hoặc liên hệ hỗ trợ.");
            resp.sendRedirect(req.getContextPath() + "/checkout");
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