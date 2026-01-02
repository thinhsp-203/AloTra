<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="container-fluid">
    <h1 class="h3 mb-4 text-gray-800">
        <c:choose>
            <c:when test="${not empty about.id}">Chỉnh sửa Bài viết</c:when>
            <c:otherwise>Thêm Bài viết Mới</c:otherwise>
        </c:choose>
    </h1>

    <%-- Thông báo --%>
    <c:if test="${not empty sessionScope.success}">
      <div class="alert alert-success alert-dismissible fade show">
          ${sessionScope.success}
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
      <c:remove var="success" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
      <div class="alert alert-danger alert-dismissible fade show">
           ${sessionScope.error}
          <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
      <c:remove var="error" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/about/save" method="POST" enctype="multipart/form-data">
        <c:if test="${not empty about.id}">
            <input type="hidden" name="id" value="${about.id}">
        </c:if>
        
        <div class="row">
            <div class="col-lg-8">
                <!-- Thông tin cơ bản -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-edit"></i> Thông tin Bài viết
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <label class="form-label">Tiêu đề <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" name="title" 
                                   value="${about.title}" required 
                                   placeholder="Nhập tiêu đề bài viết">
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Nội dung</label>
                            <textarea class="form-control" name="content" rows="15" 
                                      placeholder="Nhập nội dung bài viết">${about.content}</textarea>
                            <div class="form-text">Có thể sử dụng HTML để định dạng nội dung</div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-4">
                <!-- Hình ảnh -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-image"></i> Hình ảnh
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-12 mb-3">
                                <label class="form-label">Xem trước</label>
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
                            <div class="col-12">
                                <label class="form-label">Upload ảnh mới <span class="badge bg-primary">Ưu tiên</span></label>
                                <input class="form-control mb-2" type="file" name="imageFile" id="imageFile" 
                                       accept="image/*">
                                <div class="form-text">Chọn file ảnh từ máy tính (JPG, PNG, GIF)</div>
                            </div>
                            <div class="col-12 mt-3">
                                <label class="form-label">Hoặc dán URL ảnh</label>
                                <input class="form-control" name="imageUrl" 
                                       value="${fn:startsWith(about.image, 'http') ? about.image : ''}" 
                                       placeholder="https://example.com/image.jpg">
                                <div class="form-text">Nhập URL ảnh nếu không upload file</div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Cài đặt -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">
                            <i class="fas fa-cog"></i> Cài đặt
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <label class="form-label">Thứ tự hiển thị</label>
                            <input type="number" class="form-control" name="sortOrder" 
                                   value="${about.sortOrder != null ? about.sortOrder : 0}" min="0">
                            <div class="form-text">Số nhỏ hơn sẽ hiển thị trước</div>
                        </div>
                        
                        <div class="mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" name="isActive" 
                                       id="isActive" value="true" 
                                       ${about.isActive != null && about.isActive ? 'checked' : ''}>
                                <label class="form-check-label" for="isActive">
                                    Hiển thị bài viết
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Actions -->
                <div class="card shadow mb-4">
                    <div class="card-body">
                        <button type="submit" class="btn btn-primary w-100 mb-2">
                            <i class="fas fa-save"></i> Lưu bài viết
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/about" class="btn btn-outline-secondary w-100">
                            <i class="fas fa-times"></i> Hủy bỏ
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

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

