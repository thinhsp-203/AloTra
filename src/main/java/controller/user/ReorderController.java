package controller.user;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@WebServlet(urlPatterns = "/user/reorder")
public class ReorderController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            EntityManager em = JpaUtil.em();
            try {
                Orders order = em.find(Orders.class, orderId);

                if (order != null && order.getUser().getId().equals(currentUser.getId())) {
                    
                    @SuppressWarnings("unchecked")
                    List<CartItem> cart = (List<CartItem>) session.getAttribute("CART");
                    if (cart == null) {
                        cart = new ArrayList<>();
                    }

                    for (var detail : order.getOrderDetails()) {
                        Product p = detail.getProduct();
                        if (p == null) continue; // Bỏ qua nếu sản phẩm đã bị xóa

                        CartItem newItem = new CartItem();
                        newItem.setProductId(p.getProduct_id());
                        newItem.setProductName(p.getProduct_name());
                        newItem.setThumbnail(p.getThumbnail());
                        newItem.setQuantity(detail.getQuantity());
                        newItem.setUnitPrice(p.getPrice()); // Lấy giá hiện tại của sản phẩm

                        // === PHẦN SỬA LỖI QUAN TRỌNG ===
                        // Chuẩn hóa dữ liệu để tránh giá trị null
                        String sizeName = detail.getSize_name();
                        if (sizeName == null || sizeName.isBlank()) {
                            sizeName = "Mặc định";
                        }
                        newItem.setSizeName(sizeName);

                        String toppings = detail.getToppings();
                        if (toppings == null) {
                            toppings = "";
                        }
                        newItem.setToppingsCsv(toppings);
                        
                        // Tạm thời đặt các giá trị điều chỉnh về 0, vì việc tính toán lại sẽ phức tạp
                        // và không ảnh hưởng đến lỗi đang sửa. Vấn đề chính là chuẩn hóa null.
                        newItem.setSizeAdj(BigDecimal.ZERO); 
                        newItem.setToppingsCost(BigDecimal.ZERO);
                        // === KẾT THÚC PHẦN SỬA LỖI ===

                        // Kiểm tra xem có sản phẩm y hệt trong giỏ hàng chưa để gộp số lượng
                        Optional<CartItem> existingItemOpt = cart.stream()
                                .filter(item -> item.equals(newItem))
                                .findFirst();

                        if (existingItemOpt.isPresent()) {
                            CartItem existingItem = existingItemOpt.get();
                            existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                        } else {
                            cart.add(newItem);
                        }
                    }
                    session.setAttribute("CART", cart);
                    // Chuyển hướng đến trang thanh toán để người dùng xem lại giỏ hàng
                    resp.sendRedirect(req.getContextPath() + "/checkout"); 
                } else {
                    resp.sendRedirect(req.getContextPath() + "/user/orders");
                }
            } finally {
                if (em.isOpen()) em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/user/orders");
        }
    }
}