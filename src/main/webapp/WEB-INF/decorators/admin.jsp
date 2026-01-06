<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="stnw.utils.Roles" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8"/>
    <title>${pageTitle != null ? pageTitle : 'Admin - AloTra'}</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/custom.css" rel="stylesheet">
</head>

<body id="page-top">
<div id="wrapper">

<!-- ================= SIDEBAR ================= -->
<ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

    <hr class="sidebar-divider my-0">

    <!-- DASHBOARD (ADMIN + STAFF) -->
    <c:if test="${sessionScope.currentUser.roleid == Roles.ADMIN 
              || sessionScope.currentUser.roleid == Roles.STAFF}">
        <li class="nav-item ${fn:contains(pageContext.request.requestURI,'/admin/dashboard')?'active':''}">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-fw fa-tachometer-alt"></i>
                <span>Dashboard</span>
            </a>
        </li>
    </c:if>

    <hr class="sidebar-divider">

    <!-- QUẢN LÝ BÁN HÀNG (ADMIN + STAFF) -->
    <div class="sidebar-heading">Quản lý Bán Hàng</div>

    <c:if test="${sessionScope.currentUser.roleid == Roles.ADMIN 
              || sessionScope.currentUser.roleid == Roles.STAFF}">

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/orders">
                <i class="fas fa-fw fa-shopping-cart"></i>
                <span>Đơn hàng</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/products">
                <i class="fas fa-fw fa-box"></i>
                <span>Sản phẩm</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/category/list">
                <i class="fas fa-fw fa-tags"></i>
                <span>Danh mục</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/toppings">
                <i class="fas fa-fw fa-ice-cream"></i>
                <span>Topping</span>
            </a>
        </li>
    </c:if>

    <hr class="sidebar-divider">

    <!-- MARKETING & NỘI DUNG (ADMIN ONLY) -->
    <div class="sidebar-heading">Marketing & Nội dung</div>

    <c:if test="${sessionScope.currentUser.roleid == Roles.ADMIN}">

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/banners">
                <i class="fas fa-fw fa-images"></i>
                <span>Banner</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/promotions">
                <i class="fas fa-fw fa-percent"></i>
                <span>Khuyến mãi</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/vouchers">
                <i class="fas fa-fw fa-ticket-alt"></i>
                <span>Vouchers</span>
            </a>
        </li>
    </c:if>

    <hr class="sidebar-divider">

    <!-- QUẢN LÝ KHÁCH HÀNG (ADMIN ONLY) -->
    <div class="sidebar-heading">Quản lý Khách hàng</div>

    <c:if test="${sessionScope.currentUser.roleid == Roles.ADMIN}">

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                <i class="fas fa-fw fa-users"></i>
                <span>Người dùng</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/rewards">
                <i class="fas fa-fw fa-gift"></i>
                <span>Quà tặng Hội viên</span>
            </a>
        </li>
    </c:if>

    <hr class="sidebar-divider">

    <!-- CỬA HÀNG & THÔNG TIN (ADMIN ONLY) -->
    <div class="sidebar-heading">Cửa hàng & Thông tin</div>

    <c:if test="${sessionScope.currentUser.roleid == Roles.ADMIN}">

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/stores">
                <i class="fas fa-fw fa-store"></i>
                <span>Cửa hàng</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/about">
                <i class="fas fa-fw fa-info-circle"></i>
                <span>Về chúng tôi</span>
            </a>
        </li>
    </c:if>

    <hr class="sidebar-divider">

    <!-- HỆ THỐNG & BÁO CÁO (ADMIN ONLY) -->
    <div class="sidebar-heading">Hệ thống & Báo cáo</div>

    <c:if test="${sessionScope.currentUser.roleid == Roles.ADMIN}">

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/reports">
                <i class="fas fa-fw fa-chart-area"></i>
                <span>Báo cáo</span>
            </a>
        </li>

    </c:if>

</ul>

<!-- ================= CONTENT ================= -->
<div id="content-wrapper" class="d-flex flex-column" style="margin-left: 0 !important;">
<div id="content">

<!-- TOPBAR -->
<nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 shadow position-relative">
    <!-- Centered Title -->
    <div class="admin-title-center position-absolute start-50">
        <a class="text-decoration-none" href="${pageContext.request.contextPath}/admin/dashboard">
            <span class="admin-title-pill">
                <i class="fas fa-mug-hot"></i> ALOTRA ADMIN
            </span>
        </a>
    </div>
    <!-- User Dropdown (Right) -->
    <div class="ms-auto d-flex align-items-center">
        <ul class="navbar-nav">

<li class="nav-item dropdown no-arrow">
    <a class="nav-link dropdown-toggle" href="#" data-toggle="dropdown">
        <span class="mr-2 d-none d-lg-inline text-gray-600 small">
            ${sessionScope.currentUser.username}
        </span>

        <c:choose>
            <c:when test="${not empty sessionScope.currentUser.avatar}">
                <c:choose>
                    <c:when test="${fn:startsWith(sessionScope.currentUser.avatar, 'http')}">
                        <img class="img-profile rounded-circle"
                             src="${sessionScope.currentUser.avatar}"
                             onerror="this.src='https://via.placeholder.com/60/4e73df/FFFFFF?text=U'"
                             style="width:2rem;height:2rem">
                    </c:when>
                    <c:when test="${fn:startsWith(sessionScope.currentUser.avatar, 'uploads/')}">
                        <img class="img-profile rounded-circle"
                             src="${pageContext.request.contextPath}/${sessionScope.currentUser.avatar}"
                             onerror="this.src='https://via.placeholder.com/60/4e73df/FFFFFF?text=U'"
                             style="width:2rem;height:2rem">
                    </c:when>
                    <c:otherwise>
                        <img class="img-profile rounded-circle"
                             src="${pageContext.request.contextPath}/uploads/${sessionScope.currentUser.avatar}"
                             onerror="this.src='https://via.placeholder.com/60/4e73df/FFFFFF?text=U'"
                             style="width:2rem;height:2rem">
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <img class="img-profile rounded-circle"
                     src="https://via.placeholder.com/60/4e73df/FFFFFF?text=U"
                     style="width:2rem;height:2rem">
            </c:otherwise>
        </c:choose>
    </a>

    <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in">
        <a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">
            <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
            Tài khoản
        </a>
        <div class="dropdown-divider"></div>
        <a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
            <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
            Đăng xuất
        </a>
    </div>
</li>

        </ul>
    </div>
</nav>

<div class="container-fluid">
    <sitemesh:write property="body"/>
</div>

</div>
</div>
</div>

<script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
</body>
</html>
