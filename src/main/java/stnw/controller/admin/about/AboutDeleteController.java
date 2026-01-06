package stnw.controller.admin.about;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminAboutService;
import stnw.service.impl.AdminAboutServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/about/delete")
public class AboutDeleteController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminAboutService aboutService;
    
    @Override
    public void init() throws ServletException {
        aboutService = new AdminAboutServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            aboutService.deleteAboutUs(id, req.getServletContext());
            req.getSession().setAttribute("success", "Đã xóa bài viết!");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/about");
    }
}

