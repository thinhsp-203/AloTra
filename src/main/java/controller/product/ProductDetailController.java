package controller.product;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.Product;
import model.Topping;

@WebServlet(urlPatterns = "/p")
public class ProductDetailController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID.");
            return;
        }

        EntityManager em = JpaUtil.em();
        try {
            Product p = em.find(Product.class, id);
            if (p == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found.");
                return;
            }
            req.setAttribute("p", p);
            
            List<Product> sameCate = Collections.emptyList();
            if (p.getCategory() != null) {
                sameCate = em.createQuery(
                    "select x from Product x where x.category.id=:c and x.product_id<>:id order by x.createdDate desc", Product.class)
                    .setParameter("c", p.getCategory().getId())
                    .setParameter("id", id)
                    .setMaxResults(8)
                    .getResultList();
            }
         // === START MODIFICATION ===
            // Lấy danh sách topping đang có sẵn
            List<Topping> toppings = em.createQuery(
                "SELECT t FROM Topping t WHERE t.isAvailable = true ORDER BY t.topping_name", Topping.class)
                .getResultList();
            req.setAttribute("toppings", toppings);
            // === END MODIFICATION ===
            
            // Chỉ tìm kiếm nếu sản phẩm có nhà cung cấp
            List<Product> sameSup = Collections.emptyList();
            if (p.getSupplier() != null) {
                sameSup = em.createQuery(
                    "select x from Product x where x.supplier.supplier_id=:s and x.product_id<>:id order by x.createdDate desc", Product.class)
                    .setParameter("s", p.getSupplier().getSupplier_id())
                    .setParameter("id", p.getProduct_id())
                    .setMaxResults(8)
                    .getResultList();
            }

            req.setAttribute("sameCate", sameCate);
            req.setAttribute("sameSup", sameSup);
            trackViewed(req, resp, id);
            req.getRequestDispatcher("/views/product/detail.jsp").forward(req, resp);
        } finally {
            em.close();
        }
    }

    private void trackViewed(HttpServletRequest req, HttpServletResponse resp, int id) {
        String name = "viewed", v = "";
        if (req.getCookies() != null)
            for (var c : req.getCookies())
                if (name.equals(c.getName()))
                    v = c.getValue();
        var set = new java.util.LinkedHashSet<>(java.util.Arrays.asList(v.split("-")));
        set.removeIf(String::isBlank);
        set.add(String.valueOf(id));
        while (set.size() > 20)
            set.remove(set.iterator().next());
        var c = new Cookie(name, String.join("-", set));
        c.setPath(req.getContextPath());
        c.setMaxAge(30 * 24 * 3600);
        resp.addCookie(c);
    }
}