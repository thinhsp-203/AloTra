package controller.product;

import config.JpaUtil;
import dao.jpa.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Product;
import model.User;
import model.Review;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebServlet("/submit-review")
public class ReviewController extends HttpServlet {

    private OrderRepository orderRepo;

    @Override
    public void init() throws ServletException {
        orderRepo = new OrderRepository(JpaUtil.em());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        
        String productIdStr = req.getParameter("productId");
        int productId = Integer.parseInt(productIdStr);

        if (currentUser == null) {
            req.getSession().setAttribute("error", "Vui lòng đăng nhập để đánh giá.");
            resp.sendRedirect(req.getContextPath() + "/login?redirect=" + req.getContextPath() + "/p?id=" + productId);
            return;
        }

        EntityManager em = JpaUtil.em();
        try {
            int rating = Integer.parseInt(req.getParameter("rating"));
            String comment = req.getParameter("comment");

            // 1. Xác thực: User đã mua hàng chưa?
            boolean hasPurchased = orderRepo.hasUserPurchasedProduct(currentUser.getId(), productId);
            if (!hasPurchased) {
                req.getSession().setAttribute("error", "Bạn chỉ có thể đánh giá sản phẩm đã mua.");
                resp.sendRedirect(req.getContextPath() + "/p?id=" + productId);
                return;
            }
            
            // 2. (Nâng cao) Kiểm tra xem đã review chưa
            // (Chúng ta sẽ làm ở ProductDetailController để ẩn form)

            Product product = em.find(Product.class, productId);
            if (product == null) {
                resp.sendRedirect(req.getContextPath());
                return;
            }

            em.getTransaction().begin();
            
            Review review = new Review();
            review.setProduct(product);
            review.setUser(currentUser);
            review.setRating(rating);
            review.setComment(comment);
            review.setCreatedDate(LocalDateTime.now());
            review.setIsApproved(true); // Tạm thời tự động duyệt
            em.persist(review);

            // 3. (QUAN TRỌNG) Cập nhật lại rating trung bình của sản phẩm
            TypedQuery<Double> avgQuery = em.createQuery(
                "SELECT AVG(r.rating) FROM Review r WHERE r.product.product_id = :pid AND r.isApproved = true", 
                Double.class
            );
            avgQuery.setParameter("pid", productId);
            Double avgRating = avgQuery.getSingleResult();
            
            if(avgRating != null) {
                // Làm tròn 1 chữ số thập phân
                product.setRating(BigDecimal.valueOf(Math.round(avgRating * 10.0) / 10.0));
                em.merge(product);
            }
            
            em.getTransaction().commit();
            
            req.getSession().setAttribute("success", "Cảm ơn bạn đã đánh giá sản phẩm!");
            resp.sendRedirect(req.getContextPath() + "/p?id=" + productId);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            req.getSession().setAttribute("error", "Đã xảy ra lỗi khi gửi đánh giá.");
            resp.sendRedirect(req.getContextPath() + "/p?id=" + productIdStr);
        } finally {
            em.close();
        }
    }
}