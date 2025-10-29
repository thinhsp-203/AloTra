<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <h1 class="h3 mb-0">
    <i class="bi bi-speedometer2 text-primary"></i> Dashboard
  </h1>
  <div class="text-muted small">
    <i class="bi bi-clock"></i> Cập nhật: <strong id="current-time"></strong>
  </div>
</div>

<!-- Thống kê tổng quan -->
<div class="row g-3 mb-4">
  <div class="col-md-3">
    <div class="card text-white bg-primary h-100">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h6 class="card-title mb-1 opacity-75">Tổng doanh thu</h6>
            <h3 class="mb-0">
              <fmt:formatNumber value="${stats.totalRevenue}" pattern="#,##0₫"/>
            </h3>
          </div>
          <div class="fs-1 opacity-50">
            <i class="bi bi-cash-stack"></i>
          </div>
        </div>
      </div>
      <div class="card-footer bg-transparent border-0 pt-0">
        <a href="${pageContext.request.contextPath}/admin/reports" class="text-white small text-decoration-none">
          Xem báo cáo <i class="bi bi-arrow-right"></i>
        </a>
      </div>
    </div>
  </div>
  
  <div class="col-md-3">
    <div class="card text-white bg-success h-100">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h6 class="card-title mb-1 opacity-75">Tổng đơn hàng</h6>
            <h3 class="mb-0">${stats.totalOrders}</h3>
          </div>
          <div class="fs-1 opacity-50">
            <i class="bi bi-receipt"></i>
          </div>
        </div>
      </div>
      <div class="card-footer bg-transparent border-0 pt-0">
        <a href="${pageContext.request.contextPath}/admin/orders" class="text-white small text-decoration-none">
          Quản lý đơn hàng <i class="bi bi-arrow-right"></i>
        </a>
      </div>
    </div>
  </div>
  
  <div class="col-md-3">
    <div class="card text-white bg-info h-100">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h6 class="card-title mb-1 opacity-75">Khách hàng</h6>
            <h3 class="mb-0">${stats.totalCustomers}</h3>
          </div>
          <div class="fs-1 opacity-50">
            <i class="bi bi-people"></i>
          </div>
        </div>
      </div>
      <div class="card-footer bg-transparent border-0 pt-0">
        <a href="${pageContext.request.contextPath}/admin/users?roleId=3" class="text-white small text-decoration-none">
          Xem khách hàng <i class="bi bi-arrow-right"></i>
        </a>
      </div>
    </div>
  </div>
  
  <div class="col-md-3">
    <div class="card text-white bg-warning h-100">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h6 class="card-title mb-1 opacity-75">Đơn chờ xử lý</h6>
            <h3 class="mb-0">${stats.pendingOrders}</h3>
          </div>
          <div class="fs-1 opacity-50">
            <i class="bi bi-hourglass-split"></i>
          </div>
        </div>
      </div>
      <div class="card-footer bg-transparent border-0 pt-0">
        <a href="${pageContext.request.contextPath}/admin/orders?status=Chờ xác nhận" 
           class="text-white small text-decoration-none">
          Xử lý ngay <i class="bi bi-arrow-right"></i>
        </a>
      </div>
    </div>
  </div>
</div>

<div class="row g-4">
  <!-- Top sản phẩm bán chạy -->
  <div class="col-md-6">
    <div class="card h-100">
      <div class="card-header bg-white border-bottom">
        <div class="d-flex justify-content-between align-items-center">
          <h5 class="card-title mb-0">
            <i class="bi bi-trophy text-warning"></i> Top 5 sản phẩm bán chạy
          </h5>
        </div>
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th class="border-0">Xếp hạng</th>
                <th class="border-0">Sản phẩm</th>
                <th class="border-0 text-end">Đã bán</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="item" items="${stats.topProducts}" varStatus="status">
                <tr>
                  <td>
                    <c:choose>
                      <c:when test="${status.index == 0}">
                        <span class="badge bg-warning text-dark">🥇 #1</span>
                      </c:when>
                      <c:when test="${status.index == 1}">
                        <span class="badge bg-secondary">🥈 #2</span>
                      </c:when>
                      <c:when test="${status.index == 2}">
                        <span class="badge bg-danger">🥉 #3</span>
                      </c:when>
                      <c:otherwise>
                        <span class="text-muted">#${status.index + 1}</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="fw-semibold">${item[0]}</td>
                  <td class="text-end">
                    <span class="badge bg-success">${item[1]} sp</span>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
  
  <!-- Sản phẩm sắp hết hàng -->
  <div class="col-md-6">
    <div class="card h-100">
      <div class="card-header bg-white border-bottom">
        <div class="d-flex justify-content-between align-items-center">
          <h5 class="card-title mb-0">
            <i class="bi bi-exclamation-triangle text-warning"></i> Cảnh báo tồn kho
          </h5>
          <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-sm btn-outline-primary">
            <i class="bi bi-plus-circle"></i> Nhập hàng
          </a>
        </div>
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th class="border-0">Sản phẩm</th>
                <th class="border-0 text-end">Tồn kho</th>
                <th class="border-0 text-center">Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              <c:choose>
                <c:when test="${empty stats.lowStock}">
                  <tr>
                    <td colspan="3" class="text-center text-muted py-4">
                      <i class="bi bi-check-circle text-success fs-3"></i>
                      <p class="mb-0 mt-2">Tất cả sản phẩm đều đủ hàng</p>
                    </td>
                  </tr>
                </c:when>
                <c:otherwise>
                  <c:forEach var="item" items="${stats.lowStock}">
                    <tr>
                      <td class="fw-semibold">${item[0]}</td>
                      <td class="text-end">
                        <span class="badge bg-${item[1] < 5 ? 'danger' : 'warning'} fs-6">
                          ${item[1]}
                        </span>
                      </td>
                      <td class="text-center">
                        <c:choose>
                          <c:when test="${item[1] < 5}">
                            <span class="text-danger small">
                              <i class="bi bi-exclamation-circle-fill"></i> Cần nhập gấp
                            </span>
                          </c:when>
                          <c:otherwise>
                            <span class="text-warning small">
                              <i class="bi bi-exclamation-triangle"></i> Sắp hết
                            </span>
                          </c:otherwise>
                        </c:choose>
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
  </div>
  
  <!-- Doanh thu 6 tháng gần nhất -->
  <div class="col-md-12">
    <div class="card">
      <div class="card-header bg-white border-bottom">
        <div class="d-flex justify-content-between align-items-center">
          <h5 class="card-title mb-0">
            <i class="bi bi-graph-up-arrow text-primary"></i> Doanh thu 6 tháng gần nhất
          </h5>
          <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-sm btn-outline-secondary">
            <i class="bi bi-file-earmark-bar-graph"></i> Báo cáo đầy đủ
          </a>
        </div>
      </div>
      <div class="card-body">
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead class="table-light">
              <tr>
                <th>Tháng/Năm</th>
                <th class="text-end">Doanh thu</th>
                <th class="text-center">So với tháng trước</th>
              </tr>
            </thead>
            <tbody>
              <c:set var="prevRevenue" value="0"/>
              <c:forEach var="item" items="${stats.monthlyRevenue}" varStatus="status">
                <tr>
                  <td>
                    <strong>Tháng ${item[1]}/${item[0]}</strong>
                  </td>
                  <td class="text-end">
                    <span class="fs-5 fw-bold text-primary">
                      <fmt:formatNumber value="${item[2]}" pattern="#,##0₫"/>
                    </span>
                  </td>
                  <td class="text-center">
                    <c:choose>
                      <c:when test="${status.index > 0 && item[2] > prevRevenue}">
                        <span class="badge bg-success">
                          <i class="bi bi-arrow-up"></i> Tăng
                        </span>
                      </c:when>
                      <c:when test="${status.index > 0 && item[2] < prevRevenue}">
                        <span class="badge bg-danger">
                          <i class="bi bi-arrow-down"></i> Giảm
                        </span>
                      </c:when>
                      <c:otherwise>
                        <span class="text-muted">-</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
                <c:set var="prevRevenue" value="${item[2]}"/>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
// Hiển thị thời gian real-time
function updateTime() {
  const now = new Date();
  const timeStr = now.toLocaleTimeString('vi-VN', { 
    hour: '2-digit', 
    minute: '2-digit',
    second: '2-digit'
  });
  const dateStr = now.toLocaleDateString('vi-VN');
  document.getElementById('current-time').textContent = dateStr + ' ' + timeStr;
}
updateTime();
setInterval(updateTime, 1000);
</script>