package config;

import dao.CategoryRepository;
import dao.impl.CategoryRepositoryImpl;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.Settings;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@WebListener
public class AppContextListener implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Application starting... Loading shared data.");

        ServletContext context = sce.getServletContext();
        loadCategories(context);
        loadSiteSettings(context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down.");

        try {
            // Đóng EntityManagerFactory / Hibernate đúng cách
            JpaUtil.shutdown();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while shutting down JPA", e);
        } finally {
            // Cleanup JDBC drivers để giảm warning/severe leak khi reload/redeploy trên Tomcat
            deregisterJdbcDrivers();
        }
    }

    /**
     * Tải danh mục và lưu vào Application Scope
     */
    private void loadCategories(ServletContext context) {
        EntityManager em = null;
        try {
            em = JpaUtil.em();
            CategoryRepository repo = new CategoryRepositoryImpl(em);
            context.setAttribute("categories", repo.findAll());
            logger.info("Categories loaded into application scope.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load categories", e);
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * Tải các cài đặt (logo, banner) từ DB vào Application Scope (biến ${siteSettings})
     */
    public static void loadSiteSettings(ServletContext context) {
        EntityManager em = null;
        try {
            em = JpaUtil.em();
            List<Settings> settingsList =
                    em.createQuery("SELECT s FROM Settings s", Settings.class)
                      .getResultList();

            Map<String, String> settingsMap = settingsList.stream()
                    .filter(s -> s.getValue() != null)
                    .collect(Collectors.toMap(Settings::getKey, Settings::getValue));

            context.setAttribute("siteSettings", settingsMap);
            logger.info("Site settings (Logo, Banner, etc.) loaded into application scope.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load site settings", e);
        } finally {
            closeEntityManager(em);
        }
    }

    private static void closeEntityManager(EntityManager em) {
        try {
            if (em != null && em.isOpen()) {
                em.close();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while closing EntityManager", e);
        }
    }

    /**
     * Gỡ đăng ký JDBC drivers được load bởi classloader của webapp.
     * Giúp giảm cảnh báo:
     * - registered the JDBC driver ... but failed to unregister it
     * - ThreadLocal leak khi Tomcat reload/redeploy
     */
    private void deregisterJdbcDrivers() {
        try {
            ClassLoader webAppCl = this.getClass().getClassLoader();
            Enumeration<Driver> drivers = DriverManager.getDrivers();

            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                // Chỉ gỡ các driver do webapp load (tránh đụng driver của container)
                if (driver.getClass().getClassLoader() == webAppCl) {
                    try {
                        DriverManager.deregisterDriver(driver);
                        logger.info("Deregistered JDBC driver: " + driver.getClass().getName());
                    } catch (Exception ex) {
                        logger.log(Level.WARNING, "Failed to deregister JDBC driver: " + driver, ex);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to deregister JDBC drivers", e);
        }
    }
}
