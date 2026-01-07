<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-info-circle text-primary" style="margin-right: 10px;"></i>
            <c:choose>
                <c:when test="${not empty about.id}">Chỉnh sửa Bài viết</c:when>
                <c:otherwise>Thêm Bài viết Mới</c:otherwise>
            </c:choose>
        </h1>
        <p class="text-muted mb-0">${not empty about.id ? 'Cập nhật thông tin bài viết' : 'Thêm bài viết mới về cửa hàng'}</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/about" class="btn btn-outline-secondary">
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

<form action="${pageContext.request.contextPath}/admin/about/save" method="POST" enctype="multipart/form-data">
    <c:if test="${not empty about.id}">
        <input type="hidden" name="id" value="${about.id}">
    </c:if>
    
    <div class="row">
        <div class="col-lg-8">
            <%-- Article Info Card --%>
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-edit" style="margin-right: 10px;"></i>Thông tin Bài viết
                    </h6>
                </div>
                <div class="card-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tiêu đề <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="title" 
                               value="${about.title}" required 
                               placeholder="Nhập tiêu đề bài viết">
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Nội dung</label>
                        <textarea class="form-control" name="content" rows="15" 
                                  placeholder="Nhập nội dung bài viết">${about.content}</textarea>
                        <div class="form-text">
                            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Có thể sử dụng HTML để định dạng nội dung
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-lg-4">
            <%-- Image Card --%>
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-image" style="margin-right: 10px;"></i>Hình ảnh
                    </h6>
                </div>
                <div class="card-body p-4">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Xem trước</label>
                        <div class="border rounded p-3 bg-light text-center">
                            <c:choose>
                                <c:when test="${not empty about.image and fn:startsWith(about.image, 'http')}">
                                    <img src="${about.image}" 
                                         class="img-fluid rounded border" 
                                         style="max-width: 100%; max-height: 200px; object-fit: cover;"
                                         alt="Preview"
                                         id="imagePreview"/>
                                </c:when>
                                <c:when test="${not empty about.image}">
                                    <img src="${pageContext.request.contextPath}/uploads/${about.image}" 
                                         class="img-fluid rounded border" 
                                         style="max-width: 100%; max-height: 200px; object-fit: cover;"
                                         alt="Preview"
                                         id="imagePreview"
                                         onerror="this.src='https://via.placeholder.com/200x200?text=Chưa+có+ảnh'"/>
                                </c:when>
                                <c:otherwise>
                                    <img src="https://via.placeholder.com/200x200?text=Chưa+có+ảnh"
                                         class="img-fluid rounded border" 
                                         style="max-width: 100%; max-height: 200px; object-fit: cover;"
                                         alt="Preview"
                                         id="imagePreview"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Upload ảnh mới <span class="badge bg-primary">Ưu tiên</span></label>
                        <input class="form-control mb-2" type="file" name="imageFile" id="imageFile" 
                               accept="image/*">
                        <div class="form-text">
                            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Chọn file ảnh từ máy tính (JPG, PNG, GIF)
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Hoặc dán URL ảnh</label>
                        <input class="form-control" name="imageUrl" 
                               value="${fn:startsWith(about.image, 'http') ? about.image : ''}" 
                               placeholder="https://example.com/image.jpg">
                        <div class="form-text">
                            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Nhập URL ảnh nếu không upload file
                        </div>
                    </div>
                </div>
            </div>
            
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
                               value="${about.sortOrder != null ? about.sortOrder : 0}" min="0">
                        <div class="form-text">
                            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Số nhỏ hơn sẽ hiển thị trước
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" name="isActive" 
                                   id="isActive" value="true" 
                                   ${about.isActive != null && about.isActive ? 'checked' : ''}>
                            <label class="form-check-label fw-semibold" for="isActive">
                                Hiển thị bài viết
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
                            <i class="fas fa-save" style="margin-right: 10px;"></i>Lưu bài viết
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/about" class="btn btn-outline-secondary">
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
document.getElementById('imageFile')?.addEventListener('change', function(e) {
    const file = e.target.files[0];
    const preview = document.getElementById('imagePreview');
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
        const preview = document.getElementById('imagePreview');
        if (url && preview && url.startsWith('http')) {
            preview.src = url;
        }
    });
}
</script>
