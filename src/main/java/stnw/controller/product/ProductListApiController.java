package stnw.controller.product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Product;
import stnw.service.ProductQueryService;
import stnw.service.impl.ProductQueryServiceImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet("/api/products")
public class ProductListApiController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductQueryService productQueryService;

    @Override
    public void init() throws ServletException {
        productQueryService = new ProductQueryServiceImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            int page = Integer.parseInt(request.getParameter("page"));
            int size = Integer.parseInt(request.getParameter("size"));
            Integer cateId = request.getParameter("cateId") != null ? Integer.parseInt(request.getParameter("cateId")) : null;
            BigDecimal minPrice = request.getParameter("minPrice") != null ? new BigDecimal(request.getParameter("minPrice")) : null;
            BigDecimal maxPrice = request.getParameter("maxPrice") != null ? new BigDecimal(request.getParameter("maxPrice")) : null;
            String keyword = request.getParameter("keyword");

            var data = productQueryService.search(cateId, null, minPrice, maxPrice, keyword, page, size);
            long total = (long) data.get("total");

            // SỬA LỖI: Lấy List<Product> từ Map
            @SuppressWarnings("unchecked")
            List<Product> products = (List<Product>) data.get("products");
            
            json(products, total, true, response); // Truyền List<Product> vào
            
        } catch (Exception e) {
            e.printStackTrace();
            json(null, 0, false, response);
        }
    }

    private void json(List<Product> products, long total, boolean success, HttpServletResponse response)
            throws IOException {
        Gson gson = new GsonBuilder().create();
        PrintWriter out = response.getWriter();
        
        Map<String, Object> result = Map.of(
            "data", products,
            "total", total,
            "success", success
        );
        out.print(gson.toJson(result));
        out.flush();
    }
}
