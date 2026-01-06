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

@WebServlet(urlPatterns = "/admin/banners/edit")
@MultipartConfig
public class BannerEditController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminBannerService bannerService;
    
    @Override
    public void init() throws ServletException {
        bannerService = new AdminBannerServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Banner banner = bannerService.getBannerById(id);
            
            if (banner == null) {
                req.getSession().setAttribute("error", "Banner không tồn tại!");
                resp.sendRedirect(req.getContextPath() + "/admin/banners");
                return;
            }
            
            req.setAttribute("banner", banner);
            req.getRequestDispatcher("/views/admin/banner-form.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("error", "ID không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/admin/banners");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/banners");
        }
    }
}

