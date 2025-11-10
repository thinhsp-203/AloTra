package dao.jpa;
import jakarta.persistence.EntityManager; import model.Product; import java.util.List;
public class ProductRepository {
  private final EntityManager em; public ProductRepository(EntityManager em){ this.em = em; }
  public List<Product> findFeatured(int limit){ return em.createQuery(
    "select p from Product p where p.isActive = true and p.isFeatured = true order by p.createdDate desc", Product.class
  ).setMaxResults(limit).getResultList(); }
  public List<Product> findNewest(int limit){ return em.createQuery(
    "select p from Product p where p.isActive = true order by p.createdDate desc", Product.class
  ).setMaxResults(limit).getResultList(); }
  public Product findById(int id, EntityManager em2) {
	// TODO Auto-generated method stub
	return null;
  }
  public List<Product> findRelatedProducts(int id, Integer product_id, EntityManager em2) {
	// TODO Auto-generated method stub
	return null;
  }
}
