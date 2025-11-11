package config;

import dao.jpa.CategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.Settings;

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

        JpaUtil.getEMFactory(); 
        
        ServletContext context = sce.getServletContext();
        loadCategories(context);
        loadSiteSettings(context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application shutting down.");
  
        JpaUtil.closeEMFactory();
    }

    /**
     * Tải danh mục và lưu vào Application Scope
     */
    private void loadCategories(ServletContext context) {
        EntityManager em = JpaUtil.em(); 
        try {
        	CategoryRepository repo = new CategoryRepository(em); 
            context.setAttribute("categories", repo.findAll());
            logger.info("Categories loaded into application scope.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load categories", e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
    
    /**
     * Tải các cài đặt (logo, banner) từ DB vào Application Scope (biến ${siteSettings})
     */
    public static void loadSiteSettings(ServletContext context) {
        EntityManager em = JpaUtil.em(); 
        try {
            List<Settings> settingsList = em.createQuery("SELECT s FROM Settings s", Settings.class).getResultList();
            
            Map<String, String> settingsMap = settingsList.stream()
                .filter(s -> s.getValue() != null)
                .collect(Collectors.toMap(Settings::getKey, Settings::getValue));
            
            context.setAttribute("siteSettings", settingsMap);
            logger.info("Site settings (Logo, Banner, etc.) loaded into application scope.");
        } catch (Exception e) {
             logger.log(Level.SEVERE, "Failed to load site settings", e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}