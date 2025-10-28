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
    String path = req.getPathInfo(); // /view
    if (path == null || "/".equals(path) || "/view".equals(path)){
      req.getRequestDispatcher("/views/cart/index.jsp").forward(req, resp);
    } else {
      resp.sendError(404);
    }
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    String path = req.getPathInfo();
    if ("/add".equals(path))       add(req, resp);
    else if ("/update".equals(path)) update(req, resp);
    else if ("/remove".equals(path)) remove(req, resp);
    else resp.sendError(404);
  }

  private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	    try {
	      int pid = Integer.parseInt(req.getParameter("productId"));
	      String size = trim(req.getParameter("size"));
	      String toppingsCsv = trim(req.getParameter("toppings")); // "1,3,5"
	      int qty = parseInt(req.getParameter("qty"), 1);

	      EntityManager em = JpaUtil.em();
	      try {
	        Product p = em.find(Product.class, pid);
	        if (p == null){ 
	          resp.setContentType("application/json; charset=UTF-8");
	          resp.getWriter().print("{\"ok\":false,\"message\":\"Sản phẩm không tồn tại\"}"); 
	          return; 
	        }

	        BigDecimal unit = p.getPrice()!=null? p.getPrice() : BigDecimal.ZERO;

	        BigDecimal sizeAdj = BigDecimal.ZERO;
	        if (size != null && !size.isBlank()){
	          var sizes = em.createQuery("select s from ProductSize s where s.product.product_id=:id and s.size_name=:name", ProductSize.class)
	                        .setParameter("id", pid).setParameter("name", size).setMaxResults(1).getResultList();
	          if(!sizes.isEmpty() && sizes.get(0).getPrice_adjustment()!=null) sizeAdj = sizes.get(0).getPrice_adjustment();
	        }

	        BigDecimal topCost = BigDecimal.ZERO;
	        if (toppingsCsv != null && !toppingsCsv.isBlank()){
	          for (String tidStr : toppingsCsv.split(",")){
	            try{
	              int tid = Integer.parseInt(tidStr.trim());
	              Topping t = em.find(Topping.class, tid);
	              if (t != null && t.getIsAvailable()!=null && t.getIsAvailable() && t.getPrice()!=null){
	                topCost = topCost.add(t.getPrice());
	              }
	            }catch(Exception ignore){}
	          }
	        }

	        CartItem ci = new CartItem();
	        ci.setProductId(pid);
	        ci.setProductName(p.getProduct_name());
	        ci.setSizeName(size);
	        ci.setToppingsCsv(toppingsCsv);
	        ci.setQuantity(qty);
	        ci.setUnitPrice(unit);
	        ci.setSizeAdj(sizeAdj);
	        ci.setToppingsCost(topCost);

	        var list = cart(req.getSession());
	        int idx = list.indexOf(ci);
	        if (idx >= 0){
	          // cùng key (productId+size+toppings) → cộng dồn
	          list.get(idx).setQuantity(list.get(idx).getQuantity() + qty);
	        } else {
	          list.add(ci);
	        }

	        resp.setContentType("application/json; charset=UTF-8");
	        resp.getWriter().print("{\"ok\":true,\"cartSize\":" + list.size() + "}");
	      } finally { em.close(); }
	    } catch (Exception e) {
	      e.printStackTrace();
	      resp.setContentType("application/json; charset=UTF-8");
	      resp.getWriter().print("{\"ok\":false,\"message\":\"Có lỗi xảy ra: " + e.getMessage() + "\"}");
	    }
	  }

  private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int pid = Integer.parseInt(req.getParameter("productId"));
    String size = trim(req.getParameter("size"));
    String toppings = trim(req.getParameter("toppings"));
    int qty = parseInt(req.getParameter("qty"), 1);

    CartItem key = new CartItem();
    key.setProductId(pid); key.setSizeName(size); key.setToppingsCsv(toppings);

    var list = cart(req.getSession());
    int idx = list.indexOf(key);
    if (idx >= 0){
      list.get(idx).setQuantity(Math.max(1, qty));
    }
    resp.setContentType("application/json; charset=UTF-8");
    resp.getWriter().print("{\"ok\":true}");
  }

  private void remove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    int pid = Integer.parseInt(req.getParameter("productId"));
    String size = trim(req.getParameter("size"));
    String toppings = trim(req.getParameter("toppings"));

    CartItem key = new CartItem();
    key.setProductId(pid); key.setSizeName(size); key.setToppingsCsv(toppings);

    var list = cart(req.getSession());
    list.remove(key);
    resp.setContentType("application/json; charset=UTF-8");
    resp.getWriter().print("{\"ok\":true}");
  }

  private static int parseInt(String s, int d){ try{return Integer.parseInt(s);}catch(Exception e){return d;} }
  private static String trim(String s){ return s==null?null:s.trim(); }
}
