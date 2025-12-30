<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="utils.Roles" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8"/>
    <title>${pageTitle != null ? pageTitle : 'Admin - AloTra'}</title>

    <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet">
</head>

<body id="page-top">
<div id="wrapper">

<!-- ================= SIDEBAR ================= -->
<ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

    <a class="sidebar-brand d-flex align-items-center justify-content-center"
       href="${pageContext.request.contextPath}/admin/dashboard">
        <div class="sidebar-brand-icon rotate-n-15">
            <i class="fas fa-mug-hot"></i>
        </div>
        <div class="sidebar-brand-text mx-3">AloTra Admin</div>
    </a>

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
                <i class="fas fa-fw fa-cart-shopping"></i>
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

    <!-- HỆ THỐNG (ADMIN ONLY) -->
    <div class="sidebar-heading">Hệ thống</div>

    <c:if test="${sessionScope.currentUser.roleid == Roles.ADMIN}">

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                <i class="fas fa-fw fa-users"></i>
                <span>Người dùng</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/vouchers">
                <i class="fas fa-fw fa-ticket-alt"></i>
                <span>Vouchers</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/reports">
                <i class="fas fa-fw fa-chart-area"></i>
                <span>Báo cáo</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/payment-config">
                <i class="fas fa-fw fa-credit-card"></i>
                <span>Cấu hình TT</span>
            </a>
        </li>

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
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/stores">
                <i class="fas fa-fw fa-store"></i>
                <span>Cửa hàng</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/settings">
                <i class="fas fa-fw fa-cog"></i>
                <span>Cài đặt</span>
            </a>
        </li>
    </c:if>

</ul>

<!-- ================= CONTENT ================= -->
<div id="content-wrapper" class="d-flex flex-column">
<div id="content">

<!-- TOPBAR -->
<nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 shadow">
<ul class="navbar-nav ml-auto">

<li class="nav-item dropdown no-arrow">
    <a class="nav-link dropdown-toggle" href="#" data-toggle="dropdown">
        <span class="mr-2 d-none d-lg-inline text-gray-600 small">
            ${sessionScope.currentUser.username}
        </span>

        <img class="img-profile rounded-circle"
             src="${pageContext.request.contextPath}/uploads/${sessionScope.currentUser.avatar}"
             onerror="this.src='https://via.placeholder.com/60/4e73df/FFFFFF?text=U'"
             style="width:2rem;height:2rem">
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
