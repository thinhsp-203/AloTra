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
        
        // Redirect all cart view requests to checkout
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

    /**
     * Add product to cart
     */
    private void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        
        // Check login
        if (session.getAttribute("currentUser") == null) {
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().print("{\"ok\":false,\"redirect\":\"" + req.getContextPath() + "/login\"}");
            return;
        }

        try {
            int pid = Integer.parseInt(req.getParameter("productId"));
            int qty = 1; // Always 1 when adding from modal
            String sizeName = req.getParameter("size");
            String toppingParam = req.getParameter("topping");

            EntityManager em = JpaUtil.em();
            try {
                Product p = em.find(Product.class, pid);
                if (p == null) {
                    resp.setContentType("application/json; charset=UTF-8");
                    resp.getWriter().print("{\"ok\":false,\"message\":\"Sản phẩm không tồn tại\"}");
                    return;
                }

                // Process Size
                BigDecimal sizeAdjustment = BigDecimal.ZERO;
                if (sizeName != null && !sizeName.isBlank()) {
                    try {
                        ProductSize productSize = em.createQuery(
                            "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sname", 
                            ProductSize.class)
                            .setParameter("pid", pid)
                            .setParameter("sname", sizeName)
                            .getSingleResult();
                        sizeAdjustment = productSize.getPrice_adjustment();
                    } catch (Exception e) {
                        sizeName = "Mặc định";
                    }
                } else {
                    sizeName = "Mặc định";
                }

                // Process Toppings
                BigDecimal toppingsCost = BigDecimal.ZERO;
                String toppingsCsv = "";
                Map<Integer, Integer> toppingQuantities = new LinkedHashMap<>();

                if (toppingParam != null && !toppingParam.isBlank()) {
                    for (String entry : toppingParam.split(",")) {
                        String[] parts = entry.split(":");
                        if (parts.length == 2) {
                            try {
                                toppingQuantities.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            } catch (NumberFormatException e) {
                                // Skip invalid entries
                            }
                        }
                    }

                    if (!toppingQuantities.isEmpty()) {
                        List<Topping> selectedToppings = em.createQuery(
                            "SELECT t FROM Topping t WHERE t.topping_id IN :ids", Topping.class)
                            .setParameter("ids", toppingQuantities.keySet())
                            .getResultList();
                        
                        StringBuilder csvBuilder = new StringBuilder();
                        for (Topping t : selectedToppings) {
                            int toppingQty = toppingQuantities.getOrDefault(t.getTopping_id(), 0);
                            if (toppingQty > 0) {
                                toppingsCost = toppingsCost.add(t.getPrice().multiply(BigDecimal.valueOf(toppingQty)));
                                if (csvBuilder.length() > 0) {
                                    csvBuilder.append(", ");
                                }
                                csvBuilder.append(t.getTopping_name());
                                if (toppingQty > 1) {
                                    csvBuilder.append(" x").append(toppingQty);
                                }
                            }
                        }
                        toppingsCsv = csvBuilder.toString();
                    }
                }

                // Create cart item
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

                // Add or update cart
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
                String newItemJson = String.format(
                    "{\"productId\":%d,\"productName\":\"%s\",\"thumbnail\":\"%s\",\"lineTotal\":%s}",
                    ci.getProductId(), 
                    escapeJson(ci.getProductName()), 
                    escapeJson(ci.getThumbnail()), 
                    ci.getLineTotal()
                );
                resp.getWriter().print("{\"ok\":true,\"cartSize\":" + list.size() + ",\"newItem\":" + newItemJson + "}");

            } finally {
                if (em.isOpen()) em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().print("{\"ok\":false,\"message\":\"Có lỗi xảy ra: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Update quantity of item in cart
     */
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
            
            // Find item in cart
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
                    
                    // Calculate new totals
                    BigDecimal lineTotal = item.getLineTotal();
                    BigDecimal cartTotal = items.stream()
                        .map(CartItem::getLineTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    resp.getWriter().print(String.format(
                        "{\"ok\":true,\"quantity\":%d,\"lineTotal\":%s,\"cartTotal\":%s,\"cartSize\":%d}",
                        newQty, lineTotal, cartTotal, items.size()
                    ));
                }
            } else {
                resp.getWriter().print("{\"ok\":false,\"message\":\"Không tìm thấy sản phẩm trong giỏ\"}");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"ok\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    /**
     * Remove item from cart
     */
    private void remove(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int pid = Integer.parseInt(req.getParameter("productId"));
            String size = req.getParameter("size");
            String toppings = req.getParameter("toppings");

            CartItem key = new CartItem();
            key.setProductId(pid);
            key.setSizeName(size);
            key.setToppingsCsv(toppings);

            cart(req.getSession()).remove(key);
            
            // Redirect back to checkout
            resp.sendRedirect(req.getContextPath() + "/checkout");
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/checkout?error=remove_failed");
        }
    }

    /**
     * Update item details (size, toppings) - from edit modal
     */
    private void updateItemDetails(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        try {
            // Old item identifiers
            int oldPid = Integer.parseInt(req.getParameter("oldProductId"));
            String oldSize = req.getParameter("oldSize");
            String oldToppings = req.getParameter("oldToppings");
            
            // New item details
            String newSize = req.getParameter("newSize");
            String newToppingParam = req.getParameter("newToppings");
            int quantity = Integer.parseInt(req.getParameter("quantity"));
            
            EntityManager em = JpaUtil.em();
            try {
                Product p = em.find(Product.class, oldPid);
                if (p == null) {
                    resp.getWriter().print("{\"ok\":false,\"message\":\"Sản phẩm không tồn tại\"}");
                    return;
                }
                
                // Calculate new size adjustment
                BigDecimal sizeAdjustment = BigDecimal.ZERO;
                if (newSize != null && !newSize.isBlank() && !"Mặc định".equals(newSize)) {
                    try {
                        ProductSize productSize = em.createQuery(
                            "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sname",
                            ProductSize.class)
                            .setParameter("pid", oldPid)
                            .setParameter("sname", newSize)
                            .getSingleResult();
                        sizeAdjustment = productSize.getPrice_adjustment();
                    } catch (Exception e) {
                        // Keep zero if not found
                    }
                }
                
                // Calculate new toppings
                BigDecimal toppingsCost = BigDecimal.ZERO;
                String toppingsCsv = "";
                
                if (newToppingParam != null && !newToppingParam.isBlank()) {
                    Map<Integer, Integer> toppingQuantities = new LinkedHashMap<>();
                    for (String entry : newToppingParam.split(",")) {
                        String[] parts = entry.split(":");
                        if (parts.length == 2) {
                            try {
                                toppingQuantities.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            } catch (NumberFormatException e) {
                                // Skip
                            }
                        }
                    }
                    
                    if (!toppingQuantities.isEmpty()) {
                        List<Topping> selectedToppings = em.createQuery(
                            "SELECT t FROM Topping t WHERE t.topping_id IN :ids", Topping.class)
                            .setParameter("ids", toppingQuantities.keySet())
                            .getResultList();
                        
                        StringBuilder csvBuilder = new StringBuilder();
                        for (Topping t : selectedToppings) {
                            int toppingQty = toppingQuantities.getOrDefault(t.getTopping_id(), 0);
                            if (toppingQty > 0) {
                                toppingsCost = toppingsCost.add(t.getPrice().multiply(BigDecimal.valueOf(toppingQty)));
                                if (csvBuilder.length() > 0) {
                                    csvBuilder.append(", ");
                                }
                                csvBuilder.append(t.getTopping_name());
                                if (toppingQty > 1) {
                                    csvBuilder.append(" x").append(toppingQty);
                                }
                            }
                        }
                        toppingsCsv = csvBuilder.toString();
                    }
                }
                
                // Find and remove old item
                List<CartItem> items = cart(req.getSession());
                CartItem oldKey = new CartItem();
                oldKey.setProductId(oldPid);
                oldKey.setSizeName(oldSize);
                oldKey.setToppingsCsv(oldToppings);
                
                int idx = items.indexOf(oldKey);
                if (idx >= 0) {
                    items.remove(idx);
                }
                
                // Add updated item
                CartItem newItem = new CartItem();
                newItem.setProductId(oldPid);
                newItem.setProductName(p.getProduct_name());
                newItem.setThumbnail(p.getThumbnail());
                newItem.setQuantity(quantity);
                newItem.setUnitPrice(p.getPrice());
                newItem.setSizeName(newSize);
                newItem.setSizeAdj(sizeAdjustment);
                newItem.setToppingsCost(toppingsCost);
                newItem.setToppingsCsv(toppingsCsv);
                
                items.add(newItem);
                
                resp.getWriter().print("{\"ok\":true,\"message\":\"Đã cập nhật sản phẩm\"}");
                
            } finally {
                if (em.isOpen()) em.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"ok\":false,\"message\":\"" + escapeJson(e.getMessage()) + "\"}");
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