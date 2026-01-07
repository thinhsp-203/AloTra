package stnw.controller.admin.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import stnw.service.AdminProductService;
import stnw.service.impl.AdminProductServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(urlPatterns = "/admin/products/save")
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class ProductSaveController extends HttpServlet {
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
            String idParam = req.getParameter("id");
            Integer productId = (idParam != null && !idParam.isEmpty()) 
                ? Integer.parseInt(idParam) 
                : null;
            
            String productName = req.getParameter("product_name");
            String description = req.getParameter("description");
            BigDecimal price = new BigDecimal(req.getParameter("price"));
            
            String discountParam = req.getParameter("discount");
            BigDecimal discount = (discountParam == null || discountParam.isEmpty()) 
                ? BigDecimal.ZERO 
                : new BigDecimal(discountParam);
            
            Integer categoryId = Integer.parseInt(req.getParameter("cate_id"));
            Boolean isActive = req.getParameter("isActive") != null;
            Boolean isFeatured = req.getParameter("isFeatured") != null;
            
            Part thumbnailFile = req.getPart("thumbnailFile");
            String thumbnailUrl = req.getParameter("thumbnailUrl");
            
            productService.saveProductFromParams(productId, productName, description, price, discount, 
                                                categoryId, isActive, isFeatured, 
                                                thumbnailFile, thumbnailUrl, req.getServletContext());
            
            req.getSession().setAttribute("success", "Đã lưu sản phẩm thành công!");
            
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}

