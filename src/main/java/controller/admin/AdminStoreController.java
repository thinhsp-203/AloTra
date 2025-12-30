package controller.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Store;
import service.AdminStoreService;
import service.impl.AdminStoreServiceImpl;

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
                req.getSession().setAttribute("error", "ID không hợp lệ!");
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
                Store store;
                
                if ("edit".equals(action)) {
                    int id = Integer.parseInt(req.getParameter("id"));
                    store = storeService.getStoreById(id);
                    if (store == null) {
                        req.getSession().setAttribute("error", "Không tìm thấy cửa hàng!");
                        resp.sendRedirect(req.getContextPath() + "/admin/stores");
                        return;
                    }
                } else {
                    store = new Store();
                }
                
                store.setStore_name(req.getParameter("store_name"));
                store.setAddress(req.getParameter("address"));
                store.setPhone(req.getParameter("phone"));
                store.setEmail(req.getParameter("email"));
                store.setWard(req.getParameter("ward"));
                store.setProvince(req.getParameter("province"));
                store.setMapIframe(req.getParameter("mapIframe"));
                store.setOpening_hours(req.getParameter("opening_hours"));
                
                store.setIsActive(req.getParameter("isActive") != null);
                
                storeService.saveStore(store);
                req.getSession().setAttribute("success", "Đã " + ("edit".equals(action) ? "cập nhật" : "thêm") + " cửa hàng thành công!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/stores");
    }
}

