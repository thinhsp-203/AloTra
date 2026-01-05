package stnw.service.impl;

import stnw.config.JpaUtil;
import stnw.dao.ProductDao;
import stnw.dao.ProductSizeDao;
import stnw.dao.impl.ProductDaoImpl;
import stnw.dao.impl.ProductSizeDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Part;
import stnw.model.Category;
import stnw.model.Product;
import stnw.model.ProductSize;
import stnw.service.AdminProductService;
import stnw.utils.UploadType;
import stnw.utils.UploadUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class AdminProductServiceImpl implements AdminProductService {
    
    private final ProductDao productDao = new ProductDaoImpl();
    
    // Constants cho giá size mặc định (không hard-code trong logic)
    private static final BigDecimal SIZE_S_PRICE = BigDecimal.ZERO; // +0 VND
    private static final BigDecimal SIZE_M_PRICE = BigDecimal.valueOf(5000); // +5,000 VND
    private static final BigDecimal SIZE_L_PRICE = BigDecimal.valueOf(10000); // +10,000 VND
    
    @Override
    public List<Product> getAllProducts() {
        return productDao.findAll(-1, -1); // Lấy tất c�?
    }
    
    @Override
    public Product getProductById(int id) {
        return productDao.findById(id);
    }
    
    @Override
    public void saveProduct(Product product, Part thumbnailFile, String thumbnailUrl, jakarta.servlet.ServletContext servletContext) {
        // 1. VALIDATION
        validateProduct(product);
        
        // 2. X�?LÝ ẢNH
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
        
        // 5. Nếu là sản phẩm thức uống mới tạo, t�?động tạo sizes S/M/L
        if (product.getProduct_id() != null && product.getCategory() != null && 
            Boolean.TRUE.equals(product.getCategory().getIsDrink())) {
            createDefaultSizesForDrink(product.getProduct_id());
        }
    }
    
    /**
     * T�?động tạo sizes mặc định (S/M/L) cho sản phẩm thức uống
     */
    private void createDefaultSizesForDrink(int productId) {
        EntityManager em = JpaUtil.em();
        try {
            ProductSizeDao sizeDao = new ProductSizeDaoImpl(em);
            
            // Kiểm tra xem đã có sizes chưa
            List<ProductSize> existingSizes = sizeDao.findByProductId(productId);
            if (!existingSizes.isEmpty()) {
                // Đã có sizes rồi, không tạo lại
                return;
            }
            
            // Lấy product đ�?set vào size
            Product product = productDao.findById(productId);
            if (product == null) {
                return;
            }
            
            // Tạo Size S
            ProductSize sizeS = new ProductSize();
            sizeS.setSize_name("S");
            sizeS.setPrice_adjustment(SIZE_S_PRICE);
            sizeS.setProduct(product);
            sizeDao.save(sizeS);
            
            // Tạo Size M
            ProductSize sizeM = new ProductSize();
            sizeM.setSize_name("M");
            sizeM.setPrice_adjustment(SIZE_M_PRICE);
            sizeM.setProduct(product);
            sizeDao.save(sizeM);
            
            // Tạo Size L
            ProductSize sizeL = new ProductSize();
            sizeL.setSize_name("L");
            sizeL.setPrice_adjustment(SIZE_L_PRICE);
            sizeL.setProduct(product);
            sizeDao.save(sizeL);
            
        } catch (Exception e) {
            // Log lỗi nhưng không throw exception (tạo size không quan trọng bằng việc lưu product)
            System.err.println("Không th�?tạo sizes mặc định cho sản phẩm #" + productId + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    @Override
    public void saveProductFromParams(Integer productId, String productName, String description, 
                                     BigDecimal price, BigDecimal discount, 
                                     Integer categoryId, Boolean isActive, Boolean isFeatured,
                                     Part thumbnailFile, String thumbnailUrl, jakarta.servlet.ServletContext servletContext) {
        // Tạo Entity từ parameters
        Product product;
        if (productId != null) {
            product = productDao.findById(productId);
            if (product == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại!");
            }
        } else {
            product = new Product();
        }
        
        // Set fields
        product.setProduct_name(productName);
        product.setDescription(description);
        product.setPrice(price);
        product.setDiscount(discount != null ? discount : BigDecimal.ZERO);
        
        // Category
        EntityManager em = JpaUtil.em();
        try {
            Category category = em.find(Category.class, categoryId);
            if (category == null) {
                throw new IllegalArgumentException("Danh mục không tồn tại!");
            }
            product.setCategory(category);
        } finally {
            em.close();
        }
        
        product.setIsActive(isActive);
        product.setIsFeatured(isFeatured);
        
        // Gọi method saveProduct hiện có
        saveProduct(product, thumbnailFile, thumbnailUrl, servletContext);
    }
    
    @Override
    public void disableProduct(int id, jakarta.servlet.ServletContext servletContext) {
        Product p = productDao.findById(id);
        if (p == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại!");
        }
        
        // Ngừng bán: set isActive = false
        p.setIsActive(false);
        p.setUpdatedDate(LocalDateTime.now());
        productDao.update(p);
    }
    
    @Override
    public void enableProduct(int id, jakarta.servlet.ServletContext servletContext) {
        Product p = productDao.findById(id);
        if (p == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại!");
        }
        
        // Kích hoạt: set isActive = true
        p.setIsActive(true);
        p.setUpdatedDate(LocalDateTime.now());
        productDao.update(p);
    }
    
    @Override
    public void deleteProduct(int id, jakarta.servlet.ServletContext servletContext) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Product p = em.find(Product.class, id);
            if (p == null) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Sản phẩm không tồn tại!");
            }
            
            // Kiểm tra sản phẩm đã có đơn hàng chưa
            Long orderCount = em.createQuery(
                "SELECT COUNT(od) FROM OrderDetail od WHERE od.product.product_id = :productId",
                Long.class
            ).setParameter("productId", id).getSingleResult();
            
            if (orderCount > 0) {
                em.getTransaction().rollback();
                throw new IllegalArgumentException("Sản phẩm đã có đơn hàng, không th�?xóa vĩnh viễn. Vui lòng dùng chức năng 'Ngừng bán'.");
            }
            
            // Xóa các bảng con trước (vì không có CASCADE)
            // 1. Xóa Review
            em.createQuery("DELETE FROM Review r WHERE r.product.product_id = :productId")
              .setParameter("productId", id)
              .executeUpdate();
            
            // 2. Xóa ViewHistory (nếu có)
            try {
                em.createQuery("DELETE FROM ViewHistory vh WHERE vh.product.product_id = :productId")
                  .setParameter("productId", id)
                  .executeUpdate();
            } catch (Exception e) {
                // Nếu bảng ViewHistory không tồn tại, b�?qua
            }
            
            // 3. Xóa WishlistItem
            em.createQuery("DELETE FROM WishlistItem wi WHERE wi.product.product_id = :productId")
              .setParameter("productId", id)
              .executeUpdate();
            
            // 4. Xóa ProductSize (có CASCADE nhưng xóa th�?công đ�?chắc chắn)
            em.createQuery("DELETE FROM ProductSize ps WHERE ps.product.product_id = :productId")
              .setParameter("productId", id)
              .executeUpdate();
            
            // 5. Xóa file ảnh
            UploadUtil.deleteOldImage(p.getThumbnail(), servletContext);
            
            // 6. Xóa Product (sau khi đã xóa các bảng con)
            em.remove(p);
            
            em.getTransaction().commit();
            
        } catch (IllegalArgumentException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi xóa sản phẩm: " + e.getMessage(), e);
        } finally {
            em.close();
        }
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
     * Validate d�?liệu sản phẩm
     */
    private void validateProduct(Product product) {
        if (product.getProduct_name() == null || product.getProduct_name().isBlank()) {
            throw new IllegalArgumentException("Tên sản phẩm không được rỗng!");
        }
        
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá sản phẩm không hợp l�?");
        }
        
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Vui lòng chọn danh mục!");
        }
    }
    
    /**
     * X�?lý upload thumbnail (ưu tiên file upload)
     * @return Relative path mới (hoặc URL), hoặc null nếu không có thay đổi
     */
    private String handleThumbnailUpload(Product product, Part thumbnailFile, String thumbnailUrl, jakarta.servlet.ServletContext servletContext) {
        try {
            // TRƯỜNG HỢP 1: Upload file (ưu tiên)
            String uploadedPath = UploadUtil.save(thumbnailFile, UploadType.PRODUCTS, servletContext);
            if (uploadedPath != null) {
                // Xóa ảnh cũ nếu tồn tại
                if (product.getThumbnail() != null && !product.getThumbnail().isEmpty()) {
                    UploadUtil.deleteOldImage(product.getThumbnail(), servletContext);
                }
                return uploadedPath;
            }
            
            // TRƯỜNG HỢP 2: URL t�?text input
            if (thumbnailUrl != null && !thumbnailUrl.isBlank()) {
                return thumbnailUrl;
            }
            
            // TRƯỜNG HỢP 3: Gi�?nguyên ảnh cũ
            return null;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage(), e);
        }
    }
}