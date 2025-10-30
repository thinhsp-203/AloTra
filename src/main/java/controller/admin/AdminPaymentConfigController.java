package controller.admin;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PaymentConfig;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/payment-config", "/admin/payment-config/*"})
public class AdminPaymentConfigController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String path = req.getPathInfo();
        EntityManager em = JpaUtil.em();
        
        try {
            if (path == null || "/".equals(path)) {
                // List all payment configs
                List<PaymentConfig> configs = em.createQuery(
                    "SELECT p FROM PaymentConfig p ORDER BY p.display_order, p.payment_method",
                    PaymentConfig.class)
                    .getResultList();
                
                req.setAttribute("configs", configs);
                req.getRequestDispatcher("/views/admin/payment_config.jsp").forward(req, resp);
                
            } else if ("/edit".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                PaymentConfig config = em.find(PaymentConfig.class, id);
                
                if (config == null) {
                    resp.sendError(404, "Payment config not found");
                    return;
                }
                
                req.setAttribute("config", config);
                req.getRequestDispatcher("/views/admin/payment_config_form.jsp").forward(req, resp);
                
            } else if ("/create".equals(path)) {
                req.getRequestDispatcher("/views/admin/payment_config_form.jsp").forward(req, resp);
                
            } else {
                resp.sendError(404);
            }
        } finally {
            em.close();
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
        
        EntityManager em = JpaUtil.em();
        var tx = em.getTransaction();
        
        try {
            tx.begin();
            
            PaymentConfig config;
            if (id == null) {
                config = new PaymentConfig();
                config.setCreatedDate(LocalDateTime.now());
            } else {
                config = em.find(PaymentConfig.class, id);
                if (config == null) {
                    resp.sendError(404);
                    return;
                }
            }
            
            config.setPayment_method(method);
            config.setDisplay_name(displayName);
            config.setApi_endpoint(endpoint);
            config.setMerchant_id(merchantId);
            config.setSecret_key(secretKey);
            config.setAccess_key(accessKey);
            config.setConfig_json(configJson);
            config.setIsActive(isActive);
            config.setUpdatedDate(LocalDateTime.now());
            
            if (id == null) {
                em.persist(config);
            } else {
                em.merge(config);
            }
            
            tx.commit();
            
            req.getSession().setAttribute("success", "Đã lưu cấu hình thanh toán!");
            resp.sendRedirect(req.getContextPath() + "/admin/payment-config");
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            req.getSession().setAttribute("error", "Có lỗi khi lưu cấu hình: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/payment-config");
        } finally {
            em.close();
        }
    }
    
    private void toggleStatus(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        
        EntityManager em = JpaUtil.em();
        var tx = em.getTransaction();
        
        try {
            tx.begin();
            
            PaymentConfig config = em.find(PaymentConfig.class, id);
            if (config != null) {
                config.setIsActive(!config.getIsActive());
                config.setUpdatedDate(LocalDateTime.now());
                em.merge(config);
            }
            
            tx.commit();
            
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
        } finally {
            em.close();
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/payment-config");
    }
}