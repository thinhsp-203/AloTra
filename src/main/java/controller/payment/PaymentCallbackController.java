package controller.payment;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderService;
import service.impl.OrderServiceImpl;
import service.impl.VNPayService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/payment/vnpay-return", "/payment/momo-return", "/payment/callback"})
public class PaymentCallbackController extends HttpServlet {

    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        orderService = new OrderServiceImpl();
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
                req.getSession().setAttribute("orderSuccess",
                    "Thanh toán thành công! Đơn hàng #" + orderId + " đã được xác nhận.");
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