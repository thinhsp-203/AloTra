package service;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.Part;
import model.Product;

/**
 * Service xử lý logic nghiệp vụ cho quản lý sản phẩm (Admin)
 */
public interface AdminProductService {
    
    /**
     * Lấy danh sách toàn bộ sản phẩm (dành cho Admin)
     */
    List<Product> getAllProducts();
    
    /**
     * Lấy thông tin 1 sản phẩm theo ID
     */
    Product getProductById(int id);
    
    /**
     * Lưu sản phẩm (tạo mới hoặc cập nhật)
     * Xử lý upload ảnh tự động
     * 
     * @param product Entity Product
     * @param thumbnailFile File upload từ form (có thể null)
     * @param thumbnailUrl URL ảnh từ text input (có thể null)
     * @throws IllegalArgumentException nếu cả 2 đều null hoặc dữ liệu không hợp lệ
     */
    void saveProduct(Product product, Part thumbnailFile, String thumbnailUrl);
    
    /**
     * Xóa sản phẩm (Soft delete: set isActive = false)
     */
    void deleteProduct(int id);
    
    /**
     * Lấy dữ liệu cho form (Categories, Suppliers)
     * @return Map với key "categories" và "suppliers"
     */
    Map<String, List<?>> getFormData();
}