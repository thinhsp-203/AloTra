<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<header class="sticky-top bg-white shadow-sm">
    <div class="container d-flex align-items-center justify-content-between py-2">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
            <img src="https://www.phuclong.com.vn/images/logo-phuclong.png" alt="AloTra Logo" style="height: 40px;">
        </a>
        
        <div class="d-flex align-items-center">
            <a href="${pageContext.request.contextPath}/cart/view" class="nav-link position-relative me-3">
                <i class="bi bi-cart-fill fs-4"></i>
                <span class="badge rounded-pill bg-danger position-absolute top-0 start-100 translate-middle" id="cart-item-count">${not empty sessionScope.CART ? fn:length(sessionScope.CART) : 0}</span>
            </a>
            <c:choose>
                <c:when test="${empty sessionScope.currentUser}">
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                </c:when>
                <c:otherwise>
                    <div class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                            <i class="bi bi-person-circle fs-4"></i>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><span class="dropdown-item-text">Chào, <strong>${sessionScope.currentUser.username}</strong></span></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">Tài Khoản</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/orders">Đơn Mua</a></li>
                            <c:if test="${sessionScope.currentUser.roleid == 1 || sessionScope.currentUser.roleid == 2}">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin">Trang Quản Trị</a></li>
                            </c:if>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Đăng Xuất</a></li>
                        </ul>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    <nav class="navbar navbar-expand-lg border-top">
        <div class="container">
            <div class="collapse navbar-collapse" id="main-nav">
                <ul class="navbar-nav mx-auto">
                    <li class="nav-item"><a class="nav-link fw-bold" href="${pageContext.request.contextPath}/home">TRANG CHỦ</a></li>
                    <li class="nav-item"><a class="nav-link fw-bold" href="${pageContext.request.contextPath}/products">MENU</a></li>
                    <li class="nav-item"><a class="nav-link fw-bold" href="#">KHUYẾN MÃI</a></li>
                    <li class="nav-item"><a class="nav-link fw-bold" href="#">VỀ CHÚNG TÔI</a></li>
                </ul>
            </div>
        </div>
    </nav>
</header>