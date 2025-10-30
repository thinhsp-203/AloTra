<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <h1 class="h4 mb-0">
    <i class="bi bi-credit-card text-primary"></i> Cấu hình thanh toán
  </h1>
  <a href="${pageContext.request.contextPath}/admin/payment-config/create" class="btn btn-primary">
    <i class="bi bi-plus-circle"></i> Thêm phương thức thanh toán
  </a>
</div>

<c:if test="${not empty sessionScope.success}">
  <div class="alert alert-success alert-dismissible fade show">
    <i class="bi bi-check-circle"></i> ${sessionScope.success}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="success" scope="session"/>
</c:if>

<div class="card">
  <div class="card-body">
    <div class="table-responsive">
      <table class="table table-hover align-middle">
        <thead class="table-light">
          <tr>
            <th style="width: 60px;">#</th>
            <th>Phương thức</th>
            <th>Tên hiển thị</th>
            <th>API Endpoint</th>
            <th class="text-center">Trạng thái</th>
            <th class="text-center" style="width: 200px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <c:choose>
            <c:when test="${empty configs}">
              <tr>
                <td colspan="6" class="text-center text-muted py-4">
                  <i class="bi bi-inbox fs-3"></i>
                  <p class="mb-0 mt-2">Chưa có cấu hình thanh toán nào</p>
                </td>
              </tr>
            </c:when>
            <c:otherwise>
              <c:forEach items="${configs}" var="config" varStatus="st">
                <tr>
                  <td><strong>${st.index + 1}</strong></td>
                  <td>
                    <strong>${config.payment_method}</strong>
                    <c:choose>
                      <c:when test="${config.payment_method eq 'VNPAY'}">
                        <span class="badge bg-primary ms-2">Cổng thanh toán</span>
                      </c:when>
                      <c:when test="${config.payment_method eq 'MOMO'}">
                        <span class="badge bg-danger ms-2">Ví điện tử</span>
                      </c:when>
                      <c:when test="${config.payment_method eq 'COD'}">
                        <span class="badge bg-success ms-2">Tiền mặt</span>
                      </c:when>
                    </c:choose>
                  </td>
                  <td>${config.display_name}</td>
                  <td>
                    <c:choose>
                      <c:when test="${not empty config.api_endpoint}">
                        <small class="text-muted">${config.api_endpoint}</small>
                      </c:when>
                      <c:otherwise>
                        <span class="text-muted">-</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td class="text-center">
                    <form method="post" action="${pageContext.request.contextPath}/admin/payment-config/toggle" style="display: inline;">
                      <input type="hidden" name="id" value="${config.config_id}">
                      <button type="submit" class="btn btn-sm btn-${config.isActive ? 'success' : 'secondary'}">
                        <i class="bi bi-${config.isActive ? 'check-circle' : 'x-circle'}"></i>
                        ${config.isActive ? 'Đang bật' : 'Đã tắt'}
                      </button>
                    </form>
                  </td>
                  <td class="text-center">
                    <div class="btn-group btn-group-sm">
                      <a href="${pageContext.request.contextPath}/admin/payment-config/edit?id=${config.config_id}" 
                         class="btn btn-outline-primary" 
                         title="Chỉnh sửa">
                        <i class="bi bi-pencil"></i>
                      </a>
                      <button type="button" class="btn btn-outline-info" title="Xem chi tiết" data-bs-toggle="modal" data-bs-target="#detailModal-${config.config_id}">
                        <i class="bi bi-eye"></i>
                      </button>
                    </div>
                  </td>
                </tr>
                
                <!-- Detail Modal -->
                <div class="modal fade" id="detailModal-${config.config_id}" tabindex="-1">
                  <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                      <div class="modal-header">
                        <h5 class="modal-title">Chi tiết cấu hình: ${config.payment_method}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                      </div>
                      <div class="modal-body">
                        <table class="table table-sm">
                          <tr>
                            <th style="width: 200px;">Merchant ID:</th>
                            <td><code>${config.merchant_id}</code></td>
                          </tr>
                          <tr>
                            <th>Secret Key:</th>
                            <td><code>••••••••</code> <small class="text-muted">(ẩn vì bảo mật)</small></td>
                          </tr>
                          <c:if test="${not empty config.access_key}">
                            <tr>
                              <th>Access Key:</th>
                              <td><code>••••••••</code></td>
                            </tr>
                          </c:if>
                          <c:if test="${not empty config.config_json}">
                            <tr>
                              <th>Config JSON:</th>
                              <td><pre class="small mb-0">${config.config_json}</pre></td>
                            </tr>
                          </c:if>
                        </table>
                      </div>
                      <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                      </div>
                    </div>
                  </div>
                </div>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </tbody>
      </table>
    </div>
  </div>
</div>

<div class="alert alert-info mt-4">
  <h6><i class="bi bi-info-circle"></i> Hướng dẫn cấu hình</h6>
  <ul class="mb-0 small">
    <li><strong>VNPAY:</strong> Đăng ký tài khoản merchant tại <a href="https://sandbox.vnpayment.vn/" target="_blank">VNPAY Sandbox</a></li>
    <li><strong>MOMO:</strong> Tích hợp theo <a href="https://developers.momo.vn/" target="_blank">tài liệu MoMo</a></li>
    <li><strong>COD:</strong> Không yêu cầu cấu hình API, chỉ cần bật/tắt</li>
  </ul>
</div>