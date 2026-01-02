package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AboutService;
import service.impl.AboutServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = {"/about", "/ve-chung-toi"})
public class AboutController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private AboutService aboutService;
    
    @Override
    public void init() throws ServletException {
        aboutService = new AboutServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("aboutList", aboutService.getActiveAboutUs());
        req.setAttribute("pageTitle", "Về chúng tôi - AloTra");
        req.getRequestDispatcher("/views/about.jsp").forward(req, resp);
    }
}

