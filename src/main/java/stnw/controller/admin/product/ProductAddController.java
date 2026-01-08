package stnw.controller.admin.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminProductService;
import stnw.service.impl.AdminProductServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// Cấu hình upload file (ảnh sản phẩm, v.v.)
@WebServlet(urlPatterns = "/admin/products/create")
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class ProductAddController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminProductService productService;

    @Override
    public void init() throws ServletException {
        productService = new AdminProductServiceImpl();
    }

    // Xử lý request GET: hiển thị form thêm sản phẩm
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Map<String, List<?>> formData = productService.getFormData();
        req.setAttribute("p", new stnw.model.Product());
        req.setAttribute("categories", formData.get("categories"));
        req.getRequestDispatcher("/views/admin/product-form.jsp").forward(req, resp);
    }
}

