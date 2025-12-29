<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page import="config.JpaUtil, model.Category, java.util.List" %>

<%

    List<Category> categories = null;
try (var em = JpaUtil.em()) {
        categories = em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class).getResultList();
} catch (Exception e) {
        e.printStackTrace();
    }
    request.setAttribute("navbarCategories", categories);
%>

<style>
/* (Giữ nguyên toàn bộ CSS của bạn từ dòng 5 đến 20) */
.search-autocomplete-container {
    position: relative;
    width: 100%;
}
.search-suggestions {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    border: 1px solid #dee2e6;
    border-top: none;
    border-radius: 0 0 1rem 1rem;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    max-height: 400px;
    overflow-y: auto;
    z-index: 1050;
    display: none;
    margin-top: -0.5rem;
}
.search-suggestions.show {
    display: block;
}
.suggestions-header {
    padding: 0.75rem 1rem;
    background: #f8f9fa;
    border-bottom: 1px solid #dee2e6;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.suggestions-header h6 {
    margin: 0;
    font-size: 0.875rem;
    font-weight: 600;
    color: #666;
}
.clear-history {
    background: none;
    border: none;
    color: #0d6efd;
    font-size: 0.8rem;
    cursor: pointer;
    padding: 0;
}
.clear-history:hover {
    text-decoration: underline;
}
.suggestion-item {
    padding: 0.75rem 1rem;
    cursor: pointer;
    transition: background 0.2s ease;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    border-bottom: 1px solid #f0f0f0;
}
.suggestion-item:last-child {
    border-bottom: none;
}
.suggestion-item:hover {
    background: #f8f9fa;
}
.suggestion-item i {
    color: #6c757d;
    font-size: 1rem;
}
.suggestion-item .suggestion-text {
    flex: 1;
    color: #333;
    font-size: 0.9rem;
}
.keyword-tags {
    padding: 1rem;
    background: #f8f9fa;
}
.keyword-tags h6 {
    font-size: 0.875rem;
    font-weight: 600;
    color: #666;
    margin-bottom: 0.75rem;
}
.keyword-tag {
    display: inline-block;
    padding: 0.4rem 0.75rem;
    margin: 0.25rem;
    background: var(--bs-primary);
    color: white;
    border-radius: 1rem;
    font-size: 0.85rem;
    text-decoration: none;
    transition: all 0.2s ease;
}
.keyword-tag:hover {
    background: #005028;
    color: white;
    transform: translateY(-2px);
}
.search-form .form-control {
    border-radius: 99px;
    background-color: #f0f0f0;
    border-color: #f0f0f0;
    padding-left: 3rem;
}
.search-form .form-control:focus {
    background-color: #fff;
    border-color: var(--bs-primary);
    box-shadow: 0 0 0 0.25rem rgba(0, 102, 51, 0.25);
}
.search-form .input-group-text {
    position: absolute;
    left: 1rem;
    top: 50%;
    transform: translateY(-50%);
    z-index: 10;
    background: transparent;
    border: none;
    color: #6c757d;
}
</style>

<header class="sticky-top bg-white shadow-sm">
    <div class="container py-2">
        <div class="row align-items-center g-2">
            <div class="col-auto">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
				    <c:choose>
				        <c:when test="${not empty siteSettings.LOGO_URL}">
				            <img src="${siteSettings.LOGO_URL}" alt="AloTra">
				        </c:when>
				    </c:choose>
				</a>
            </div>
            <div class="col-auto" style="max-width: 500px;">
                <div class="search-autocomplete-container">
                    <form action="${pageContext.request.contextPath}/products" method="get" class="search-form position-relative">
                        <span class="input-group-text"><i class="bi bi-search"></i></span>
              <input class="form-control" 
                     type="search" 
                     name="q" 
                     id="searchInput"
                     placeholder="Bạn muốn mua gì..." 
                     value="${param.q}"
                     autocomplete="off">
                    </form>
                    <div class="search-suggestions" id="searchSuggestions">
                        <div class="suggestions-header">
                            <h6>Tìm kiếm gần đây</h6>
                            <button type="button" class="clear-history" onclick="clearSearchHistory()">Xóa tất cả</button>
                        </div>
                        <div id="suggestionsList"></div>
                        <div class="keyword-tags">
                            <h6>Từ khóa phổ biến</h6>
                            <a href="${pageContext.request.contextPath}/products?q=trà sữa" class="keyword-tag">Trà sữa phúc long</a>
                            <a href="${pageContext.request.contextPath}/products?q=trà xanh" class="keyword-tag">Trà xanh thái nguyên</a>
                            <a href="${pageContext.request.contextPath}/products?q=sinh tố" class="keyword-tag">Sinh tố xoài</a>
                            <a href="${pageContext.request.contextPath}/products?q=cà phê" class="keyword-tag">Cà phê culi</a>
                            <a href="${pageContext.request.contextPath}/products?q=nước ép" class="keyword-tag">Nước ép dâu</a>
                            <a href="${pageContext.request.contextPath}/products?q=bánh choco" class="keyword-tag">Bánh choco trà xanh</a>
                            <a href="${pageContext.request.contextPath}/products?q=latte" class="keyword-tag">latte</a>
                            <a href="${pageContext.request.contextPath}/products?q=nho khô" class="keyword-tag">Nho khô Úc</a>
                            <a href="${pageContext.request.contextPath}/products?q=cà phê sét" class="keyword-tag">Cà phê sét</a>
                            <a href="${pageContext.request.contextPath}/products?q=caramel" class="keyword-tag">Caramel đá xay</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-auto ms-auto">
                <div class="d-flex align-items-center gap-3">
                    <a href="${pageContext.request.contextPath}/cart/view" 
                       class="nav-link position-relative">
                        <i class="bi bi-cart fs-4"></i>
                        <span class="badge rounded-pill bg-danger position-absolute top-0 start-100 translate-middle" id="cart-item-count">
                            ${not empty sessionScope.cart ? fn:length(sessionScope.cart.items) : 0}
                        </span>
                    </a>
                    <c:choose>
                        <c:when test="${empty sessionScope.currentUser}">
                            <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                        </c:when>
                        <c:otherwise>
                            <div class="nav-item dropdown">
                                <a class="nav-link" href="#" role="button" data-bs-toggle="dropdown">
                                    <i class="bi bi-person-circle fs-4"></i>
                                </a>
                                <ul class="dropdown-menu dropdown-menu-end">
                                    <li><span class="dropdown-item-text">Chào, <strong>${sessionScope.currentUser.username}</strong></span></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile"><i class="bi bi-person-fill me-2"></i> Hồ Sơ</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/orders"><i class="bi bi-receipt me-2"></i> Đơn Mua</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/wishlist"><i class="bi bi-heart me-2"></i> Yêu thích</a></li>
                                    <c:if test="${sessionScope.currentUser.roleid == 1 || sessionScope.currentUser.roleid == 2}">
                                        <li><hr class="dropdown-divider"></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin/dashboard"><i class="bi bi-shield-lock me-2"></i> Quản Trị</a></li>
                                    </c:if>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right me-2"></i> Đăng Xuất</a></li>
                                </ul>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <nav class="navbar navbar-expand-lg border-top main-nav">
        <div class="container">
            <div class="collapse navbar-collapse">
                <ul class="navbar-nav mx-auto">
                   <li class="nav-item">
                        <a class="nav-link fw-bold" href="${pageContext.request.contextPath}/home">TRANG CHỦ</a>
                    </li>
                    <li class="nav-item dropdown">
                        <a class="nav-link fw-bold dropdown-toggle" href="${pageContext.request.contextPath}/products" id="menuDropdown">
                            MENU
                        </a>
                        <c:if test="${not empty navbarCategories}">
                            <div class="dropdown-menu megamenu shadow-lg" aria-labelledby="menuDropdown">
                                <div class="container">
                                    <div class="row">
                                        <div class="col-lg-4 col-md-6">
                                            <h6 class="dropdown-header"><i class="bi bi-cup-straw"></i> Thức uống</h6>
                                            <c:forEach var="cat" items="${navbarCategories}">
                                                <c:set var="lowerName" value="${fn:toLowerCase(cat.name)}" />
                                                <c:if test="${fn:contains(lowerName, 'trà') || fn:contains(lowerName, 'cà phê') || fn:contains(lowerName, 'sinh tố') || fn:contains(lowerName, 'nước')}">
                                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/products?cate=${cat.id}">
                                                        ${cat.name}
                                                    </a>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                        <div class="col-lg-4 col-md-6">
                                            <h6 class="dropdown-header"><i class="bi bi-cake2"></i> Bánh & Đồ ăn vặt</h6>
                                            <c:forEach var="cat" items="${navbarCategories}">
                                                <c:set var="lowerName" value="${fn:toLowerCase(cat.name)}" />
                                                <c:if test="${fn:contains(lowerName, 'bánh') || fn:contains(lowerName, 'ăn vặt') || fn:contains(lowerName, 'snack')}">
                                                    <a class="dropdown-item" href="${pageContext.request.contextPath}/products?cate=${cat.id}">
                                                        ${cat.name}
                                                    </a>
                                                </c:if>
                                            </c:forEach>
                                        </div>
                                        <div class="col-lg-4 col-md-12">
                                            <div class="p-3">
                                                <c:choose>
                                                    <c:when test="${not empty siteSettings.MENU_BANNER_URL}">
                                                        <img src="${siteSettings.MENU_BANNER_URL}" class="img-fluid rounded" alt="Promo">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="https://static.phuclong.com.vn/storage/5/2024/5/2/663305417df89_bsttraxanhtraicayvuongtronvinangluong.jpg" 
                                                             class="img-fluid rounded" alt="Promo">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-bold" href="#">KHUYẾN MÃI</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-bold" href="#">VỀ CHÚNG TÔI</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link fw-bold" href="#">HỘI VIÊN</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</header>