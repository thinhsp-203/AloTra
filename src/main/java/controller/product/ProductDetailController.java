package controller.product;

import config.JpaUtil;
import dao.jpa.OrderRepository;
import dao.jpa.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;
import model.Review;
import model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
            // Lấy ID sản phẩm từ URL
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/products");
                return;
            }

            int id = Integer.parseInt(idParam);
            em = JpaUtil.em();

            // 1. Lấy thông tin sản phẩm
            Product p = productRepo.findById(id, em);
            if (p == null) {
                resp.sendRedirect(req.getContextPath() + "/products");
                return;
            }
            req.setAttribute("p", p);

            // 2. Lấy sản phẩm liên quan (Cùng danh mục)
            List<Product> relatedProducts = productRepo.findRelatedProducts(p.getCategory().getId(), p.getProduct_id(), em);
            req.setAttribute("relatedProducts", relatedProducts);

            // 3. Lấy danh sách đánh giá (Review)
            TypedQuery<Review> reviewQuery = em.createQuery(
                "SELECT r FROM Review r WHERE r.product.product_id = :pid AND r.isApproved = true ORDER BY r.createdDate DESC",
                Review.class
            );
            reviewQuery.setParameter("pid", p.getProduct_id());
            List<Review> reviews = reviewQuery.getResultList();
            req.setAttribute("reviews", reviews);

            // 4. Tính điểm đánh giá trung bình
            if (p.getRating() != null && p.getRating().doubleValue() > 0) {
                req.setAttribute("avgRating", p.getRating().doubleValue());
                req.setAttribute("totalReviews", reviews.size());
            }

            // 5. Kiểm tra quyền đánh giá của User (Đã mua & chưa review)
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

            // 6. XỬ LÝ SẢN PHẨM ĐÃ XEM (RECENTLY VIEWED)
            handleRecentlyViewed(req, resp, p, em);

            // Forward sang View
            req.getRequestDispatcher("/views/product/detail.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/products");
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Logic xử lý Cookie lưu danh sách ID sản phẩm đã xem
     */
    private void handleRecentlyViewed(HttpServletRequest req, HttpServletResponse resp, Product currentProduct, EntityManager em) {
        String cookieName = "viewedProducts";
        String separator = "-";
        String newValue = String.valueOf(currentProduct.getProduct_id());
        String oldValue = "";
        
        // Lấy cookie cũ nếu có
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (cookieName.equals(c.getName())) {
                    oldValue = c.getValue();
                    break;
                }
            }
        }

        // Xử lý chuỗi ID (Ví dụ: "1-5-9")
        List<String> ids = new ArrayList<>();
        if (!oldValue.isEmpty()) {
            ids.addAll(Arrays.asList(oldValue.split(separator)));
        }

        // Xóa ID hiện tại nếu đã tồn tại (để đưa lên đầu danh sách)
        ids.remove(newValue);
        // Thêm ID hiện tại vào đầu danh sách
        ids.add(0, newValue);

        // Giới hạn chỉ lưu 6 sản phẩm gần nhất
        if (ids.size() > 6) {
            ids = ids.subList(0, 6);
        }

        // Tạo chuỗi mới và lưu vào Cookie
        String finalValue = String.join(separator, ids);
        Cookie newCookie = new Cookie(cookieName, finalValue);
        newCookie.setMaxAge(60 * 60 * 24 * 30); // Lưu 30 ngày
        newCookie.setPath("/"); // Có hiệu lực trên toàn website
        resp.addCookie(newCookie);

        // Lấy danh sách Entity sản phẩm từ DB để hiển thị
        // Loại bỏ sản phẩm đang xem khỏi danh sách "Đã xem" để tránh trùng lặp trên giao diện
        List<String> displayIds = new ArrayList<>(ids);
        displayIds.remove(newValue); 

        if (!displayIds.isEmpty()) {
            try {
                // Convert String ID sang Integer
                List<Integer> intIds = new ArrayList<>();
                for (String s : displayIds) {
                    try {
                        intIds.add(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        // Bỏ qua ID lỗi
                    }
                }

                if (!intIds.isEmpty()) {
                    TypedQuery<Product> query = em.createQuery(
                        "SELECT p FROM Product p WHERE p.product_id IN :ids", Product.class);
                    query.setParameter("ids", intIds);
                    List<Product> viewedProducts = query.getResultList();
                    
                    // Lưu vào request attribute để hiển thị bên JSP
                    req.setAttribute("viewedProducts", viewedProducts);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}