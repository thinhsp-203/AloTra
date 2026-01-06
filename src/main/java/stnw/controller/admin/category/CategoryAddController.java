package stnw.controller.admin.category;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import stnw.service.CategoryService;
import stnw.service.impl.CategoryServiceImpl;
import stnw.utils.UploadType;
import stnw.utils.UploadUtils;

@WebServlet(urlPatterns = {"/admin/category/add"})
@MultipartConfig(fileSizeThreshold = 2*1024*1024, maxFileSize = 10*1024*1024, maxRequestSize = 50*1024*1024)
public class CategoryAddController extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private final CategoryService service = new CategoryServiceImpl();

  @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.getRequestDispatcher("/views/admin/category-form.jsp").forward(req, resp);
  }

  @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    String name = req.getParameter("name");
    String isDrinkParam = req.getParameter("isDrink");
    Boolean isDrink = "true".equalsIgnoreCase(isDrinkParam);

    String iconPath = null; // Relative path sẽ lưu vào DB: "uploads/categories/filename"
    Part part = req.getPart("icon");
    
    if (part != null && part.getSize() > 0) {
      iconPath = UploadUtils.save(part, UploadType.CATEGORIES, req.getServletContext());
    }

    service.insertFromParams(name, iconPath, isDrink);

    req.getSession().setAttribute("success", "Đã thêm danh mục thành công!");
    resp.sendRedirect(req.getContextPath() + "/admin/category/list");
  }
}

