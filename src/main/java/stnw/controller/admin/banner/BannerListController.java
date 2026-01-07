package stnw.controller.admin.banner;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminBannerService;
import stnw.service.AdminSettingsService;
import stnw.service.impl.AdminBannerServiceImpl;
import stnw.service.impl.AdminSettingsServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/banners")
public class BannerListController extends HttpServlet {
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
        req.setAttribute("siteSettings", settingsService.getAllSettings());
        req.getRequestDispatcher("/views/admin/banners.jsp").forward(req, resp);
    }
}

