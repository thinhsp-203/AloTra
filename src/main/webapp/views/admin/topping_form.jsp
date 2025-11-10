<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h1 class="h3 mb-4 text-gray-800">${empty item.topping_id ? 'Tạo mới' : 'Chỉnh sửa'} Topping</h1>

<div class="card shadow mb-4">
  <div class="card-header py-3">
    <h6 class="m-0 font-weight-bold text-primary">Thông tin Topping</h6>
  </div>
  <div class="card-body">
    <form method="post" action="${pageContext.request.contextPath}/admin/toppings/save">
      <input type="hidden" name="id" value="${item.topping_id}">
      
      <div class="row g-3">
        <div class="col-md-8">
          <label class="form-label">Tên Topping <span class="text-danger">*</span></label>
          <input type="text" class="form-control" name="topping_name" value="${item.topping_name}" required>
          <div class="form-text">Ví dụ: Trân châu đen, Pudding trứng, Thạch nha đam...</div>
        </div>
        
        <div class="col-md-4">
          <label class="form-label">Giá (Phụ phí) <span class="text-danger">*</span></label>
          <input type="number" class="form-control" name="price" value="${item.price}" step="100" required>
          <div class="form-text">Ví dụ: 5000, 7000...</div>
        </div>
        
        <div class="col-12">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" name="isAvailable" 
                   id="isAvailable" ${item.isAvailable || empty item.topping_id ? 'checked' : ''}>
            <label class="form-check-label" for="isAvailable">
                Kích hoạt (Cho phép bán)
            </label>
          </div>
        </div>
        
        <div class="col-12">
          <hr>
          <button type="submit" class="btn btn-primary">
            <i class="fas fa-save fa-sm"></i> Lưu Topping
          </button>
          <a href="${pageContext.request.contextPath}/admin/toppings" class="btn btn-outline-secondary">Hủy</a>
        </div>
      </div>
    </form>
  </div>
</div>