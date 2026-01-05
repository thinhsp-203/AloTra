package stnw.controller.cart;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import stnw.model.CartItem;
import stnw.service.CartService;
import stnw.service.impl.CartServiceImpl;

import java.io.IOException;
import java.util.*;


@WebServlet(urlPatterns = {"/cart", "/cart/*"})
public class CartController extends HttpServlet {

    private static final long serialVersionUID = 1L;
	private CartService cartService;
    
    @Override
    public void init() throws ServletException {
        cartService = new CartServiceImpl();
    }
    
    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        var list = (List<CartItem>) session.getAttribute("CART");
        if (list == null) {
            list = new ArrayList<>();
            session.setAttribute("CART", list);
        }
        return list;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null || "/".equals(path) || "/view".equals(path)) {
            resp.sendRedirect(req.getContextPath() + "/checkout");
        } else {
            resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String path = req.getPathInfo();
        
        if ("/add".equals(path)) {
            handleAdd(req, resp);
        } else if ("/update".equals(path)) {
            handleUpdate(req, resp);
        } else if ("/remove".equals(path)) {
            handleRemove(req, resp);
        } else if ("/update-item".equals(path)) {
            handleUpdateItemDetails(req, resp);
        } else {
            resp.sendError(404);
        }
    }

    // ==================== PRIVATE METHODS ====================

    private void handleAdd(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        HttpSession session = req.getSession();
        
        // Kiểm tra đăng nhập
        if (session.getAttribute("currentUser") == null) {
            resp.getWriter().print("{\"ok\":false,\"redirect\":\"" + 
                req.getContextPath() + "/login\"}");
            return;
        }

        try {
            // Lấy tham s�?
            int productId = Integer.parseInt(req.getParameter("productId"));
            int quantity = Integer.parseInt(req.getParameter("quantity"));
            String sizeName = req.getParameter("size");
            String sugarLevel = req.getParameter("sweetness"); // Đ�?ngọt
            String iceLevel = req.getParameter("ice"); // Mức đá
            String toppingParam = req.getParameter("topping");
            
            // Gọi service x�?lý
            List<CartItem> cart = getCart(session);
            CartItem resultItem = cartService.addToCart(cart, productId, quantity, 
                                                       sizeName, sugarLevel, iceLevel, toppingParam);
            
            // Tr�?v�?JSON
            String itemJson = String.format(
                "{\"productId\":%d,\"productName\":\"%s\",\"thumbnail\":\"%s\",\"lineTotal\":%s}",
                resultItem.getProductId(), 
                escapeJson(resultItem.getProductName()), 
                escapeJson(resultItem.getThumbnail()), 
                resultItem.getLineTotal()
            );
            
            resp.getWriter().print("{\"ok\":true,\"cartSize\":" + cart.size() + 
                ",\"newItem\":" + itemJson + "}");

        } catch (IllegalArgumentException e) {
            resp.getWriter().print("{\"ok\":false,\"message\":\"" + 
                escapeJson(e.getMessage()) + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"ok\":false,\"message\":\"Có lỗi xảy ra: " + 
                escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            String size = req.getParameter("size");
            String sugarLevel = req.getParameter("sugarLevel");
            String iceLevel = req.getParameter("iceLevel");
            String toppings = req.getParameter("toppings");
            int newQuantity = Integer.parseInt(req.getParameter("quantity"));
            
            List<CartItem> cart = getCart(req.getSession());
            cartService.updateQuantity(cart, productId, size, sugarLevel, iceLevel, toppings, newQuantity);
            
            resp.getWriter().print("{\"ok\":true}");
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"ok\":false,\"message\":\"" + 
                escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleRemove(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        try {
            int productId = Integer.parseInt(req.getParameter("productId"));
            String size = req.getParameter("size");
            String sugarLevel = req.getParameter("sugarLevel");
            String iceLevel = req.getParameter("iceLevel");
            String toppings = req.getParameter("toppings");
            
            List<CartItem> cart = getCart(req.getSession());
            cartService.removeItem(cart, productId, size, sugarLevel, iceLevel, toppings);
            
            resp.sendRedirect(req.getContextPath() + "/checkout");
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/checkout?error=remove_failed");
        }
    }

    private void handleUpdateItemDetails(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        try {
            int oldProductId = Integer.parseInt(req.getParameter("oldProductId"));
            String oldSize = req.getParameter("oldSize");
            String oldSugarLevel = req.getParameter("oldSugarLevel");
            String oldIceLevel = req.getParameter("oldIceLevel");
            String oldToppingsCsv = req.getParameter("oldToppingsCsv");
            
            String newSize = req.getParameter("newSize");
            String newSugarLevel = req.getParameter("newSugarLevel");
            String newIceLevel = req.getParameter("newIceLevel");
            String newToppingParam = req.getParameter("newToppings");
            int quantity = Integer.parseInt(req.getParameter("quantity"));
            
            List<CartItem> cart = getCart(req.getSession());
            boolean success = cartService.updateItemDetails(cart, oldProductId, 
                oldSize, oldSugarLevel, oldIceLevel, oldToppingsCsv, 
                newSize, newSugarLevel, newIceLevel, newToppingParam, quantity);
            
            if (success) {
                resp.getWriter().print("{\"ok\":true, \"message\":\"Cập nhật gi�?hàng thành công!\"}");
            } else {
                resp.getWriter().print("{\"ok\":false,\"message\":\"Sản phẩm không tồn tại\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"ok\":false,\"message\":\"Lỗi máy ch�? " + 
                e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}