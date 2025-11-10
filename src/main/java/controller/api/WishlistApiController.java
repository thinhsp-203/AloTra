package controller.api;

import com.google.gson.Gson; // THÊM IMPORT NÀY
import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Product;
import model.User;
import model.WishlistItem;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/api/wishlist/toggle", "/api/wishlist/ids"})
public class WishlistApiController extends HttpServlet {
    
    // Sửa lỗi: Khởi tạo Gson
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/toggle")) {
            toggleWishlistItem(req, resp);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/ids")) {
            getWishlistIds(req, resp);
        }
    }

    private void toggleWishlistItem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        EntityManager em = JpaUtil.em();

        try {
            HttpSession session = req.getSession();
            User currentUser = (User) session.getAttribute("currentUser");

            if (currentUser == null) {
                resp.setStatus(401); // Unauthorized
                responseData.put("status", "error");
                responseData.put("message", "Bạn cần đăng nhập để thực hiện việc này.");
                resp.getWriter().write(gson.toJson(responseData)); // Sửa lỗi
                return;
            }

            int productId = Integer.parseInt(req.getParameter("productId"));
            Product product = em.find(Product.class, productId);
            if (product == null) {
                resp.setStatus(404); // Not Found
                responseData.put("status", "error");
                responseData.put("message", "Sản phẩm không tồn tại.");
                resp.getWriter().write(gson.toJson(responseData)); // Sửa lỗi
                return;
            }

            // Kiểm tra xem đã tồn tại chưa
            TypedQuery<WishlistItem> query = em.createQuery(
                "SELECT w FROM WishlistItem w WHERE w.user.id = :userId AND w.product.id = :productId",
                WishlistItem.class
            );
            query.setParameter("userId", currentUser.getId());
            query.setParameter("productId", productId);

            WishlistItem existingItem = null;
            try {
                existingItem = query.getSingleResult();
            } catch (NoResultException e) {
                // Không tìm thấy, không sao
            }

            em.getTransaction().begin();
            if (existingItem != null) {
                // Đã tồn tại -> Xóa
                em.remove(existingItem);
                responseData.put("status", "removed");
                responseData.put("message", "Đã xóa khỏi danh sách yêu thích.");
            } else {
                // Chưa tồn tại -> Thêm mới
                WishlistItem newItem = new WishlistItem();
                newItem.setUser(currentUser);
                newItem.setProduct(product);
                newItem.setAddedDate(LocalDateTime.now());
                em.persist(newItem);
                responseData.put("status", "added");
                responseData.put("message", "Đã thêm vào danh sách yêu thích.");
            }
            em.getTransaction().commit();
            resp.setStatus(200);

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            resp.setStatus(500); // Internal Server Error
            responseData.put("status", "error");
            responseData.put("message", "Lỗi máy chủ: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            resp.getWriter().write(gson.toJson(responseData)); // Sửa lỗi
        }
    }
    
    private void getWishlistIds(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        Map<String, Object> responseData = new HashMap<>();
        EntityManager em = JpaUtil.em();
        
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            responseData.put("wishlistIds", Set.of()); // Trả về set rỗng
            resp.getWriter().write(gson.toJson(responseData)); // Sửa lỗi
            return;
        }

        try {
            User currentUser = (User) session.getAttribute("currentUser");
            TypedQuery<Integer> query = em.createQuery(
                "SELECT w.product.id FROM WishlistItem w WHERE w.user.id = :userId", Integer.class
            );
            query.setParameter("userId", currentUser.getId());
            Set<Integer> wishlistIds = query.getResultStream().collect(Collectors.toSet());
            
            responseData.put("wishlistIds", wishlistIds);
            resp.getWriter().write(gson.toJson(responseData)); // Sửa lỗi
            
        } catch (Exception e) {
             resp.setStatus(500);
             responseData.put("error", e.getMessage());
             resp.getWriter().write(gson.toJson(responseData)); // Sửa lỗi
        } finally {
            em.close();
        }
    }
}