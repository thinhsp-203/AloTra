package controller.product;

import config.JpaUtil;
import dao.jpa.OrderRepository;
import dao.jpa.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.Review;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/p")
public class ProductDetailController extends HttpServlet {

    private ProductRepository productRepo;
    private OrderRepository orderRepo;

    @Override
    public void init() throws ServletException {
        productRepo = new ProductRepository();
        orderRepo = new OrderRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       EntityManager em = null; 
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            em = JpaUtil.em(); 

            Product p = productRepo.findById(id, em); 
            if (p == null) {
                resp.sendRedirect(req.getContextPath() + "/products");
                return;
            }
            
            req.setAttribute("p", p);

            // Lấy sản phẩm liên quan
            List<Product> relatedProducts = productRepo.findRelatedProducts(p.getCategory().getId(), p.getProduct_id(), em); // Truyền em
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

            // 2. Tính toán rating 
            if (p.getRating() != null && p.getRating().doubleValue() > 0) {
                req.setAttribute("avgRating", p.getRating().doubleValue());
                req.setAttribute("totalReviews", reviews.size());
            }

            // 3. Kiểm tra xem user có thể review không
            User currentUser = (User) req.getSession().getAttribute("currentUser");
            boolean canReview = false;
            if (currentUser != null) {
                
                boolean hasPurchased = orderRepo.hasUserPurchasedProduct(currentUser.getId(), p.getProduct_id(), em); 
                
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
            req.getRequestDispatcher("/views/product/detail.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/products"); // Chuyển hướng về trang an toàn
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}