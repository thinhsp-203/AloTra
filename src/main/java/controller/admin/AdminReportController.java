package controller.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AdminReportService;
import service.impl.AdminReportServiceImpl;

@WebServlet(urlPatterns = "/admin/reports")
public class AdminReportController extends HttpServlet {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AdminReportService reportService;
    
    @Override
    public void init() throws ServletException {
        reportService = new AdminReportServiceImpl();
    }
    
    @Override 
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            req.setAttribute("rev", reportService.getMonthlyRevenue());
            req.setAttribute("top", reportService.getTopProducts(10));
            req.setAttribute("stock", reportService.getStockReport());
            
            req.getRequestDispatcher("/views/admin/reports.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi khi tải báo cáo: " + e.getMessage());
            req.getRequestDispatcher("/views/admin/reports.jsp").forward(req, resp);
        }
    }
}