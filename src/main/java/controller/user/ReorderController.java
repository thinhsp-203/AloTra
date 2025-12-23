package controller.user;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CartItem;
import model.User;
import service.ReorderService;
import service.impl.ReorderServiceImpl;

@WebServlet(urlPatterns = "/user/reorder")
public class ReorderController extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ReorderService reorderService;
    
    @Override
    public void init() throws ServletException {
        reorderService = new ReorderServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        var session = req.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            session.setAttribute("redirectAfterLogin", req.getRequestURI() + "?" + req.getQueryString());
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        try {
            int orderId = Integer.parseInt(req.getParameter("orderId"));
            
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("CART");
            if (cart == null) {
                cart = new java.util.ArrayList<>();
                session.setAttribute("CART", cart);
            }
            
            var result = reorderService.reorder(currentUser.getId(), orderId, cart);
            
            int added = result.get("addedItems");
            int unavailable = result.get("unavailableItems");
            
            if (added > 0 && unavailable == 0) {
                session.setAttribute("orderSuccess", 
                    "Đã thêm " + added + " sản phẩm từ đơn hàng #" + orderId + " vào giỏ hàng!");
            } else if (added > 0 && unavailable > 0) {
                session.setAttribute("orderSuccess", 
                    "Đã thêm " + added + " sản phẩm. " + unavailable + " sản phẩm không còn khả dụng.");
            } else {
                session.setAttribute("orderError", 
                    "Không thể thêm sản phẩm. Tất cả sản phẩm trong đơn hàng đã hết hàng hoặc không còn bán.");
            }
            
            resp.sendRedirect(req.getContextPath() + "/checkout");
            
        } catch (IllegalArgumentException e) {
            session.setAttribute("orderError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/user/orders");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("orderError", "Không thể thêm lại sản phẩm. Vui lòng thử lại!");
            resp.sendRedirect(req.getContextPath() + "/user/orders");
        }
    }
}