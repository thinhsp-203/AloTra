package stnw.controller.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Product;
import stnw.model.ProductSize;
import stnw.model.Topping;
import stnw.service.ProductQueryService;
import stnw.service.impl.ProductQueryServiceImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/api/product-details")
public class ProductModalApiController extends HttpServlet {

    private final ProductQueryService productQueryService = new ProductQueryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");

        try {
            int productId = Integer.parseInt(req.getParameter("id"));
            Product product = productQueryService.getById(productId);
            if (product == null || product.getPrice() == null) {
                resp.setStatus(404);
                resp.getWriter().print("{\"ok\":false, \"message\":\"Sản phẩm không tồn tại hoặc thiếu thông tin giá.\"}");
                return;
            }

            String categoryName = (product.getCategory() != null) ? product.getCategory().getName() : "";
            boolean isDrink = (product.getCategory() != null && Boolean.TRUE.equals(product.getCategory().getIsDrink()));

            List<ProductSize> sizes = productQueryService.getSizes(productId);
            List<Topping> toppings = productQueryService.getAvailableToppingsForCategory(categoryName);

            // Sử dụng giá sau giảm (finalPrice) thay vì giá gốc
            java.math.BigDecimal displayPrice = product.getFinalPrice();
            java.math.BigDecimal originalPrice = product.getPrice();
            java.math.BigDecimal discount = product.getDiscount();
            
            String productJson = String.format(
                "{\"id\":%d, \"name\":\"%s\", \"basePrice\":%s, \"originalPrice\":%s, \"discount\":%s, \"hasDiscount\":%s, \"thumbnail\":\"%s\", \"categoryName\":\"%s\", \"isDrink\":%s}",
                product.getProduct_id(), 
                escapeJson(product.getProduct_name()), 
                displayPrice, 
                originalPrice != null ? originalPrice : displayPrice,
                discount != null ? discount : "0",
                product.hasDiscount(),
                escapeJson(product.getThumbnail()), 
                escapeJson(categoryName), 
                isDrink
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

        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().print("{\"ok\":false, \"message\":\"Lỗi máy chủ " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
