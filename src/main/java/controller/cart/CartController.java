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
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Optional;

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
            add(req, resp);
        } else if ("/update".equals(path)) {
            update(req, resp);
        } else if ("/remove".equals(path)) {
            remove(req, resp);
        } else if ("/update-item".equals(path)) {
            updateItemDetails(req, resp);
        } else {
            resp.sendError(404);
        }
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
            String sizeName = req.getParameter("size");
            String toppingParam = req.getParameter("topping");

            EntityManager em = JpaUtil.em();
            try {
                Product p = em.find(Product.class, pid);
                if (p == null) {
                    resp.getWriter().print("{\"ok\":false,\"message\":\"Sản phẩm không tồn tại\"}");
                    return;
                }

                BigDecimal sizeAdjustment = BigDecimal.ZERO;
                if (sizeName != null && !sizeName.isBlank()) {
                    try {
                        ProductSize productSize = em.createQuery("SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sname", ProductSize.class)
                                .setParameter("pid", pid).setParameter("sname", sizeName).getSingleResult();
                        sizeAdjustment = productSize.getPrice_adjustment();
                    } catch (Exception e) {
                        sizeName = "Mặc định";
                    }
                } else {
                    sizeName = "Mặc định";
                }

                BigDecimal toppingsCost = BigDecimal.ZERO;
                String toppingsCsv = "";
                if (toppingParam != null && !toppingParam.isBlank()) {
                    Map<Integer, Integer> toppingQuantities = new LinkedHashMap<>();
                    for (String entry : toppingParam.split(",")) {
                        String[] parts = entry.split(":");
                        if (parts.length == 2) toppingQuantities.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                    }
                    if (!toppingQuantities.isEmpty()) {
                        List<Topping> selectedToppings = em.createQuery("SELECT t FROM Topping t WHERE t.topping_id IN :ids", Topping.class)
                                .setParameter("ids", toppingQuantities.keySet()).getResultList();
                        StringBuilder csvBuilder = new StringBuilder();
                        for (Topping t : selectedToppings) {
                            int toppingQty = toppingQuantities.getOrDefault(t.getTopping_id(), 0);
                            if (toppingQty > 0) {
                                toppingsCost = toppingsCost.add(t.getPrice().multiply(BigDecimal.valueOf(toppingQty)));
                                if (csvBuilder.length() > 0) csvBuilder.append(", ");
                                csvBuilder.append(t.getTopping_name());
                                if (toppingQty > 1) csvBuilder.append(" x").append(toppingQty);
                            }
                        }
                        toppingsCsv = csvBuilder.toString();
                    }
                }

                CartItem ci = new CartItem();
                ci.setProductId(pid);
                ci.setProductName(p.getProduct_name());
                ci.setThumbnail(p.getThumbnail());
                ci.setQuantity(qty);
                ci.setUnitPrice(p.getPrice());
                ci.setSizeName(sizeName);
                ci.setSizeAdj(sizeAdjustment);
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
            resp.getWriter().print("{\"ok\":false,\"message\":\"Có lỗi xảy ra: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            int pid = Integer.parseInt(req.getParameter("productId"));
            String size = req.getParameter("size");
            String toppings = req.getParameter("toppings");
            int newQty = Integer.parseInt(req.getParameter("quantity"));
            if (newQty < 0) {
                resp.getWriter().print("{\"ok\":false,\"message\":\"Số lượng không hợp lệ\"}");
                return;
            }
            List<CartItem> items = cart(req.getSession());
            CartItem key = new CartItem();
            key.setProductId(pid);
            key.setSizeName(size);
            key.setToppingsCsv(toppings);
            int idx = items.indexOf(key);
            if (idx >= 0) {
                if (newQty == 0) {
                    items.remove(idx);
                    resp.getWriter().print("{\"ok\":true,\"removed\":true,\"cartSize\":" + items.size() + "}");
                } else {
                    CartItem item = items.get(idx);
                    item.setQuantity(newQty);
                    BigDecimal lineTotal = item.getLineTotal();
                    BigDecimal cartTotal = items.stream().map(CartItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
                    resp.getWriter().print(String.format("{\"ok\":true,\"quantity\":%d,\"lineTotal\":%s,\"cartTotal\":%s,\"cartSize\":%d}", newQty, lineTotal, cartTotal, items.size()));
                }
            } else {
                resp.getWriter().print("{\"ok\":false,\"message\":\"Không tìm thấy sản phẩm trong giỏ\"}");
            }
        } catch (Exception e) {
            resp.getWriter().print("{\"ok\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void remove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int pid = Integer.parseInt(req.getParameter("productId"));
            String size = req.getParameter("size");
            String toppings = req.getParameter("toppings");

            final String finalSize = (size == null || "undefined".equalsIgnoreCase(size) || size.isBlank()) ? "Mặc định" : size;
            final String finalToppings = (toppings == null || "undefined".equalsIgnoreCase(toppings)) ? "" : toppings;

            List<CartItem> cart = cart(req.getSession());
            
            cart.removeIf(item -> 
                item.getProductId().equals(pid) &&
                Objects.equals(item.getSizeName(), finalSize) &&
                Objects.equals(item.getToppingsCsv(), finalToppings)
            );
            
            resp.sendRedirect(req.getContextPath() + "/checkout");
        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/checkout?error=remove_failed");
        }
    }

    private void updateItemDetails(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        HttpSession session = req.getSession();
        try {
            int oldPid = Integer.parseInt(req.getParameter("oldProductId"));
            String oldSize = req.getParameter("oldSize");
            String oldToppingsCsv = req.getParameter("oldToppingsCsv");
            String newSize = req.getParameter("newSize");
            String newToppingParam = req.getParameter("newToppings");
            int quantity = Integer.parseInt(req.getParameter("quantity"));

            final String finalOldSize = (oldSize == null || "undefined".equalsIgnoreCase(oldSize) || oldSize.isBlank()) ? "Mặc định" : oldSize;
            final String finalOldToppingsCsv = (oldToppingsCsv == null || "undefined".equalsIgnoreCase(oldToppingsCsv)) ? "" : oldToppingsCsv;

            EntityManager em = JpaUtil.em();
            try {
                Product p = em.find(Product.class, oldPid);
                if (p == null) {
                    resp.getWriter().print("{\"ok\":false,\"message\":\"Sản phẩm không tồn tại\"}");
                    return;
                }

                BigDecimal sizeAdjustment = BigDecimal.ZERO;
                String finalNewSize = (newSize == null || newSize.isBlank()) ? "Mặc định" : newSize;
                if (!"Mặc định".equals(finalNewSize)) {
                    try {
                        ProductSize ps = em.createQuery("SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sname", ProductSize.class)
                                .setParameter("pid", oldPid).setParameter("sname", finalNewSize).getSingleResult();
                        sizeAdjustment = ps.getPrice_adjustment();
                    } catch (Exception e) { /* Ignore */ }
                }

                BigDecimal toppingsCost = BigDecimal.ZERO;
                String newToppingsCsv = "";
                if (newToppingParam != null && !newToppingParam.isBlank()) {
                    Map<Integer, Integer> toppingQuantities = new LinkedHashMap<>();
                     for (String entry : newToppingParam.split(",")) {
                        String[] parts = entry.split(":");
                        if (parts.length == 2) toppingQuantities.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                    }
                    if (!toppingQuantities.isEmpty()) {
                        List<Topping> selectedToppings = em.createQuery("SELECT t FROM Topping t WHERE t.topping_id IN :ids", Topping.class)
                                .setParameter("ids", toppingQuantities.keySet()).getResultList();
                        StringBuilder csvBuilder = new StringBuilder();
                        for (Topping t : selectedToppings) {
                            int qty = toppingQuantities.get(t.getTopping_id());
                            toppingsCost = toppingsCost.add(t.getPrice().multiply(BigDecimal.valueOf(qty)));
                            if (csvBuilder.length() > 0) csvBuilder.append(", ");
                            csvBuilder.append(t.getTopping_name());
                            if (qty > 1) csvBuilder.append(" x").append(qty);
                        }
                        newToppingsCsv = csvBuilder.toString();
                    }
                }

                List<CartItem> cart = cart(session);
                
                cart.removeIf(item ->
                    item.getProductId().equals(oldPid) &&
                    Objects.equals(item.getSizeName(), finalOldSize) &&
                    Objects.equals(item.getToppingsCsv(), finalOldToppingsCsv)
                );
                
                CartItem newItem = new CartItem();
                newItem.setProductId(p.getProduct_id());
                newItem.setProductName(p.getProduct_name());
                newItem.setThumbnail(p.getThumbnail());
                newItem.setQuantity(quantity);
                newItem.setUnitPrice(p.getPrice());
                newItem.setSizeName(finalNewSize);
                newItem.setSizeAdj(sizeAdjustment);
                newItem.setToppingsCsv(newToppingsCsv);
                newItem.setToppingsCost(toppingsCost);

                Optional<CartItem> existingSimilarItemOpt = cart.stream().filter(item -> item.equals(newItem)).findFirst();
                if (existingSimilarItemOpt.isPresent()) {
                    CartItem existing = existingSimilarItemOpt.get();
                    existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
                } else {
                    cart.add(newItem);
                }

                resp.getWriter().print("{\"ok\":true, \"message\":\"Cập nhật giỏ hàng thành công!\"}");
            } finally {
                if (em.isOpen()) em.close();
            }
        } catch (Exception e) {
            resp.getWriter().print("{\"ok\":false,\"message\":\"Lỗi máy chủ: " + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}