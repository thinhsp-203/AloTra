<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-gift text-primary" style="margin-right: 10px;"></i>Quản lý Khuyến mãi
        </h1>
        <p class="text-muted mb-0">Quản lý các chương trình khuyến mãi của cửa hàng</p>
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

<%-- Promotion Form Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>
            <c:choose>
                <c:when test="${not empty promotion}">Chỉnh sửa Khuyến mãi</c:when>
                <c:otherwise>Thêm Khuyến mãi Mới</c:otherwise>
            </c:choose>
        </h6>
    </div>
    <div class="card-body p-4">
        <form action="${pageContext.request.contextPath}/admin/promotions/${not empty promotion ? 'edit' : 'add'}" method="POST" enctype="multipart/form-data">
            <c:if test="${not empty promotion}">
                <input type="hidden" name="id" value="${promotion.id}">
            </c:if>
            
            <div class="row g-3">
                <div class="col-md-12">
                    <label for="title" class="form-label fw-semibold">Tiêu đề <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="title" name="title" 
                           value="${promotion.title}" required>
                </div>
                <div class="col-md-12">
                    <label for="description" class="form-label fw-semibold">Mô tả ngắn</label>
                    <textarea class="form-control" id="description" name="description" rows="2">${promotion.description}</textarea>
                </div>
                <div class="col-md-12">
                    <label for="content" class="form-label fw-semibold">Nội dung chi tiết</label>
                    <textarea class="form-control" id="content" name="content" rows="5">${promotion.content}</textarea>
                </div>
                <div class="col-md-6">
                    <label for="promotionFile" class="form-label fw-semibold">Chọn ảnh (Ưu tiên)</label>
                    <input type="file" class="form-control" id="promotionFile" name="promotionFile" accept="image/*">
                    <div class="form-text">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Chọn file từ máy tính.
                    </div>
                    
                    <%-- Preview ảnh khi chọn file --%>
                    <div class="mt-3" id="imagePreviewContainer" style="display: none;">
                        <small class="text-muted d-block mb-2">Xem trước ảnh mới:</small>
                        <img id="imagePreview" src="" alt="Preview" 
                             class="border rounded" 
                             style="max-width: 200px; max-height: 200px; object-fit: cover;">
                    </div>
                    
                    <%-- Hiển thị ảnh hiện tại (khi edit) --%>
                    <c:if test="${not empty promotion and not empty promotion.imageUrl}">
                        <div class="mt-3" id="currentImageContainer">
                            <small class="text-muted d-block mb-2">Ảnh hiện tại:</small>
                            <c:set var="promoPreviewSrc" value="${promotion.imageUrl}"/>
                            <c:if test="${not empty promoPreviewSrc}">
                                <c:choose>
                                    <c:when test="${fn:startsWith(promoPreviewSrc, 'http')}">
                                        <c:set var="promoPreviewSrc" value="${promotion.imageUrl}"/>
                                    </c:when>
                                    <c:when test="${fn:startsWith(promoPreviewSrc, 'uploads/')}">
                                        <c:set var="promoPreviewSrc" value="${pageContext.request.contextPath}/${promotion.imageUrl}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="promoPreviewSrc" value="${pageContext.request.contextPath}/uploads/${promotion.imageUrl}"/>
                                    </c:otherwise>
                                </c:choose>
                                <img src="${promoPreviewSrc}" alt="Current" id="currentImage"
                                     class="border rounded" 
                                     style="max-width: 200px; max-height: 200px; object-fit: cover;"
                                     onerror="this.src='https://via.placeholder.com/200?text=Chưa+có+ảnh'">
                            </c:if>
                        </div>
                    </c:if>
                </div>
                <div class="col-md-6">
                    <label for="imageUrl" class="form-label fw-semibold">Hoặc dán URL ảnh</label>
                    <input type="text" class="form-control" id="imageUrl" name="imageUrl" 
                           value="${promotion.imageUrl}" placeholder="https://...">
                    <div class="form-text">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Nếu không upload file, hệ thống sẽ lấy URL này.
                    </div>
                </div>
                <div class="col-md-12">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="isActive" name="isActive" 
                               ${promotion.isActive() ? 'checked' : ''}>
                        <label class="form-check-label fw-semibold" for="isActive">
                            Hiển thị?
                        </label>
                    </div>
                </div>
                <div class="col-md-12">
                    <hr class="my-4">
                    <div class="d-flex gap-3">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save" style="margin-right: 10px;"></i>
                            <c:choose>
                                <c:when test="${not empty promotion}">Cập nhật</c:when>
                                <c:otherwise>Thêm</c:otherwise>
                            </c:choose>
                        </button>
                        <c:if test="${not empty promotion}">
                            <a href="${pageContext.request.contextPath}/admin/promotions" class="btn btn-outline-secondary">
                                <i class="fas fa-times" style="margin-right: 10px;"></i>Hủy
                            </a>
                        </c:if>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- Promotions List Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Danh sách Khuyến mãi
            </h6>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 120px;" class="ps-4">Ảnh</th>
                        <th>Tiêu đề</th>
                        <th>Mô tả</th>
                        <th style="width: 120px;" class="text-center">Trạng thái</th>
                        <th style="width: 150px;">Ngày tạo</th>
                        <th style="width: 180px;" class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty promotions}">
                            <tr>
                                <td colspan="6" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có khuyến mãi nào</h5>
                                        <p class="text-muted small mb-0">Bắt đầu bằng cách thêm khuyến mãi mới</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${promotions}" var="p">
                                <tr class="border-bottom">
                                    <td class="ps-4">
                                        <c:set var="promoImgSrc" value="${p.imageUrl}"/>
                                        <c:if test="${not empty promoImgSrc}">
                                            <c:choose>
                                                <c:when test="${fn:startsWith(promoImgSrc, 'http')}">
                                                    <c:set var="promoImgSrc" value="${p.imageUrl}"/>
                                                </c:when>
                                                <c:when test="${fn:startsWith(promoImgSrc, 'uploads/')}">
                                                    <c:set var="promoImgSrc" value="${pageContext.request.contextPath}/${p.imageUrl}"/>
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="promoImgSrc" value="${pageContext.request.contextPath}/uploads/${p.imageUrl}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                        <img src="${empty promoImgSrc ? 'https://via.placeholder.com/80' : promoImgSrc}" 
                                             alt="Promotion" 
                                             class="rounded shadow-sm"
                                             style="width: 80px; height: 80px; object-fit: cover;"
                                             onerror="this.src='https://via.placeholder.com/80'">
                                    </td>
                                    <td>
                                        <div class="fw-semibold fs-5">${p.title}</div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${fn:length(p.description) > 50}">
                                                ${fn:substring(p.description, 0, 50)}...
                                            </c:when>
                                            <c:otherwise>
                                                ${p.description}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <span class="badge ${p.isActive() ? 'bg-success' : 'bg-secondary'} text-white px-3 py-2">
                                            <i class="fas ${p.isActive() ? 'fa-check-circle' : 'fa-times-circle'}" style="margin-right: 5px;"></i>
                                            ${p.isActive() ? 'Hiển thị' : 'Ẩn'}
                                        </span>
                                    </td>
                                    <td>
                                        <small class="text-muted">
                                            <fmt:formatDate value="${p.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </small>
                                    </td>
                                    <td class="text-center pe-4">
                                        <div class="d-flex justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/promotions?id=${p.id}" 
                                               class="btn btn-sm btn-outline-primary" 
                                               title="Chỉnh sửa"
                                               style="margin: 0 7.5px;">
                                                <i class="fas fa-pencil-alt"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/promotions/delete" method="POST" 
                                                  style="display: inline; margin: 0 7.5px;" 
                                                  onsubmit="return confirm('Xác nhận xóa khuyến mãi \'${p.title}\'?')">
                                                <input type="hidden" name="id" value="${p.id}">
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

<script>
// Preview ảnh khi chọn file
document.getElementById('promotionFile').addEventListener('change', function(e) {
    const file = e.target.files[0];
    const previewContainer = document.getElementById('imagePreviewContainer');
    const previewImg = document.getElementById('imagePreview');
    const currentImageContainer = document.getElementById('currentImageContainer');
    
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            previewImg.src = e.target.result;
            previewContainer.style.display = 'block';
            // Ẩn ảnh hiện tại khi có preview mới
            if (currentImageContainer) {
                currentImageContainer.style.display = 'none';
            }
        };
        reader.readAsDataURL(file);
    } else {
        previewContainer.style.display = 'none';
        // Hiện lại ảnh hiện tại nếu không chọn file
        if (currentImageContainer) {
            currentImageContainer.style.display = 'block';
        }
    }
});
</script>
