package controller;

import config.JpaUtil;
import dao.jpa.ProductRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.Category;

public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        var em = JpaUtil.em();
        try {
            var repo = new ProductRepository(em);
            req.setAttribute("featured", repo.findFeatured(8));
            req.setAttribute("newest",   repo.findNewest(8));

            // THÊM LOGIC LẤY DANH MỤC
            List<Category> categories = em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class)
                                          .getResultList();
            req.setAttribute("categories", categories);

        } finally {
            em.close();
        }
        req.getRequestDispatcher("/views/home.jsp").forward(req, resp);
    }
}