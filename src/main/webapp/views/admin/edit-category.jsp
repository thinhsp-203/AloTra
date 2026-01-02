<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h1 class="h3 mb-4 text-gray-800">Chỉnh sửa danh mục</h1>

<div class="row">
  <div class="col-lg-8">
    <div class="card shadow mb-4">
       <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Thông tin danh mục</h6>
      </div>
      <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/category/edit" 
              method="post" 
              enctype="multipart/form-data">
 
         <input type="hidden" name="id" value="${category.id}"/>
          
          <div class="mb-3">
            <label class="form-label">
              Tên danh mục <span class="text-danger">*</span>
            </label>
            <input type="text" 
          
         class="form-control" 
                   name="name" 
                   value="${category.name}" 
                   required 
                   autofocus/>
          </div>
 
          <div class="mb-3">
            <label class="form-label">Ảnh đại diện</label>
            <div class="row">
              <div class="col-md-5 mb-3">
                <label class="form-label">Xem trước</label>
                <div class="border rounded p-3 bg-light text-center">
                  <c:choose>
                    <c:when test="${not empty category.icon}">
                      <img src="${pageContext.request.contextPath}/uploads/categories/${category.icon}" 
                           class="img-fluid rounded border" 
                           style="max-width: 100%; max-height: 200px; object-fit: cover;"
                           alt="${category.name}"
                           id="imagePreview"
                           onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/uploads/categories/default.png';"/>
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
                <div class="form-text">Chỉ chọn file nếu muốn thay đổi ảnh</div>
              </div>
            </div>
          </div>
          
          <hr>
          
          <div class="d-flex gap-2">
            <button type="submit" class="btn btn-primary">
              <i class="fas fa-save fa-sm"></i> Cập nhật
            </button>
            <a href="${pageContext.request.contextPath}/admin/category/list" 
               class="btn btn-outline-secondary">
              Hủy
            </a>
          </div>
        </form>
      </div>
    </div>
  </div>
  
  <div class="col-lg-4">
    <div class="card shadow-sm mb-4">
      <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">
          <i class="fas fa-info-circle fa-sm"></i> Thông tin
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