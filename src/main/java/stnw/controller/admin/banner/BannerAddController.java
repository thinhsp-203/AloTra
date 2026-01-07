package stnw.controller.admin.banner;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Banner;
import stnw.service.AdminBannerService;
import stnw.service.impl.AdminBannerServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/banners/create")
@MultipartConfig
public class BannerAddController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminBannerService bannerService;
    
    @Override
    public void init() throws ServletException {
        bannerService = new AdminBannerServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        Banner banner = new Banner();
        int maxSortOrder = bannerService.getMaxSortOrder();
        banner.setSortOrder(maxSortOrder + 1);
        req.setAttribute("banner", banner);
        req.getRequestDispatcher("/views/admin/banner-form.jsp").forward(req, resp);
    }
}

