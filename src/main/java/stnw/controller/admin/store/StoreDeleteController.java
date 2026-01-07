package stnw.controller.admin.store;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminStoreService;
import stnw.service.impl.AdminStoreServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/stores/delete")
public class StoreDeleteController extends HttpServlet {
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
            int id = Integer.parseInt(req.getParameter("id"));
            storeService.deleteStore(id);
            req.getSession().setAttribute("success", "Đã xóa cửa hàng thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/stores");
    }
}

