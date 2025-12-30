package controller.api;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Store;
import service.StoreService;
import service.impl.StoreServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/stores/search")
public class StoreSearchController extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private StoreService storeService;

    @Override
    public void init() throws ServletException {
        storeService = new StoreServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String keyword = req.getParameter("keyword");
        
        try {
            List<Store> stores = storeService.searchStores(keyword);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stores", stores);
            response.put("count", stores.size());
            
            Gson gson = new Gson();
            resp.getWriter().write(gson.toJson(response));
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            
            Gson gson = new Gson();
            resp.getWriter().write(gson.toJson(response));
        }
    }
}

