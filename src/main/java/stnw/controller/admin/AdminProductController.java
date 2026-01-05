package stnw.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import stnw.model.Product;
import stnw.service.AdminProductService;
import stnw.service.impl.AdminProductServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@WebServlet(urlPatterns = {
    "/admin/products", 
    "/admin/products/create", 
    "/admin/products/edit", 
    "/admin/products/save", 
    "/admin/products/delete",
    "/admin/products/disable",
    "/admin/products/enable"
}, asyncSupported = false)
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class AdminProductController extends HttpServlet {

    private static final long serialVersionUID = 1L;
	private AdminProductService productService;

    @Override
    public void init() throws ServletException {
        productService = new AdminProductServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        
        try {
            if (uri.endsWith("/admin/products")) {
                showProductList(req, resp);
            } else if (uri.endsWith("/admin/products/create")) {
                showProductForm(req, resp, null);
            } else if (uri.endsWith("/admin/products/edit")) {
                int id = Integer.parseInt(req.getParameter("id"));
                showProductForm(req, resp, id);
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("error", "ID sản phẩm không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/admin/products");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/products");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        
        try {
            if (uri.endsWith("/admin/products/save")) {
                saveProduct(req, resp);
            } else if (uri.endsWith("/admin/products/delete")) {
                deleteProduct(req, resp);
            } else if (uri.endsWith("/admin/products/disable")) {
                disableProduct(req, resp);
            } else if (uri.endsWith("/admin/products/enable")) {
                enableProduct(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/products");
        }
    }

    // ==================== PRIVATE METHODS ====================

    private void showProductList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Product> products = productService.getAllProducts();
        req.setAttribute("list", products);
        req.getRequestDispatcher("/views/admin/products.jsp").forward(req, resp);
    }

    private void showProductForm(HttpServletRequest req, HttpServletResponse resp, Integer id)
            throws ServletException, IOException {
        Product product;
        
        if (id != null) {
            product = productService.getProductById(id);
            if (product == null) {
                req.getSession().setAttribute("error", "Sản phẩm không tồn tại!");
                resp.sendRedirect(req.getContextPath() + "/admin/products");
                return;
            }
        } else {
            product = new Product(); // OK: chỉ để hiển thị form trống
        }
        
        // Lấy dữ liệu cho form (Categories)
        Map<String, List<?>> formData = productService.getFormData();
        
        req.setAttribute("p", product);
        req.setAttribute("categories", formData.get("categories"));
        
        req.getRequestDispatcher("/views/admin/product-form.jsp").forward(req, resp);
    }

    private void saveProduct(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // 1. LẤY THÔNG TIN TỪ FORM
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
            
            // 2. LẤY ẢNH
            Part thumbnailFile = req.getPart("thumbnailFile");
            String thumbnailUrl = req.getParameter("thumbnailUrl");
            
            // 3. GỌI SERVICE XỬ LÝ
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

    private void disableProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            productService.disableProduct(id, req.getServletContext());
            req.getSession().setAttribute("success", "Đã ngừng bán sản phẩm!");
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
    
    private void enableProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
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
    
    private void deleteProduct(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            productService.deleteProduct(id, req.getServletContext());
            req.getSession().setAttribute("success", "Đã xóa sản phẩm vĩnh viễn!");
        } catch (IllegalArgumentException e) {
            req.getSession().setAttribute("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/products");
    }
}
