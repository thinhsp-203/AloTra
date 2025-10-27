package config;
import jakarta.persistence.*;
public class JpaUtil {
  private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("alotraPU");
  public static EntityManager em(){ return emf.createEntityManager(); }
}
