package stnw.controller.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Voucher;
import stnw.service.AdminVoucherService;
import stnw.service.impl.AdminVoucherServiceImpl;

@WebServlet(urlPatterns = {
    "/admin/vouchers",
    "/admin/vouchers/create",
    "/admin/vouchers/edit",
    "/admin/vouchers/save",
    "/admin/vouchers/delete"
})
public class AdminVoucherController extends HttpServlet {
	    
	    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		private AdminVoucherService voucherService;
	    
	    @Override
	    public void init() throws ServletException {
	        voucherService = new AdminVoucherServiceImpl();
	    }
	    
	    @Override
	    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
	        String uri = req.getRequestURI();
	        
	        if (uri.endsWith("/admin/vouchers")) {
	            req.setAttribute("vouchers", voucherService.getAllVouchers());
	            req.getRequestDispatcher("/views/admin/vouchers.jsp").forward(req, resp);
	            
	        } else if (uri.endsWith("/admin/vouchers/create")) {
	            req.setAttribute("v", new Voucher());
	            req.getRequestDispatcher("/views/admin/voucher-form.jsp").forward(req, resp);
	            
	        } else if (uri.endsWith("/admin/vouchers/edit")) {
	            int id = Integer.parseInt(req.getParameter("id"));
	            req.setAttribute("v", voucherService.getVoucherById(id));
	            req.getRequestDispatcher("/views/admin/voucher-form.jsp").forward(req, resp);
	        }
	    }
	    
	    @Override
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {
	        String uri = req.getRequestURI();
	        
	        try {
	            if (uri.endsWith("/admin/vouchers/save")) {
	                String idParam = req.getParameter("id");
	                Voucher v = (idParam != null && !idParam.isEmpty()) 
	                    ? voucherService.getVoucherById(Integer.parseInt(idParam)) 
	                    : new Voucher();
	                
                v.setCode(req.getParameter("code").toUpperCase());
                v.setDescription(req.getParameter("description"));
                String discountType = req.getParameter("discount_type");
                v.setDiscount_type(discountType);
                
                // Validate discount_value based on discount_type
                BigDecimal discountValue = new BigDecimal(req.getParameter("discount_value"));
                if ("PERCENT".equals(discountType)) {
                    if (discountValue.compareTo(BigDecimal.ZERO) < 0 || discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
                        throw new IllegalArgumentException("Giá tr�?giảm giá theo % phải t�?0 đến 100");
                    }
                } else if ("AMOUNT".equals(discountType)) {
                    if (discountValue.compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("Giá tr�?giảm giá phải lớn hơn 0");
                    }
                }
                v.setDiscount_value(discountValue);
	                
	                String minOrder = req.getParameter("min_order_value");
	                v.setMin_order_value((minOrder == null || minOrder.isEmpty()) ? null : new BigDecimal(minOrder));
	                
	                String maxDiscount = req.getParameter("max_discount");
	                v.setMax_discount((maxDiscount == null || maxDiscount.isEmpty()) ? null : new BigDecimal(maxDiscount));
	                
                String usageLimit = req.getParameter("usage_limit");
                v.setUsage_limit((usageLimit == null || usageLimit.isEmpty()) ? null : Integer.parseInt(usageLimit));
                
                // Parse datetime-local format (yyyy-MM-ddTHH:mm or yyyy-MM-ddTHH:mm:ss)
                String startDateStr = req.getParameter("start_date");
                if (startDateStr != null && !startDateStr.isEmpty()) {
                    try {
                        // Try with seconds first, then without
                        DateTimeFormatter formatter = startDateStr.length() > 16 
                            ? DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                            : DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        v.setStart_date(LocalDateTime.parse(startDateStr, formatter));
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Ngày bắt đầu không hợp l�? " + e.getMessage());
                    }
                } else {
                    throw new IllegalArgumentException("Ngày bắt đầu là bắt buộc");
                }
                
                String endDateStr = req.getParameter("end_date");
                if (endDateStr != null && !endDateStr.isEmpty()) {
                    try {
                        // Try with seconds first, then without
                        DateTimeFormatter formatter = endDateStr.length() > 16 
                            ? DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                            : DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                        v.setEnd_date(LocalDateTime.parse(endDateStr, formatter));
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Ngày kết thúc không hợp l�? " + e.getMessage());
                    }
                } else {
                    throw new IllegalArgumentException("Ngày kết thúc là bắt buộc");
                }
                
                // Validate: end_date must be after start_date
                if (v.getStart_date() != null && v.getEnd_date() != null 
                    && !v.getEnd_date().isAfter(v.getStart_date())) {
                    throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
                }
                
                v.setIsActive(req.getParameter("isActive") != null);
	                
	                voucherService.saveVoucher(v);
	                req.getSession().setAttribute("success", "Đã lưu voucher thành công!");
	                
	            } else if (uri.endsWith("/admin/vouchers/delete")) {
	                int id = Integer.parseInt(req.getParameter("id"));
	                voucherService.deleteVoucher(id);
	                req.getSession().setAttribute("success", "Đã xóa voucher!");
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
	        }
	        
	        resp.sendRedirect(req.getContextPath() + "/admin/vouchers");
	    }
	}
