package config;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Tải các dữ liệu dùng chung (như danh mục) khi ứng dụng khởi động
 * để tránh truy vấn DB lặp lại trên mỗi request.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(AppContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Application starting... Loading shared data.");
        loadCategories(sce.getServletContext());
    }

    private void loadCategories(ServletContext context) {
        EntityManager em = JpaUtil.em();
        List<Category> categories = Collections.emptyList();
        try {
            categories = em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class)
                           .getResultList();
            context.setAttribute("APP_CATEGORIES", categories); 
            logger.info("Loaded {} categories into application scope.", categories.size());
        } catch (Exception e) {
            logger.error("Failed to load categories into application scope.", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
         logger.info("Application shutting down.");
    }
}