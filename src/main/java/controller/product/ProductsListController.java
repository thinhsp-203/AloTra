package controller.product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import service.CatalogService;
import service.ProductQueryService;
import service.impl.CatalogServiceImpl;
import service.impl.ProductQueryServiceImpl;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/products")
public class ProductsListController extends HttpServlet {

    private ProductQueryService productQueryService;
    private CatalogService catalogService;

    @Override
    public void init() throws ServletException {
        productQueryService = new ProductQueryServiceImpl();
        catalogService = new CatalogServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cateIdParam = req.getParameter("cate");
        String keyword = req.getParameter("q");
        
        // THAM SỐ LỌC MỚI
        String sortBy = req.getParameter("sortBy"); // newest, price-asc, price-desc
        String priceRange = req.getParameter("price"); // 0-50000, 50000-100000, 100000+
        
        Integer cateId = null;
        if (cateIdParam != null && !cateIdParam.isEmpty()) { // Kiểm tra isEmpty
            cateId = Integer.parseInt(cateIdParam);
        }

        // Load 18 sản phẩm từ đầu (hiển thị 6, ẩn 12), theo logic của trang home
        List<Product> products = productQueryService.findProducts(
            cateId, 
            keyword, 
            sortBy, 
            priceRange,
            0,
            18
        );
        
        req.setAttribute("products", products);
        req.setAttribute("categories", catalogService.getAllCategories());
        
        // Giữ lại các giá trị đã lọc để hiển thị trên form
        req.setAttribute("selectedCateId", cateIdParam); // Giữ lại chuỗi
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedSortBy", sortBy);
        req.setAttribute("selectedPrice", priceRange);

        req.getRequestDispatcher("/views/product/list.jsp").forward(req, resp);
    }
}