package stnw.controller.category;

import jakarta.servlet.*;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import stnw.model.Category;
import stnw.service.CategoryService;
import stnw.service.impl.CategoryServiceImpl;
import stnw.utils.UploadType;
import stnw.utils.UploadUtil;

@WebServlet(urlPatterns = {"/admin/category/edit"})
@MultipartConfig(fileSizeThreshold = 2*1024*1024, maxFileSize = 10*1024*1024, maxRequestSize = 50*1024*1024)
public class CategoryEditController extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private final CategoryService service = new CategoryServiceImpl();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      int id = Integer.parseInt(req.getParameter("id"));
      Category c = service.get(id);
      if (c == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
      req.setAttribute("category", c);
      req.getRequestDispatcher("/views/admin/category-form.jsp").forward(req, resp);
    } catch (Exception e) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid id");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    int id = Integer.parseInt(req.getParameter("id"));
    String name = req.getParameter("name");
    String isDrinkParam = req.getParameter("isDrink");
    Boolean isDrink = "true".equalsIgnoreCase(isDrinkParam);

    
    Category old = service.get(id);
    if (old == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

    String iconPath = old.getIcon(); // Giữ lại ảnh cũ làm mặc định
    Part part = req.getPart("icon");
    
    if (part != null && part.getSize() > 0) {
      // Xóa ảnh cũ nếu có
      if (old.getIcon() != null && !old.getIcon().isEmpty()) {
          UploadUtil.deleteOldImage(old.getIcon(), req.getServletContext());
      }
      
      // Upload file mới
      iconPath = UploadUtil.save(part, UploadType.CATEGORIES, req.getServletContext());
    }

    service.editFromParams(id, name, iconPath, isDrink);

    req.getSession().setAttribute("success", "Đã cập nhật danh mục!");
    resp.sendRedirect(req.getContextPath() + "/admin/category/list");
  }
}
