<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-images text-primary" style="margin-right: 10px;"></i>Quản lý Banner & Logo
        </h1>
        <p class="text-muted mb-0">Quản lý banner và logo website</p>
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

<%-- Logo Settings Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-image" style="margin-right: 10px;"></i>Cài đặt Logo Website
        </h6>
    </div>
    <div class="card-body p-4">
        <form method="POST" action="${pageContext.request.contextPath}/admin/banners">
            <input type="hidden" name="action" value="updateLogo">
            <div class="row g-3">
                <div class="col-md-8">
                    <label for="logoUrl" class="form-label fw-semibold">Logo URL</label>
                    <input type="text" class="form-control" id="logoUrl" name="LOGO_URL" 
                           value="${siteSettings.LOGO_URL}" 
                           placeholder="https://.../logo.png hoặc /uploads/logo.png">
                    <div class="form-text">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Dán link ảnh logo. Sẽ hiển thị ở góc trên bên trái (navbar).
                    </div>
                </div>
                <div class="col-md-4">
                    <label class="form-label fw-semibold">Xem trước Logo</label>
                    <div class="border rounded p-3 bg-light text-center">
                        <c:choose>
                            <c:when test="${not empty siteSettings.LOGO_URL}">
                                <img src="${siteSettings.LOGO_URL}" 
                                     alt="Logo" 
                                     id="logoPreview"
                                     style="max-height: 80px; max-width: 100%; object-fit: contain;">
                            </c:when>
                            <c:otherwise>
                                <div class="text-muted py-3" id="logoPreview">Chưa có logo</div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div class="col-md-12">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save" style="margin-right: 10px;"></i>Lưu Logo
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
// Preview logo khi nhập URL
document.getElementById('logoUrl')?.addEventListener('input', function(e) {
    const url = this.value.trim();
    const preview = document.getElementById('logoPreview');
    if (url && preview) {
        if (url.startsWith('http') || url.startsWith('/')) {
            preview.innerHTML = `<img src="${url}" alt="Logo" style="max-height: 80px; max-width: 100%; object-fit: contain;">`;
        }
    } else {
        preview.innerHTML = '<div class="text-muted py-3">Chưa có logo</div>';
    }
});
</script>

<%-- Banner Form Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-images" style="margin-right: 10px;"></i>Thêm Banner Mới
        </h6>
    </div>
    <div class="card-body p-4">
        <form action="${pageContext.request.contextPath}/admin/banners" method="POST" enctype="multipart/form-data">
            <input type="hidden" name="action" value="add">
            
            <div class="row g-3">
                <div class="col-md-6">
                    <label for="bannerFile" class="form-label fw-semibold">Chọn ảnh (Ưu tiên)</label>
                    <input type="file" class="form-control" id="bannerFile" name="bannerFile" accept="image/*">
                    <div class="form-text">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Chọn file từ máy tính.
                    </div>
                </div>
                <div class="col-md-6">
                    <label for="imageUrl" class="form-label fw-semibold">Hoặc dán URL ảnh</label>
                    <input type="text" class="form-control" id="imageUrl" name="imageUrl" placeholder="https://...">
                    <div class="form-text">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Nếu không upload file, hệ thống sẽ lấy URL này.
                    </div>
                </div>
                <div class="col-md-3">
                    <label for="linkUrl" class="form-label fw-semibold">Link khi click</label>
                    <input type="text" class="form-control" id="linkUrl" name="linkUrl" placeholder="/hoặc URL đầy đủ">
                </div>
                <div class="col-md-2">
                    <label for="sortOrder" class="form-label fw-semibold">Thứ tự:</label>
                    <input type="number" class="form-control" id="sortOrder" name="sortOrder" value="0">
                </div>
                <div class="col-md-4 d-flex align-items-end">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="isActive" name="isActive" value="true" checked>
                        <label class="form-check-label fw-semibold" for="isActive">
                            Hiển thị?
                        </label>
                    </div>
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-plus" style="margin-right: 10px;"></i>Thêm
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<%-- Banners List Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <div class="d-flex justify-content-between align-items-center">
            <h6 class="m-0 font-weight-bold text-primary">
                <i class="fas fa-list-alt" style="margin-right: 10px;"></i>Danh sách Banner
            </h6>
        </div>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th style="width: 120px;" class="ps-4">Ảnh</th>
                        <th>Đường dẫn Ảnh/URL</th>
                        <th>Link</th>
                        <th style="width: 100px;" class="text-center">Thứ tự</th>
                        <th style="width: 120px;" class="text-center">Trạng thái</th>
                        <th style="width: 120px;" class="text-center pe-4">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty banners}">
                            <tr>
                                <td colspan="6" class="text-center py-5">
                                    <div class="py-4">
                                        <i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
                                        <h5 class="text-muted mb-2">Không có banner nào</h5>
                                        <p class="text-muted small mb-0">Bắt đầu bằng cách thêm banner mới</p>
                                    </div>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${banners}" var="b">
                                <tr class="border-bottom">
                                    <td class="ps-4">
                                        <c:choose>
                                            <c:when test="${fn:startsWith(b.imageUrl, 'http')}">
                                                <img src="${b.imageUrl}" alt="Banner" 
                                                     class="rounded shadow-sm" 
                                                     style="width: 80px; height: 80px; object-fit: cover;">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/uploads/${b.imageUrl}" 
                                                     alt="Banner" 
                                                     class="rounded shadow-sm"
                                                     style="width: 80px; height: 80px; object-fit: cover;">
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <small class="text-muted">${b.imageUrl}</small>
                                    </td>
                                    <td>
                                        <c:if test="${not empty b.linkUrl}">
                                            <a href="${b.linkUrl}" target="_blank" class="text-primary">
                                                <i class="fas fa-external-link-alt" style="margin-right: 5px;"></i>${fn:length(b.linkUrl) > 30 ? fn:substring(b.linkUrl, 0, 30).concat('...') : b.linkUrl}
                                            </a>
                                        </c:if>
                                        <c:if test="${empty b.linkUrl}">
                                            <span class="text-muted">-</span>
                                        </c:if>
                                    </td>
                                    <td class="text-center">${b.sortOrder}</td>
                                    <td class="text-center">
                                        <span class="badge ${b.isActive() ? 'bg-success' : 'bg-secondary'} text-white px-3 py-2">
                                            <i class="fas ${b.isActive() ? 'fa-check-circle' : 'fa-times-circle'}" style="margin-right: 5px;"></i>
                                            ${b.isActive() ? 'Hiển thị' : 'Ẩn'}
                                        </span>
                                    </td>
                                    <td class="text-center pe-4">
                                        <form action="${pageContext.request.contextPath}/admin/banners" method="POST" 
                                              style="display: inline;" 
                                              onsubmit="return confirm('Xác nhận xóa banner này?')">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${b.id}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
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
