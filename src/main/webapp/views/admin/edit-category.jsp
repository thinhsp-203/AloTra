<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <h1 class="h4 mb-0">
    <i class="bi bi-pencil text-primary"></i> Chỉnh sửa danh mục
  </h1>
  <a href="${pageContext.request.contextPath}/admin/category/list" class="btn btn-outline-secondary">
    <i class="bi bi-arrow-left"></i> Quay lại
  </a>
</div>

<div class="row">
  <div class="col-md-8">
    <div class="card">
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
                  <img 
src="${pageContext.request.contextPath}/uploads/${category.icon}" 
                       class="rounded border" 
                       style="max-width: 200px;
max-height: 200px; object-fit: cover;"
                       alt="${category.name}"
                       id="currentImage"/>
                </c:when>
                <c:otherwise>
                  <div 
class="bg-secondary text-white rounded d-flex align-items-center justify-content-center"
                       style="width: 200px;
height: 200px;"
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
              <i class="bi bi-save"></i> Cập nhật
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
  
  <div class="col-md-4">
    <div class="card">
      <div class="card-header bg-light">
        <h6 class="card-title mb-0">
          <i class="bi bi-info-circle text-primary"></i> Thông tin danh mục
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
    
    <div class="card bg-light mt-3">
      <div class="card-body">
   
     <h6 class="card-title">
          <i class="bi bi-lightbulb text-warning"></i> Lưu ý
        </h6>
        <ul class="small mb-0 ps-3">
          <li class="mb-2">Thay đổi tên danh mục không ảnh hưởng đến sản phẩm</li>
          <li class="mb-2">Ảnh cũ sẽ được thay thế nếu chọn ảnh mới</li>
          <li>Để trống ô "Chọn ảnh mới" nếu giữ nguyên 
ảnh hiện tại</li>
        </ul>
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