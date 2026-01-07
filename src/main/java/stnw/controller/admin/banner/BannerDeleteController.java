package stnw.controller.admin.banner;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminBannerService;
import stnw.service.impl.AdminBannerServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/banners/delete")
public class BannerDeleteController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminBannerService bannerService;
    
    @Override
    public void init() throws ServletException {
        bannerService = new AdminBannerServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            bannerService.deleteBanner(id, req.getServletContext());
            req.getSession().setAttribute("success", "Đã xóa banner!");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/banners");
    }
}

