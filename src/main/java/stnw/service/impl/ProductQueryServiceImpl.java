package stnw.service.impl;

import stnw.config.JpaUtil;
import stnw.dao.ProductDao;
import stnw.dao.ProductQueryRepository;
import stnw.dao.ProductSizeDao;
import stnw.dao.impl.ProductDaoImpl;
import stnw.dao.impl.ProductQueryRepositoryImpl;
import stnw.dao.impl.ProductSizeDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import stnw.model.Category;
import stnw.model.Product;
import stnw.model.ProductSize;
import stnw.model.Topping;
import stnw.service.ProductQueryService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ProductQueryServiceImpl implements ProductQueryService {
    
    // Constants cho giá size mặc định (không hard-code trong logic)
    private static final BigDecimal SIZE_S_PRICE = BigDecimal.ZERO; // +0 VND
    private static final BigDecimal SIZE_M_PRICE = BigDecimal.valueOf(5000); // +5,000 VND
    private static final BigDecimal SIZE_L_PRICE = BigDecimal.valueOf(10000); // +10,000 VND

    private final ProductDao productDao = new ProductDaoImpl();

    @Override
    public List<Product> findProducts(Integer cateId, String keyword, String sortBy, String priceRange, int offset, int limit) {
        ProductQueryRepository repo = new ProductQueryRepositoryImpl();
        return repo.findProducts(cateId, keyword, sortBy, priceRange, offset, limit);
    }

    @Override
    public Map<String, Object> search(Integer cateId, Integer suppId, BigDecimal minPrice, BigDecimal maxPrice, String keyword, int page, int size) {
        ProductQueryRepository repo = new ProductQueryRepositoryImpl();
        return repo.search(cateId, suppId, minPrice, maxPrice, keyword, page, size);
    }

    @Override
    public Product getById(int id) {
        return productDao.findById(id);
    }

    @Override
    public List<ProductSize> getSizes(int productId) {
        EntityManager em = JpaUtil.em();
        try {
            // Sort theo thứ tự logic: S (1) -> M (2) -> L (3) -> các size khác (99)
            TypedQuery<ProductSize> query = em.createQuery(
                    "SELECT ps FROM ProductSize ps WHERE ps.product.product_id = :pid " +
                    "ORDER BY CASE ps.size_name " +
                    "    WHEN 'S' THEN 1 " +
                    "    WHEN 'M' THEN 2 " +
                    "    WHEN 'L' THEN 3 " +
                    "    ELSE 99 " +
                    "END",
                    ProductSize.class);
            query.setParameter("pid", productId);
            List<ProductSize> sizes = query.getResultList();
            
            // Nếu không có sizes, kiểm tra xem sản phẩm có thuộc category thức uống không
            if (sizes.isEmpty()) {
                TypedQuery<Product> productQuery = em.createQuery(
                        "SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.product_id = :pid",
                        Product.class);
                productQuery.setParameter("pid", productId);
                Product product;
                try {
                    product = productQuery.getSingleResult();
                } catch (Exception e) {
                    // Nếu không tìm thấy product, trả về size mặc định
                    ProductSize defaultSize = new ProductSize();
                    defaultSize.setSize_name("Mặc định");
                    defaultSize.setPrice_adjustment(BigDecimal.ZERO);
                    return Collections.singletonList(defaultSize);
                }
                
                // Nếu là thức uống (isDrink = true), tự động tạo và LƯU sizes S/M/L vào DB
                if (product != null && product.getCategory() != null && 
                    Boolean.TRUE.equals(product.getCategory().getIsDrink())) {
                    // Tạo sizes mặc định cho thức uống: S/M/L với giá tăng dần
                    ProductSizeDao sizeDao = new ProductSizeDaoImpl(em);
                    
                    ProductSize sizeS = new ProductSize();
                    sizeS.setSize_name("S");
                    sizeS.setPrice_adjustment(SIZE_S_PRICE);
                    sizeS.setProduct(product);
                    // Lưu vào DB nếu chưa tồn tại
                    if (!sizeDao.exists(productId, "S")) {
                        sizeDao.save(sizeS);
                    }
                    
                    ProductSize sizeM = new ProductSize();
                    sizeM.setSize_name("M");
                    sizeM.setPrice_adjustment(SIZE_M_PRICE);
                    sizeM.setProduct(product);
                    if (!sizeDao.exists(productId, "M")) {
                        sizeDao.save(sizeM);
                    }
                    
                    ProductSize sizeL = new ProductSize();
                    sizeL.setSize_name("L");
                    sizeL.setPrice_adjustment(SIZE_L_PRICE);
                    sizeL.setProduct(product);
                    if (!sizeDao.exists(productId, "L")) {
                        sizeDao.save(sizeL);
                    }
                    
                    // Query lại từ DB để đảm bảo có ID và các thông tin đầy đủ
                    return sizeDao.findByProductId(productId);
                } else {
                    // Nếu không phải thức uống, trả về size "Mặc định" (không lưu vào DB)
                    ProductSize defaultSize = new ProductSize();
                    defaultSize.setSize_name("Mặc định");
                    defaultSize.setPrice_adjustment(BigDecimal.ZERO);
                    return Collections.singletonList(defaultSize);
                }
            }
            return sizes;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Topping> getAvailableToppingsForCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) {
            return List.of();
        }
        EntityManager em = JpaUtil.em();
        try {
            // Lấy category từ name để check isDrink (thay vì hard-code check "trà")
            TypedQuery<Category> catQuery = em.createQuery(
                    "SELECT c FROM Category c WHERE c.name = :name", 
                    Category.class);
            catQuery.setParameter("name", categoryName);
            List<Category> categories = catQuery.getResultList();
            
            // Chỉ trả về toppings nếu là thức uống (isDrink = true)
            if (categories.isEmpty() || !Boolean.TRUE.equals(categories.get(0).getIsDrink())) {
                return List.of();
            }
            
            return em.createQuery("SELECT t FROM Topping t WHERE t.isAvailable = true ORDER BY t.topping_name", Topping.class)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}
