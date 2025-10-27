package controller;

import config.JpaUtil;
import dao.jpa.ProductRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;


public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Trang chủ nên public. Nếu bạn muốn chặn, xem biến thể B bên dưới.
        // Nạp dữ liệu bằng JPA trước khi forward
        var em = JpaUtil.em();
        try {
            var repo = new ProductRepository(em);
            req.setAttribute("featured", repo.findFeatured(8));
            req.setAttribute("newest",   repo.findNewest(8));
        } finally {
            em.close();
        }

        // Forward SAU khi set attribute
        req.getRequestDispatcher("/views/home.jsp").forward(req, resp);
    }
}
