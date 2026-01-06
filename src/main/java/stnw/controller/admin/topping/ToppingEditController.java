package stnw.controller.admin.topping;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminToppingService;
import stnw.service.impl.AdminToppingServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/toppings/edit")
public class ToppingEditController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminToppingService toppingService;
    
    @Override
    public void init() throws ServletException {
        toppingService = new AdminToppingServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            req.setAttribute("item", toppingService.getToppingById(id));
            req.getRequestDispatcher("/views/admin/topping-form.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("error", "ID không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/admin/toppings");
        }
    }
}

