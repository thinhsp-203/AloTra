package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import model.User;
import service.UserService;
import service.UserServiceImpl;
import utils.Roles;

@WebServlet(urlPatterns = "/admin/users")
public class UserListController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserService userService = new UserServiceImpl();
    private static final int[] PAGE_SIZES = {5, 10, 20, 50};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null || currentUser.getRoleId() != 1) {
            resp.sendRedirect(req.getContextPath()  "/login");
            return;
        }

        String keyword = trimToNull(req.getParameter("keyword"));
        String roleParam = trimToNull(req.getParameter("roleId"));
        Integer roleId = parseInteger(roleParam);
        int pageSize = parsePositive(req.getParameter("size"), 10);
        int requestedPage = parsePositive(req.getParameter("page"), 1);

        int totalUsers = userService.countUsers(keyword, roleId);
        int totalPages = totalUsers == 0 ? 0 : (int) Math.ceil(totalUsers / (double) pageSize);
        int currentPage = totalPages == 0 ? 1 : Math.min(requestedPage, totalPages);
        List<User> users = totalUsers == 0
                ? Collections.emptyList()
                : userService.searchUsers(keyword, roleId, currentPage, pageSize);

        int fromRecord = totalUsers == 0 ? 0 : (currentPage - 1) * pageSize  1;
        int toRecord = totalUsers == 0 ? 0 : Math.min(fromRecord  pageSize - 1, totalUsers);

        req.setAttribute("users", users);
        req.setAttribute("keyword", keyword != null ? keyword : "");
        req.setAttribute("selectedRoleId", roleId);
        req.setAttribute("roleIdParam", roleParam != null ? roleParam : "");
        req.setAttribute("page", currentPage);
        req.setAttribute("pageSize", pageSize);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalUsers", totalUsers);
        req.setAttribute("fromRecord", fromRecord);
        req.setAttribute("toRecord", toRecord);
        req.setAttribute("pageSizes", PAGE_SIZES);
        req.setAttribute("roles", Roles.names());

        req.getRequestDispatcher("/views/admin/list-user.jsp").forward(req, resp);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private int parsePositive(String value, int defaultValue) {
        Integer parsed = parseInteger(value);
        return (parsed == null || parsed <= 0) ? defaultValue : parsed;
    }

    private Integer parseInteger(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}