package controller.cart;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*; 
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.*;

@WebServlet(urlPatterns = {"/cart", "/cart/*"})
public class CartController extends HttpServlet {
  @SuppressWarnings("unchecked")
  private List<CartItem> cart(HttpSession session){
    var list = (List<CartItem>) session.getAttribute("CART");
    if (list == null){ list = new ArrayList<>(); session.setAttribute("CART", list); }
    return list;
  }

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getPathInfo();
    if (path == null || "/".equals(path) || "/view".equals(path)){
      req.getRequestDispatcher("/views/cart/index.jsp").forward(req, resp);
    } else {
      resp.sendError(404);
    }
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String path = req.getPathInfo();
    if ("/add".equals(path))       add(req, resp);
    else if ("/remove".equals(path)) remove(req, resp);
    else resp.sendError(404);
  }

  private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      int pid = Integer.parseInt(req.getParameter("productId"));
      int qty = 1; // Mặc định thêm 1 sản phẩm mỗi lần click

      EntityManager em = JpaUtil.em();
      try {
        Product p = em.find(Product.class, pid);
        if (p == null){ 
          resp.setContentType("application/json; charset=UTF-8");
          resp.getWriter().print("{\"ok\":false,\"message\":\"Sản phẩm không tồn tại\"}"); 
          return; 
        }

        CartItem ci = new CartItem();
        ci.setProductId(pid);
        ci.setProductName(p.getProduct_name());
        ci.setThumbnail(p.getThumbnail());
        ci.setQuantity(qty);
        ci.setUnitPrice(p.getPrice());
        ci.setSizeAdj(BigDecimal.ZERO);
        ci.setToppingsCost(BigDecimal.ZERO);

        var list = cart(req.getSession());
        int idx = list.indexOf(ci);
        if (idx >= 0){
          list.get(idx).setQuantity(list.get(idx).getQuantity() + qty);
        } else {
          list.add(ci);
        }

        resp.setContentType("application/json; charset=UTF-8");
        // HOÀN CHỈNH: Trả về số lượng item trong giỏ
        resp.getWriter().print("{\"ok\":true,\"cartSize\":" + list.size() + "}");
      } finally { em.close(); }
    } catch (Exception e) {
      e.printStackTrace();
      resp.setContentType("application/json; charset=UTF-8");
      resp.getWriter().print("{\"ok\":false,\"message\":\"Có lỗi xảy ra: " + e.getMessage() + "\"}");
    }
  }

  private void remove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int pid = Integer.parseInt(req.getParameter("productId"));
    String size = req.getParameter("size");
    String toppings = req.getParameter("toppings");

    CartItem key = new CartItem();
    key.setProductId(pid); 
    key.setSizeName(size); 
    key.setToppingsCsv(toppings);

    cart(req.getSession()).remove(key);
    
    // Chuyển hướng về trang giỏ hàng sau khi xóa
    resp.sendRedirect(req.getContextPath() + "/cart/view");
  }
}