<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-gift text-primary" style="margin-right: 10px;"></i>Quản lý Quà Tặng Hội Viên
        </h1>
        <p class="text-muted mb-0">Quản lý các phần quà tặng cho hội viên</p>
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

<%-- Reward Form Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>
            <c:choose>
                <c:when test="${not empty reward}">Chỉnh sửa Quà Tặng</c:when>
                <c:otherwise>Thêm Quà Tặng Mới</c:otherwise>
            </c:choose>
        </h6>
    </div>
    <div class="card-body p-4">
        <form action="${pageContext.request.contextPath}/admin/rewards/${not empty reward ? 'edit' : 'add'}" method="POST" enctype="multipart/form-data">
            <c:if test="${not empty reward}">
                <input type="hidden" name="id" value="${reward.reward_id}">
            </c:if>
            
            <div class="row g-3">
                <div class="col-md-12">
                    <label for="name" class="form-label fw-semibold">Tên quà tặng <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="name" name="name" 
                           value="${reward.name}" required>
                </div>
                <div class="col-md-12">
                    <label for="description" class="form-label fw-semibold">Mô tả</label>
                    <textarea class="form-control" id="description" name="description" rows="3">${reward.description}</textarea>
                </div>
                <div class="col-md-6">
                    <label for="points_required" class="form-label fw-semibold">Điểm cần thiết <span class="text-danger">*</span></label>
                    <input type="number" class="form-control" id="points_required" name="points_required" 
                           value="${reward.points_required}" min="1" required>
                </div>
                <div class="col-md-6">
                    <label for="stock" class="form-label fw-semibold">Số lượng tồn kho</label>
                    <input type="number" class="form-control" id="stock" name="stock" 
                           value="${reward.stock}" min="0" placeholder="Để trống = không giới hạn">
                    <div class="form-text">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Để trống nếu không giới hạn số lượng
                    </div>
                </div>
                <div class="col-md-12">
                    <label class="form-label fw-semibold">Hình ảnh quà tặng</label>
                    <div class="row">
                        <!-- Preview ảnh -->
                        <div class="col-md-5 mb-3">
                            <label class="form-label small text-muted">Ảnh hiện tại</label>
                            <div class="border rounded p-3 bg-light text-center" style="min-height: 200px;">
                                <c:set var="imageSrc" value="${reward.image_url}"/>
                                <c:if test="${not empty imageSrc and not fn:startsWith(imageSrc, 'http')}">
                                    <c:set var="imageSrc" value="${pageContext.request.contextPath}/${reward.image_url}"/>
                                </c:if>
                                
                                <img src="${empty imageSrc ? 'https://via.placeholder.com/300x300?text=Chưa+có+ảnh' : imageSrc}" 
                                     id="imagePreview"
                                     class="img-fluid rounded border" 
                                     style="max-width: 100%; max-height: 250px; object-fit: cover;"
                                     alt="Preview"
                                     onerror="this.src='https://via.placeholder.com/300x300?text=Chưa+có+ảnh'"/>
                            </div>
                        </div>
                        
                        <!-- Upload options -->
                        <div class="col-md-7">
                            <div class="mb-3">
                                <label for="imageFile" class="form-label">Upload ảnh mới <span class="badge bg-primary">Ưu tiên</span></label>
                                <input class="form-control" type="file" name="imageFile" id="imageFile" 
                                       accept="image/jpeg,image/jpg,image/png,image/webp">
                                <div class="form-text">Chọn file ảnh từ máy tính (JPG, PNG, WEBP). Kích thước đề xuất: 500x500px</div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="image_url" class="form-label">Hoặc dán URL ảnh</label>
                                <input type="url" class="form-control" id="image_url" name="image_url" 
                                       value="${fn:startsWith(reward.image_url, 'http') ? reward.image_url : ''}" 
                                       placeholder="https://...">
                                <div class="form-text">Nhập URL ảnh nếu không upload file (ưu tiên thấp hơn)</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-12">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="isActive" name="isActive" 
                               ${reward.isActive ? 'checked' : ''} ${empty reward ? 'checked' : ''}>
                        <label class="form-check-label fw-semibold" for="isActive">
                            Kích hoạt
                        </label>
                    </div>
                </div>
                <div class="col-md-12">
                    <hr class="my-4">
                    <div class="d-flex gap-3">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save" style="margin-right: 10px;"></i>${not empty reward ? 'Cập nhật' : 'Thêm mới'}
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/rewards" class="btn btn-outline-secondary">
                            <i class="fas fa-times" style="margin-right: 10px;"></i>Hủy
                        </a>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- Rewards List Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Danh sách Quà Tặng
            </h6>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4">ID</th>
                        <th style="width: 80px;">Ảnh</th>
                        <th>Tên quà</th>
                        <th>Mô tả</th>
                        <th style="width: 120px;" class="text-end">Điểm cần</th>
                        <th style="width: 100px;" class="text-center">Tồn kho</th>
                        <th style="width: 120px;" class="text-center">Trạng thái</th>
                        <th style="width: 180px;" class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty rewards}">
                            <tr>
                                <td colspan="8" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có quà tặng nào</h5>
                                        <p class="text-muted small mb-0">Bắt đầu bằng cách thêm quà tặng mới</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="r" items="${rewards}">
                                <tr class="border-bottom">
                                    <td class="ps-4">${r.reward_id}</td>
                                    <td>
                                        <c:set var="rewardImageSrc" value="${r.image_url}"/>
                                        <c:if test="${not empty rewardImageSrc and not fn:startsWith(rewardImageSrc, 'http')}">
                                            <c:set var="rewardImageSrc" value="${pageContext.request.contextPath}/${r.image_url}"/>
                                        </c:if>
                                        <img src="${empty rewardImageSrc ? 'https://via.placeholder.com/60x60?text=No+Image' : rewardImageSrc}" 
                                             class="img-thumbnail" 
                                             style="width: 60px; height: 60px; object-fit: cover;"
                                             alt="${r.name}"
                                             onerror="this.src='https://via.placeholder.com/60x60?text=No+Image'"/>
                                    </td>
                                    <td>
                                        <div class="fw-semibold fs-5">${r.name}</div>
                                    </td>
                                    <td>${r.description}</td>
                                    <td class="text-end">
                                        <span class="badge bg-warning text-dark px-3 py-2">
                                            <fmt:formatNumber value="${r.points_required}" pattern="#,##0"/>
                                        </span>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${r.stock == null}">
                                                <span class="text-muted">∞</span>
                                            </c:when>
                                            <c:otherwise>
                                                ${r.stock}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <span class="badge ${r.isActive ? 'bg-success' : 'bg-secondary'} text-white px-3 py-2">
                                            <i class="fas ${r.isActive ? 'fa-check-circle' : 'fa-times-circle'}" style="margin-right: 5px;"></i>
                                            ${r.isActive ? 'Hoạt động' : 'Ngừng'}
                                        </span>
                                    </td>
                                    <td class="text-center pe-4">
                                        <div class="d-flex justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/rewards?id=${r.reward_id}" 
                                               class="btn btn-sm btn-outline-primary" 
                                               title="Chỉnh sửa"
                                               style="margin: 0 7.5px;">
                                                <i class="fas fa-pencil-alt"></i>
                                            </a>
                                            <form action="${pageContext.request.contextPath}/admin/rewards/delete" method="POST" 
                                                  style="display: inline; margin: 0 7.5px;" 
                                                  onsubmit="return confirm('Xác nhận xóa quà tặng \'${r.name}\'?')">
                                                <input type="hidden" name="id" value="${r.reward_id}">
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
document.getElementById('imageFile')?.addEventListener('change', function(e) {
    const file = e.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const preview = document.getElementById('imagePreview');
            if (preview) {
                preview.src = e.target.result;
            }
        };
        reader.readAsDataURL(file);
    }
});

// Preview ảnh khi nhập URL
const imageUrlInput = document.getElementById('image_url');
if (imageUrlInput) {
    imageUrlInput.addEventListener('blur', function() {
        const url = this.value.trim();
        const preview = document.getElementById('imagePreview');
        if (url && preview && url.startsWith('http')) {
            preview.src = url;
        }
    });
}
</script>
