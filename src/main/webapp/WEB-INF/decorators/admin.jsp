<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8"/>
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
  <title><c:out value="${pageTitle != null ? pageTitle : 'Admin - AloTra'}"/></title>
  
  <link href="${pageContext.request.contextPath}/assets/vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/assets/css/sb-admin-2.min.css" rel="stylesheet"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

</head>
<body id="page-top" data-context-path="${pageContext.request.contextPath}">
  <div id="wrapper">
    <ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">
      <a class="sidebar-brand d-flex align-items-center justify-content-center" href="${pageContext.request.contextPath}/admin/dashboard">
        <div class="sidebar-brand-icon rotate-n-15">
          <i class="fas fa-mug-hot"></i>
        </div>
        <div class="sidebar-brand-text mx-3">AloTra Admin</div>
      </a>
      <hr class="sidebar-divider my-0">
      <li class="nav-item ${fn:contains(pageContext.request.requestURI, '/admin/dashboard') ? 'active' : ''}">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">
          <i class="fas fa-fw fa-tachometer-alt"></i>
          <span>Dashboard</span>
        </a>
      </li>
      <hr class="sidebar-divider">
      <div class="sidebar-heading">
        Quản lý Bán Hàng
      </div>
      <li class="nav-item ${fn:contains(pageContext.request.requestURI, '/admin/orders') ? 'active' : ''}">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/orders">
          <i class="fas fa-fw fa-cart-shopping"></i>
          <span>Đơn hàng</span>
        </a>
      </li>
      <li class="nav-item ${fn:contains(pageContext.request.requestURI, '/admin/products') ? 'active' : ''}">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/products">
          <i class="fas fa-fw fa-box"></i>
          <span>Sản phẩm</span>
        </a>
      </li>
      <li class="nav-item ${fn:contains(pageContext.request.requestURI, '/admin/category') ? 'active' : ''}">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/category/list">
          <i class="fas fa-fw fa-tags"></i>
          <span>Danh mục</span>
        </a>
      </li>
      <li class="nav-item ${fn:contains(pageContext.request.requestURI, '/admin/toppings') ? 'active' : ''}">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/toppings">
          <i class="fas fa-fw fa-ice-cream"></i>
          <span>Topping</span>
        </a>
      </li>
      <hr class="sidebar-divider">
      <div class="sidebar-heading">
        Hệ thống
      </div>
      <li class="nav-item ${fn:contains(pageContext.request.requestURI, '/admin/users') ? 'active' : ''}">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">
          <i class="fas fa-fw fa-users"></i>
          <span>Người dùng</span>
        </a>
      </li>
      <li class="nav-item ${fn:contains(pageContext.request.requestURI, '/admin/vouchers') ? 'active' : ''}">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/vouchers">
          <i class="fas fa-fw fa-ticket-alt"></i>
          <span>Vouchers</span>
        </a>
      </li>
      <li class="nav-item ${fn:contains(pageContext.request.requestURI, '/admin/reports') ? 'active' : ''}">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/reports">
          <i class="fas fa-fw fa-chart-area"></i>
          <span>Báo cáo</span>
        </a>
      </li>
      <li class="nav-item ${fn:contains(pageContext.request.requestURI, '/admin/payment-config') ? 'active' : ''}">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/payment-config">
          <i class="fas fa-fw fa-credit-card"></i>
          <span>Cấu hình TT</span>
        </a>
      </li>
      
      <li class="nav-item ${fn:contains(pageContext.request.requestURI, '/admin/settings') ? 'active' : ''}">
        <a class="nav-link" href="${pageContext.request.contextPath}/admin/settings">
          <i class="fas fa-fw fa-cog"></i>
          <span>Cài đặt Website</span>
        </a>
      </li>
      
      <hr class="sidebar-divider d-none d-md-block">
      <div class="text-center d-none d-md-inline">
        <button class="rounded-circle border-0" id="sidebarToggle"></button>
      </div>
    </ul>
    
    <div id="content-wrapper" class="d-flex flex-column">
      <div id="content">
        <nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">
          <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
            <i class="fa fa-bars"></i>
          </button>
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <a class="nav-link" href="${pageContext.request.contextPath}/home" target="_blank" title="Xem trang web">
                 <i class="fas fa-external-link-alt fa-fw text-gray-600"></i>
              </a>
            </li>
            <div class="topbar-divider d-none d-sm-block"></div>
            <li class="nav-item dropdown no-arrow">
              <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                 data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <span class="mr-2 d-none d-lg-inline text-gray-600 small">${sessionScope.currentUser.username}</span>
                <c:set var="avatarSrc">
                    <c:choose>
                        <c:when test="${not empty sessionScope.currentUser.avatar}">
                            ${pageContext.request.contextPath}/uploads/${sessionScope.currentUser.avatar}
                        </c:when>
                        <c:otherwise>
                            https://via.placeholder.com/60/4e73df/FFFFFF?text=${fn:substring(sessionScope.currentUser.username, 0, 1)}
                        </c:otherwise>
                    </c:choose>
                </c:set>
                <img class="img-profile rounded-circle" src="${avatarSrc}" style="width: 2rem; height: 2rem; object-fit: cover;">
              </a>
              <div class="dropdown-menu dropdown-menu-end shadow animated--grow-in"
                   aria-labelledby="userDropdown">
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
          <sitemesh:write property='body'/>
        </div>
      </div>
      <footer class="sticky-footer bg-white">
        <div class="container my-auto">
          <div class="copyright text-center my-auto">
            <span>Copyright &copy; AloTra 2025</span>
          </div>
        </div>
      </footer>
    </div>
  </div>
  <a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
  </a>

  <script src="${pageContext.request.contextPath}/assets/vendor/jquery/jquery.min.js"></script>
  <script src="${pageContext.request.contextPath}/assets/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/assets/vendor/jquery-easing/jquery.easing.min.js"></script>
  <script src="${pageContext.request.contextPath}/assets/js/sb-admin-2.min.js"></script>
  <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>