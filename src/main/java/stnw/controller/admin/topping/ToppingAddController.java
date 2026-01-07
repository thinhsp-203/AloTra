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

@WebServlet(urlPatterns = "/admin/toppings/create")
public class ToppingAddController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminToppingService toppingService;
    
    @Override
    public void init() throws ServletException {
        toppingService = new AdminToppingServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        req.setAttribute("item", new Topping());
        req.getRequestDispatcher("/views/admin/topping-form.jsp").forward(req, resp);
    }
}

