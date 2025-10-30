<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page import="config.JpaUtil, model.Category, java.util.List" %>

<%
    List<Category> categories = null;
    try (var em = JpaUtil.em()) {
        categories = em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class).getResultList();
    } catch (Exception e) {
        // Bỏ qua lỗi
    }
    request.setAttribute("navbarCategories", categories);
%>

<header class="sticky-top bg-white shadow-sm">
    <div class="container py-2">
        <div class="row align-items-center g-3">
            <div class="col-auto">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
                    <img src="https://www.phuclong.com.vn/images/logo-phuclong.png" alt="AloTra Logo" style="height: 40px;">
                </a>
            </div>
            <div class="col">
                <form action="${pageContext.request.contextPath}/products" method="get" class="search-form mx-auto" style="max-width: 500px;">
                    <div class="input-group">
                        <span class="input-group-text bg-transparent border-end-0"><i class="bi bi-search"></i></span>
                        <input class="form-control border-start-0" type="search" name="q" placeholder="Bạn muốn mua gì...">
                    </div>
                </form>
            </div>
            <div class="col-auto">
                <div class="d-flex align-items-center gap-3">
                    <a href="${pageContext.request.contextPath}/cart/view" class="nav-link position-relative">
                        <i class="bi bi-cart fs-4"></i>
                        <span class="badge rounded-pill bg-danger position-absolute top-0 start-100 translate-middle" id="cart-item-count">
                            ${not empty sessionScope.CART ? fn:length(sessionScope.CART) : 0}
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
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile"><i class="bi bi-person-fill me-2"></i> Hồ Sơ Của Tôi</a></li>
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/orders"><i class="bi bi-receipt me-2"></i> Đơn Mua</a></li>
                                    <c:if test="${sessionScope.currentUser.roleid == 1 || sessionScope.currentUser.roleid == 2}">
                                        <li><hr class="dropdown-divider"></li>
                                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin"><i class="bi bi-shield-lock me-2"></i> Trang Quản Trị</a></li>
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
                                        <div class="col-lg-3 col-md-4">
											    <h6 class="dropdown-header">Thức uống</h6>
											    <c:forEach var="cat" items="${navbarCategories}">
											        <a class="dropdown-item" href="${pageContext.request.contextPath}/products?cate=${cat.id}">${cat.name}</a>
											    </c:forEach>
											</div>
											<div class="col-lg-3 col-md-4">
											    <h6 class="dropdown-header">Bánh Ngọt</h6>
											    <a class="dropdown-item" href="#">Bánh lạnh</a>
											    <a class="dropdown-item" href="#">Bánh cookies</a>
											</div>
                                        <div class="col-lg-6 col-md-4">
                                             <div class="p-3">
                                                <img src="https://static.phuclong.com.vn/storage/5/2024/5/2/663305417df89_bsttraxanhtraicayvuongtronvinangluong.jpg" class="img-fluid rounded" alt="Promo">
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
                </ul>
            </div>
        </div>
    </nav>
</header>