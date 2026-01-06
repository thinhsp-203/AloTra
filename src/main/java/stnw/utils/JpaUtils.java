package stnw.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * JPAUtil: Singleton EMF + EM per-call, dùng cho Servlet/Tomcat.
 */
public final class JpaUtils {

    // ĐỔI TÊN NÀY CHO KHỚP persistence.xml
    private static final String PU_NAME = "AloTra";

    // double-checked locking
    private static volatile EntityManagerFactory emf;

    private JpaUtils() {}

    private static EntityManagerFactory getEMF() {
        EntityManagerFactory local = emf;
        if (local == null) {
            synchronized (JpaUtils.class) {
                local = emf;
                if (local == null) {
                    emf = local = Persistence.createEntityManagerFactory(PU_NAME);
                }
            }
        }
        return local;
    }

    /** Luôn cấp EntityManager mới; DAO phải đóng trong finally. */
    public static EntityManager em() {
        return getEMF().createEntityManager();
    }

    /** Dọn tài nguyên khi app dừng. Gọi từ Listener. */
    public static synchronized void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }
}

