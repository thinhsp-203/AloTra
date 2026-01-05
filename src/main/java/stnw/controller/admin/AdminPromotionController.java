package stnw.controller.admin;

import java.io.IOException;

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

@WebServlet(urlPatterns = "/admin/promotions")
@MultipartConfig
public class AdminPromotionController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private AdminPromotionService promotionService;
    
    @Override
    public void init() throws ServletException {
        promotionService = new AdminPromotionServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            // Edit mode - load promotion by id
            try {
                int id = Integer.parseInt(idParam);
                Promotion promotion = promotionService.getPromotionById(id);
                if (promotion != null) {
                    req.setAttribute("promotion", promotion);
                } else {
                    req.getSession().setAttribute("error", "Không tìm thấy khuyến mãi!");
                }
            } catch (NumberFormatException e) {
                req.getSession().setAttribute("error", "ID không hợp lệ!");
            }
        }
        req.setAttribute("promotions", promotionService.getAllPromotions());
        req.getRequestDispatcher("/views/admin/promotions.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String action = req.getParameter("action");
        
        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                promotionService.deletePromotion(id, req.getServletContext());
                req.getSession().setAttribute("success", "Đã xóa khuyến mãi thành công!");
                
            } else if ("add".equals(action) || "edit".equals(action)) {
                Promotion promotion;
                
                if ("edit".equals(action)) {
                    int id = Integer.parseInt(req.getParameter("id"));
                    promotion = promotionService.getPromotionById(id);
                    if (promotion == null) {
                        req.getSession().setAttribute("error", "Không tìm thấy khuyến mãi!");
                        resp.sendRedirect(req.getContextPath() + "/admin/promotions");
                        return;
                    }
                } else {
                    promotion = new Promotion();
                }
                
                promotion.setTitle(req.getParameter("title"));
                promotion.setDescription(req.getParameter("description"));
                promotion.setContent(req.getParameter("content"));
                promotion.setActive(req.getParameter("isActive") != null);
                
                Part filePart = req.getPart("promotionFile");
                String imageUrlFromText = req.getParameter("imageUrl");
                
                promotionService.savePromotion(promotion, filePart, imageUrlFromText, req.getServletContext());
                req.getSession().setAttribute("success", "Đã " + ("edit".equals(action) ? "cập nhật" : "thêm") + " khuyến mãi thành công!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/promotions");
    }
}
