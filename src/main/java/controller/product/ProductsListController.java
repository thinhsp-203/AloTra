package controller.product;

import config.JpaUtil;
import dao.jpa.CategoryRepository;
import dao.jpa.ProductQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category; // Thêm import
import model.Product; // Thêm import
import java.io.IOException;
import java.util.List; // Thêm import

@WebServlet(urlPatterns = "/products")
public class ProductsListController extends HttpServlet {
    
    // SỬA LỖI: Khởi tạo Repository bằng cách truyền EntityManager
    private ProductQueryRepository productRepo;
    private CategoryRepository categoryRepo;

    @Override
    public void init() throws ServletException {
        productRepo = new ProductQueryRepository(JpaUtil.em());
        categoryRepo = new CategoryRepository(JpaUtil.em());
    }
    // KẾT THÚC SỬA LỖI

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
            req.setAttribute("category", categoryRepo.findById(cateId));
        }

        // Truyền các tham số mới vào repository
        List<Product> products = productRepo.findProducts(
            cateId, 
            keyword, 
            sortBy, 
            priceRange
        );
        
        req.setAttribute("products", products);
        req.setAttribute("categories", categoryRepo.findAll());
        
        // Giữ lại các giá trị đã lọc để hiển thị trên form
        req.setAttribute("selectedCateId", cateIdParam); // Giữ lại chuỗi
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedSortBy", sortBy);
        req.setAttribute("selectedPrice", priceRange);

        req.getRequestDispatcher("/views/product/list.jsp").forward(req, resp);
    }
}