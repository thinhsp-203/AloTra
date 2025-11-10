<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h1 class="h3 mb-4 text-gray-800">${empty v.voucher_id ? 'Tạo mới' : 'Chỉnh sửa'} Voucher</h1>

<div class="card shadow mb-4">
  <div class="card-header py-3">
    <h6 class="m-0 font-weight-bold text-primary">Thông tin Voucher</h6>
  </div>
  <div class="card-body">
    <form method="post" action="${pageContext.request.contextPath}/admin/vouchers/save">
      <input type="hidden" name="id" value="${v.voucher_id}">
      
      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">Mã Code <span class="text-danger">*</span></label>
          <input type="text" class="form-control text-uppercase" name="code" value="${v.code}" required>
          <div class="form-text">Khách hàng sẽ nhập mã này. (Ví dụ: ALOTRA10K)</div>
        </div>
        
        <div class="col-md-3">
          <label class="form-label">Loại giảm giá <span class="text-danger">*</span></label>
          <select class="form-select" name="discount_type" required>
            <option value="PERCENT" ${v.discount_type eq 'PERCENT' ? 'selected' : ''}>Giảm theo %</option>
            <option value="AMOUNT" ${v.discount_type eq 'AMOUNT' ? 'selected' : ''}>Giảm số tiền</option>
          </select>
        </div>
        
        <div class="col-md-3">
          <label class="form-label">Giá trị <span class="text-danger">*</span></label>
          <input type="number" class="form-control" name="discount_value" value="${v.discount_value}" step="0.01" required>
          <div class="form-text">Nếu là %, nhập 10. Nếu là tiền, nhập 10000.</div>
        </div>
        
        <div class="col-md-6">
          <label class="form-label">Giá trị đơn hàng TỐI THIỂU</label>
          <input type="number" class="form-control" name="min_order_value" value="${v.min_order_value}" step="1000">
          <div class="form-text">Bỏ trống nếu không yêu cầu.</div>
        </div>

        <div class="col-md-6">
          <label class="form-label">Giảm giá TỐI ĐA</label>
          <input type="number" class="form-control" name="max_discount" value="${v.max_discount}" step="1000">
          <div class="form-text">Hữu ích khi giảm theo %. Bỏ trống nếu không giới hạn.</div>
        </div>
        
        <div class="col-md-6">
          <label class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
          <input type="datetime-local" class="form-control" name="start_date" value="${v.start_date}" required>
        </div>
        
        <div class="col-md-6">
          <label class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
          <input type="datetime-local" class="form-control" name="end_date" value="${v.end_date}" required>
        </div>
        
        <div class="col-md-6">
          <label class="form-label">Mô tả</label>
          <textarea class="form-control" name="description" rows="2">${v.description}</textarea>
        </div>
        
        <div class="col-md-6">
          <label class="form-label">Giới hạn lượt dùng</label>
          <input type="number" class="form-control" name="usage_limit" value="${v.usage_limit}" step="1">
          <div class="form-text">Bỏ trống nếu không giới hạn.</div>
        </div>
        
        <div class="col-12">
          <div class="form-check">
            <input class="form-check-input" type="checkbox" name="isActive" 
                   id="isActive" ${v.isActive || empty v.voucher_id ? 'checked' : ''}>
            <label class="form-check-label" for="isActive">
                Kích hoạt (Cho phép sử dụng)
            </label>
          </div>
        </div>
        
        <div class="col-12">
          <hr>
          <button type="submit" class="btn btn-primary">
            <i class="fas fa-save fa-sm"></i> Lưu Voucher
          </button>
          <a href="${pageContext.request.contextPath}/admin/vouchers" class="btn btn-outline-secondary">Hủy</a>
        </div>
      </div>
    </form>
  </div>
</div>