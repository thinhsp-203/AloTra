package stnw.controller.admin.about;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminAboutService;
import stnw.service.impl.AdminAboutServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/about")
public class AboutListController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminAboutService aboutService;
    
    @Override
    public void init() throws ServletException {
        aboutService = new AdminAboutServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        req.setAttribute("aboutList", aboutService.getAllAboutUs());
        req.getRequestDispatcher("/views/admin/about.jsp").forward(req, resp);
    }
}

