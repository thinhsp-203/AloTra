package controller.cart;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.*;

@WebServlet(urlPatterns = {"/cart", "/cart/*"})
public class CartController extends HttpServlet {
    @SuppressWarnings("unchecked")
    private List<CartItem> cart(HttpSession session) {
        var list = (List<CartItem>) session.getAttribute("CART");
        if (list == null) {
            list = new ArrayList<>();
            session.setAttribute("CART", list);
        }
        return list;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null || "/".equals(path) || "/view".equals(path)) {
            req.getRequestDispatcher("/views/cart/index.jsp").forward(req, resp);
        } else {
            resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String path = req.getPathInfo();
        if ("/add".equals(path)) add(req, resp);
        else if ("/remove".equals(path)) remove(req, resp);
        else resp.sendError(404);
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        if (session.getAttribute("currentUser") == null) {
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().print("{\"ok\":false,\"redirect\":\"" + req.getContextPath() + "/login\"}");
            return;
        }

        try {
            int pid = Integer.parseInt(req.getParameter("productId"));
            int qty = 1;
            String[] toppingIdsParam = req.getParameterValues("topping");

            EntityManager em = JpaUtil.em();
            try {
                Product p = em.find(Product.class, pid);
                if (p == null) {
                    resp.setContentType("application/json; charset=UTF-8");
                    resp.getWriter().print("{\"ok\":false,\"message\":\"Sản phẩm không tồn tại\"}");
                    return;
                }

                BigDecimal toppingsCost = BigDecimal.ZERO;
                String toppingsCsv = "";
                if (toppingIdsParam != null && toppingIdsParam.length > 0) {
                    List<Integer> ids = Arrays.stream(toppingIdsParam)
                                              .map(Integer::parseInt)
                                              .sorted()
                                              .collect(Collectors.toList());
                    
                    List<Topping> selectedToppings = em.createQuery("SELECT t FROM Topping t WHERE t.topping_id IN :ids", Topping.class)
                                                       .setParameter("ids", ids)
                                                       .getResultList();

                    toppingsCost = selectedToppings.stream().map(Topping::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                    toppingsCsv = selectedToppings.stream().map(Topping::getTopping_name).collect(Collectors.joining(", "));
                }

                CartItem ci = new CartItem();
                ci.setProductId(pid);
                ci.setProductName(p.getProduct_name());
                ci.setThumbnail(p.getThumbnail());
                ci.setQuantity(qty);
                ci.setUnitPrice(p.getPrice());
                ci.setSizeAdj(BigDecimal.ZERO);
                ci.setToppingsCost(toppingsCost);
                ci.setToppingsCsv(toppingsCsv);

                var list = cart(session);
                int idx = list.indexOf(ci);
                if (idx >= 0) {
                    CartItem existingItem = list.get(idx);
                    existingItem.setQuantity(existingItem.getQuantity() + qty);
                    ci = existingItem;
                } else {
                    list.add(ci);
                }

                resp.setContentType("application/json; charset=UTF-8");
                String newItemJson = String.format("{\"productId\":%d,\"productName\":\"%s\",\"thumbnail\":\"%s\",\"lineTotal\":%s}",
                                                   ci.getProductId(), escapeJson(ci.getProductName()), escapeJson(ci.getThumbnail()), ci.getLineTotal());
                resp.getWriter().print("{\"ok\":true,\"cartSize\":" + list.size() + ",\"newItem\":" + newItemJson + "}");

            } finally {
                if (em.isOpen()) em.close();
            }
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
        resp.sendRedirect(req.getContextPath() + "/cart/view");
    }
}