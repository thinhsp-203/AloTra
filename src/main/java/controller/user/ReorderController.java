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
                        if (p == null) continue; 

                        CartItem newItem = new CartItem();
                        newItem.setProductId(p.getProduct_id());
                        newItem.setProductName(p.getProduct_name());
                        newItem.setThumbnail(p.getThumbnail());
                        newItem.setQuantity(detail.getQuantity());
                        newItem.setUnitPrice(p.getPrice());

                        // === PHẦN SỬA LỖI QUAN TRỌNG: CHUẨN HÓA DỮ LIỆU ===
                        String sizeName = detail.getSize_name();
                        newItem.setSizeName((sizeName == null || sizeName.isBlank()) ? "Mặc định" : sizeName);

                        String toppings = detail.getToppings();
                        newItem.setToppingsCsv((toppings == null) ? "" : toppings);
                        
                        // Các giá trị này sẽ được tính lại khi cần, tạm thời đặt là 0
                        newItem.setSizeAdj(BigDecimal.ZERO); 
                        newItem.setToppingsCost(BigDecimal.ZERO);
                        // === KẾT THÚC PHẦN SỬA LỖI ===

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