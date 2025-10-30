package controller.payment;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Orders;
import service.impl.VNPayService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/payment/vnpay-return", "/payment/momo-return", "/payment/callback"})
public class PaymentCallbackController extends HttpServlet {
    
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
            String method = req.getParameter("method");
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
            updateOrderStatus(txnRef, "VNPAY", "Đã thanh toán", "Đang chuẩn bị", req, resp);
        } else {
            // Payment failed
            String errorMessage = VNPayService.getResponseDescription(responseCode);
            updateOrderStatus(txnRef, "VNPAY", "Thất bại", "Đã hủy", req, resp);
            
            req.getSession().setAttribute("checkoutError", 
                "Thanh toán thất bại: " + errorMessage);
            resp.sendRedirect(req.getContextPath() + "/checkout");
        }
    }
    
    
    /**
     * Update order status and redirect user
     */
    private void updateOrderStatus(String txnRef, String paymentMethod, 
                                   String paymentStatus, String orderStatus,
                                   HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        try {
            int orderId = Integer.parseInt(txnRef);
            
            EntityManager em = JpaUtil.em();
            var tx = em.getTransaction();
            
            try {
                tx.begin();
                Orders order = em.find(Orders.class, orderId);
                
                if (order != null) {
                    order.setPayment_status(paymentStatus);
                    order.setOrder_status(orderStatus);
                    order.setUpdatedDate(java.time.LocalDateTime.now());
                    em.merge(order);
                }
                
                tx.commit();
                
                // Clear cart and redirect
                req.getSession().removeAttribute("CART");
                
                if ("Đã thanh toán".equals(paymentStatus)) {
                    req.getSession().setAttribute("orderSuccess", 
                        "Thanh toán thành công! Đơn hàng #" + orderId + " đã được xác nhận.");
                }
                
                resp.sendRedirect(req.getContextPath() + "/user/orders");
                
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
                
                req.getSession().setAttribute("checkoutError", 
                    "Có lỗi khi xử lý thanh toán. Vui lòng liên hệ CSKH với mã đơn: " + orderId);
                resp.sendRedirect(req.getContextPath() + "/user/orders");
            } finally {
                em.close();
            }
            
        } catch (NumberFormatException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/");
        }
    }
    
    /**
     * Update order status silently (for IPN callback)
     */
    private void updateOrderStatusSilent(String txnRef, String paymentMethod,
                                        String paymentStatus, String orderStatus) {
        try {
            int orderId = Integer.parseInt(txnRef);
            
            EntityManager em = JpaUtil.em();
            var tx = em.getTransaction();
            
            try {
                tx.begin();
                Orders order = em.find(Orders.class, orderId);
                
                if (order != null) {
                    order.setPayment_status(paymentStatus);
                    order.setOrder_status(orderStatus);
                    order.setUpdatedDate(java.time.LocalDateTime.now());
                    em.merge(order);
                }
                
                tx.commit();
                System.out.println("Order #" + orderId + " updated via IPN");
                
            } catch (Exception e) {
                if (tx.isActive()) tx.rollback();
                e.printStackTrace();
            } finally {
                em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}