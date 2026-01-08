package stnw.service.impl;

import stnw.dao.CategoryDao;
import stnw.dao.OrderDao;
import stnw.dao.ProductDao;
import stnw.dao.ProductSizeDao;
import stnw.dao.impl.CategoryDaoImpl;
import stnw.dao.impl.OrderDaoImpl;
import stnw.dao.impl.ProductDaoImpl;
import stnw.dao.impl.ProductSizeDaoImpl;
import jakarta.servlet.http.Part;
import stnw.model.Category;
import stnw.model.Product;
import stnw.model.ProductSize;
import stnw.service.AdminProductService;
import stnw.enums.UploadType;
import stnw.utils.UploadUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class AdminProductServiceImpl implements AdminProductService {
    
    private final ProductDao productDao = new ProductDaoImpl();
    private final CategoryDao categoryDao = new CategoryDaoImpl();
    private final OrderDao orderDao = new OrderDaoImpl();
    private final ProductSizeDao productSizeDao = new ProductSizeDaoImpl();
    
    // Constants cho giá size mặc định (không hard-code trong logic)
    private static final BigDecimal SIZE_S_PRICE = BigDecimal.ZERO; // +0 VND
    private static final BigDecimal SIZE_M_PRICE = BigDecimal.valueOf(5000); // +5,000 VND
    private static final BigDecimal SIZE_L_PRICE = BigDecimal.valueOf(10000); // +10,000 VND
    
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
        
        // 5. Nếu là sản phẩm thức uống mới tạo, tự động tạo sizes S/M/L
        if (product.getProduct_id() != null && product.getCategory() != null && 
            Boolean.TRUE.equals(product.getCategory().getIsDrink())) {
            createDefaultSizesForDrink(product.getProduct_id());
        }
    }
    
    /**
     * Tự động tạo sizes mặc định (S/M/L) cho sản phẩm thức uống
     */
    private void createDefaultSizesForDrink(int productId) {
        try {
            // Kiểm tra xem đã có sizes chưa
            List<ProductSize> existingSizes = productSizeDao.findByProductId(productId);
            if (!existingSizes.isEmpty()) {
                // Đã có sizes rồi, không tạo lại
                return;
            }
            
            // Lấy product để set vào size
            Product product = productDao.findById(productId);
            if (product == null) {
                return;
            }
            
            // Tạo Size S
            ProductSize sizeS = new ProductSize();
            sizeS.setSize_name("S");
            sizeS.setPrice_adjustment(SIZE_S_PRICE);
            sizeS.setProduct(product);
            productSizeDao.save(sizeS);
            
            // Tạo Size M
            ProductSize sizeM = new ProductSize();
            sizeM.setSize_name("M");
            sizeM.setPrice_adjustment(SIZE_M_PRICE);
            sizeM.setProduct(product);
            productSizeDao.save(sizeM);
            
            // Tạo Size L
            ProductSize sizeL = new ProductSize();
            sizeL.setSize_name("L");
            sizeL.setPrice_adjustment(SIZE_L_PRICE);
            sizeL.setProduct(product);
            productSizeDao.save(sizeL);
        } catch (Exception e) {
            // Log lỗi nhưng không throw exception (tạo size không quan trọng bằng việc lưu product)
            System.err.println("Không thể tạo sizes mặc định cho sản phẩm #" + productId + ": " + e.getMessage());
            e.printStackTrace();
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
        Category category = categoryDao.findById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Danh mục không tồn tại!");
        }
        product.setCategory(category);
        
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
        System.out.println("[AdminProductService] Bắt đầu xóa sản phẩm ID: " + id);
        
        Product p = productDao.findById(id);
        if (p == null) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại!");
        }
        
        // Kiểm tra sản phẩm đã có đơn hàng chưa
        long orderCount = orderDao.countOrdersByProductId(id);
        System.out.println("[AdminProductService] Số lượng OrderDetail: " + orderCount);
        if (orderCount > 0) {
            throw new IllegalArgumentException("Sản phẩm đã có đơn hàng, không thể xóa vĩnh viễn. Vui lòng dùng chức năng 'Ngừng bán'.");
        }
        
        // Xóa file ảnh trước (không cần transaction)
        System.out.println("[AdminProductService] Xóa file ảnh...");
        UploadUtils.deleteOldImage(p.getThumbnail(), servletContext);
        
        // Xóa Product và tất cả các bảng con trong CÙNG MỘT TRANSACTION
        // ProductDao sẽ xử lý tất cả: OrderDetail, Review, WishlistItem, ProductSize, ViewHistory, Product
        System.out.println("[AdminProductService] Xóa Product và các bảng con...");
        productDao.deleteProduct(id);
        
        System.out.println("[AdminProductService] Xóa sản phẩm thành công ID: " + id);
    }
    
    @Override
    public Map<String, List<?>> getFormData() {
        List<Category> categories = categoryDao.findAll();
        
        Map<String, List<?>> data = new HashMap<>();
        data.put("categories", categories);
        return data;
    }
    
    // ==================== PRIVATE HELPER METHODS ====================
    
    /**
     * Validate dữ liệu sản phẩm
     */
    private void validateProduct(Product product) {
        if (product.getProduct_name() == null || product.getProduct_name().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá sản phẩm phải >= 0!");
        }
        if (product.getCategory() == null) {
            throw new IllegalArgumentException("Danh mục không được để trống!");
        }
    }
    
    /**
     * Xử lý upload ảnh thumbnail
     */
    private String handleThumbnailUpload(Product product, Part thumbnailFile, String thumbnailUrl, jakarta.servlet.ServletContext servletContext) {
        try {
            // TRƯỜNG HỢP 1: Upload file (ưu tiên)
            String uploadedPath = UploadUtils.save(thumbnailFile, UploadType.PRODUCTS, servletContext);
            if (uploadedPath != null) {
                // Xóa ảnh cũ nếu đang edit
                if (product.getProduct_id() != null && product.getThumbnail() != null) {
                    UploadUtils.deleteOldImage(product.getThumbnail(), servletContext);
                }
                return uploadedPath;
            }
            
            // TRƯỜNG HỢP 2: URL từ text input
            if (thumbnailUrl != null && !thumbnailUrl.trim().isEmpty()) {
                return thumbnailUrl.trim();
            }
            
            // TRƯỜNG HỢP 3: Giữ nguyên ảnh cũ
            return null;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload ảnh: " + e.getMessage(), e);
        }
    }
}
