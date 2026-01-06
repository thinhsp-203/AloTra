package stnw.controller.admin.banner;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import stnw.model.Banner;
import stnw.service.AdminBannerService;
import stnw.service.AdminSettingsService;
import stnw.service.impl.AdminBannerServiceImpl;
import stnw.service.impl.AdminSettingsServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = {"/admin/banners/save"})
@MultipartConfig
public class BannerSaveController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminBannerService bannerService;
    private AdminSettingsService settingsService;
    
    @Override
    public void init() throws ServletException {
        bannerService = new AdminBannerServiceImpl();
        settingsService = new AdminSettingsServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String action = req.getParameter("action");
        
        try {
            if ("updateLogo".equals(action)) {
                var settings = new java.util.HashMap<String, String>();
                settings.put("LOGO_URL", req.getParameter("LOGO_URL"));
                settingsService.updateSettings(settings);
                stnw.config.AppContextListener.loadSiteSettings(req.getServletContext());
                req.getSession().setAttribute("success", "Đã cập nhật logo!");
            } else {
                String idParam = req.getParameter("id");
                Integer id = (idParam != null && !idParam.isEmpty()) 
                    ? Integer.parseInt(idParam) 
                    : null;
                
                Banner banner = (id != null) 
                    ? bannerService.getBannerById(id) 
                    : new Banner();
                
                banner.setLinkUrl(null);
                
                if (id != null) {
                    String sortOrderStr = req.getParameter("sortOrder");
                    if (sortOrderStr != null && !sortOrderStr.isEmpty()) {
                        banner.setSortOrder(Integer.parseInt(sortOrderStr));
                    }
                }
                
                banner.setActive(req.getParameter("isActive") != null);
                
                Part filePart = req.getPart("bannerFile");
                String imageUrl = req.getParameter("imageUrl");
                
                bannerService.saveBanner(banner, filePart, imageUrl, req.getServletContext());
                req.getSession().setAttribute("success", "Đã lưu banner thành công!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/banners");
    }
}

