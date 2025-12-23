package service.impl;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.*;
import service.CartService;

import java.math.BigDecimal;
import java.util.*;

public class CartServiceImpl implements CartService {
    
    @Override
    public CartItem addToCart(List<CartItem> cart, int productId, int quantity, 
                             String sizeName, String toppingParam) {
        EntityManager em = JpaUtil.em();
        try {
            // 1. LẤY THÔNG TIN SẢN PHẨM
            Product p = em.find(Product.class, productId);
            if (p == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại");
            }
            
            // 2. XỬ LÝ SIZE
            String finalSizeName = (sizeName == null || sizeName.isBlank() || "Mặc định".equals(sizeName)) 
                ? "Mặc định" 
                : sizeName;
            
            BigDecimal sizeAdjustment = BigDecimal.ZERO;
            if (!"Mặc định".equals(finalSizeName)) {
                try {
                    ProductSize productSize = em.createQuery(
                        "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sname", 
                        ProductSize.class)
                        .setParameter("pid", productId)
                        .setParameter("sname", finalSizeName)
                        .getSingleResult();
                    sizeAdjustment = productSize.getPrice_adjustment();
                } catch (Exception e) {
                    // Size không tồn tại, dùng mặc định
                    finalSizeName = "Mặc định";
                }
            }
            
            // 3. XỬ LÝ TOPPING
            BigDecimal toppingsCost = BigDecimal.ZERO;
            String toppingsCsv = "";
            
            if (toppingParam != null && !toppingParam.isBlank()) {
                Map<Integer, Integer> toppingQuantities = parseToppingParam(toppingParam);
                
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
                            
                            if (csvBuilder.length() > 0) csvBuilder.append(", ");
                            csvBuilder.append(t.getTopping_name());
                            if (toppingQty > 1) csvBuilder.append(" x").append(toppingQty);
                        }
                    }
                    toppingsCsv = csvBuilder.toString();
                }
            }
            
            // 4. TẠO CART ITEM MỚI
            CartItem newItem = new CartItem();
            newItem.setProductId(productId);
            newItem.setProductName(p.getProduct_name());
            newItem.setThumbnail(p.getThumbnail());
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(p.getPrice());
            newItem.setSizeName(finalSizeName);
            newItem.setSizeAdj(sizeAdjustment);
            newItem.setToppingsCost(toppingsCost);
            newItem.setToppingsCsv(toppingsCsv);
            
            // 5. KIỂM TRA ITEM ĐÃ TỒN TẠI
            CartItem existingItem = findCartItem(cart, productId, finalSizeName, toppingsCsv);
            
            if (existingItem != null) {
                // Cộng dồn số lượng
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                return existingItem;
            } else {
                // Thêm item mới
                cart.add(newItem);
                return newItem;
            }
            
        } finally {
            if (em.isOpen()) em.close();
        }
    }
    
    @Override
    public void updateQuantity(List<CartItem> cart, int productId, String sizeName, 
                              String toppingsCsv, int newQuantity) {
        String finalSize = normalizeSizeName(sizeName);
        String finalToppings = normalizeToppings(toppingsCsv);
        
        if (newQuantity <= 0) {
            removeItem(cart, productId, finalSize, finalToppings);
            return;
        }
        
        CartItem item = findCartItem(cart, productId, finalSize, finalToppings);
        if (item != null) {
            item.setQuantity(newQuantity);
        }
    }
    
    @Override
    public void removeItem(List<CartItem> cart, int productId, String sizeName, 
                          String toppingsCsv) {
        String finalSize = normalizeSizeName(sizeName);
        String finalToppings = normalizeToppings(toppingsCsv);
        
        cart.removeIf(item -> 
            item.getProductId().equals(productId) &&
            Objects.equals(item.getSizeName(), finalSize) &&
            Objects.equals(item.getToppingsCsv(), finalToppings)
        );
    }
    
    @Override
    public boolean updateItemDetails(List<CartItem> cart, int oldProductId, 
                                    String oldSize, String oldToppingsCsv,
                                    String newSize, String newToppingParam, 
                                    int quantity) {
        EntityManager em = JpaUtil.em();
        try {
            // 1. CHUẨN HÓA DỮ LIỆU CŨ
            String finalOldSize = normalizeSizeName(oldSize);
            String finalOldToppingsCsv = normalizeToppings(oldToppingsCsv);
            
            // 2. LẤY THÔNG TIN SẢN PHẨM
            Product p = em.find(Product.class, oldProductId);
            if (p == null) {
                return false;
            }
            
            // 3. TÍNH TOÁN THUỘC TÍNH MỚI
            String finalNewSize = (newSize == null || newSize.isBlank()) ? "Mặc định" : newSize;
            BigDecimal sizeAdjustment = BigDecimal.ZERO;
            
            if (!"Mặc định".equals(finalNewSize)) {
                try {
                    ProductSize ps = em.createQuery(
                        "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sname", 
                        ProductSize.class)
                        .setParameter("pid", oldProductId)
                        .setParameter("sname", finalNewSize)
                        .getSingleResult();
                    sizeAdjustment = ps.getPrice_adjustment();
                } catch (Exception e) {
                    finalNewSize = "Mặc định";
                }
            }
            
            // 4. TÍNH TOPPING MỚI
            BigDecimal toppingsCost = BigDecimal.ZERO;
            String newToppingsCsv = "";
            
            if (newToppingParam != null && !newToppingParam.isBlank()) {
                Map<Integer, Integer> toppingQuantities = parseToppingParam(newToppingParam);
                
                if (!toppingQuantities.isEmpty()) {
                    List<Topping> selectedToppings = em.createQuery(
                        "SELECT t FROM Topping t WHERE t.topping_id IN :ids", Topping.class)
                        .setParameter("ids", toppingQuantities.keySet())
                        .getResultList();
                    
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
            
            // 5. XÓA ITEM CŨ
            removeItem(cart, oldProductId, finalOldSize, finalOldToppingsCsv);
            
            // 6. TẠO ITEM MỚI
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
            
            // 7. MERGE VỚI ITEM TƯƠNG TỰ (NẾU CÓ)
            CartItem existingSimilar = findCartItem(cart, newItem.getProductId(), 
                                                    newItem.getSizeName(), 
                                                    newItem.getToppingsCsv());
            
            if (existingSimilar != null) {
                existingSimilar.setQuantity(existingSimilar.getQuantity() + newItem.getQuantity());
            } else {
                cart.add(newItem);
            }
            
            return true;
            
        } finally {
            if (em.isOpen()) em.close();
        }
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    /**
     * Tìm CartItem trong giỏ theo productId, size, toppings
     */
    private CartItem findCartItem(List<CartItem> cart, int productId, 
                                  String sizeName, String toppingsCsv) {
        return cart.stream()
            .filter(item -> 
                item.getProductId().equals(productId) &&
                Objects.equals(item.getSizeName(), sizeName) &&
                Objects.equals(item.getToppingsCsv(), toppingsCsv)
            )
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Parse chuỗi topping "id:qty,id:qty" thành Map
     */
    private Map<Integer, Integer> parseToppingParam(String toppingParam) {
        Map<Integer, Integer> toppingQuantities = new LinkedHashMap<>();
        
        if (toppingParam == null || toppingParam.isBlank()) {
            return toppingQuantities;
        }
        
        for (String entry : toppingParam.split(",")) {
            String[] parts = entry.split(":");
            if (parts.length == 2) {
                try {
                    int toppingId = Integer.parseInt(parts[0].trim());
                    int qty = Integer.parseInt(parts[1].trim());
                    if (qty > 0) {
                        toppingQuantities.put(toppingId, qty);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }
        
        return toppingQuantities;
    }
    
    /**
     * Chuẩn hóa tên size
     */
    private String normalizeSizeName(String sizeName) {
        if (sizeName == null || "undefined".equalsIgnoreCase(sizeName) || sizeName.isBlank()) {
            return "Mặc định";
        }
        return sizeName;
    }
    
    /**
     * Chuẩn hóa chuỗi toppings
     */
    private String normalizeToppings(String toppings) {
        if (toppings == null || "undefined".equalsIgnoreCase(toppings)) {
            return "";
        }
        return toppings;
    }
}