<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<h1 class="h3 mb-4 text-gray-800">${empty p.product_id ? 'Thêm' : 'Sửa'} sản phẩm</h1>

<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show">
        ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success alert-dismissible fade show">
        ${sessionScope.success}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
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
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-info-circle"></i> Thông tin cơ bản
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
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-image"></i> Hình ảnh sản phẩm
                    </h6>
                </div>
                <div class="card-body">
                    <div class="row">
                        <!-- Preview ảnh -->
                        <div class="col-md-5 mb-3">
                            <label class="form-label">Ảnh hiện tại</label>
                            <div class="border rounded p-3 bg-light text-center">
                                <c:set var="thumbnailSrc" value="${p.thumbnail}"/>
                                <c:if test="${not empty thumbnailSrc and not fn:startsWith(thumbnailSrc, 'http')}">
                                    <c:set var="thumbnailSrc" value="${pageContext.request.contextPath}/uploads/${p.thumbnail}"/>
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
        
        <!-- Cột phải: Giá, tồn kho, cài đặt -->
        <div class="col-lg-4">
            <!-- Giá & Tồn kho -->
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-dollar-sign"></i> Giá & Tồn kho
                    </h6>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label class="form-label">Giá bán <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <input class="form-control" name="price" type="number" step="100" min="0" 
                                   value="${p.price}" placeholder="0" required/>
                            <span class="input-group-text">VNĐ</span>
                        </div>
                        <div class="form-text">Giá bán của sản phẩm</div>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label">Giảm giá</label>
                        <div class="input-group">
                            <input class="form-control" name="discount" type="number" step="1" min="0" max="100" 
                                   value="${p.discount}" placeholder="0"/>
                            <span class="input-group-text">%</span>
                        </div>
                        <div class="form-text">Phần trăm giảm giá (0-100%)</div>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label">Số lượng tồn kho</label>
                        <input class="form-control" name="stock" type="number" step="1" min="0" 
                               value="${p.stock}" placeholder="0"/>
                        <div class="form-text">Số lượng sản phẩm còn trong kho</div>
                    </div>
                </div>
            </div>
            
            <!-- Danh mục & Nhà cung cấp -->
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">
                        <i class="fas fa-tags"></i> Phân loại
                    </h6>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label class="form-label">Danh mục <span class="text-danger">*</span></label>
                        <select class="form-select" name="cate_id" required>
                            <option value="">-- Chọn danh mục --</option>
                            <c:forEach var="c" items="${categories}">
                                <option value="${c.id}" 
                                        ${p.category != null && p.category.id == c.id ? 'selected' : ''}>
                                    ${c.name}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label">Nhà cung cấp</label>
                        <select class="form-select" name="supplier_id">
                            <option value="">-- Chọn nhà cung cấp --</option>
                            <c:forEach var="s" items="${suppliers}">
                                <option value="${s.supplier_id}" 
                                        ${p.supplier != null && p.supplier.supplier_id == s.supplier_id ? 'selected' : ''}>
                                    ${s.supplier_name}
                                </option>
                            </c:forEach>
                        </select>
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
    <div class="card shadow mb-4">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <button class="btn btn-primary" type="submit">
                        <i class="fas fa-save"></i> Lưu sản phẩm
                    </button>
                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/products">
                        <i class="fas fa-times"></i> Hủy bỏ
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
