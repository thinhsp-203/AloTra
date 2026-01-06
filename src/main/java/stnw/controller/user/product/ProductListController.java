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
        String categoryIdParam = req.getParameter("category");
        String keyword = req.getParameter("keyword");
        String sortBy = req.getParameter("sortBy");
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

        List<Product> products = productQueryService.findProducts(categoryId, keyword, sortBy, null, offset, pageSize);
        
        // Tính tổng số sản phẩm bằng cách lấy tất cả (không giới hạn)
        List<Product> allProducts = productQueryService.findProducts(categoryId, keyword, sortBy, null, 0, -1);
        int totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize);

        req.setAttribute("products", products);
        req.setAttribute("categories", catalogService.getAllCategories());
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("pageSize", pageSize);
        req.setAttribute("sortBy", sortBy);
        if (categoryIdParam != null && !categoryIdParam.isEmpty()) {
            try {
                req.setAttribute("selectedCategory", Integer.parseInt(categoryIdParam));
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        req.getRequestDispatcher("/views/product/list.jsp").forward(req, resp);
    }
}

