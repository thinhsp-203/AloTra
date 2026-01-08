package stnw.controller.admin.promotion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import stnw.model.Promotion;
import stnw.service.AdminPromotionService;
import stnw.service.impl.AdminPromotionServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/promotions/add")
@MultipartConfig
public class PromotionAddController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminPromotionService promotionService;
    
    @Override
    public void init() throws ServletException {
        promotionService = new AdminPromotionServiceImpl();
    }
    
    // Xử lý POST: thêm mới khuyến mãi
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        try {
            Promotion promotion = new Promotion();
            
            promotion.setTitle(req.getParameter("title"));
            promotion.setDescription(req.getParameter("description"));
            promotion.setContent(req.getParameter("content"));
            promotion.setActive(req.getParameter("isActive") != null);
            
            Part filePart = req.getPart("promotionFile");
            String imageUrlFromText = req.getParameter("imageUrl");
            
            promotionService.savePromotion(promotion, filePart, imageUrlFromText, req.getServletContext());
            req.getSession().setAttribute("success", "Đã thêm khuyến mãi thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/promotions");
    }
}

