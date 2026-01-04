<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-cube text-primary" style="margin-right: 10px;"></i>${empty item.topping_id ? 'Tạo mới' : 'Chỉnh sửa'} Topping
        </h1>
        <p class="text-muted mb-0">${empty item.topping_id ? 'Thêm topping mới vào hệ thống' : 'Chỉnh sửa thông tin topping'}</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/toppings" class="btn btn-outline-secondary">
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

<div class="card shadow-sm border-0 mb-4">
  <div class="card-header bg-white border-bottom py-3">
    <h6 class="m-0 font-weight-bold text-primary">
      <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Thông tin Topping
    </h6>
  </div>
  <div class="card-body">
    <form method="post" action="${pageContext.request.contextPath}/admin/toppings/save">
      <input type="hidden" name="id" value="${item.topping_id}">
      
      <div class="row g-3">
        <div class="col-md-8">
          <label class="form-label fw-semibold mb-2">
            <i class="fas fa-tag text-primary" style="margin-right: 10px;"></i>Tên Topping <span class="text-danger">*</span>
          </label>
          <input type="text" 
                 class="form-control" 
                 name="topping_name" 
                 value="${item.topping_name}" 
                 required
                 placeholder="Nhập tên topping"
                 style="font-size: 1rem; padding: 0.75rem;"/>
          <div class="form-text mt-2">
            <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Ví dụ: Trân châu đen, Pudding trứng, Thạch nha đam...
          </div>
        </div>
        
        <div class="col-md-4">
          <label class="form-label fw-semibold mb-2">
            <i class="fas fa-money-bill-wave text-success" style="margin-right: 10px;"></i>Giá (Phụ phí) <span class="text-danger">*</span>
          </label>
          <div class="input-group" style="width: 100%;">
            <input type="number" 
                   class="form-control" 
                   name="price" 
                   value="${item.price}" 
                   step="100" 
                   required
                   placeholder="Nhập giá"
                   style="font-size: 1rem; padding: 0.75rem; flex: 1 1 auto; min-width: 0;"/>
            <span class="input-group-text bg-light border-start-0">VNĐ</span>
          </div>
          <div class="form-text mt-2">
            <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Ví dụ: 5000, 7000, 10000...
          </div>
        </div>
        
        <div class="col-12">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" name="isAvailable" 
                   id="isAvailable" ${item.isAvailable || empty item.topping_id ? 'checked' : ''}>
            <label class="form-check-label fw-semibold" for="isAvailable">
              Kích hoạt (Cho phép bán)
            </label>
            <div class="form-text mt-1">
              <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>Bỏ chọn nếu tạm thời ngừng bán topping này
            </div>
          </div>
        </div>
        
        <div class="col-12">
          <hr class="my-4">
          <div class="d-flex gap-3">
            <button type="submit" class="btn btn-primary">
              <i class="fas fa-save" style="margin-right: 10px;"></i>Lưu Topping
            </button>
            <a href="${pageContext.request.contextPath}/admin/toppings" class="btn btn-outline-secondary">
              <i class="fas fa-times" style="margin-right: 10px;"></i>Hủy bỏ
            </a>
          </div>
        </div>
      </div>
    </form>
  </div>
</div>