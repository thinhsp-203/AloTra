package stnw.service.impl;

import stnw.dao.OrderDao;
import stnw.dao.ProductSizeDao;
import stnw.dao.impl.OrderDaoImpl;
import stnw.dao.impl.ProductSizeDaoImpl;
import stnw.model.CartItem;
import stnw.model.OrderDetail;
import stnw.model.Orders;
import stnw.model.Product;
import stnw.model.ProductSize;
import stnw.service.ReorderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReorderServiceImpl implements ReorderService {
    
    private final OrderDao orderDao = new OrderDaoImpl();
    private final ProductSizeDao productSizeDao = new ProductSizeDaoImpl();
    
    @Override
    public Map<String, Integer> reorder(int userId, int orderId, List<CartItem> currentCart) {
        // Fetch order với orderDetails và products
        Orders order = orderDao.findByIdWithDetails(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Đơn hàng không tồn tại!");
        }
        
        // Kiểm tra quyền sở hữu
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Đơn hàng không thuộc về bạn!");
        }
        
        int addedItems = 0;
        int unavailableItems = 0;
        
        for (OrderDetail detail : order.getOrderDetails()) {
            Product p = detail.getProduct();
            
            // Kiểm tra sản phẩm còn hoạt động
            if (p == null || p.getIsActive() == null || !p.getIsActive()) {
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
                ProductSize ps = productSizeDao.findByProductIdAndSizeName(p.getProduct_id(), sizeName);
                if (ps != null) {
                    sizeAdj = ps.getPrice_adjustment();
                } else {
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
    }
}
