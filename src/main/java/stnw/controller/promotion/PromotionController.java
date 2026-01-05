package stnw.controller.promotion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import stnw.service.PromotionService;
import stnw.service.impl.PromotionServiceImpl;

@WebServlet(name = "PromotionController", urlPatterns = {"/promotions", "/promotions/*", "/khuyen-mai"})
public class PromotionController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private PromotionService promotionService;

    @Override
    public void init() throws ServletException {
        promotionService = new PromotionServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // e.g., "/detail" or null
        String idParam = req.getParameter("id");
        
        // If path is /promotions/detail but no id parameter, redirect to list
        if ("/detail".equals(pathInfo) && (idParam == null || idParam.isEmpty())) {
            resp.sendRedirect(req.getContextPath() + "/promotions");
            return;
        }
        
        if (idParam != null && !idParam.isEmpty()) {
            // Chi tiết khuyến mãi
            try {
                int id = Integer.parseInt(idParam);
                var promotion = promotionService.getPromotionById(id);
                if (promotion != null && promotion.isActive()) {
                    req.setAttribute("promotion", promotion);
                    // Get related promotions (exclude current one, limit to 3)
                    var relatedPromotions = promotionService.getRelatedPromotions(id, 3);
                    req.setAttribute("relatedPromotions", relatedPromotions);
                    req.getRequestDispatcher("/views/promotion/detail.jsp").forward(req, resp);
                    return;
                } else {
                    req.setAttribute("error", "Khuyến mãi không tồn tại hoặc đã bị ẩn!");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("error", "ID khuyến mãi không hợp lệ");
            }
        }
        
        // Danh sách khuyến mãi
        req.setAttribute("promotions", promotionService.getAllActivePromotions());
        req.getRequestDispatcher("/views/promotion/list.jsp").forward(req, resp);
    }
}

