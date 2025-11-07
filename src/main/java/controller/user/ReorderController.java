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
            session.setAttribute("redirectAfterLogin", req.getRequestURI() + "?" + req.getQueryString());
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            EntityManager em = JpaUtil.em();
            try {
                Orders order = em.find(Orders.class, orderId);

                if (order == null || !order.getUser().getId().equals(currentUser.getId())) {
                    session.setAttribute("orderError", "Đơn hàng không tồn tại hoặc không thuộc về bạn!");
                    resp.sendRedirect(req.getContextPath() + "/user/orders");
                    return;
                }

                @SuppressWarnings("unchecked")
                List<CartItem> cart = (List<CartItem>) session.getAttribute("CART");
                if (cart == null) {
                    cart = new ArrayList<>();
                }

                int addedItems = 0;
                int unavailableItems = 0;

                for (OrderDetail detail : order.getOrderDetails()) {
                    Product p = detail.getProduct();
                    
                    // Check if product exists and is active
                    if (p == null || p.getIsActive() == null || !p.getIsActive()) {
                        unavailableItems++;
                        continue;
                    }

                    // Check stock availability
                    if (p.getStock() == null || p.getStock() < detail.getQuantity()) {
                        unavailableItems++;
                        continue;
                    }

                    CartItem newItem = new CartItem();
                    newItem.setProductId(p.getProduct_id());
                    newItem.setProductName(p.getProduct_name());
                    newItem.setThumbnail(p.getThumbnail());
                    newItem.setQuantity(detail.getQuantity());
                    newItem.setUnitPrice(p.getPrice()); // Use current price

                    // Handle size
                    String sizeName = detail.getSize_name();
                    if (sizeName == null || sizeName.isBlank()) {
                        sizeName = "Mặc định";
                    }
                    newItem.setSizeName(sizeName);

                    // Calculate size adjustment from current DB data
                    BigDecimal sizeAdj = BigDecimal.ZERO;
                    if (!"Mặc định".equals(sizeName)) {
                        try {
                            ProductSize ps = em.createQuery(
                                "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sname",
                                ProductSize.class)
                                .setParameter("pid", p.getProduct_id())
                                .setParameter("sname", sizeName)
                                .getSingleResult();
                            sizeAdj = ps.getPrice_adjustment();
                        } catch (Exception e) {
                            // If size not found, keep default
                            sizeName = "Mặc định";
                            newItem.setSizeName(sizeName);
                        }
                    }
                    newItem.setSizeAdj(sizeAdj);

                    // Handle toppings
                    String toppings = detail.getToppings();
                    if (toppings == null) {
                        toppings = "";
                    }
                    newItem.setToppingsCsv(toppings);
                    
                    // Calculate topping cost (simplified - just set to 0 since we don't store topping IDs)
                    // For accurate reorder, should save topping_ids in OrderDetail
                    newItem.setToppingsCost(BigDecimal.ZERO);

                    // Add to cart or merge with existing item
                    Optional<CartItem> existingItemOpt = cart.stream()
                            .filter(item -> item.equals(newItem))
                            .findFirst();

                    if (existingItemOpt.isPresent()) {
                        CartItem existingItem = existingItemOpt.get();
                        existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                    } else {
                        cart.add(newItem);
                    }
                    
                    addedItems++;
                }
                
                session.setAttribute("CART", cart);
                
                // Set appropriate message
                if (addedItems > 0 && unavailableItems == 0) {
                    session.setAttribute("orderSuccess", 
                        "Đã thêm " + addedItems + " sản phẩm từ đơn hàng #" + orderId + " vào giỏ hàng!");
                } else if (addedItems > 0 && unavailableItems > 0) {
                    session.setAttribute("orderSuccess", 
                        "Đã thêm " + addedItems + " sản phẩm. " + unavailableItems + " sản phẩm không còn khả dụng.");
                } else {
                    session.setAttribute("orderError", 
                        "Không thể thêm sản phẩm. Tất cả sản phẩm trong đơn hàng đã hết hàng hoặc không còn bán.");
                }
                
                resp.sendRedirect(req.getContextPath() + "/checkout");
                
            } finally {
                if (em.isOpen()) em.close();
            }
        } catch (NumberFormatException e) {
            session.setAttribute("orderError", "Mã đơn hàng không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/user/orders");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("orderError", "Không thể thêm lại sản phẩm. Vui lòng thử lại!");
            resp.sendRedirect(req.getContextPath() + "/user/orders");
        }
    }
}