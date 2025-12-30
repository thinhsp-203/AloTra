package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import service.PromotionService;
import service.impl.PromotionServiceImpl;

@WebServlet(name = "PromotionController", urlPatterns = {"/promotions", "/khuyen-mai"})
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
        String idParam = req.getParameter("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            // Chi tiết khuyến mãi
            try {
                int id = Integer.parseInt(idParam);
                var promotion = promotionService.getPromotionById(id);
                if (promotion != null && promotion.isActive()) {
                    req.setAttribute("promotion", promotion);
                    req.getRequestDispatcher("/views/promotion_detail.jsp").forward(req, resp);
                    return;
                } else {
                    req.setAttribute("error", "Khuyến mãi không tồn tại hoặc đã bị ẩn!");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("error", "ID khuyến mãi không hợp lệ!");
            }
        }
        
        // Danh sách khuyến mãi
        req.setAttribute("promotions", promotionService.getAllActivePromotions());
        req.getRequestDispatcher("/views/promotions.jsp").forward(req, resp);
    }
}

