package stnw.service.impl;

import stnw.dao.ProductDao;
import stnw.dao.impl.ProductDaoImpl;
import stnw.model.Product;
import stnw.service.ProductService;
import java.util.List;

public class ProductServiceImpl implements ProductService {
    
    private ProductDao productDao = new ProductDaoImpl();

    @Override
    public List<Product> getAllProducts(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return productDao.findAll(offset, pageSize);
    }

    @Override
    public Product getProductById(int id) {
        return productDao.findById(id);
    }

    @Override
    public void saveProduct(Product product) {
        // Validate dữ liệu trước khi lưu
        if (product.getPrice() == null || product.getPrice().doubleValue() < 0) {
            throw new IllegalArgumentException("Giá sản phẩm không hợp lệ!");
        }
        productDao.save(product);
    }

    @Override
    public void deleteProduct(int id) {
        productDao.delete(id);
    }

    @Override
    public int getTotalPages(int pageSize) {
        long total = productDao.count();
        return (int) Math.ceil((double) total / pageSize);
    }
}
