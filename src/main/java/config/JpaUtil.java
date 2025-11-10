package config;

import jakarta.persistence.*;

public class JpaUtil {

    private static EntityManagerFactory emf;
    public static EntityManagerFactory getEMFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("alotraPU");
        }
        return emf;
    }
    public static EntityManager em() {
        if (emf == null) {
            getEMFactory(); 
        }
        return emf.createEntityManager();
    }

    public static void closeEMFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}