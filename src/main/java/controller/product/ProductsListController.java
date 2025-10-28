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
            var categories = em.createQuery("SELECT c FROM Category c ORDER BY c.id", model.Category.class)
                              .getResultList();
            req.setAttribute("categories", categories);
            
            String keyword = req.getParameter("q");
            req.setAttribute("searchKeyword", keyword != null ? keyword.trim() : "");

            req.getRequestDispatcher("/views/product/list.jsp").forward(req, resp);
        } finally {
            em.close();
        }
    }
}