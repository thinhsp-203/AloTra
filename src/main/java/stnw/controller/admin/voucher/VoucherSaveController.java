package stnw.controller.admin.voucher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Voucher;
import stnw.service.AdminVoucherService;
import stnw.service.impl.AdminVoucherServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(urlPatterns = "/admin/vouchers/save")
public class VoucherSaveController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminVoucherService voucherService;
    
    @Override
    public void init() throws ServletException {
        voucherService = new AdminVoucherServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        try {
            String idParam = req.getParameter("id");
            Voucher v = (idParam != null && !idParam.isEmpty()) 
                ? voucherService.getVoucherById(Integer.parseInt(idParam)) 
                : new Voucher();
            
            v.setCode(req.getParameter("code").toUpperCase());
            v.setDescription(req.getParameter("description"));
            String discountType = req.getParameter("discount_type");
            v.setDiscount_type(discountType);
            
            BigDecimal discountValue = new BigDecimal(req.getParameter("discount_value"));
            if ("PERCENT".equals(discountType)) {
                if (discountValue.compareTo(BigDecimal.ZERO) < 0 || discountValue.compareTo(BigDecimal.valueOf(100)) > 0) {
                    throw new IllegalArgumentException("Giá trị giảm giá theo % phải từ 0 đến 100");
                }
            } else if ("AMOUNT".equals(discountType)) {
                if (discountValue.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Giá trị giảm giá phải lớn hơn 0");
                }
            }
            v.setDiscount_value(discountValue);
            
            String minOrder = req.getParameter("min_order_value");
            v.setMin_order_value((minOrder == null || minOrder.isEmpty()) ? null : new BigDecimal(minOrder));
            
            String maxDiscount = req.getParameter("max_discount");
            v.setMax_discount((maxDiscount == null || maxDiscount.isEmpty()) ? null : new BigDecimal(maxDiscount));
            
            String usageLimit = req.getParameter("usage_limit");
            v.setUsage_limit((usageLimit == null || usageLimit.isEmpty()) ? null : Integer.parseInt(usageLimit));
            
            String startDateStr = req.getParameter("start_date");
            if (startDateStr != null && !startDateStr.isEmpty()) {
                try {
                    DateTimeFormatter formatter = startDateStr.length() > 16 
                        ? DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        : DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    v.setStart_date(LocalDateTime.parse(startDateStr, formatter));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Ngày bắt đầu không hợp lệ: " + e.getMessage());
                }
            } else {
                throw new IllegalArgumentException("Ngày bắt đầu là bắt buộc");
            }
            
            String endDateStr = req.getParameter("end_date");
            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    DateTimeFormatter formatter = endDateStr.length() > 16 
                        ? DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                        : DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    v.setEnd_date(LocalDateTime.parse(endDateStr, formatter));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Ngày kết thúc không hợp lệ: " + e.getMessage());
                }
            } else {
                throw new IllegalArgumentException("Ngày kết thúc là bắt buộc");
            }
            
            if (v.getStart_date() != null && v.getEnd_date() != null 
                && !v.getEnd_date().isAfter(v.getStart_date())) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
            }
            
            v.setIsActive(req.getParameter("isActive") != null);
            
            voucherService.saveVoucher(v);
            req.getSession().setAttribute("success", "Đã lưu voucher thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/vouchers");
    }
}

