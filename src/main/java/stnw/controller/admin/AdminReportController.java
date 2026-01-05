package stnw.controller.admin;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminReportService;
import stnw.service.impl.AdminReportServiceImpl;

@WebServlet(urlPatterns = "/admin/reports")
public class AdminReportController extends HttpServlet {
    
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
            // Lấy loại báo cáo (mặc định là "revenue")
            String reportType = req.getParameter("type");
            if (reportType == null || reportType.isEmpty()) {
                reportType = "revenue";
            }
            
            // Parse ngày bắt đầu và kết thúc
            LocalDateTime startDate = null;
            LocalDateTime endDate = null;
            
            String startDateStr = req.getParameter("startDate");
            String endDateStr = req.getParameter("endDate");
            
            if (startDateStr != null && !startDateStr.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(startDateStr);
                    startDate = date.atStartOfDay();
                } catch (DateTimeParseException e) {
                    // Invalid date format, ignore
                }
            }
            
            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(endDateStr);
                    endDate = date.atTime(LocalTime.MAX);
                } catch (DateTimeParseException e) {
                    // Invalid date format, ignore
                }
            }
            
            // Nếu không có date range, mặc định là tháng này
            if (startDate == null && endDate == null) {
                LocalDate today = LocalDate.now();
                startDate = today.withDayOfMonth(1).atStartOfDay();
                endDate = today.atTime(LocalTime.MAX);
            }
            
            // Đặt attribute cho form
            req.setAttribute("reportType", reportType);
            req.setAttribute("startDate", startDate != null ? startDate.toLocalDate().toString() : "");
            req.setAttribute("endDate", endDate != null ? endDate.toLocalDate().toString() : "");
            
            // Load dữ liệu theo loại báo cáo
            switch (reportType) {
                case "revenue":
                    // Báo cáo doanh thu
                    req.setAttribute("monthlyRevenue", reportService.getMonthlyRevenue(startDate, endDate));
                    req.setAttribute("dailyRevenue", reportService.getDailyRevenue(startDate, endDate));
                    
                    // So sánh với kỳ trước (tháng trước nếu filter theo tháng)
                    if (startDate != null && endDate != null) {
                        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate());
                        LocalDateTime prevStartDate = startDate.minusDays(daysBetween + 1);
                        LocalDateTime prevEndDate = startDate.minusDays(1).toLocalDate().atTime(LocalTime.MAX);
                        req.setAttribute("revenueComparison", 
                            reportService.compareRevenuePeriods(startDate, endDate, prevStartDate, prevEndDate));
                    }
                    break;
                    
                case "products":
                    // Báo cáo sản phẩm
                    req.setAttribute("topProducts", reportService.getTopProducts(startDate, endDate, 20));
                    req.setAttribute("productRevenue", reportService.getProductRevenue(startDate, endDate));
                    break;
                    
                case "orders":
                    // Báo cáo đơn hàng
                    req.setAttribute("orderStatsByStatus", reportService.getOrderStatsByStatus(startDate, endDate));
                    req.setAttribute("orderStatsByPayment", reportService.getOrderStatsByPaymentMethod(startDate, endDate));
                    break;
                    
                case "customers":
                    // Báo cáo khách hàng
                    req.setAttribute("topCustomersByRevenue", reportService.getTopCustomersByRevenue(startDate, endDate, 20));
                    req.setAttribute("topCustomersByOrderCount", reportService.getTopCustomersByOrderCount(startDate, endDate, 20));
                    break;
                    
                default:
                    reportType = "revenue";
                    req.setAttribute("reportType", reportType);
                    req.setAttribute("monthlyRevenue", reportService.getMonthlyRevenue(startDate, endDate));
                    req.setAttribute("dailyRevenue", reportService.getDailyRevenue(startDate, endDate));
                    break;
            }
            
            req.getRequestDispatcher("/views/admin/reports.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Lỗi khi tải báo cáo: " + e.getMessage());
            req.getRequestDispatcher("/views/admin/reports.jsp").forward(req, resp);
        }
    }
}
