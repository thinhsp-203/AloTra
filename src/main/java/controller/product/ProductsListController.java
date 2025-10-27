package controller.product;

import config.JpaUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = "/products")
public class ProductsListController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        var em = JpaUtil.em();
        try {
            // Load categories cho filter sidebar
            var categories = em.createQuery("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.displayOrder", model.Category.class)
                              .getResultList();
            req.setAttribute("categories", categories);
            
            req.getRequestDispatcher("/views/product/products.jsp").forward(req, resp);
        } finally {
            em.close();
        }
    }
}