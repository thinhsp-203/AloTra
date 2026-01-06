package stnw.controller.user.wishlist;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.User;
import stnw.service.WishlistService;
import stnw.service.impl.WishlistServiceImpl;
import java.io.IOException;

@WebServlet(urlPatterns = "/user/wishlist")
public class WishlistController extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WishlistService wishlistService;

    @Override
    public void init() throws ServletException {
        wishlistService = new WishlistServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("currentUser");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setAttribute("wishlistItems", wishlistService.listItems(user.getId()));
        req.getRequestDispatcher("/views/user/wishlist.jsp").forward(req, resp);
    }
}

