<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-credit-card text-primary" style="margin-right: 10px;"></i>${empty config.config_id ? 'Thêm' : 'Sửa'} cấu hình thanh toán
        </h1>
        <p class="text-muted mb-0">${empty config.config_id ? 'Thêm phương thức thanh toán mới' : 'Chỉnh sửa thông tin cấu hình thanh toán'}</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/payment-config" class="btn btn-outline-secondary">
        <i class="fas fa-arrow-left" style="margin-right: 10px;"></i>Quay lại
    </a>
</div>

<%-- Alert Messages --%>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-exclamation-circle" style="margin-right: 10px;"></i><strong>Lỗi!</strong> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-check-circle" style="margin-right: 10px;"></i><strong>Thành công!</strong> ${sessionScope.success}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="success" scope="session"/>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/admin/payment-config/save" id="paymentConfigForm">
    
    <c:if test="${not empty config.config_id}">
        <input type="hidden" name="id" value="${config.config_id}"/>
    </c:if>
    
    <div class="row">
        <!-- Cột trái: Thông tin chính -->
        <div class="col-lg-8">
            <!-- Thông tin cơ bản -->
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Thông tin cơ bản
                    </h6>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label for="payment_method" class="form-label fw-bold">Phương thức thanh toán <span class="text-danger">*</span></label>
                        <select class="form-select" id="payment_method" name="payment_method" required>
                            <option value="">-- Chọn phương thức --</option>
                            <option value="VNPAY" ${config.payment_method eq 'VNPAY' ? 'selected' : ''}>VNPay</option>
                            <option value="MOMO" ${config.payment_method eq 'MOMO' ? 'selected' : ''}>MOMO</option>
                            <option value="COD" ${config.payment_method eq 'COD' ? 'selected' : ''}>COD (Thanh toán khi nhận hàng)</option>
                        </select>
                    </div>
                    
                    <div class="mb-3">
                        <label for="display_name" class="form-label fw-bold">Tên hiển thị <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="display_name" name="display_name" 
                               value="${config.display_name}" required placeholder="Ví dụ: Thanh toán qua VNPay">
                    </div>
                    
                    <div class="mb-3">
                        <label for="api_endpoint" class="form-label fw-bold">API Endpoint</label>
                        <input type="text" class="form-control" id="api_endpoint" name="api_endpoint" 
                               value="${config.api_endpoint}" placeholder="https://sandbox.vnpayment.vn/paymentv2/vpcpay.html">
                    </div>
                </div>
            </div>
            
            <!-- Thông tin xác thực -->
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-key" style="margin-right: 10px;"></i>Thông tin xác thực
                    </h6>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label for="merchant_id" class="form-label fw-bold">Merchant ID</label>
                        <input type="text" class="form-control" id="merchant_id" name="merchant_id" 
                               value="${config.merchant_id}" placeholder="Nhập Merchant ID">
                    </div>
                    
                    <div class="mb-3">
                        <label for="secret_key" class="form-label fw-bold">Secret Key</label>
                        <input type="password" class="form-control" id="secret_key" name="secret_key" 
                               value="${config.secret_key}" placeholder="Nhập Secret Key">
                    </div>
                    
                    <div class="mb-3">
                        <label for="access_key" class="form-label fw-bold">Access Key</label>
                        <input type="text" class="form-control" id="access_key" name="access_key" 
                               value="${config.access_key}" placeholder="Nhập Access Key">
                    </div>
                    
                    <div class="mb-3">
                        <label for="config_json" class="form-label fw-bold">Config JSON (Tùy chọn)</label>
                        <textarea class="form-control" id="config_json" name="config_json" rows="4" 
                                  placeholder='{"key": "value"}'></textarea>
                        <small class="text-muted">Cấu hình bổ sung dạng JSON (nếu cần)</small>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Cột phải: Trạng thái -->
        <div class="col-lg-4">
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-toggle-on" style="margin-right: 10px;"></i>Trạng thái
                    </h6>
                </div>
                <div class="card-body">
                    <div class="form-check form-switch">
                        <input class="form-check-input" type="checkbox" id="isActive" name="isActive" 
                               ${config.isActive ? 'checked' : ''}>
                        <label class="form-check-label" for="isActive">
                            Kích hoạt phương thức thanh toán này
                        </label>
                    </div>
                </div>
            </div>
            
            <!-- Nút lưu -->
            <div class="d-grid gap-2">
                <button type="submit" class="btn btn-primary btn-lg">
                    <i class="fas fa-save" style="margin-right: 10px;"></i>Lưu cấu hình
                </button>
                <a href="${pageContext.request.contextPath}/admin/payment-config" class="btn btn-outline-secondary">
                    <i class="fas fa-times" style="margin-right: 10px;"></i>Hủy
                </a>
            </div>
        </div>
    </div>
</form>

<script>
    // Pre-fill config_json if exists
    <c:if test="${not empty config.config_json}">
    document.getElementById('config_json').value = '${config.config_json}';
    </c:if>
</script>

