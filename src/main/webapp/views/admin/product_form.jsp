<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-box text-primary" style="margin-right: 10px;"></i>${empty p.product_id ? 'Thêm' : 'Sửa'} sản phẩm
        </h1>
        <p class="text-muted mb-0">${empty p.product_id ? 'Thêm sản phẩm mới vào hệ thống' : 'Chỉnh sửa thông tin sản phẩm'}</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-secondary">
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

<form method="post" action="${pageContext.request.contextPath}/admin/products/save" 
      enctype="multipart/form-data" id="productForm">
    
    <input type="hidden" name="id" value="${p.product_id}"/>
    
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
                        <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
                        <input class="form-control" name="product_name" value="${p.product_name}" 
                               placeholder="Nhập tên sản phẩm" required/>
                        <div class="form-text">Tên sản phẩm sẽ hiển thị trên website</div>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label">Mô tả sản phẩm</label>
                        <textarea class="form-control" name="description" rows="6" 
                                  placeholder="Nhập mô tả chi tiết về sản phẩm...">${p.description}</textarea>
                        <div class="form-text">Mô tả chi tiết giúp khách hàng hiểu rõ hơn về sản phẩm</div>
                    </div>
                </div>
            </div>
            
            <!-- Hình ảnh -->
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-image" style="margin-right: 10px;"></i>Hình ảnh sản phẩm
                    </h6>
                </div>
                <div class="card-body">
                    <div class="row">
                        <!-- Preview ảnh -->
                        <div class="col-md-5 mb-3">
                            <label class="form-label">Ảnh hiện tại</label>
                            <div class="border rounded p-3 bg-light text-center">
                                <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
                                <c:if test="${not empty thumbnailSrc}">
                                    <c:choose>
                                        <c:when test="${fn:startsWith(thumbnailSrc, 'http')}">
                                            <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
                                        </c:when>
                                        <c:when test="${fn:startsWith(thumbnailSrc, 'uploads/')}">
                                            <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/${p.thumbnail}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/uploads/products/${p.thumbnail}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                                
                                <img src="${empty thumbnailSrc ? 'https://via.placeholder.com/300x300?text=Chưa+có+ảnh' : thumbnailSrc}" 
                                     id="imagePreview"
                                     class="img-fluid rounded border" 
                                     style="max-width: 100%; max-height: 300px; object-fit: cover;"
                                     alt="Preview"/>
                            </div>
                        </div>
                        
                        <!-- Upload options -->
                        <div class="col-md-7">
                            <div class="mb-3">
                                <label class="form-label">Upload ảnh mới <span class="badge bg-primary">Ưu tiên</span></label>
                                <input class="form-control" type="file" name="thumbnailFile" id="thumbnailFile" 
                                       accept="image/*">
                                <div class="form-text">Chọn file ảnh từ máy tính (JPG, PNG, GIF). Kích thước đề xuất: 500x500px</div>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Hoặc dán URL ảnh</label>
                                <input class="form-control" name="thumbnailUrl" 
                                       value="${fn:startsWith(p.thumbnail, 'http') ? p.thumbnail : ''}" 
                                       placeholder="https://example.com/image.jpg"/>
                                <div class="form-text">Nhập URL ảnh nếu không upload file (ưu tiên thấp hơn)</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Cột phải: Giá, cài đặt -->
        <div class="col-lg-4">
            <!-- Giá -->
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-dollar-sign" style="margin-right: 10px;"></i>Giá bán
                    </h6>
                </div>
                <div class="card-body">
                    <div class="mb-4">
                        <label class="form-label fw-semibold mb-2">
                            <i class="fas fa-money-bill-wave text-success" style="margin-right: 10px;"></i>Giá bán <span class="text-danger">*</span>
                        </label>
                        <div class="input-group" style="width: 100%;">
                            <input class="form-control" name="price" type="number" step="100" min="0" 
                                   value="${p.price}" placeholder="Nhập giá bán" required
                                   style="font-size: 1rem; padding: 0.75rem; flex: 1 1 auto; min-width: 0;"/>
                            <span class="input-group-text bg-light border-start-0">VNĐ</span>
                        </div>
                        <div class="form-text mt-2">
                            <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Ví dụ: 25000, 50000, 75000...
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label fw-semibold mb-2">
                            <i class="fas fa-percent text-warning" style="margin-right: 10px;"></i>Giảm giá
                        </label>
                        <div class="input-group" style="width: 100%;">
                            <input class="form-control" name="discount" type="number" step="1" min="0" max="100" 
                                   value="${p.discount}" placeholder="Nhập phần trăm giảm giá"
                                   style="font-size: 1rem; padding: 0.75rem; flex: 1 1 auto; min-width: 0;"/>
                            <span class="input-group-text bg-light border-start-0">%</span>
                        </div>
                        <div class="form-text mt-2">
                            <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Phần trăm giảm giá từ 0 đến 100 (ví dụ: 10 = 10%)
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Danh mục & Nhà cung cấp -->
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-tags" style="margin-right: 10px;"></i>Phân loại
                    </h6>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold mb-2" style="font-size: 1.1rem;">
                            <i class="fas fa-tags text-primary" style="margin-right: 10px;"></i>Danh mục <span class="text-danger">*</span>
                        </label>
                        <select class="form-select form-select-lg" name="cate_id" required
                                style="font-size: 1.1rem; padding: 0.75rem 1rem; height: auto;">
                            <option value="">-- Chọn danh mục --</option>
                            <c:forEach var="c" items="${categories}">
                                <option value="${c.id}" 
                                        ${p.category != null && p.category.id == c.id ? 'selected' : ''}
                                        style="font-size: 1.1rem;">
                                    ${c.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
            
            <!-- Cài đặt -->
            <div class="card shadow-sm border-0 mb-4">
                <div class="card-header bg-white border-bottom py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-cog" style="margin-right: 10px;"></i>Cài đặt
                    </h6>
                </div>
                <div class="card-body">
                    <div class="form-check mb-3">
                        <input class="form-check-input" type="checkbox" name="isActive" 
                               id="isActive" ${p.isActive != null && p.isActive ? 'checked' : ''}/>
                        <label class="form-check-label" for="isActive">
                            <strong>Hiển thị trên website</strong>
                        </label>
                        <div class="form-text">Sản phẩm sẽ hiển thị cho khách hàng</div>
                    </div>
                    
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="isFeatured" 
                               id="isFeatured" ${p.isFeatured != null && p.isFeatured ? 'checked' : ''}/>
                        <label class="form-check-label" for="isFeatured">
                            <strong>Sản phẩm nổi bật</strong>
                        </label>
                        <div class="form-text">Sản phẩm sẽ được hiển thị ở trang chủ</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Actions -->
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center gap-2">
                <div class="d-flex gap-3">
                    <button class="btn btn-primary" type="submit">
                        <i class="fas fa-save" style="margin-right: 10px;"></i>Lưu sản phẩm
                    </button>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/products">
                        <i class="fas fa-times" style="margin-right: 10px;"></i>Hủy bỏ
                    </a>
                </div>
                <small class="text-muted">
                    <span class="text-danger">*</span> là các trường bắt buộc
                </small>
            </div>
        </div>
    </div>
</form>

<script>
// Script preview ảnh khi chọn file
document.getElementById('thumbnailFile')?.addEventListener('change', function(e) {
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
const thumbnailUrlInput = document.querySelector('input[name="thumbnailUrl"]');
if (thumbnailUrlInput) {
    thumbnailUrlInput.addEventListener('blur', function() {
        const url = this.value.trim();
        const preview = document.getElementById('imagePreview');
        if (url && preview && url.startsWith('http')) {
            preview.src = url;
        }
    });
}
</script>
