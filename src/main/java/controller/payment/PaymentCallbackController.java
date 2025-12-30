package controller.payment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.LoyaltyService;
import service.OrderService;
import service.impl.LoyaltyServiceImpl;
import service.impl.OrderServiceImpl;
import service.impl.VNPayService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/payment/vnpay-return", "/payment/momo-return", "/payment/callback"})
public class PaymentCallbackController extends HttpServlet {

    private OrderService orderService;
    private LoyaltyService loyaltyService;

    @Override
    public void init() throws ServletException {
        orderService = new OrderServiceImpl();
        loyaltyService = new LoyaltyServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        
        if (uri.endsWith("/vnpay-return")) {
            handleVNPayReturn(req, resp);
        } else {
            resp.sendError(404);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Handle IPN (Instant Payment Notification) from payment gateways
        String uri = req.getRequestURI();
        
        if (uri.endsWith("/callback")) {
            // TODO: handle other gateways IPN if needed
        } else {
            doGet(req, resp);
        }
    }
    
    /**
     * Handle VNPay payment return
     */
    private void handleVNPayReturn(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException, ServletException {
        // Get all parameters
        Map<String, String> params = new HashMap<>();
        req.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                params.put(key, values[0]);
            }
        });
        
        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        
        System.out.println("VNPay Return - TxnRef: " + txnRef + ", ResponseCode: " + responseCode);
        
        // Verify payment signature
        VNPayService vnpayService = new VNPayService();
        boolean isValid = vnpayService.verifyPayment(params);
        
        if (isValid && "00".equals(responseCode)) {
            // Payment successful
            updateOrderStatus(txnRef, "Đã thanh toán", "Đang chuẩn bị", req, resp);
        } else {
            // Payment failed
            String errorMessage = VNPayService.getResponseDescription(responseCode);
            updateOrderStatus(txnRef, "Thất bại", "Đã hủy", req, resp);
            
            req.getSession().setAttribute("checkoutError", 
                "Thanh toán thất bại: " + errorMessage);
            resp.sendRedirect(req.getContextPath() + "/checkout");
        }
    }
    
    
    /**
     * Update order status and redirect user
     */
    private void updateOrderStatus(String txnRef,
                                   String paymentStatus, String orderStatus,
                                   HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        try {
            int orderId = Integer.parseInt(txnRef);
            orderService.markOrderPaid(orderId, paymentStatus, orderStatus);

            req.getSession().removeAttribute("CART");

            if ("Đã thanh toán".equals(paymentStatus)) {
                // Tích điểm cho user sau khi thanh toán thành công
                try {
                    jakarta.persistence.EntityManager em = config.JpaUtil.em();
                    try {
                        model.Orders order = em.find(model.Orders.class, orderId);
                        if (order != null && order.getUser() != null) {
                            int pointsEarned = loyaltyService.earnPointsFromOrder(order.getUser(), order.getTotal_amount(), orderId);
                            if (pointsEarned > 0) {
                                // Refresh user trong session
                                jakarta.servlet.http.HttpSession session = req.getSession();
                                model.User currentUser = (model.User) session.getAttribute("currentUser");
                                if (currentUser != null && currentUser.getId().equals(order.getUser().getId())) {
                                    jakarta.persistence.EntityManager refreshEm = config.JpaUtil.em();
                                    try {
                                        model.User refreshedUser = refreshEm.find(model.User.class, currentUser.getId());
                                        if (refreshedUser != null) {
                                            session.setAttribute("currentUser", refreshedUser);
                                        }
                                    } finally {
                                        refreshEm.close();
                                    }
                                }
                                req.getSession().setAttribute("orderSuccess",
                                    "Thanh toán thành công! Đơn hàng #" + orderId + " đã được xác nhận. Bạn đã nhận được " + pointsEarned + " điểm tích lũy.");
                            } else {
                                req.getSession().setAttribute("orderSuccess",
                                    "Thanh toán thành công! Đơn hàng #" + orderId + " đã được xác nhận.");
                            }
                        } else {
                            req.getSession().setAttribute("orderSuccess",
                                "Thanh toán thành công! Đơn hàng #" + orderId + " đã được xác nhận.");
                        }
                    } finally {
                        em.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    req.getSession().setAttribute("orderSuccess",
                        "Thanh toán thành công! Đơn hàng #" + orderId + " đã được xác nhận.");
                }
            }

            resp.sendRedirect(req.getContextPath() + "/user/orders");
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }
    
    /**
     * Update order status silently (for IPN callback)
     */
}