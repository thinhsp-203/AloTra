package stnw.controller.admin;

import java.io.IOException;

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

@WebServlet(urlPatterns = {
    "/admin/banners",
    "/admin/banners/create",
    "/admin/banners/edit",
    "/admin/banners/save"
})
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
        String uri = req.getRequestURI();
        
        try {
            if (uri.endsWith("/admin/banners")) {
                showBannerList(req, resp);
            } else if (uri.endsWith("/admin/banners/create")) {
                showBannerForm(req, resp, null);
            } else if (uri.endsWith("/admin/banners/edit")) {
                int id = Integer.parseInt(req.getParameter("id"));
                showBannerForm(req, resp, id);
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("error", "ID không hợp l�?");
            resp.sendRedirect(req.getContextPath() + "/admin/banners");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/banners");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String uri = req.getRequestURI();
        String action = req.getParameter("action");
        
        try {
            if (uri.endsWith("/admin/banners/save")) {
                saveBanner(req, resp);
            } else if ("updateLogo".equals(action)) {
                // Cập nhật logo
                var settings = new java.util.HashMap<String, String>();
                settings.put("LOGO_URL", req.getParameter("LOGO_URL"));
                settingsService.updateSettings(settings);
                
                // Reload application scope
                stnw.config.AppContextListener.loadSiteSettings(req.getServletContext());
                
                req.getSession().setAttribute("success", "Đã cập nhật logo!");
                resp.sendRedirect(req.getContextPath() + "/admin/banners");
                
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                bannerService.deleteBanner(id, req.getServletContext());
                req.getSession().setAttribute("success", "Đã xóa banner!");
                resp.sendRedirect(req.getContextPath() + "/admin/banners");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/banners");
        }
    }
    
    private void showBannerList(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        req.setAttribute("banners", bannerService.getAllBanners());
        // Load site settings đ�?hiển th�?logo hiện tại
        req.setAttribute("siteSettings", settingsService.getAllSettings());
        req.getRequestDispatcher("/views/admin/banners.jsp").forward(req, resp);
    }
    
    private void showBannerForm(HttpServletRequest req, HttpServletResponse resp, Integer id)
        throws ServletException, IOException {
        Banner banner;
        
        if (id != null) {
            banner = bannerService.getBannerById(id);
            if (banner == null) {
                req.getSession().setAttribute("error", "Banner không tồn tại!");
                resp.sendRedirect(req.getContextPath() + "/admin/banners");
                return;
            }
        } else {
            banner = new Banner();
            // T�?động set th�?t�?= max + 1
            int maxSortOrder = bannerService.getMaxSortOrder();
            banner.setSortOrder(maxSortOrder + 1);
        }
        
        req.setAttribute("banner", banner);
        req.getRequestDispatcher("/views/admin/banner-form.jsp").forward(req, resp);
    }
    
    private void saveBanner(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            String idParam = req.getParameter("id");
            Integer id = (idParam != null && !idParam.isEmpty()) 
                ? Integer.parseInt(idParam) 
                : null;
            
            Banner banner = (id != null) 
                ? bannerService.getBannerById(id) 
                : new Banner();
            
            banner.setLinkUrl(null);
            
            // Ch�?set sortOrder t�?form nếu đang edit, còn create thì đã set t�?động trong service
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
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/banners");
    }
}