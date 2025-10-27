package dao.jpa;
import jakarta.persistence.*; import jakarta.persistence.criteria.*; import model.Product;
import java.math.BigDecimal; import java.util.*;

public class ProductQueryRepository {
  private final EntityManager em; public ProductQueryRepository(EntityManager em){ this.em = em; }

  public List<Product> search(Integer cateId, Integer supplierId, BigDecimal min, BigDecimal max, String q, int page, int size){
    CriteriaBuilder cb = em.getCriteriaBuilder(); CriteriaQuery<Product> cq = cb.createQuery(Product.class);
    Root<Product> root = cq.from(Product.class);
    List<Predicate> ps = new ArrayList<>(); ps.add(cb.isTrue(root.get("isActive")));
    if(cateId!=null) ps.add(cb.equal(root.get("category").get("cate_id"), cateId));
    if(supplierId!=null) ps.add(cb.equal(root.get("supplier").get("supplier_id"), supplierId));
    if(min!=null) ps.add(cb.ge(root.get("price"), min));
    if(max!=null) ps.add(cb.le(root.get("price"), max));
    if(q!=null && !q.isBlank()) ps.add(cb.like(cb.lower(root.get("product_name")), "%"+q.toLowerCase()+"%"));
    cq.where(ps.toArray(new Predicate[0])).orderBy(cb.desc(root.get("createdDate")));
    return em.createQuery(cq).setFirstResult(page*size).setMaxResults(size).getResultList();
  }

  public long count(Integer cateId, Integer supplierId, BigDecimal min, BigDecimal max, String q){
    CriteriaBuilder cb = em.getCriteriaBuilder(); CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Product> root = cq.from(Product.class);
    List<Predicate> ps = new ArrayList<>(); ps.add(cb.isTrue(root.get("isActive")));
    if(cateId!=null) ps.add(cb.equal(root.get("category").get("cate_id"), cateId));
    if(supplierId!=null) ps.add(cb.equal(root.get("supplier").get("supplier_id"), supplierId));
    if(min!=null) ps.add(cb.ge(root.get("price"), min));
    if(max!=null) ps.add(cb.le(root.get("price"), max));
    if(q!=null && !q.isBlank()) ps.add(cb.like(cb.lower(root.get("product_name")), "%"+q.toLowerCase()+"%"));
    cq.select(cb.count(root)).where(ps.toArray(new Predicate[0]));
    return em.createQuery(cq).getSingleResult();
  }
}
