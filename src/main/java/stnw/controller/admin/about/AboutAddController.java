package stnw.controller.admin.about;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.AboutUs;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/about/create")
@MultipartConfig(
    fileSizeThreshold = 2 * 1024 * 1024,
    maxFileSize = 10 * 1024 * 1024,
    maxRequestSize = 50 * 1024 * 1024
)
public class AboutAddController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        req.setAttribute("about", new AboutUs());
        req.getRequestDispatcher("/views/admin/about-form.jsp").forward(req, resp);
    }
}

