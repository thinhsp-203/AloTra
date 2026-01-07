package stnw.controller.admin.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminProductService;
import stnw.service.impl.AdminProductServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/products/delete")
public class ProductDeleteController extends HttpServlet {
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
            System.out.println("[ProductDeleteController] Bắt đầu xóa sản phẩm ID: " + id);
            productService.deleteProduct(id, req.getServletContext());
            System.out.println("[ProductDeleteController] Xóa sản phẩm thành công ID: " + id);
            req.getSession().setAttribute("success", "Đã xóa sản phẩm vĩnh viễn!");
        } catch (IllegalArgumentException e) {
            System.err.println("[ProductDeleteController] Lỗi validation: " + e.getMessage());
            e.printStackTrace();
            req.getSession().setAttribute("error", e.getMessage());
        } catch (Exception e) {
            System.err.println("[ProductDeleteController] Lỗi khi xóa sản phẩm: " + e.getMessage());
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}

