package stnw.controller.admin.store;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import stnw.service.AdminStoreService;
import stnw.service.impl.AdminStoreServiceImpl;

import java.io.IOException;

@WebServlet(urlPatterns = "/admin/stores")
public class StoreListController extends HttpServlet {
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
            try {
                int id = Integer.parseInt(idParam);
                stnw.model.Store store = storeService.getStoreById(id);
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
}

