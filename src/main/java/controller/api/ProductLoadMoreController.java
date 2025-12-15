package controller.api;

import config.JpaUtil;
import dao.ProductQueryRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/products/load")
public class ProductLoadMoreController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String cateIdParam = req.getParameter("cate");
        String keyword = req.getParameter("q");
        String sortBy = req.getParameter("sortBy");
        String priceRange = req.getParameter("price");
        
        int page = 1;
        try {
            page = Integer.parseInt(req.getParameter("page"));
        } catch (NumberFormatException e) { }
        
        int pageSize = 6; 
        int offset = (page - 1) * pageSize;

        Integer cateId = (cateIdParam != null && !cateIdParam.isEmpty()) ? Integer.parseInt(cateIdParam) : null;

        var em = JpaUtil.em();
        try {
            ProductQueryRepository repo = new ProductQueryRepository(em);
            List<Product> products = repo.findProducts(cateId, keyword, sortBy, priceRange, offset, pageSize);
            
           req.setAttribute("products", products);
           req.getRequestDispatcher("/views/_partials/product_list_fragment.jsp").forward(req, resp);
            
        } finally {
            em.close();
        }
    }
}