<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-tags text-primary" style="margin-right: 10px;"></i>Thêm danh mục mới
        </h1>
        <p class="text-muted mb-0">Thêm danh mục sản phẩm mới vào hệ thống</p>
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
        <form action="${pageContext.request.contextPath}/admin/category/add"
              method="post" 
              enctype="multipart/form-data"
            id="categoryForm">
          
          <div class="mb-4">
            <label class="form-label fw-semibold mb-2">
              <i class="fas fa-tag text-primary" style="margin-right: 10px;"></i>Tên danh mục <span class="text-danger">*</span>
            </label>
            <input type="text" 
                   class="form-control" 
                   name="name" 
                   placeholder="Ví dụ: Trà sữa, Cà phê, Bánh ngọt..." 
                   required 
                   autofocus
                   style="font-size: 1rem; padding: 0.75rem;"/>
            <div class="form-text mt-2">
              <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Tên danh mục sẽ hiển thị trên trang chủ và menu
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
                  <img id="imagePreview" 
                       src="https://via.placeholder.com/200x200?text=Chưa+có+ảnh"
                       class="img-fluid rounded border" 
                       style="max-width: 100%; max-height: 200px; object-fit: cover;"
                       alt="Preview"/>
                </div>
              </div>
              <div class="col-md-7">
                <input type="file" 
                       class="form-control mb-2" 
                       name="icon" 
                       accept="image/*"
                       id="iconInput"/>
                <div class="form-text">Định dạng: JPG, PNG. Kích thước đề xuất: 200x200px</div>
              </div>
            </div>
          </div>
          
          <hr class="my-4">
          
          <div class="d-flex gap-3">
            <button type="submit" class="btn btn-primary">
              <i class="fas fa-save" style="margin-right: 10px;"></i>Lưu danh mục
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
          <i class="fas fa-lightbulb" style="margin-right: 10px;"></i>Gợi ý
        </h6>
      </div>
      <div class="card-body">
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