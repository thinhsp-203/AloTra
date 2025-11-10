package controller.admin;

import config.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Topping;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(urlPatterns = {
    "/admin/toppings",
    "/admin/toppings/create",
    "/admin/toppings/edit",
    "/admin/toppings/save",
    "/admin/toppings/delete"
})
public class AdminToppingController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/admin/toppings")) {
            this.showList(req, resp);
        } else if (uri.endsWith("/admin/toppings/create")) {
            this.showForm(req, resp, null);
        } else if (uri.endsWith("/admin/toppings/edit")) {
            int id = Integer.parseInt(req.getParameter("id"));
            this.showForm(req, resp, id);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/admin/toppings/save")) {
            this.save(req, resp);
        } else if (uri.endsWith("/admin/toppings/delete")) {
            this.delete(req, resp);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        EntityManager em = JpaUtil.em();
        TypedQuery<Topping> query = em.createQuery("SELECT t FROM Topping t", Topping.class);
        req.setAttribute("items", query.getResultList());
        req.getRequestDispatcher("/views/admin/toppings.jsp").forward(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Integer id)
        throws ServletException, IOException {
        EntityManager em = JpaUtil.em();
        Topping item = new Topping();
        if (id != null) {
            item = em.find(Topping.class, id);
        }
        req.setAttribute("item", item);
        req.getRequestDispatcher("/views/admin/topping_form.jsp").forward(req, resp);
    }

    private void save(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            String idParam = req.getParameter("id");
            Topping item;
            if (idParam != null && !idParam.isEmpty()) {
                item = em.find(Topping.class, Integer.parseInt(idParam));
            } else {
                item = new Topping();
            }

            // SỬA LỖI: Dùng đúng tên trường trong Model
            item.setTopping_name(req.getParameter("topping_name"));
            item.setPrice(new BigDecimal(req.getParameter("price")));
            item.setIsAvailable(req.getParameter("isAvailable") != null); // Sửa từ isActive

            if (item.getTopping_id() == null) {
                em.persist(item);
            } else {
                em.merge(item);
            }
            
            em.getTransaction().commit();
            req.getSession().setAttribute("success", "Đã lưu Topping thành công!");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        } finally {
            em.close();
        }
        resp.sendRedirect(req.getContextPath() + "/admin/toppings");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            int id = Integer.parseInt(req.getParameter("id"));
            Topping item = em.find(Topping.class, id);
            if (item != null) {
                em.remove(item);
                req.getSession().setAttribute("success", "Đã xóa Topping!");
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            req.getSession().setAttribute("error", "Lỗi: " + e.getMessage());
        } finally {
            em.close();
        }
        resp.sendRedirect(req.getContextPath() + "/admin/toppings");
    }
}