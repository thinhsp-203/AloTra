package stnw.controller.admin.promotion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminPromotionService;
import stnw.service.impl.AdminPromotionServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/promotions/delete")
public class PromotionDeleteController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminPromotionService promotionService;
    
    @Override
    public void init() throws ServletException {
        promotionService = new AdminPromotionServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            promotionService.deletePromotion(id, req.getServletContext());
            req.getSession().setAttribute("success", "Đã xóa khuyến mãi thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/promotions");
    }
}

