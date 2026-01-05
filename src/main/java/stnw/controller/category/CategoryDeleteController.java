package stnw.controller.category;

import stnw.service.CategoryService;
import stnw.service.impl.CategoryServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(urlPatterns = {"/admin/category/delete"})
public class CategoryDeleteController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(CategoryDeleteController.class);
    
    private CategoryService categoryService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Chuyển hướng về trang danh sách nếu truy cập bằng GET
        resp.sendRedirect(req.getContextPath() + "/admin/category/list");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            categoryService.delete(id);
            req.getSession().setAttribute("success", "Đã xóa danh mục thành công!");
        } catch (NumberFormatException e) {
            logger.error("Invalid category ID format", e);
            req.getSession().setAttribute("error", "ID danh mục không hợp lệ!");
        } catch (Exception e) {
              logger.error("Error deleting category", e);
            req.getSession().setAttribute("error", "Không thể xóa danh mục. (Có thể vẫn còn sản phẩm thuộc danh mục này).");
        }
        resp.sendRedirect(req.getContextPath() + "/admin/category/list");
    }
}
