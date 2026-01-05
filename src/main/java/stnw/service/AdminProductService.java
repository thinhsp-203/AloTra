package stnw.service;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.Part;
import stnw.model.Product;

/**
 * Service x�?lý logic nghiệp v�?cho quản lý sản phẩm (Admin)
 */
public interface AdminProductService {
    
    /**
     * Lấy danh sách toàn b�?sản phẩm (dành cho Admin)
     */
    List<Product> getAllProducts();
    
    /**
     * Lấy thông tin 1 sản phẩm theo ID
     */
    Product getProductById(int id);
    
    /**
     * Lưu sản phẩm (tạo mới hoặc cập nhật)
     * X�?lý upload ảnh t�?động
     * 
     * @param product Entity Product
     * @param thumbnailFile File upload t�?form (có th�?null)
     * @param thumbnailUrl URL ảnh t�?text input (có th�?null)
     * @param servletContext ServletContext đ�?lấy đường dẫn upload
     * @throws IllegalArgumentException nếu c�?2 đều null hoặc d�?liệu không hợp l�?
     */
    void saveProduct(Product product, Part thumbnailFile, String thumbnailUrl, jakarta.servlet.ServletContext servletContext);
    
    /**
     * Lưu sản phẩm từ parameters (tạo mới hoặc cập nhật)
     * Controller chỉ truyền parameters, Service tự tạo Entity
     */
    void saveProductFromParams(Integer productId, String productName, String description, 
                              java.math.BigDecimal price, java.math.BigDecimal discount, 
                              Integer categoryId, Boolean isActive, Boolean isFeatured,
                              Part thumbnailFile, String thumbnailUrl, jakarta.servlet.ServletContext servletContext);
    
    /**
     * Ngừng bán sản phẩm (Soft delete: set isActive = false)
     * @param servletContext ServletContext (không dùng trong disable, ch�?đ�?đồng nhất signature)
     */
    void disableProduct(int id, jakarta.servlet.ServletContext servletContext);
    
    /**
     * Kích hoạt/Hiển th�?sản phẩm (set isActive = true)
     * @param servletContext ServletContext (không dùng trong enable, ch�?đ�?đồng nhất signature)
     */
    void enableProduct(int id, jakarta.servlet.ServletContext servletContext);
    
    /**
     * Xóa sản phẩm vĩnh viễn (Hard delete)
     * @param servletContext ServletContext đ�?xóa file ảnh
     * @throws IllegalArgumentException nếu sản phẩm đã có đơn hàng
     */
    void deleteProduct(int id, jakarta.servlet.ServletContext servletContext);
    
    /**
     * Lấy d�?liệu cho form (Categories)
     * @return Map với key "categories"
     */
    Map<String, List<?>> getFormData();
}