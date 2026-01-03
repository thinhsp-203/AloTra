<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-images text-primary" style="margin-right: 10px;"></i>
            <c:choose>
                <c:when test="${not empty banner.id}">Chỉnh sửa Banner</c:when>
                <c:otherwise>Thêm Banner Mới</c:otherwise>
            </c:choose>
        </h1>
        <p class="text-muted mb-0">${not empty banner.id ? 'Cập nhật thông tin banner' : 'Thêm banner mới cho website'}</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/banners" class="btn btn-outline-secondary">
        <i class="fas fa-arrow-left" style="margin-right: 10px;"></i>Quay lại
    </a>
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

<form action="${pageContext.request.contextPath}/admin/banners/save" method="POST" enctype="multipart/form-data">
    <c:if test="${not empty banner.id}">
        <input type="hidden" name="id" value="${banner.id}">
    </c:if>
    
    <div class="row">
        <div class="col-lg-8">
            <%-- Image Card --%>
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-image" style="margin-right: 10px;"></i>Hình ảnh Banner
                    </h6>
                </div>
                <div class="card-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Xem trước</label>
                        <div class="border rounded p-3 bg-light text-center">
                            <c:choose>
                                <c:when test="${not empty banner.imageUrl and fn:startsWith(banner.imageUrl, 'http')}">
                                    <img src="${banner.imageUrl}" 
                                         class="img-fluid rounded border" 
                                         style="max-width: 100%; max-height: 200px; object-fit: cover;"
                                         alt="Preview"
                                         id="bannerPreview"/>
                                </c:when>
                                <c:when test="${not empty banner.imageUrl}">
                                    <img src="${pageContext.request.contextPath}/uploads/${banner.imageUrl}" 
                                         class="img-fluid rounded border" 
                                         style="max-width: 100%; max-height: 200px; object-fit: cover;"
                                         alt="Preview"
                                         id="bannerPreview"
                                         onerror="this.src='https://via.placeholder.com/200x200?text=Chưa+có+ảnh'"/>
                                </c:when>
                                <c:otherwise>
                                    <img src="https://via.placeholder.com/200x200?text=Chưa+có+ảnh"
                                         class="img-fluid rounded border" 
                                         style="max-width: 100%; max-height: 200px; object-fit: cover;"
                                         alt="Preview"
                                         id="bannerPreview"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Upload ảnh mới <span class="badge bg-primary">Ưu tiên</span></label>
                        <input class="form-control mb-2" type="file" name="bannerFile" id="bannerFile" 
                               accept="image/*">
                        <div class="form-text">
                            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Chọn file ảnh từ máy tính (JPG, PNG, GIF)
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Hoặc dán URL ảnh</label>
                        <input class="form-control" name="imageUrl" 
                               value="${fn:startsWith(banner.imageUrl, 'http') ? banner.imageUrl : ''}" 
                               placeholder="https://example.com/image.jpg">
                        <div class="form-text">
                            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Nhập URL ảnh nếu không upload file
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-lg-4">
            <%-- Settings Card --%>
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-cog" style="margin-right: 10px;"></i>Cài đặt
                    </h6>
                </div>
                <div class="card-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Thứ tự hiển thị</label>
                        <input type="number" class="form-control" name="sortOrder" 
                               value="${banner.sortOrder}" min="0" 
                               ${empty banner.id ? 'readonly' : ''}>
                        <div class="form-text">
                            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>
                            <c:choose>
                                <c:when test="${empty banner.id}">
                                    Thứ tự sẽ tự động tăng (${banner.sortOrder}). Có thể chỉnh sửa sau khi lưu.
                                </c:when>
                                <c:otherwise>
                                    Số nhỏ hơn sẽ hiển thị trước.
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="isActive" 
                                   id="isActive" value="true" 
                                   ${banner.isActive() ? 'checked' : ''}>
                            <label class="form-check-label fw-semibold" for="isActive">
                                Hiển thị banner
                            </label>
                        </div>
                    </div>
                </div>
            </div>
            
            <%-- Actions Card --%>
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-body p-4">
                    <div class="d-flex gap-3">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save" style="margin-right: 10px;"></i>${not empty banner.id ? 'Cập nhật' : 'Thêm mới'} & Lưu
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/banners" class="btn btn-outline-secondary">
                            <i class="fas fa-times" style="margin-right: 10px;"></i>Hủy bỏ
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</form>

<script>
// Script preview ảnh khi chọn file
document.getElementById('bannerFile')?.addEventListener('change', function(e) {
    const file = e.target.files[0];
    const preview = document.getElementById('bannerPreview');
    if (file && preview) {
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
});

// Xử lý khi thay đổi URL ảnh
const imageUrlInput = document.querySelector('input[name="imageUrl"]');
if (imageUrlInput) {
    imageUrlInput.addEventListener('blur', function() {
        const url = this.value.trim();
        const preview = document.getElementById('bannerPreview');
        if (url && preview && url.startsWith('http')) {
            preview.src = url;
        }
    });
}
</script>
