package controller.user;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.CartItem;
import model.Orders;
import model.User;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
                        CartItem newItem = new CartItem();
                        newItem.setProductId(detail.getProduct().getProduct_id());
                        newItem.setProductName(detail.getProduct_name());
                        newItem.setThumbnail(detail.getProduct().getThumbnail());
                        newItem.setQuantity(detail.getQuantity());
                        newItem.setUnitPrice(detail.getPrice());
                        newItem.setToppingsCsv(detail.getToppings());
                        newItem.setSizeName(detail.getSize_name());
                        newItem.setSizeAdj(BigDecimal.ZERO); 
                        newItem.setToppingsCost(BigDecimal.ZERO);

                        int idx = cart.indexOf(newItem);
                        if (idx >= 0) {
                            cart.get(idx).setQuantity(cart.get(idx).getQuantity() + newItem.getQuantity());
                        } else {
                            cart.add(newItem);
                        }
                    }
                    session.setAttribute("CART", cart);
                    resp.sendRedirect(req.getContextPath() + "/cart/view");
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