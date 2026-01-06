package stnw.controller.admin.about;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminAboutService;
import stnw.service.impl.AdminAboutServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/about/edit")
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class AboutEditController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminAboutService aboutService;
    
    @Override
    public void init() throws ServletException {
        aboutService = new AdminAboutServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            stnw.model.AboutUs about = aboutService.getAboutUsById(id);
            
            if (about == null) {
                req.getSession().setAttribute("error", "Bài viết không tồn tại!");
                resp.sendRedirect(req.getContextPath() + "/admin/about");
                return;
            }
            
            req.setAttribute("about", about);
            req.getRequestDispatcher("/views/admin/about-form.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("error", "ID không hợp lệ!");
            resp.sendRedirect(req.getContextPath() + "/admin/about");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/about");
        }
    }
}

