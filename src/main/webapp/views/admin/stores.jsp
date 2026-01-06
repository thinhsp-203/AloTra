<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-store text-primary" style="margin-right: 10px;"></i>Quản lý Cửa hàng
        </h1>
        <p class="text-muted mb-0">Quản lý thông tin các cửa hàng</p>
    </div>
</div>

<%-- Alert Messages --%>
<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-check-circle" style="margin-right: 10px;"></i><strong>Thành công!</strong> ${sessionScope.success}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="success" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-exclamation-circle" style="margin-right: 10px;"></i><strong>Lỗi!</strong> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<%-- Store Form Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>
            <c:choose>
                <c:when test="${not empty store}">Chỉnh sửa Cửa hàng</c:when>
                <c:otherwise>Thêm Cửa hàng Mới</c:otherwise>
            </c:choose>
        </h6>
    </div>
    <div class="card-body p-4">
        <form action="${pageContext.request.contextPath}/admin/stores/${not empty store ? 'edit' : 'add'}" method="POST">
            <c:if test="${not empty store}">
                <input type="hidden" name="id" value="${store.store_id}">
            </c:if>
            
            <div class="row g-3">
                <div class="col-md-6">
                    <label for="store_name" class="form-label fw-semibold">Tên cửa hàng <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="store_name" name="store_name" 
                           value="${store.store_name}" required>
                </div>
                <div class="col-md-6">
                    <label for="phone" class="form-label fw-semibold">Số điện thoại</label>
                    <input type="text" class="form-control" id="phone" name="phone" 
                           value="${store.phone}">
                </div>
                <div class="col-md-12">
                    <label for="address" class="form-label fw-semibold">Địa chỉ</label>
                    <input type="text" class="form-control" id="address" name="address" 
                           value="${store.address}">
                </div>
                <div class="col-md-6">
                    <label for="ward" class="form-label fw-semibold">Xã/Phường</label>
                    <input type="text" class="form-control" id="ward" name="ward" 
                           value="${store.ward}">
                </div>
                <div class="col-md-6">
                    <label for="province" class="form-label fw-semibold">Tỉnh/Thành phố <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="province" name="province" 
                           value="${store.province}" required>
                </div>
                <div class="col-md-12">
                    <label for="mapIframe" class="form-label fw-semibold">Mã nhúng bản đồ Google Maps (iframe)</label>
                    <textarea class="form-control" id="mapIframe" name="mapIframe" rows="4" 
                              placeholder="Dán mã iframe từ Google Maps...">${store.mapIframe}</textarea>
                    <div class="form-text">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Lấy mã iframe từ Google Maps: Chọn địa điểm → Chia sẻ → Nhúng bản đồ → Sao chép HTML
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="email" class="form-label fw-semibold">Email</label>
                    <input type="email" class="form-control" id="email" name="email" 
                           value="${store.email}">
                </div>
                <div class="col-md-6">
                    <label for="opening_hours" class="form-label fw-semibold">Giờ mở cửa</label>
                    <input type="text" class="form-control" id="opening_hours" name="opening_hours" 
                           value="${store.opening_hours}" placeholder="VD: 8:00 - 22:00">
                </div>
                <div class="col-md-12">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="isActive" name="isActive" 
                               ${store.isActive ? 'checked' : ''}>
                        <label class="form-check-label fw-semibold" for="isActive">
                            Kích hoạt
                        </label>
                    </div>
                </div>
                <div class="col-md-12">
                    <hr class="my-4">
                    <div class="d-flex gap-3">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save" style="margin-right: 10px;"></i>
                            <c:choose>
                                <c:when test="${not empty store}">Cập nhật</c:when>
                                <c:otherwise>Thêm</c:otherwise>
                            </c:choose>
                        </button>
                        <c:if test="${not empty store}">
                            <a href="${pageContext.request.contextPath}/admin/stores" class="btn btn-outline-secondary">
                                <i class="fas fa-times" style="margin-right: 10px;"></i>Hủy
                            </a>
                        </c:if>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- Stores List Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Danh sách Cửa hàng
            </h6>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">ID</th>
                        <th>Tên cửa hàng</th>
                        <th>Địa chỉ</th>
                        <th>Số điện thoại</th>
                        <th>Tỉnh/Thành phố</th>
                        <th class="text-center">Trạng thái</th>
                        <th class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty stores}">
                            <tr>
                                <td colspan="7" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có cửa hàng nào</h5>
                                        <p class="text-muted small mb-0">Bắt đầu bằng cách thêm cửa hàng mới</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${stores}" var="s">
                                <tr class="border-bottom">
                                    <td class="ps-4">${s.store_id}</td>
                                    <td>
                                        <div class="fw-semibold fs-5">${s.store_name}</div>
                                    </td>
                                    <td>${s.address}</td>
                                    <td>${s.phone}</td>
                                    <td>${s.province}</td>
                                    <td class="text-center">
                                        <span class="badge ${s.isActive ? 'bg-success' : 'bg-secondary'} text-white px-3 py-2">
                                            <i class="fas ${s.isActive ? 'fa-check-circle' : 'fa-times-circle'}" style="margin-right: 5px;"></i>
                                            ${s.isActive ? 'Kích hoạt' : 'Vô hiệu'}
                                        </span>
                                    </td>
                                    <td class="text-center pe-4">
                                        <div class="d-flex justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/stores?id=${s.store_id}" 
                                               class="btn btn-sm btn-outline-primary" 
                                               title="Chỉnh sửa"
                                               style="margin: 0 7.5px;">
                                                <i class="fas fa-pencil-alt"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/stores/delete" method="POST" 
                                                  style="display: inline; margin: 0 7.5px;" 
                                                  onsubmit="return confirm('Xác nhận xóa cửa hàng \'${s.store_name}\'?')">
                                                <input type="hidden" name="id" value="${s.store_id}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </div>
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
