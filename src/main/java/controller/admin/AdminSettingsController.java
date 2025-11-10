package controller.admin;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Settings;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/admin/settings"})
public class AdminSettingsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        
        EntityManager em = JpaUtil.em();
        // Lấy tất cả cài đặt từ DB và chuyển thành dạng Map (key -> value)
        try {
            List<Settings> settingsList = em.createQuery("SELECT s FROM Settings s", Settings.class).getResultList();
            Map<String, String> settingsMap = settingsList.stream()
                .collect(Collectors.toMap(Settings::getKey, Settings::getValue));
            req.setAttribute("settings", settingsMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        
        req.getRequestDispatcher("/views/admin/settings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();

            // Lấy các giá trị từ form
            String logoUrl = req.getParameter("LOGO_URL");
            String bannerUrl = req.getParameter("BANNER_URL");
            String bannerText = req.getParameter("BANNER_TEXT");
            
            // Cập nhật hoặc Tạo mới
            saveSetting(em, "LOGO_URL", logoUrl);
            saveSetting(em, "BANNER_URL", bannerUrl);
            saveSetting(em, "BANNER_TEXT", bannerText);
            
            em.getTransaction().commit();
            req.getSession().setAttribute("success", "Đã cập nhật cài đặt!");

            // Cập nhật lại settings trong application scope
            config.AppContextListener.loadSiteSettings(req.getServletContext());

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi khi lưu cài đặt: " + e.getMessage());
        } finally {
            em.close();
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/settings");
    }

    /**
     * Hàm trợ giúp để Cập nhật (merge) hoặc Tạo mới (persist) một cài đặt
     */
    private void saveSetting(EntityManager em, String key, String value) {
        Settings setting = em.find(Settings.class, key);
        if (setting == null) {
            // Tạo mới nếu chưa có
            setting = new Settings();
            setting.setKey(key);
        }
        setting.setValue(value);
        em.merge(setting); // Dùng merge để vừa tạo mới vừa cập nhật
    }
}