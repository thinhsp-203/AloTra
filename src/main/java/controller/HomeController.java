package controller;

import config.JpaUtil;
import dao.jpa.BannerRepository; // THÊM IMPORT
import dao.jpa.ProductRepository;
import jakarta.persistence.EntityManager; // THÊM IMPORT
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // THÊM IMPORT
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.Banner; // THÊM IMPORT
import model.Category;
import model.Product; 

public class HomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ProductRepository productRepo;
    private BannerRepository bannerRepo; // THÊM DÒNG NÀY

    @Override
    public void init() throws ServletException {
        productRepo = new ProductRepository();
        bannerRepo = new BannerRepository(); // THÊM DÒNG NÀY
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        var em = JpaUtil.em();
        try {
            // Lấy sản phẩm (như cũ)
            req.setAttribute("featured", productRepo.findFeatured(8, em));
            req.setAttribute("newest",   productRepo.findNewest(8, em));

            // Lấy danh mục (như cũ)
            List<Category> categories = em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class)
                                          .getResultList();
            req.setAttribute("categories", categories);

            // === BẮT ĐẦU CODE MỚI: LẤY BANNERS ===
            List<Banner> banners = bannerRepo.findAllActive(em);
            req.setAttribute("banners", banners);
            // === KẾT THÚC CODE MỚI ===

        } finally {
            em.close();
        }
        req.getRequestDispatcher("/views/home.jsp").forward(req, resp);
    }
}