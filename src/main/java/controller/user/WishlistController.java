package controller.user;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.WishlistItem;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/user/wishlist")
public class WishlistController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        EntityManager em = JpaUtil.em();
        TypedQuery<WishlistItem> query = em.createQuery(
            "SELECT w FROM WishlistItem w JOIN FETCH w.product WHERE w.user.id = :userId ORDER BY w.addedDate DESC", 
            WishlistItem.class
        );
        query.setParameter("userId", user.getId());
        List<WishlistItem> items = query.getResultList();
        
        req.setAttribute("wishlistItems", items);
        req.getRequestDispatcher("/views/user/wishlist.jsp").forward(req, resp);
    }
}