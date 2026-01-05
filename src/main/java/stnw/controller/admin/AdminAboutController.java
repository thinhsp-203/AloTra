package stnw.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import stnw.model.AboutUs;
import stnw.service.AdminAboutService;
import stnw.service.impl.AdminAboutServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = {
    "/admin/about",
    "/admin/about/create",
    "/admin/about/edit",
    "/admin/about/save",
    "/admin/about/delete"
})
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class AdminAboutController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private AdminAboutService aboutService;
    
    @Override
    public void init() throws ServletException {
        aboutService = new AdminAboutServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        
        try {
            if (uri.endsWith("/admin/about")) {
                showAboutList(req, resp);
            } else if (uri.endsWith("/admin/about/create")) {
                showAboutForm(req, resp, null);
            } else if (uri.endsWith("/admin/about/edit")) {
                int id = Integer.parseInt(req.getParameter("id"));
                showAboutForm(req, resp, id);
            }
        } catch (NumberFormatException e) {
            req.getSession().setAttribute("error", "ID không hợp l�?");
            resp.sendRedirect(req.getContextPath() + "/admin/about");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/admin/about");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String uri = req.getRequestURI();
        
        try {
            if (uri.endsWith("/admin/about/save")) {
                saveAbout(req, resp);
            } else if (uri.endsWith("/admin/about/delete")) {
                deleteAbout(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/about");
    }
    
    private void showAboutList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("aboutList", aboutService.getAllAboutUs());
        req.getRequestDispatcher("/views/admin/about.jsp").forward(req, resp);
    }
    
    private void showAboutForm(HttpServletRequest req, HttpServletResponse resp, Integer id)
            throws ServletException, IOException {
        AboutUs about;
        
        if (id != null) {
            about = aboutService.getAboutUsById(id);
            if (about == null) {
                req.getSession().setAttribute("error", "Bài viết không tồn tại!");
                resp.sendRedirect(req.getContextPath() + "/admin/about");
                return;
            }
        } else {
            about = new AboutUs();
        }
        
        req.setAttribute("about", about);
        req.getRequestDispatcher("/views/admin/about-form.jsp").forward(req, resp);
    }
    
    private void saveAbout(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String idParam = req.getParameter("id");
            Integer id = (idParam != null && !idParam.isEmpty()) 
                ? Integer.parseInt(idParam) 
                : null;
            
            AboutUs about = (id != null) 
                ? aboutService.getAboutUsById(id) 
                : new AboutUs();
            
            about.setTitle(req.getParameter("title"));
            about.setContent(req.getParameter("content"));
            
            String sortOrderParam = req.getParameter("sortOrder");
            about.setSortOrder((sortOrderParam == null || sortOrderParam.isEmpty()) 
                ? 0 
                : Integer.parseInt(sortOrderParam));
            
            about.setIsActive(req.getParameter("isActive") != null);
            
            Part imageFile = req.getPart("imageFile");
            String imageUrl = req.getParameter("imageUrl");
            
            aboutService.saveAboutUs(about, imageFile, imageUrl, req.getServletContext());
            
            req.getSession().setAttribute("success", "Đã lưu bài viết thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
    }
    
    private void deleteAbout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            aboutService.deleteAboutUs(id, req.getServletContext());
            req.getSession().setAttribute("success", "Đã xóa bài viết!");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
    }
}

