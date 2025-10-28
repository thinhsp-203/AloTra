<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title><c:out value="${pageTitle != null ? pageTitle : 'Admin - AloTra'}"/></title>
  <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet"/>
  <link href="${pageContext.request.contextPath}/assets/css/admin-style.css" rel="stylesheet"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <style>
    .admin-sidebar {
      min-height: calc(100vh - 56px);
      background-color: #f8f9fa;
      border-right: 1px solid #dee2e6;
    }
    .admin-nav .nav-link {
      color: #495057;
      border-radius: 0.25rem;
      margin-bottom: 0.25rem;
    }
    .admin-nav .nav-link:hover,
    .admin-nav .nav-link.active {
      background-color: #e9ecef;
      color: #0d6efd;
    }
  </style>
</head>
<body data-context-path="${pageContext.request.contextPath}">
  <!-- Top Navbar -->
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
      <a class="navbar-brand" href="${pageContext.request.contextPath}/admin/dashboard">
        <i class="bi bi-shield-lock"></i> AloTra Admin
      </a>
      
      <ul class="navbar-nav ms-auto">
        <li class="nav-item">
          <a class="nav-link" href="${pageContext.request.contextPath}/home" target="_blank">
            <i class="bi bi-box-arrow-up-right"></i> Xem web
          </a>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
            <i class="bi bi-person-circle"></i> ${sessionScope.currentUser.username}
          </a>
          <ul class="dropdown-menu dropdown-menu-end">
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">Tài khoản</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Đăng xuất</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </nav>

  <div class="container-fluid">
    <div class="row">
      <!-- Sidebar -->
      <nav class="col-md-2 d-md-block admin-sidebar p-3">
        <div class="admin-nav nav flex-column">
          <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="bi bi-speedometer2"></i> Dashboard
          </a>
          <a class="nav-link" href="${pageContext.request.contextPath}/admin/products">
            <i class="bi bi-box-seam"></i> Sản phẩm
          </a>
          <a class="nav-link" href="${pageContext.request.contextPath}/admin/category/list">
            <i class="bi bi-tags"></i> Danh mục
          </a>
          <a class="nav-link" href="${pageContext.request.contextPath}/admin/orders">
            <i class="bi bi-cart-check"></i> Đơn hàng
          </a>
          <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
            <i class="bi bi-people"></i> Người dùng
          </a>
          <a class="nav-link" href="${pageContext.request.contextPath}/admin/reports">
            <i class="bi bi-graph-up"></i> Báo cáo
          </a>
        </div>
      </nav>

      <!-- Main Content -->
      <main class="col-md-10 ms-sm-auto px-md-4 py-4">
        <sitemesh:write property='body'/>
      </main>
    </div>
  </div>

  <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>