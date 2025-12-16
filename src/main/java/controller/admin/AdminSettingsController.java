package controller.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AdminSettingsService;
import service.impl.AdminSettingsServiceImpl;

@WebServlet(urlPatterns = {"/admin/settings"})
public class AdminSettingsController extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AdminSettingsService settingsService;
    
    @Override
    public void init() throws ServletException {
        settingsService = new AdminSettingsServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        req.setAttribute("settings", settingsService.getAllSettings());
        req.getRequestDispatcher("/views/admin/settings.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        try {
            var settings = new java.util.HashMap<String, String>();
            settings.put("LOGO_URL", req.getParameter("LOGO_URL"));
            settings.put("BANNER_URL", req.getParameter("BANNER_URL"));
            settings.put("BANNER_TEXT", req.getParameter("BANNER_TEXT"));
            
            settingsService.updateSettings(settings);
            req.getSession().setAttribute("success", "Đã cập nhật cài đặt!");
            
            // Reload application scope
            config.AppContextListener.loadSiteSettings(req.getServletContext());
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi khi lưu cài đặt: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/settings");
    }
}