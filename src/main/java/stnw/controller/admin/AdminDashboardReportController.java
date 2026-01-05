package stnw.controller.admin;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminDashboardService;
import stnw.service.impl.AdminDashboardServiceImpl;

@WebServlet(urlPatterns = "/admin/dashboard", asyncSupported = false)
public class AdminDashboardReportController extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AdminDashboardService dashboardService;
    
    @Override
    public void init() throws ServletException {
        dashboardService = new AdminDashboardServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            Map<String, Object> stats = dashboardService.getDashboardStats();
            req.setAttribute("stats", stats);
            req.getRequestDispatcher("/views/admin/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi khi tải dashboard: " + e.getMessage());
            req.getRequestDispatcher("/views/admin/dashboard.jsp").forward(req, resp);
        }
    }
}
