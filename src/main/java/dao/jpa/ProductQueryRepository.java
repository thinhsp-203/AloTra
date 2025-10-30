package dao.jpa;
import jakarta.persistence.*; 
import jakarta.persistence.criteria.*; 
import model.Product;
import java.math.BigDecimal; 
import java.util.*;

public class ProductQueryRepository {
  private final EntityManager em; 
  public ProductQueryRepository(EntityManager em){ this.em = em; }

  public List<Product> search(Integer cateId, Integer supplierId, BigDecimal min, BigDecimal max, String q, int page, int size){
    CriteriaBuilder cb = em.getCriteriaBuilder(); 
    CriteriaQuery<Product> cq = cb.createQuery(Product.class);
    Root<Product> root = cq.from(Product.class);
    
    List<Predicate> ps = new ArrayList<>(); 
    
    // Chỉ hiển thị sản phẩm đang active
    ps.add(cb.isTrue(root.get("isActive")));
    
    // Lọc theo danh mục
    if(cateId != null) {
        ps.add(cb.equal(root.get("category").get("id"), cateId));
    }
    
    // Lọc theo nhà cung cấp
    if(supplierId != null) {
        ps.add(cb.equal(root.get("supplier").get("supplier_id"), supplierId));
    }
    
    // Lọc theo khoảng giá
    if(min != null) {
        ps.add(cb.ge(root.get("price"), min));
    }
    if(max != null) {
        ps.add(cb.le(root.get("price"), max));
    }
    
    // Tìm kiếm theo từ khóa (tên sản phẩm hoặc mô tả)
    if(q != null && !q.trim().isEmpty()) {
        String searchPattern = "%" + q.toLowerCase().trim() + "%";
        Predicate namePredicate = cb.like(cb.lower(root.get("product_name")), searchPattern);
        Predicate descPredicate = cb.like(cb.lower(root.get("description")), searchPattern);
        ps.add(cb.or(namePredicate, descPredicate));
    }
    
    cq.where(ps.toArray(new Predicate[0])).orderBy(cb.desc(root.get("createdDate")));
    
    return em.createQuery(cq)
             .setFirstResult(page * size)
             .setMaxResults(size)
             .getResultList();
  }

  public long count(Integer cateId, Integer supplierId, BigDecimal min, BigDecimal max, String q){
    CriteriaBuilder cb = em.getCriteriaBuilder(); 
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Product> root = cq.from(Product.class);
    
    List<Predicate> ps = new ArrayList<>(); 
    
    // Chỉ đếm sản phẩm đang active
    ps.add(cb.isTrue(root.get("isActive")));
    
    // Lọc theo danh mục
    if(cateId != null) {
        ps.add(cb.equal(root.get("category").get("id"), cateId));
    }
    
    // Lọc theo nhà cung cấp
    if(supplierId != null) {
        ps.add(cb.equal(root.get("supplier").get("supplier_id"), supplierId));
    }
    
    // Lọc theo khoảng giá
    if(min != null) {
        ps.add(cb.ge(root.get("price"), min));
    }
    if(max != null) {
        ps.add(cb.le(root.get("price"), max));
    }
    
    // Tìm kiếm theo từ khóa
    if(q != null && !q.trim().isEmpty()) {
        String searchPattern = "%" + q.toLowerCase().trim() + "%";
        Predicate namePredicate = cb.like(cb.lower(root.get("product_name")), searchPattern);
        Predicate descPredicate = cb.like(cb.lower(root.get("description")), searchPattern);
        ps.add(cb.or(namePredicate, descPredicate));
    }
    
    cq.select(cb.count(root)).where(ps.toArray(new Predicate[0]));
    return em.createQuery(cq).getSingleResult();
  }
}