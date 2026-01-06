package stnw.controller.admin.topping;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Topping;
import stnw.service.AdminToppingService;
import stnw.service.impl.AdminToppingServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(urlPatterns = "/admin/toppings/save")
public class ToppingSaveController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminToppingService toppingService;
    
    @Override
    public void init() throws ServletException {
        toppingService = new AdminToppingServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            String idParam = req.getParameter("id");
            Topping item = (idParam != null && !idParam.isEmpty()) 
                ? toppingService.getToppingById(Integer.parseInt(idParam)) 
                : new Topping();
            
            item.setTopping_name(req.getParameter("topping_name"));
            item.setPrice(new BigDecimal(req.getParameter("price")));
            item.setIsAvailable(req.getParameter("isAvailable") != null);
            
            toppingService.saveTopping(item);
            req.getSession().setAttribute("success", "Đã lưu Topping thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/toppings");
    }
}

