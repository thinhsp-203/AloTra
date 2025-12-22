package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import service.CatalogService;
import service.impl.CatalogServiceImpl;

@WebServlet(name = "HomeController",
urlPatterns = {"/home", "/trang-chu", ""})
public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private CatalogService catalogService;

    @Override
    public void init() throws ServletException {
        catalogService = new CatalogServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        req.setAttribute("featured", catalogService.getFeaturedProducts(8));
        req.setAttribute("newest",   catalogService.getNewestProducts(8));
        req.setAttribute("categories", catalogService.getAllCategories());
        req.setAttribute("banners", catalogService.getActiveBanners());
        req.getRequestDispatcher("/views/home.jsp").forward(req, resp);
    }
}