package controller.admin;

import java.io.IOException;
import java.math.BigDecimal;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Topping;
import service.AdminToppingService;
import service.impl.AdminToppingServiceImpl;

@WebServlet(urlPatterns = {
    "/admin/toppings",
    "/admin/toppings/create",
    "/admin/toppings/edit",
    "/admin/toppings/save",
    "/admin/toppings/delete"
})
public class AdminToppingController extends HttpServlet {
	    
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private AdminToppingService toppingService;
	    
	    @Override
	    public void init() throws ServletException {
	        toppingService = new AdminToppingServiceImpl();
	    }
	    
	    @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
	        String uri = req.getRequestURI();
	        
	        if (uri.endsWith("/admin/toppings")) {
	            req.setAttribute("items", toppingService.getAllToppings());
	            req.getRequestDispatcher("/views/admin/toppings.jsp").forward(req, resp);
	            
	        } else if (uri.endsWith("/admin/toppings/create")) {
	            req.setAttribute("item", new Topping());
	            req.getRequestDispatcher("/views/admin/topping_form.jsp").forward(req, resp);
	            
	        } else if (uri.endsWith("/admin/toppings/edit")) {
	            int id = Integer.parseInt(req.getParameter("id"));
	            req.setAttribute("item", toppingService.getToppingById(id));
	            req.getRequestDispatcher("/views/admin/topping_form.jsp").forward(req, resp);
	        }
	    }
	    
	    @Override
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
	        String uri = req.getRequestURI();
	        
	        try {
	            if (uri.endsWith("/admin/toppings/save")) {
	                String idParam = req.getParameter("id");
	                Topping item = (idParam != null && !idParam.isEmpty()) 
	                    ? toppingService.getToppingById(Integer.parseInt(idParam)) 
	                    : new Topping();
	                
	                item.setTopping_name(req.getParameter("topping_name"));
	                item.setPrice(new BigDecimal(req.getParameter("price")));
	                item.setIsAvailable(req.getParameter("isAvailable") != null);
	                
	                toppingService.saveTopping(item);
	                req.getSession().setAttribute("success", "Đã lưu Topping thành công!");
	                
	            } else if (uri.endsWith("/admin/toppings/delete")) {
	                int id = Integer.parseInt(req.getParameter("id"));
	                toppingService.deleteTopping(id);
	                req.getSession().setAttribute("success", "Đã xóa Topping!");
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
	        }
	        
	        resp.sendRedirect(req.getContextPath() + "/admin/toppings");
	    }
	}