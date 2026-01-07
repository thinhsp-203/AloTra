package stnw.controller.user.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Product;
import stnw.service.CatalogService;
import stnw.service.ProductQueryService;
import stnw.service.impl.CatalogServiceImpl;
import stnw.service.impl.ProductQueryServiceImpl;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/products")
public class ProductListController extends HttpServlet {

    private static final long serialVersionUID = 1L;
	private ProductQueryService productQueryService;
    private CatalogService catalogService;

    @Override
    public void init() throws ServletException {
        productQueryService = new ProductQueryServiceImpl();
        catalogService = new CatalogServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Đọc parameter: ưu tiên "cate" (từ JSP), fallback "category" (tương thích ngược)
        String categoryIdParam = req.getParameter("cate");
        if (categoryIdParam == null || categoryIdParam.isEmpty()) {
            categoryIdParam = req.getParameter("category");
        }
        
        // Đọc parameter: ưu tiên "q" (từ JSP), fallback "keyword" (tương thích ngược)
        String keyword = req.getParameter("q");
        if (keyword == null || keyword.isEmpty()) {
            keyword = req.getParameter("keyword");
        }
        
        String sortBy = req.getParameter("sortBy");
        String priceRange = req.getParameter("price");
        // Xử lý trường hợp dấu + bị mất hoặc bị decode thành space
        if (priceRange != null) {
            priceRange = priceRange.trim();
            // Nếu priceRange là "100000" (thiếu dấu +), thêm dấu + vào
            if ("100000".equals(priceRange)) {
                priceRange = "100000+";
            }
            // Nếu priceRange có space ở cuối (do + bị decode), thay bằng +
            if (priceRange.endsWith(" ")) {
                priceRange = priceRange.substring(0, priceRange.length() - 1) + "+";
            }
        }
        String pageParam = req.getParameter("page");
        String pageSizeParam = req.getParameter("pageSize");

        int page = (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;
        int pageSize = (pageSizeParam != null && !pageSizeParam.isEmpty()) ? Integer.parseInt(pageSizeParam) : 12;
        int offset = (page - 1) * pageSize;

        Integer categoryId = null;
        if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdParam);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        List<Product> products = productQueryService.findProducts(categoryId, keyword, sortBy, priceRange, offset, pageSize);
        
        // Tính tổng số sản phẩm bằng cách lấy tất cả (không giới hạn)
        List<Product> allProducts = productQueryService.findProducts(categoryId, keyword, sortBy, priceRange, 0, -1);
        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        // Lấy category object nếu có categoryId
        stnw.model.Category selectedCategoryObj = null;
        if (categoryId != null) {
            List<stnw.model.Category> allCategories = catalogService.getAllCategories();
            for (stnw.model.Category cat : allCategories) {
                if (cat.getId() == categoryId.intValue()) {
                    selectedCategoryObj = cat;
                    break;
                }
            }
        }

        req.setAttribute("products", products);
        req.setAttribute("categories", catalogService.getAllCategories());
        req.setAttribute("category", selectedCategoryObj); // Category object để hiển thị tên
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("pageSize", pageSize);
        req.setAttribute("sortBy", sortBy);
        req.setAttribute("selectedSortBy", sortBy); // Để JSP sử dụng
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedPrice", priceRange); // Để JSP highlight price filter
        if (categoryId != null) {
            req.setAttribute("selectedCategory", categoryId);
            req.setAttribute("selectedCateId", categoryId); // Để JSP sử dụng
        }

        req.getRequestDispatcher("/views/product/list.jsp").forward(req, resp);
    }
}

