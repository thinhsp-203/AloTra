package dao;

import model.ProductSize;
import java.util.List;

public interface ProductSizeDao {
    /**
     * Lưu ProductSize vào DB (insert hoặc update)
     */
    void save(ProductSize productSize);
    
    /**
     * Lấy tất cả sizes của một sản phẩm
     */
    List<ProductSize> findByProductId(int productId);
    
    /**
     * Xóa size theo ID
     */
    void delete(int sizeId);
    
    /**
     * Xóa tất cả sizes của một sản phẩm
     */
    void deleteByProductId(int productId);
    
    /**
     * Kiểm tra size đã tồn tại chưa (theo product_id và size_name)
     */
    boolean exists(int productId, String sizeName);
}


