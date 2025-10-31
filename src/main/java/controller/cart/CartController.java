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
            // Lấy số lượng từ request thay vì mặc định là 1
            int qty = Integer.parseInt(req.getParameter("quantity"));
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
                if (sizeName != null && !sizeName.isBlank() && !"Mặc định".equals(sizeName)) {
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

                CartItem newItem = new CartItem();
                newItem.setProductId(pid);
                newItem.setProductName(p.getProduct_name());
                newItem.setThumbnail(p.getThumbnail());
                newItem.setQuantity(qty);
                newItem.setUnitPrice(p.getPrice());
                newItem.setSizeName(sizeName);
                newItem.setSizeAdj(sizeAdjustment);
                newItem.setToppingsCost(toppingsCost);
                newItem.setToppingsCsv(toppingsCsv);

                var list = cart(session);
                
                // === PHẦN SỬA LỖI ===
                // Thay vì gán lại biến ci, chúng ta dùng một biến mới (finalItem)
                // để lưu trữ item cuối cùng sẽ được trả về trong JSON.
                CartItem finalItem;
                Optional<CartItem> existingItemOpt = list.stream().filter(item -> item.equals(newItem)).findFirst();
                
                if (existingItemOpt.isPresent()) {
                    CartItem existingItem = existingItemOpt.get();
                    existingItem.setQuantity(existingItem.getQuantity() + qty);
                    finalItem = existingItem; // Dùng item đã tồn tại
                } else {
                    list.add(newItem);
                    finalItem = newItem; // Dùng item mới
                }
                // === KẾT THÚC PHẦN SỬA LỖI ===

                resp.setContentType("application/json; charset=UTF-8");
                String newItemJson = String.format("{\"productId\":%d,\"productName\":\"%s\",\"thumbnail\":\"%s\",\"lineTotal\":%s}",
                        finalItem.getProductId(), escapeJson(finalItem.getProductName()), escapeJson(finalItem.getThumbnail()), finalItem.getLineTotal());
                resp.getWriter().print("{\"ok\":true,\"cartSize\":" + list.size() + ",\"newItem\":" + newItemJson + "}");

            } finally {
                if (em.isOpen()) em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"ok\":false,\"message\":\"Có lỗi xảy ra: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // This logic is for the +/- buttons on the checkout page. It seems correct.
        // ... (body of this method is unchanged)
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
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/checkout?error=remove_failed");
        }
    }

    /**
     * Updates an item's details (size, toppings) from the edit modal.
     * This is the final corrected version.
     */
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

                // --- Calculate properties of the NEW item ---
                BigDecimal sizeAdjustment = BigDecimal.ZERO;
                String finalNewSize = (newSize == null || newSize.isBlank()) ? "Mặc định" : newSize;
                if (!"Mặc định".equals(finalNewSize)) {
                    try {
                        ProductSize ps = em.createQuery("SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sname", ProductSize.class)
                                .setParameter("pid", oldPid).setParameter("sname", finalNewSize).getSingleResult();
                        sizeAdjustment = ps.getPrice_adjustment();
                    } catch (Exception e) { /* Ignore if size not found */ }
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
                
                // --- CORE LOGIC FIX ---
                // 1. Remove the old item from the cart
                cart.removeIf(item ->
                    item.getProductId().equals(oldPid) &&
                    Objects.equals(item.getSizeName(), finalOldSize) &&
                    Objects.equals(item.getToppingsCsv(), finalOldToppingsCsv)
                );
                
                // 2. Create the new (edited) item
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

                // 3. Add/Merge the new item back into the cart
                Optional<CartItem> existingSimilarItemOpt = cart.stream().filter(item -> item.equals(newItem)).findFirst();
                if (existingSimilarItemOpt.isPresent()) {
                    // If an identical item already exists, just add the quantity to it
                    CartItem existing = existingSimilarItemOpt.get();
                    existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
                } else {
                    // Otherwise, add the new item to the cart
                    cart.add(newItem);
                }
                // --- END OF CORE LOGIC FIX ---

                resp.getWriter().print("{\"ok\":true, \"message\":\"Cập nhật giỏ hàng thành công!\"}");
            } finally {
                if (em.isOpen()) em.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().print("{\"ok\":false,\"message\":\"Lỗi máy chủ: " + e.getMessage() + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}