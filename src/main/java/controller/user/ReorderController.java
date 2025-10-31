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

	                for (OrderDetail detail : order.getOrderDetails()) {
	                    Product p = detail.getProduct();
	                    if (p == null || !p.getIsActive()) continue;

	                    CartItem newItem = new CartItem();
	                    newItem.setProductId(p.getProduct_id());
	                    newItem.setProductName(p.getProduct_name());
	                    newItem.setThumbnail(p.getThumbnail());
	                    newItem.setQuantity(detail.getQuantity());
	                    newItem.setUnitPrice(p.getPrice());

	                    // FIX: Chuẩn hóa size name
	                    String sizeName = detail.getSize_name();
	                    if (sizeName == null || sizeName.isBlank()) {
	                        sizeName = "Mặc định";
	                    }
	                    newItem.setSizeName(sizeName);

	                    // FIX: Tính lại size adjustment từ DB
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
	                            // Nếu không tìm thấy, giữ = 0
	                        }
	                    }
	                    newItem.setSizeAdj(sizeAdj);

	                    // FIX: Chuẩn hóa toppings
	                    String toppings = detail.getToppings();
	                    if (toppings == null) {
	                        toppings = "";
	                    }
	                    newItem.setToppingsCsv(toppings);
	                    
	                    // FIX: Tính lại topping cost từ DB (nếu có)
	                    BigDecimal toppingsCost = BigDecimal.ZERO;
	                    if (!toppings.isEmpty()) {
	                        // Parse toppings string để lấy các topping IDs
	                        // Format: "Trân châu x2, Thạch dừa x1"
	                        // Tuy nhiên, detail chỉ lưu tên, không lưu ID
	                        // Nên ta chỉ có thể ước lượng hoặc set = 0
	                        // Để chính xác, cần thêm field topping_ids vào OrderDetail
	                    }
	                    newItem.setToppingsCost(toppingsCost);

	                    // Thêm vào cart hoặc merge
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
	                session.setAttribute("orderSuccess", "Đã thêm lại sản phẩm từ đơn hàng #" + orderId + " vào giỏ hàng!");
	                resp.sendRedirect(req.getContextPath() + "/checkout");
	            } else {
	                resp.sendRedirect(req.getContextPath() + "/user/orders");
	            }
	        } finally {
	            if (em.isOpen()) em.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        session.setAttribute("orderError", "Không thể thêm lại sản phẩm. Vui lòng thử lại!");
	        resp.sendRedirect(req.getContextPath() + "/user/orders");
	    }
	}
}