package stnw.controller.store;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import stnw.service.StoreService;
import stnw.service.impl.StoreServiceImpl;

@WebServlet(name = "StoreController", urlPatterns = {"/stores", "/cua-hang", "/danh-sach-cua-hang"})
public class StoreController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private StoreService storeService;

    @Override
    public void init() throws ServletException {
        storeService = new StoreServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        String keyword = req.getParameter("keyword");
        
        if (idParam != null && !idParam.isEmpty()) {
            // Chi tiết cửa hàng
            try {
                int id = Integer.parseInt(idParam);
                var store = storeService.getStoreById(id);
                if (store != null && (store.getIsActive() == null || store.getIsActive())) {
                    req.setAttribute("store", store);
                    req.getRequestDispatcher("/views/store/detail.jsp").forward(req, resp);
                    return;
                } else {
                    req.setAttribute("error", "Cửa hàng không tồn tại hoặc đã bị ẩn!");
                }
            } catch (NumberFormatException e) {
                req.setAttribute("error", "ID cửa hàng không hợp lệ");
            }
        }
        
        // Danh sách cửa hàng
        if (keyword != null && !keyword.trim().isEmpty()) {
            req.setAttribute("stores", storeService.searchStores(keyword));
            req.setAttribute("keyword", keyword);
        } else {
            req.setAttribute("stores", storeService.getAllStores());
        }
        req.getRequestDispatcher("/views/store/list.jsp").forward(req, resp);
    }
}

