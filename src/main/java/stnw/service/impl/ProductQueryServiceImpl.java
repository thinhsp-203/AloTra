package stnw.service.impl;

import stnw.dao.CategoryDao;
import stnw.dao.ProductDao;
import stnw.dao.ProductQueryRepository;
import stnw.dao.ProductSizeDao;
import stnw.dao.ToppingDao;
import stnw.dao.impl.CategoryDaoImpl;
import stnw.dao.impl.ProductDaoImpl;
import stnw.dao.impl.ProductQueryRepositoryImpl;
import stnw.dao.impl.ProductSizeDaoImpl;
import stnw.dao.impl.ToppingDaoImpl;
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
    private final ToppingDao toppingDao = new ToppingDaoImpl();
    private final CategoryDao categoryDao = new CategoryDaoImpl();

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
        ProductSizeDao sizeDao = new ProductSizeDaoImpl();
        List<ProductSize> sizes = sizeDao.findByProductId(productId);
        if (sizes.isEmpty()) {
            // Nếu chưa có sizes, kiểm tra xem có phải là thức uống không
            Product product = productDao.findById(productId);
            if (product == null) {
                // Nếu không tìm thấy product, trả về size mặc định
                ProductSize defaultSize = new ProductSize();
                defaultSize.setSize_name("Mặc định");
                defaultSize.setPrice_adjustment(BigDecimal.ZERO);
                return Collections.singletonList(defaultSize);
            }
            
            // Nếu là thức uống (isDrink = true), tự động tạo và LƯU sizes S/M/L vào DB
            if (product.getCategory() != null && Boolean.TRUE.equals(product.getCategory().getIsDrink())) {
                // Tạo sizes mặc định cho thức uống: S/M/L với giá tăng dần
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
    }

    @Override
    public List<Topping> getAvailableToppingsForCategory(String categoryName) {
        // Chỉ trả về topping nếu category là thức uống (isDrink = true)
        if (categoryName == null || categoryName.isEmpty()) {
            return List.of();
        }
        
        // Lấy category để kiểm tra isDrink
        Category category = categoryDao.findByName(categoryName);
        if (category == null) {
            return List.of();
        }
        
        // Chỉ trả về topping nếu category có isDrink = true
        if (!Boolean.TRUE.equals(category.getIsDrink())) {
            return List.of();
        }
        
        // Lấy tất cả topping đang available (isAvailable = true)
        List<Topping> allToppings = toppingDao.findAll();
        // Filter chỉ lấy topping có isAvailable = true
        return allToppings.stream()
                .filter(t -> t.getIsAvailable() != null && t.getIsAvailable())
                .toList();
    }
}
