package service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import model.CartItem;
import model.OrderDetail;
import model.Orders;
import model.Product;
import model.ProductSize;
import service.ReorderService;

public class ReorderServiceImpl implements ReorderService {
    
    @Override
    public Map<String, Integer> reorder(int userId, int orderId, List<CartItem> currentCart) {
        EntityManager em = JpaUtil.em();
        try {
            Orders order = em.find(Orders.class, orderId);
            
            if (order == null || !order.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("Đơn hàng không tồn tại hoặc không thuộc về bạn!");
            }
            
            int addedItems = 0;
            int unavailableItems = 0;
            
            for (OrderDetail detail : order.getOrderDetails()) {
                Product p = detail.getProduct();
                
                // Kiểm tra sản phẩm còn hoạt động và còn hàng
                if (p == null || p.getIsActive() == null || !p.getIsActive() ||
                    p.getStock() == null || p.getStock() < detail.getQuantity()) {
                    unavailableItems++;
                    continue;
                }
                
                // Tạo CartItem từ OrderDetail
                CartItem newItem = new CartItem();
                newItem.setProductId(p.getProduct_id());
                newItem.setProductName(p.getProduct_name());
                newItem.setThumbnail(p.getThumbnail());
                newItem.setQuantity(detail.getQuantity());
                newItem.setUnitPrice(p.getPrice()); // Giá hiện tại
                
                // Xử lý size
                String sizeName = detail.getSize_name();
                if (sizeName == null || sizeName.isBlank()) {
                    sizeName = "Mặc định";
                }
                newItem.setSizeName(sizeName);
                
                // Tính lại size adjustment
                java.math.BigDecimal sizeAdj = java.math.BigDecimal.ZERO;
                if (!"Mặc định".equals(sizeName)) {
                    try {
                        ProductSize ps = em.createQuery(
                            "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid AND ps.size_name = :sname",
                            ProductSize.class)
                            .setParameter("pid", p.getProduct_id())
                            .setParameter("sname", sizeName)
                            .getSingleResult();
                        sizeAdj = ps.getPrice_adjustment();
                    } catch (Exception e) {
                        sizeName = "Mặc định";
                        newItem.setSizeName(sizeName);
                    }
                }
                newItem.setSizeAdj(sizeAdj);
                
                // Xử lý toppings (đơn giản hóa)
                String toppings = detail.getToppings();
                newItem.setToppingsCsv(toppings != null ? toppings : "");
                newItem.setToppingsCost(java.math.BigDecimal.ZERO);
                
                // Merge vào cart
                Optional<CartItem> existingItemOpt = currentCart.stream()
                    .filter(item -> item.equals(newItem))
                    .findFirst();
                
                if (existingItemOpt.isPresent()) {
                    CartItem existing = existingItemOpt.get();
                    existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
                } else {
                    currentCart.add(newItem);
                }
                
                addedItems++;
            }
            
            Map<String, Integer> result = new HashMap<>();
            result.put("addedItems", addedItems);
            result.put("unavailableItems", unavailableItems);
            return result;
            
        } finally {
            if (em.isOpen()) em.close();
        }
    }
}