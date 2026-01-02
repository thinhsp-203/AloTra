package controller.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Banner;
import service.AdminBannerService;
import service.AdminSettingsService;
import service.impl.AdminBannerServiceImpl;
import service.impl.AdminSettingsServiceImpl;

@WebServlet(urlPatterns = "/admin/banners")
@MultipartConfig
public class AdminBannerController extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AdminBannerService bannerService;
	private AdminSettingsService settingsService;
    
    @Override
    public void init() throws ServletException {
        bannerService = new AdminBannerServiceImpl();
        settingsService = new AdminSettingsServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        req.setAttribute("banners", bannerService.getAllBanners());
        // Load site settings để hiển thị logo hiện tại
        req.setAttribute("siteSettings", settingsService.getAllSettings());
        req.getRequestDispatcher("/views/admin/banners.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String action = req.getParameter("action");
        
        try {
            if ("updateLogo".equals(action)) {
                // Cập nhật logo
                var settings = new java.util.HashMap<String, String>();
                settings.put("LOGO_URL", req.getParameter("LOGO_URL"));
                settingsService.updateSettings(settings);
                
                // Reload application scope
                config.AppContextListener.loadSiteSettings(req.getServletContext());
                
                req.getSession().setAttribute("success", "Đã cập nhật logo!");
                
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                bannerService.deleteBanner(id);
                
            } else if ("add".equals(action)) {
                Banner newBanner = new Banner();
                
                Part filePart = req.getPart("bannerFile");
                String imageUrlFromText = req.getParameter("imageUrl");
                
                newBanner.setLinkUrl(req.getParameter("linkUrl"));
                
                String sortOrderStr = req.getParameter("sortOrder");
                newBanner.setSortOrder((sortOrderStr == null || sortOrderStr.isEmpty()) 
                    ? 0 
                    : Integer.parseInt(sortOrderStr));
                
                newBanner.setActive(req.getParameter("isActive") != null);
                
                bannerService.saveBanner(newBanner, filePart, imageUrlFromText);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/banners");
    }
}