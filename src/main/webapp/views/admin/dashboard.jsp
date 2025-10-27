<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<h1 class="h4 mb-4">Dashboard Quản trị</h1>

<!-- Thống kê tổng quan -->
<div class="row g-3 mb-4">
  <div class="col-md-3">
    <div class="card text-white bg-primary">
      <div class="card-body">
        <h6 class="card-title">Tổng doanh thu</h6>
        <h3 class="mb-0">
          <fmt:formatNumber value="${stats.totalRevenue}" pattern="#,##0₫"/>
        </h3>
      </div>
    </div>
  </div>
  
  <div class="col-md-3">
    <div class="card text-white bg-success">
      <div class="card-body">
        <h6 class="card-title">Tổng đơn hàng</h6>
        <h3 class="mb-0">${stats.totalOrders}</h3>
      </div>
    </div>
  </div>
  
  <div class="col-md-3">
    <div class="card text-white bg-info">
      <div class="card-body">
        <h6 class="card-title">Khách hàng</h6>
        <h3 class="mb-0">${stats.totalCustomers}</h3>
      </div>
    </div>
  </div>
  
  <div class="col-md-3">
    <div class="card text-white bg-warning">
      <div class="card-body">
        <h6 class="card-title">Đơn chờ xử lý</h6>
        <h3 class="mb-0">${stats.pendingOrders}</h3>
        <a href="${pageContext.request.contextPath}/admin/orders?status=Chờ xác nhận" 
           class="text-white small">Xem chi tiết →</a>
      </div>
    </div>
  </div>
</div>

<!-- Quick Actions -->
<div class="row g-3 mb-4">
  <div class="col-md-12">
    <div class="card">
      <div class="card-header bg-light">
        <h5 class="card-title mb-0">Thao tác nhanh</h5>
      </div>
      <div class="card-body">
        <div class="btn-group" role="group">
          <a href="${pageContext.request.contextPath}/admin/products/create" class="btn btn-primary">
            <i class="bi bi-plus-circle"></i> Thêm sản phẩm
          </a>
          <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-success">
            <i class="bi bi-list-check"></i> Quản lý đơn hàng
          </a>
          <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-info">
            <i class="bi bi-people"></i> Quản lý người dùng
          </a>
          <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-secondary">
            <i class="bi bi-graph-up"></i> Báo cáo chi tiết
          </a>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="row g-4">
  <!-- Top sản phẩm bán chạy -->
  <div class="col-md-6">
    <div class="card">
      <div class="card-header bg-light">
        <h5 class="card-title mb-0">Top 5 sản phẩm bán chạy</h5>
      </div>
      <div class="card-body">
        <table class="table table-sm mb-0">
          <thead>
            <tr><th>Sản phẩm</th><th class="text-end">Đã bán</th></tr>
          </thead>
          <tbody>
            <c:forEach var="item" items="${stats.topProducts}">
              <tr>
                <td>${item[0]}</td>
                <td class="text-end fw-bold">${item[1]}</td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  
  <!-- Sản phẩm sắp hết hàng -->
  <div class="col-md-6">
    <div class="card">
      <div class="card-header bg-light">
        <h5 class="card-title mb-0">Sản phẩm sắp hết hàng</h5>
      </div>
      <div class="card-body">
        <table class="table table-sm mb-0">
          <thead>
            <tr><th>Sản phẩm</th><th class="text-end">Tồn kho</th></tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${empty stats.lowStock}">
                <tr><td colspan="2" class="text-center text-muted">Tất cả sản phẩm đều đủ hàng</td></tr>
              </c:when>
              <c:otherwise>
                <c:forEach var="item" items="${stats.lowStock}">
                  <tr>
                    <td>${item[0]}</td>
                    <td class="text-end">
                      <span class="badge bg-${item[1] < 5 ? 'danger' : 'warning'}">${item[1]}</span>
                    </td>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  
  <!-- Doanh thu 6 tháng gần nhất -->
  <div class="col-md-12">
    <div class="card">
      <div class="card-header bg-light">
        <h5 class="card-title mb-0">Doanh thu 6 tháng gần nhất</h5>
      </div>
      <div class="card-body">
        <table class="table table-striped">
          <thead>
            <tr><th>Tháng/Năm</th><th class="text-end">Doanh thu</th></tr>
          </thead>
          <tbody>
            <c:forEach var="item" items="${stats.monthlyRevenue}">
              <tr>
                <td>Tháng ${item[1]}/${item[0]}</td>
                <td class="text-end fw-bold text-primary">
                  <fmt:formatNumber value="${item[2]}" pattern="#,##0₫"/>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>