<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title><c:out value="${pageTitle != null ? pageTitle : 'Admin - AloTra'}"/></title>
  <%-- Sử dụng các file CSS đã có --%>
  <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet"/>
  <link href="${pageContext.request.contextPath}/assets/css/admin-style.css" rel="stylesheet"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  
  <style>
    body {
      display: flex;
flex-direction: column;
      min-height: 100vh;
      background-color: #f4f7f6; /* Nền xám nhạt cho khu vực content */
    }
    .top-navbar {
      flex-shrink: 0;
/* Ngăn navbar co lại */
      z-index: 1030;
    }
    .main-wrapper {
      display: flex;
flex-grow: 1; /* Phần thân sẽ chiếm hết không gian còn lại */
      height: calc(100vh - 56px); /* Giới hạn chiều cao bằng viewport trừ đi navbar */
    }
    .admin-sidebar {
      flex-shrink: 0;
width: 250px; /* Độ rộng cố định cho sidebar */
      overflow-y: auto;
/* Cho phép cuộn nếu nội dung sidebar dài */
      height: 100%; /* Chiều cao full */
      position: sticky;
      top: 56px;
/* Dính vào dưới navbar */
      background: #ffffff; /* Đổi nền sidebar thành trắng */
      border-right: 1px solid #dee2e6;
    }
    .main-content {
      flex-grow: 1;
overflow-y: auto; /* Chỉ vùng nội dung này được cuộn */
      padding: 1.5rem; /* Giảm padding một chút */
/* Xóa height: calc(100vh - 56px) để nó tự động fill */
    }
  </style>
</head>
<body data-context-path="${pageContext.request.contextPath}">
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top top-navbar">
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

  <div class="main-wrapper">
    <nav class="admin-sidebar p-3">
        <div class="admin-nav nav flex-column">
		  <a class="nav-link ${fn:contains(pageContext.request.requestURI, '/admin/dashboard') ?
'active' : ''}" 
             href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="bi bi-speedometer2"></i> Dashboard
          </a>
          <a class="nav-link ${fn:contains(pageContext.request.requestURI, '/products') ?
'active' : ''}" 
             href="${pageContext.request.contextPath}/admin/products">
            <i class="bi bi-box-seam"></i> Sản phẩm
          </a>
          <a class="nav-link ${fn:contains(pageContext.request.requestURI, '/category') ?
'active' : ''}" 
             href="${pageContext.request.contextPath}/admin/category/list">
            <i class="bi bi-tags"></i> Danh mục
          </a>
          <a class="nav-link ${fn:contains(pageContext.request.requestURI, '/orders') ?
'active' : ''}" 
             href="${pageContext.request.contextPath}/admin/orders">
            <i class="bi bi-cart-check"></i> Đơn hàng
          </a>
          <a class="nav-link ${fn:contains(pageContext.request.requestURI, '/users') ?
'active' : ''}" 
             href="${pageContext.request.contextPath}/admin/users">
            <i class="bi bi-people"></i> Người dùng
          </a>
          <a class="nav-link ${fn:contains(pageContext.request.requestURI, '/reports') ?
'active' : ''}" 
             href="${pageContext.request.contextPath}/admin/reports">
            <i class="bi bi-graph-up"></i> Báo cáo
          </a>
           <a class="nav-link ${fn:contains(pageContext.request.requestURI, '/payment-config') ? 'active' : ''}" 
             href="${pageContext.request.contextPath}/admin/payment-config">
            <i class="bi bi-credit-card"></i> Cấu hình TT
          </a>
        </div>
      </nav>

      <main class="main-content">
        <sitemesh:write property='body'/>
      </main>
  </div>

  <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>