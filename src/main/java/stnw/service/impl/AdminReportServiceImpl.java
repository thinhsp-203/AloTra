package stnw.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import stnw.dao.OrderDao;
import stnw.dao.impl.OrderDaoImpl;
import stnw.service.AdminReportService;
import stnw.utils.OrderStatus;

public class AdminReportServiceImpl implements AdminReportService {
    
    private final OrderDao orderDao = new OrderDaoImpl();
    
    // QUAN TRỌNG: Tất cả các method tính doanh thu chỉ tính từ đơn hàng có status = "Hoàn thành"
    // Đơn hàng đã thanh toán nhưng chưa hoàn thành KHÔNG được tính
    // Đơn hàng bị hủy (Hủy bởi khách, Hủy bởi shop, Từ chối) KHÔNG được tính
    
    @Override
    public List<Object[]> getMonthlyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return orderDao.getMonthlyRevenue(startDate, endDate, OrderStatus.HOAN_THANH.getDisplayName());
    }
    
    @Override
    public List<Object[]> getDailyRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return orderDao.getDailyRevenue(startDate, endDate, OrderStatus.HOAN_THANH.getDisplayName());
    }
    
    @Override
    public List<Object[]> getTopProducts(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return orderDao.getTopProducts(startDate, endDate, OrderStatus.HOAN_THANH.getDisplayName(), limit);
    }
    
    @Override
    public List<Object[]> getProductRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        return orderDao.getProductRevenue(startDate, endDate, OrderStatus.HOAN_THANH.getDisplayName());
    }
    
    @Override
    public List<Object[]> getOrderStatsByStatus(LocalDateTime startDate, LocalDateTime endDate) {
        return orderDao.getOrderStatsByStatus(startDate, endDate);
    }
    
    @Override
    public List<Object[]> getOrderStatsByPaymentMethod(LocalDateTime startDate, LocalDateTime endDate) {
        return orderDao.getOrderStatsByPaymentMethod(startDate, endDate, OrderStatus.HOAN_THANH.getDisplayName());
    }
    
    @Override
    public List<Object[]> getTopCustomersByRevenue(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return orderDao.getTopCustomersByRevenue(startDate, endDate, OrderStatus.HOAN_THANH.getDisplayName(), limit);
    }
    
    @Override
    public List<Object[]> getTopCustomersByOrderCount(LocalDateTime startDate, LocalDateTime endDate, int limit) {
        return orderDao.getTopCustomersByOrderCount(startDate, endDate, OrderStatus.HOAN_THANH.getDisplayName(), limit);
    }
    
    @Override
    public Object[] compareRevenuePeriods(LocalDateTime currentStart, LocalDateTime currentEnd,
                                         LocalDateTime previousStart, LocalDateTime previousEnd) {
        return orderDao.compareRevenuePeriods(currentStart, currentEnd, previousStart, previousEnd, 
                                             OrderStatus.HOAN_THANH.getDisplayName());
    }
}
