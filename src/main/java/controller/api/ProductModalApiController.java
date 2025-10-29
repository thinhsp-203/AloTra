package controller.api;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.ProductSize;
import model.Topping;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/api/product-details")
public class ProductModalApiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");

        try {
            int productId = Integer.parseInt(req.getParameter("id"));
            EntityManager em = JpaUtil.em();
            try {
                Product product = em.find(Product.class, productId);
                if (product == null || product.getPrice() == null) {
                    resp.setStatus(404);
                    resp.getWriter().print("{\"ok\":false, \"message\":\"Sản phẩm không tồn tại hoặc thiếu thông tin giá.\"}");
                    return;
                }

                List<ProductSize> sizes = em.createQuery("SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid ORDER BY ps.size_name", ProductSize.class)
                                            .setParameter("pid", productId)
                                            .getResultList();
                // Nếu không có size nào, tạo một size mặc định "Mặc định" để logic không bị lỗi
                if (sizes.isEmpty()) {
                    ProductSize defaultSize = new ProductSize();
                    defaultSize.setSize_name("Mặc định");
                    defaultSize.setPrice_adjustment(java.math.BigDecimal.ZERO);
                    sizes = Collections.singletonList(defaultSize);
                }

                List<Topping> toppings = em.createQuery("SELECT t FROM Topping t WHERE t.isAvailable = true ORDER BY t.topping_name", Topping.class)
                                           .getResultList();

                String productJson = String.format(
                    "{\"id\":%d, \"name\":\"%s\", \"basePrice\":%s, \"thumbnail\":\"%s\"}",
                    product.getProduct_id(), escapeJson(product.getProduct_name()), product.getPrice(), escapeJson(product.getThumbnail())
                );

                String sizesJson = sizes.stream()
                    .map(s -> String.format("{\"name\":\"%s\", \"priceAdjustment\":%s}", escapeJson(s.getSize_name()), s.getPrice_adjustment()))
                    .collect(Collectors.joining(","));

                String toppingsJson = toppings.stream()
                    .map(t -> String.format("{\"id\":%d, \"name\":\"%s\", \"price\":%s}", t.getTopping_id(), escapeJson(t.getTopping_name()), t.getPrice()))
                    .collect(Collectors.joining(","));

                String finalJson = String.format(
                    "{\"ok\":true, \"product\":%s, \"sizes\":[%s], \"toppings\":[%s]}",
                    productJson, sizesJson, toppingsJson
                );

                resp.getWriter().print(finalJson);

            } finally {
                if (em.isOpen()) em.close();
            }
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print("{\"ok\":false, \"message\":\"Lỗi máy chủ: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}