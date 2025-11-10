package controller.product;

import config.JpaUtil;
import dao.jpa.OrderRepository; // THÊM IMPORT
import dao.jpa.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.Review; // THÊM IMPORT
import model.User;   // THÊM IMPORT

import java.io.IOException;
import java.util.List; // THÊM IMPORT

@WebServlet(urlPatterns = "/p")
public class ProductDetailController extends HttpServlet {

    private ProductRepository productRepo;
    private OrderRepository orderRepo; // THÊM DÒNG NÀY

    @Override
    public void init() throws ServletException {
        productRepo = new ProductRepository(JpaUtil.em());
        orderRepo = new OrderRepository(JpaUtil.em()); // THÊM DÒNG NÀY
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        EntityManager em = JpaUtil.em(); // Dùng em mới để tránh lỗi
        
        Product p = productRepo.findById(id, em);
        if (p == null) {
            resp.sendRedirect(req.getContextPath() + "/products");
            return;
        }
        
        req.setAttribute("p", p);

        // Lấy sản phẩm liên quan
        List<Product> relatedProducts = productRepo.findRelatedProducts(p.getCategory().getId(), p.getProduct_id(), em);
        req.setAttribute("relatedProducts", relatedProducts);

        // --- BẮT ĐẦU CODE MỚI CHO REVIEW ---
        
        // 1. Lấy danh sách reviews đã được duyệt
        TypedQuery<Review> reviewQuery = em.createQuery(
            "SELECT r FROM Review r WHERE r.product.product_id = :pid AND r.isApproved = true ORDER BY r.createdDate DESC", 
            Review.class
        );
        reviewQuery.setParameter("pid", p.getProduct_id());
        List<Review> reviews = reviewQuery.getResultList();
        req.setAttribute("reviews", reviews);

        // 2. Tính toán rating trung bình (đã lưu trong sản phẩm)
        if (p.getRating() != null && p.getRating().doubleValue() > 0) {
            req.setAttribute("avgRating", p.getRating().doubleValue());
            req.setAttribute("totalReviews", reviews.size());
        }

        // 3. Kiểm tra xem user có thể review không
        User currentUser = (User) req.getSession().getAttribute("currentUser");
        boolean canReview = false;
        if (currentUser != null) {
            // Kiểm tra xem đã mua VÀ chưa review
            boolean hasPurchased = orderRepo.hasUserPurchasedProduct(currentUser.getId(), p.getProduct_id());
            boolean hasReviewed = false;
            for (Review r : reviews) {
                if (r.getUser().getId().equals(currentUser.getId())) {
                    hasReviewed = true;
                    break;
                }
            }
            
            if (hasPurchased && !hasReviewed) {
                canReview = true;
            }
        }
        req.setAttribute("canReview", canReview);
        
        // --- KẾT THÚC CODE MỚI ---
        
        em.close(); // Đóng em đã mở
        
        req.getRequestDispatcher("/views/product/detail.jsp").forward(req, resp);
    }
}