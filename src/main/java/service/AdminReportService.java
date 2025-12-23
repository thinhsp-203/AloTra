package service;

import java.util.List;

public interface AdminReportService {
    List<Object[]> getMonthlyRevenue();
    List<Object[]> getTopProducts(int limit);
    List<Object[]> getStockReport();
}