package stnw.config;

import jakarta.persistence.EntityManager;
import stnw.utils.JpaUtils;

/**
 * Bridge config class for JPA.
 * Delegates to {@link stnw.utils.JpaUtils} to keep compatibility with code or docs
 * that reference JpaConfig instead of JpaUtils.
 */
public class JpaConfig {

    /**
     * Create a new EntityManager.
     */
    public static EntityManager em() {
        return JpaUtils.em();
    }

    /**
     * Shutdown the shared EntityManagerFactory.
     */
    public static void shutdown() {
        JpaUtils.shutdown();
    }
}

