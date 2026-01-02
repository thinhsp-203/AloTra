
package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.*;
import service.*;
import service.impl.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/orders", "/admin/orders/*"})
public class AdminOrderController extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AdminOrderService orderService;
    
    @Override
    public void init() throws ServletException {
        orderService = new AdminOrderServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String path = req.getPathInfo();
        
        try {
            if (path == null || "/".equals(path)) {
                String keyword = req.getParameter("keyword");
                String status = req.getParameter("status");
                
                List<Orders> orders = orderService.searchOrders(keyword, status);
                
                req.setAttribute("orders", orders);
                req.setAttribute("keyword", keyword);
                req.setAttribute("selectedStatus", status);
                req.getRequestDispatcher("/views/admin/orders.jsp").forward(req, resp);
                
            } else if ("/detail".equals(path)) {
                int orderId = Integer.parseInt(req.getParameter("id"));
                Orders order = orderService.getOrderDetails(orderId);
                
                if (order == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                    return;
                }
                
                req.setAttribute("order", order);
                req.setAttribute("details", order.getOrderDetails());
                req.getRequestDispatcher("/views/admin/order_detail.jsp").forward(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error processing order request: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String action = req.getParameter("action");
        int orderId = Integer.parseInt(req.getParameter("orderId"));
        
        try {
            if ("updateStatus".equals(action)) {
                String newStatus = req.getParameter("status");
                orderService.updateOrderStatus(orderId, newStatus);
                req.getSession().setAttribute("success", "Đã cập nhật trạng thái đơn hàng thành công!");
                
            } else if ("updatePayment".equals(action)) {
                String paymentStatus = req.getParameter("paymentStatus");
                orderService.updatePaymentStatus(orderId, paymentStatus);
                req.getSession().setAttribute("success", "Đã cập nhật trạng thái thanh toán thành công!");
            }
            
            resp.sendRedirect(req.getContextPath() + "/admin/orders/detail?id=" + orderId);
            
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders/detail?id=" + orderId);
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/orders/detail?id=" + orderId);
        }
    }
}