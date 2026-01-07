package stnw.filter;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import stnw.model.Category;
import stnw.service.CategoryService;
import stnw.service.impl.CategoryServiceImpl;

/**
 * Bổ sung dữ liệu navbar cho mọi request user-facing.
 * Lưu ý: tránh admin path để không load dữ liệu không cần thiết.
 */
@WebFilter("/*")
public class NavbarDataFilter implements Filter {

    private final CategoryService categoryService = new CategoryServiceImpl();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        String uri = httpReq.getRequestURI();
        String ctx = httpReq.getContextPath();

        boolean isAdmin = uri.startsWith(ctx + "/admin");
        boolean hasNavbarData = request.getAttribute("navbarCategories") != null;

        if (!isAdmin && !hasNavbarData) {
            List<Category> categories = categoryService.getAll();
            request.setAttribute("navbarCategories", categories);
        }

        chain.doFilter(request, response);
    }
}

