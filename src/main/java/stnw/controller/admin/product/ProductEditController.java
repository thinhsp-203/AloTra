package stnw.controller.admin.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Product;
import stnw.service.AdminProductService;
import stnw.service.impl.AdminProductServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/admin/products/edit")
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class ProductEditController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminProductService productService;

    @Override
    public void init() throws ServletException {
        productService = new AdminProductServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Product product = productService.getProductById(id);
            
            if (product == null) {
                req.getSession().setAttribute("error", "Sản phẩm không tồn tại!");
                resp.sendRedirect(req.getContextPath() + "/admin/products");
                return;
            }
            
            Map<String, List<?>> formData = productService.getFormData();
            req.setAttribute("p", product);
            req.setAttribute("categories", formData.get("categories"));
            req.getRequestDispatcher("/views/admin/product-form.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("error", "ID sản phẩm không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/admin/products");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/products");
        }
    }
}

