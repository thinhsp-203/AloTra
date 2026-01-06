package stnw.controller.admin.promotion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminPromotionService;
import stnw.service.impl.AdminPromotionServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/promotions")
public class PromotionListController extends HttpServlet {
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
            try {
                int id = Integer.parseInt(idParam);
                stnw.model.Promotion promotion = promotionService.getPromotionById(id);
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
}

