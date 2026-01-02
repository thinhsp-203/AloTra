package service.impl;

import config.JpaUtil;
import dao.ProductDao;
import dao.impl.ProductDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Part;
import model.Category;
import model.Product;
import service.AdminProductService;
import utils.Constant;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

public class AdminProductServiceImpl implements AdminProductService {
    
    private static final String PRODUCT_SUBDIR = "products";
    private final ProductDao productDao = new ProductDaoImpl();
    
    @Override
    public List<Product> getAllProducts() {
        return productDao.findAll(-1, -1); // Lấy tất cả
    }
    
    @Override
    public Product getProductById(int id) {
        return productDao.findById(id);
    }
    
    @Override
    public void saveProduct(Product product, Part thumbnailFile, String thumbnailUrl, jakarta.servlet.ServletContext servletContext) {
        // 1. VALIDATION
        validateProduct(product);
        
        // 2. XỬ LÝ ẢNH
        String finalThumbnail = handleThumbnailUpload(product, thumbnailFile, thumbnailUrl, servletContext);
        if (finalThumbnail != null) {
            product.setThumbnail(finalThumbnail);
        }
        
        // 3. SET TIMESTAMPS
        if (product.getProduct_id() == null) {
            product.setCreatedDate(LocalDateTime.now());
        }
        product.setUpdatedDate(LocalDateTime.now());
        
        // 4. LƯU VÀO DB
        productDao.save(product);
    }
    
    @Override
    public void deleteProduct(int id, jakarta.servlet.ServletContext servletContext) {
        Product p = productDao.findById(id);
        if (p == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại!");
        }
        
        // Soft delete
        p.setIsActive(false);
        p.setUpdatedDate(LocalDateTime.now());
        productDao.update(p);
        
        // (Tùy chọn) Xóa file ảnh
        deleteProductImage(p.getThumbnail(), servletContext);
    }
    
    @Override
    public Map<String, List<?>> getFormData() {
        EntityManager em = JpaUtil.em();
        try {
            List<Category> categories = em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
            
            Map<String, List<?>> data = new HashMap<>();
            data.put("categories", categories);
            return data;
        } finally {
            em.close();
        }
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    /**
     * Validate dữ liệu sản phẩm
     */
    private void validateProduct(Product product) {
        if (product.getProduct_name() == null || product.getProduct_name().isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng!");
        }
        
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá sản phẩm không hợp lệ!");
        }
        
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Vui lòng chọn danh mục!");
        }
    }
    
    /**
     * Xử lý upload thumbnail (ưu tiên file upload)
     * @return Tên file mới (hoặc URL), hoặc null nếu không có thay đổi
     */
    private String handleThumbnailUpload(Product product, Part thumbnailFile, String thumbnailUrl, jakarta.servlet.ServletContext servletContext) {
        try {
            String originalFileName = (thumbnailFile != null) 
                ? Paths.get(thumbnailFile.getSubmittedFileName()).getFileName().toString() 
                : null;
            
            // TRƯỜNG HỢP 1: Upload file (ưu tiên)
            if (originalFileName != null && !originalFileName.isEmpty()) {
                String extension = "";
                int i = originalFileName.lastIndexOf('.');
                if (i > 0) {
                    extension = originalFileName.substring(i);
                }
                String finalFileName = "product-" + UUID.randomUUID().toString() + extension;
                
                // Lưu file vào thư mục uploads/products
                String uploadPath = Constant.getUploadPath(servletContext);
                File productsDir = new File(uploadPath, PRODUCT_SUBDIR);
                if (!productsDir.exists()) productsDir.mkdirs();
                
                File fileToSave = new File(productsDir, finalFileName);
                
                // Xóa ảnh cũ nếu tồn tại
                if (product.getThumbnail() != null && 
                    !product.getThumbnail().isEmpty() && 
                    !product.getThumbnail().startsWith("http")) {
                    deleteProductImage(product.getThumbnail(), servletContext);
                }
                
                // Lưu file mới
                try (InputStream input = thumbnailFile.getInputStream()) {
                    Files.copy(input, fileToSave.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                
                return PRODUCT_SUBDIR + "/" + finalFileName;
            }
            
            // TRƯỜNG HỢP 2: URL từ text input
            if (thumbnailUrl != null && !thumbnailUrl.isBlank()) {
                return thumbnailUrl;
            }
            
            // TRƯỜNG HỢP 3: Giữ nguyên ảnh cũ
            return null;
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage(), e);
        }
    }
    
    /**
     * Xóa file ảnh sản phẩm
     */
    private void deleteProductImage(String thumbnailPath, jakarta.servlet.ServletContext servletContext) {
        if (thumbnailPath == null || thumbnailPath.isEmpty() || thumbnailPath.startsWith("http")) {
            return;
        }
        
        try {
            // thumbnailPath có thể là "products/filename" hoặc chỉ "filename"
            String fileName = Paths.get(thumbnailPath).getFileName().toString();
            String uploadPath = Constant.getUploadPath(servletContext);
            File productsDir = new File(uploadPath, PRODUCT_SUBDIR);
            File oldFile = new File(productsDir, fileName);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        } catch (Exception e) {
            // Log lỗi nhưng không throw exception (xóa ảnh không quan trọng)
            System.err.println("Không thể xóa ảnh: " + e.getMessage());
        }
    }
}