<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-tags text-primary" style="margin-right: 10px;"></i>Chỉnh sửa danh mục
        </h1>
        <p class="text-muted mb-0">Chỉnh sửa thông tin danh mục sản phẩm</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/category/list" class="btn btn-outline-secondary">
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

<div class="row">
  <div class="col-lg-8">
    <div class="card shadow-sm border-0 mb-4">
       <div class="card-header bg-white border-bottom py-3">
        <h6 class="m-0 font-weight-bold text-primary">
          <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Thông tin danh mục
        </h6>
      </div>
      <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/category/edit" 
              method="post" 
              enctype="multipart/form-data">
 
         <input type="hidden" name="id" value="${category.id}"/>
          
          <div class="mb-4">
            <label class="form-label fw-semibold mb-2">
              <i class="fas fa-tag text-primary" style="margin-right: 10px;"></i>Tên danh mục <span class="text-danger">*</span>
            </label>
            <input type="text" 
                   class="form-control" 
                   name="name" 
                   value="${category.name}" 
                   required 
                   autofocus
                   style="font-size: 1rem; padding: 0.75rem;"/>
            <div class="form-text mt-2">
              <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Tên danh mục sẽ hiển thị trên trang chủ và menu
            </div>
          </div>

          <div class="mb-4">
            <label class="form-label fw-semibold mb-2">
              <i class="fas fa-list text-primary" style="margin-right: 10px;"></i>Loại danh mục <span class="text-danger">*</span>
            </label>
            <div class="form-check mb-2">
              <input class="form-check-input" 
                     type="radio" 
                     name="isDrink" 
                     id="isDrink_true" 
                     value="true" 
                     ${category.isDrink ? 'checked' : ''}
                     required>
              <label class="form-check-label" for="isDrink_true">
                <i class="bi bi-cup-straw text-primary"></i> Thức uống
              </label>
              <div class="form-text ms-4">Sản phẩm thuộc danh mục này sẽ có size (S/M/L)</div>
            </div>
            <div class="form-check">
              <input class="form-check-input" 
                     type="radio" 
                     name="isDrink" 
                     id="isDrink_false" 
                     value="false"
                     ${not category.isDrink ? 'checked' : ''}
                     required>
              <label class="form-check-label" for="isDrink_false">
                <i class="bi bi-cake2 text-warning"></i> Bánh & Đồ ăn vặt
              </label>
              <div class="form-text ms-4">Sản phẩm thuộc danh mục này không có size</div>
            </div>
          </div>
 
          <div class="mb-4">
            <label class="form-label fw-semibold mb-2">
              <i class="fas fa-image text-primary" style="margin-right: 10px;"></i>Ảnh đại diện
            </label>
            <div class="row">
              <div class="col-md-5 mb-3">
                <label class="form-label">Xem trước</label>
                <div class="border rounded p-3 bg-light text-center">
                  <c:choose>
                    <c:when test="${not empty category.icon}">
                      <c:choose>
                          <c:when test="${fn:startsWith(category.icon, 'http')}">
                              <img src="${category.icon}" 
                                   class="img-fluid rounded border" 
                                   style="max-width: 100%; max-height: 200px; object-fit: cover;"
                                   alt="${category.name}"
                                   id="imagePreview"/>
                          </c:when>
                          <c:otherwise>
                              <img src="${pageContext.request.contextPath}/${category.icon}" 
                                   class="img-fluid rounded border" 
                                   style="max-width: 100%; max-height: 200px; object-fit: cover;"
                                   alt="${category.name}"
                                   id="imagePreview"
                                   onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/uploads/categories/default.png';"/>
                          </c:otherwise>
                      </c:choose>
                    </c:when>
                    <c:otherwise>
                      <img src="${pageContext.request.contextPath}/uploads/categories/default.png" 
                           class="img-fluid rounded border" 
                           style="max-width: 100%; max-height: 200px; object-fit: cover;"
                           alt="${category.name}"
                           id="imagePreview"/>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
              <div class="col-md-7">
                <label class="form-label">Chọn ảnh mới (để trống nếu không đổi)</label>
                <input type="file" 
                       class="form-control mb-2" 
                       name="icon" 
                       accept="image/*"
                       id="iconInput"/>
                <div class="form-text mt-2">
                  <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Chỉ chọn file nếu muốn thay đổi ảnh
                </div>
              </div>
            </div>
          </div>
          
          <hr class="my-4">
          
          <div class="d-flex gap-3">
            <button type="submit" class="btn btn-primary">
              <i class="fas fa-save" style="margin-right: 10px;"></i>Cập nhật
            </button>
            <a href="${pageContext.request.contextPath}/admin/category/list" 
               class="btn btn-outline-secondary">
              <i class="fas fa-times" style="margin-right: 10px;"></i>Hủy bỏ
            </a>
          </div>
        </form>
      </div>
    </div>
  </div>
  
  <div class="col-lg-4">
    <div class="card shadow-sm border-0 mb-4">
      <div class="card-header bg-white border-bottom py-3">
        <h6 class="m-0 font-weight-bold text-primary">
          <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Thông tin
        </h6>
      </div>
      <div class="card-body">
        <div class="mb-2">
          <small class="text-muted">ID danh mục</small>
          <div class="fw-bold">${category.id}</div>
        </div>
        <div class="mb-2">
          <small class="text-muted">Tên hiện tại</small>
          <div class="fw-bold">${category.name}</div>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
// Script preview ảnh khi chọn file
document.getElementById('iconInput')?.addEventListener('change', function(e) {
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
</script>