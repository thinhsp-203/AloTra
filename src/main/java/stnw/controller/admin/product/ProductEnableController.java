package stnw.controller.admin.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminProductService;
import stnw.service.impl.AdminProductServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/products/enable")
public class ProductEnableController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminProductService productService;

    @Override
    public void init() throws ServletException {
        productService = new AdminProductServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            productService.enableProduct(id, req.getServletContext());
            req.getSession().setAttribute("success", "Đã kích hoạt sản phẩm!");
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}

