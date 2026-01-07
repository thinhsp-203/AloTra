package stnw.controller.admin.store;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminStoreService;
import stnw.service.impl.AdminStoreServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/stores/add")
public class StoreAddController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminStoreService storeService;
    
    @Override
    public void init() throws ServletException {
        storeService = new AdminStoreServiceImpl();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        try {
            String storeName = req.getParameter("store_name");
            String address = req.getParameter("address");
            String phone = req.getParameter("phone");
            String email = req.getParameter("email");
            String ward = req.getParameter("ward");
            String province = req.getParameter("province");
            String mapIframe = req.getParameter("mapIframe");
            String openingHours = req.getParameter("opening_hours");
            Boolean isActive = req.getParameter("isActive") != null;
            
            storeService.saveStoreFromParams(null, storeName, address, phone, email, 
                                            ward, province, mapIframe, openingHours, isActive);
            req.getSession().setAttribute("success", "Đã thêm cửa hàng thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/stores");
    }
}

