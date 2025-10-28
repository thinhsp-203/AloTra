<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="navbar navbar-expand-lg bg-body-tertiary border-bottom sticky-top">
  <div class="container">
    <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/home">AloTra</a>

    <form class="d-flex mx-auto" style="min-width: 40%;" action="${pageContext.request.contextPath}/products" method="get">
      <input class="form-control me-2" type="search" name="q" placeholder="Tìm trà sữa, cà phê..." value="${param.q}"/>
      <button class="btn btn-primary" type="submit"><i class="bi bi-search"></i> Tìm</button>
    </form>

    <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
      <c:choose>
        <%-- KHI CHƯA ĐĂNG NHẬP --%>
        <c:when test="${empty sessionScope.currentUser}">
          <li class="nav-item">
            <a class="nav-link" href="${pageContext.request.contextPath}/register">Đăng ký</a>
          </li>
          <li class="nav-item">
            <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
          </li>
        </c:when>

        <%-- KHI ĐÃ ĐĂNG NHẬP --%>
        <c:otherwise>
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              <i class="bi bi-person-circle"></i> Chào, ${sessionScope.currentUser.username}
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">Tài Khoản Của Tôi</a></li>
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/orders">Đơn Mua</a></li>
              
              <%-- Hiển thị link trang quản trị cho Admin/Manager --%>
              <c:if test="${sessionScope.currentUser.roleid == 1 || sessionScope.currentUser.roleid == 2}">
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin">Trang Quản Trị</a></li>
              </c:if>

              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Đăng Xuất</a></li>
            </ul>
          </li>
        </c:otherwise>
      </c:choose>
    </ul>
  </div>
</nav>