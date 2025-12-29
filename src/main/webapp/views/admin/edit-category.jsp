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
            <label class="form-label">Ảnh hiện tại</label>
            <div class="mb-2">
              <c:choose>
                <c:when test="${not empty category.icon}">
                  <img src="${pageContext.request.contextPath}/uploads/${category.icon}" 
                       class="rounded border" 
                       style="max-width: 200px; max-height: 200px; object-fit: cover;"
                       alt="${category.name}"
                       id="currentImage"/>
                </c:when>
                <c:otherwise>
                  <div class="bg-secondary text-white rounded d-flex align-items-center justify-content-center"
                       style="width: 200px; height: 200px;"
                       id="currentImage">
                    <i class="bi bi-image fs-1"></i>
                  </div>
                </c:otherwise>
              </c:choose>
     
       </div>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Chọn ảnh mới (để trống nếu không đổi)</label>
            <input type="file" 
                   class="form-control" 
                   name="icon" 
                   accept="image/*"
                   id="iconInput"/>
            <div class="form-text">Chỉ chọn file nếu muốn thay đổi ảnh</div>
          </div>
          
          
          <div class="mb-3" id="previewContainer" style="display: none;">
            <label class="form-label">Xem trước ảnh mới</label>
            <div>
              <img id="imagePreview" 
                   class="rounded border" 
                   style="max-width: 200px; max-height: 200px; object-fit: cover;"
                   alt="Preview"/>
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
// Preview ảnh khi chọn file
document.getElementById('iconInput').addEventListener('change', function(e) {
  const file = e.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = function(e) {
      document.getElementById('imagePreview').src = e.target.result;
      document.getElementById('previewContainer').style.display = 'block';
    }
    reader.readAsDataURL(file);
} else {
    document.getElementById('previewContainer').style.display = 'none';
  }
});
</script>