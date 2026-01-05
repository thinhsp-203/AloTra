package stnw.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.PaymentConfig;
import stnw.service.PaymentConfigService;
import stnw.service.impl.PaymentConfigServiceImpl;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/payment-config", "/admin/payment-config/*"})
public class AdminPaymentConfigController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
	private PaymentConfigService paymentConfigService;
	
	@Override
    public void init() throws ServletException {
        paymentConfigService = new PaymentConfigServiceImpl();
    }

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String path = req.getPathInfo();
        
        try {
            if (path == null || "/".equals(path)) {
                // List all payment configs
                List<PaymentConfig> configs = paymentConfigService.getAllPaymentConfigs();
                req.setAttribute("configs", configs);
                req.getRequestDispatcher("/views/admin/payment-config.jsp").forward(req, resp);
                
            } else if ("/edit".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                PaymentConfig config = paymentConfigService.getPaymentConfigById(id);
                
                if (config == null) {
                    resp.sendError(404, "Payment config not found");
                    return;
                }
                
                req.setAttribute("config", config);
                req.getRequestDispatcher("/views/admin/payment-config-form.jsp").forward(req, resp);
                
            } else if ("/create".equals(path)) {
                req.getRequestDispatcher("/views/admin/payment-config-form.jsp").forward(req, resp);
                
            } else {
                resp.sendError(404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi: " + e.getMessage());
            req.getRequestDispatcher("/views/admin/payment-config.jsp").forward(req, resp);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String path = req.getPathInfo();
        
        if ("/save".equals(path)) {
            saveConfig(req, resp);
        } else if ("/toggle".equals(path)) {
            toggleStatus(req, resp);
        } else {
            resp.sendError(404);
        }
    }
    
    private void saveConfig(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        try {
            String idParam = req.getParameter("id");
            Integer id = (idParam != null && !idParam.isEmpty()) ? Integer.parseInt(idParam) : null;
            
            String method = req.getParameter("payment_method");
            String displayName = req.getParameter("display_name");
            String endpoint = req.getParameter("api_endpoint");
            String merchantId = req.getParameter("merchant_id");
            String secretKey = req.getParameter("secret_key");
            String accessKey = req.getParameter("access_key");
            String configJson = req.getParameter("config_json");
            boolean isActive = "on".equals(req.getParameter("isActive"));
            
            paymentConfigService.savePaymentConfig(id, method, displayName, endpoint, 
                                                  merchantId, secretKey, accessKey, configJson, isActive);
            
            req.getSession().setAttribute("success", "Đã lưu cấu hình thanh toán!");
            resp.sendRedirect(req.getContextPath() + "/admin/payment-config");
            
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/payment-config");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Có lỗi khi lưu cấu hình: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/payment-config");
        }
    }
    
    private void toggleStatus(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            paymentConfigService.togglePaymentConfigStatus(id);
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Có lỗi khi cập nhật trạng thái: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/payment-config");
    }
}