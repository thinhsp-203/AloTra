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
            int qty = 1; // Số lượng sản phẩm chính luôn là 1 khi thêm từ modal
            String sizeName = req.getParameter("size"); // Lấy tên size, ví dụ: "L"
            String toppingParam = req.getParameter("topping"); // VD: "5:1,8:2"

            EntityManager em = JpaUtil.em();
            try {
                Product p = em.find(Product.class, pid);
                if (p == null) {
                    resp.setContentType("application/json; charset=UTF-8");
                    resp.getWriter().print("{\"ok\":false,\"message\":\"Sản phẩm không tồn tại\"}");
                    return;
                }

                // Xử lý Size
                BigDecimal sizeAdjustment = BigDecimal.ZERO;
                if (sizeName != null && !sizeName.isBlank()) {
                    try {
                        ProductSize productSize = em.createQuery(
                            "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sname", ProductSize.class)
                            .setParameter("pid", pid)
                            .setParameter("sname", sizeName)
                            .getSingleResult();
                        sizeAdjustment = productSize.getPrice_adjustment();
                    } catch (Exception e) {
                        // Nếu không tìm thấy size trong DB (ví dụ sản phẩm chỉ có 1 size mặc định), gán tên và giữ giá gốc
                        sizeName = "Mặc định";
                    }
                } else {
                    sizeName = "Mặc định";
                }

                // Xử lý Topping
                BigDecimal toppingsCost = BigDecimal.ZERO;
                String toppingsCsv = "";
                Map<Integer, Integer> toppingQuantities = new LinkedHashMap<>();

                if (toppingParam != null && !toppingParam.isBlank()) {
                    // Phân tích chuỗi topping: "id1:qty1,id2:qty2"
                    for (String entry : toppingParam.split(",")) {
                        String[] parts = entry.split(":");
                        if (parts.length == 2) {
                            try {
                                toppingQuantities.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
                            } catch (NumberFormatException e) {
                                // Bỏ qua nếu entry không hợp lệ
                            }
                        }
                    }

                    if (!toppingQuantities.isEmpty()) {
                        List<Topping> selectedToppings = em.createQuery("SELECT t FROM Topping t WHERE t.topping_id IN :ids", Topping.class)
                                                           .setParameter("ids", toppingQuantities.keySet())
                                                           .getResultList();
                        
                        // Tính tổng chi phí và tạo chuỗi mô tả
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