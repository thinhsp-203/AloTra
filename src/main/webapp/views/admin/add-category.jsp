<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="d-flex justify-content-between align-items-center mb-4">
  <h1 class="h4 mb-0">
    <i class="bi bi-plus-circle text-primary"></i> Thêm danh mục mới
  </h1>
  <a href="${pageContext.request.contextPath}/admin/category/list" class="btn btn-outline-secondary">
    <i class="bi bi-arrow-left"></i> Quay lại
  </a>
</div>

<div class="row">
  <div class="col-md-8">
    <div class="card">
      <div class="card-body">
        <form action="${pageContext.request.contextPath}/admin/category/add"
              method="post" 
              enctype="multipart/form-data"
              id="categoryForm">
          
          <div class="mb-3">
            <label class="form-label">
              Tên danh mục <span class="text-danger">*</span>
            </label>
            <input type="text" 
                   class="form-control" 
                   name="name" 
                   placeholder="Ví dụ: Trà sữa, Cà phê, Bánh ngọt..." 
                   required 
                   autofocus/>
            <div class="form-text">Tên danh mục sẽ hiển thị trên trang chủ và menu</div>
          </div>
          
          <div class="mb-3">
            <label class="form-label">Ảnh đại diện</label>
            <input type="file" 
                   class="form-control" 
                   name="icon" 
                   accept="image/*"
                   id="iconInput"/>
            <div class="form-text">Định dạng: JPG, PNG. Kích thước đề xuất: 200x200px</div>
          </div>
          
          <!-- Preview ảnh -->
          <div class="mb-3" id="previewContainer" style="display: none;">
            <label class="form-label">Xem trước</label>
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
              <i class="bi bi-save"></i> Lưu danh mục
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
    <div class="card bg-light">
      <div class="card-body">
        <h6 class="card-title">
          <i class="bi bi-lightbulb text-warning"></i> Gợi ý
        </h6>
        <ul class="small mb-0 ps-3">
          <li class="mb-2">Đặt tên danh mục ngắn gọn, dễ hiểu</li>
          <li class="mb-2">Sử dụng ảnh có nền trắng hoặc trong suốt</li>
          <li class="mb-2">Ảnh vuông (1:1) sẽ hiển thị đẹp nhất</li>
          <li>Tránh đặt tên trùng với danh mục đã có</li>
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