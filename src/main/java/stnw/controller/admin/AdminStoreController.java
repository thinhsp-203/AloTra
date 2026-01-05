package stnw.controller.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.model.Store;
import stnw.service.AdminStoreService;
import stnw.service.impl.AdminStoreServiceImpl;

@WebServlet(urlPatterns = "/admin/stores")
public class AdminStoreController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private AdminStoreService storeService;
    
    @Override
    public void init() throws ServletException {
        storeService = new AdminStoreServiceImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            // Edit mode - load store by id
            try {
                int id = Integer.parseInt(idParam);
                Store store = storeService.getStoreById(id);
                if (store != null) {
                    req.setAttribute("store", store);
                } else {
                    req.getSession().setAttribute("error", "Không tìm thấy cửa hàng!");
                }
            } catch (NumberFormatException e) {
                req.getSession().setAttribute("error", "ID không hợp l�?");
            }
        }
        req.setAttribute("stores", storeService.getAllStores());
        req.getRequestDispatcher("/views/admin/stores.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException {
        String action = req.getParameter("action");
        
        try {
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                storeService.deleteStore(id);
                req.getSession().setAttribute("success", "Đã xóa cửa hàng thành công!");
                
            } else if ("add".equals(action) || "edit".equals(action)) {
                String idParam = req.getParameter("id");
                Integer storeId = ("edit".equals(action) && idParam != null && !idParam.isEmpty()) 
                    ? Integer.parseInt(idParam) 
                    : null;
                
                if (storeId != null) {
                    Store existingStore = storeService.getStoreById(storeId);
                    if (existingStore == null) {
                        req.getSession().setAttribute("error", "Không tìm thấy cửa hàng!");
                        resp.sendRedirect(req.getContextPath() + "/admin/stores");
                        return;
                    }
                }
                
                String storeName = req.getParameter("store_name");
                String address = req.getParameter("address");
                String phone = req.getParameter("phone");
                String email = req.getParameter("email");
                String ward = req.getParameter("ward");
                String province = req.getParameter("province");
                String mapIframe = req.getParameter("mapIframe");
                String openingHours = req.getParameter("opening_hours");
                Boolean isActive = req.getParameter("isActive") != null;
                
                storeService.saveStoreFromParams(storeId, storeName, address, phone, email, 
                                                ward, province, mapIframe, openingHours, isActive);
                req.getSession().setAttribute("success", "Đã " + ("edit".equals(action) ? "cập nhật" : "thêm") + " cửa hàng thành công!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/stores");
    }
}

