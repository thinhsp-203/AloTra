<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-ticket-alt text-primary" style="margin-right: 10px;"></i>${empty v.voucher_id ? 'Tạo mới' : 'Chỉnh sửa'} Voucher
        </h1>
        <p class="text-muted mb-0">${empty v.voucher_id ? 'Thêm mã giảm giá mới cho cửa hàng' : 'Cập nhật thông tin mã giảm giá'}</p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/vouchers" class="btn btn-outline-secondary">
        <i class="fas fa-arrow-left" style="margin-right: 10px;"></i>Quay lại
    </a>
</div>

<%-- Alert Messages --%>
<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-check-circle" style="margin-right: 10px;"></i><strong>Thành công!</strong> ${sessionScope.success}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="success" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-exclamation-circle" style="margin-right: 10px;"></i><strong>Lỗi!</strong> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<%-- Voucher Form Card --%>
<div class="card shadow-sm border-0 mb-4">
    <div class="card-header bg-white border-bottom py-3">
        <h6 class="m-0 font-weight-bold text-primary">
            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Thông tin Voucher
        </h6>
    </div>
    <div class="card-body p-4">
        <form method="post" action="${pageContext.request.contextPath}/admin/vouchers/save">
            <input type="hidden" name="id" value="${v.voucher_id}">
            
            <div class="row g-3">
                <%-- Mã Code --%>
                <div class="col-md-6">
                    <label class="form-label fw-semibold mb-2">
                        <i class="fas fa-barcode text-primary" style="margin-right: 10px;"></i>Mã Code <span class="text-danger">*</span>
                    </label>
                    <input type="text" class="form-control" name="code" 
                           value="${v.code}" 
                           placeholder="VD: ALOTRA10K"
                           style="text-transform: uppercase;"
                           required>
                    <div class="form-text">
                        <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Mã mà khách hàng sẽ nhập khi thanh toán
                    </div>
                </div>
                
                <%-- Loại giảm giá và Giá trị --%>
                <div class="col-md-4">
                    <label class="form-label fw-semibold mb-3">
                        Loại giảm giá <span class="text-danger">*</span>
                    </label>
                    <div class="d-flex gap-4">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="discount_type" 
                                   id="discount_type_amount" value="AMOUNT"
                                   style="width: 20px; height: 20px; margin-top: 0.3rem;"
                                   ${empty v.discount_type || v.discount_type eq 'AMOUNT' ? 'checked' : ''}>
                            <label class="form-check-label fw-semibold fs-5" for="discount_type_amount" style="margin-right: 40px;">
                                <i class="fas fa-money-bill-wave text-success" style="margin-right: 10px;"></i>Giảm số tiền
                            </label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="discount_type" 
                                   id="discount_type_percent" value="PERCENT"
                                   style="width: 20px; height: 20px; margin-top: 0.3rem;"
                                   ${v.discount_type eq 'PERCENT' ? 'checked' : ''}>
                            <label class="form-check-label fw-semibold fs-5" for="discount_type_percent">
                                <i class="fas fa-percent text-primary" style="margin-right: 10px;"></i>Giảm theo %
                            </label>
                        </div>
                    </div>
                </div>
                
                <div class="col-md-4">
                    <label class="form-label fw-semibold mb-2">
                        <i class="fas fa-money-bill-wave text-success" style="margin-right: 10px;"></i>Giá trị <span class="text-danger">*</span>
                    </label>
                    <input type="number" class="form-control" name="discount_value" 
                           id="discount_value"
                           value="${v.discount_value}" 
                           step="0.01" 
                           min="0"
                           required>
                    <div class="form-text" id="discount_value_help">
                        <c:choose>
                            <c:when test="${v.discount_type eq 'PERCENT'}">
                                <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Nhập % (0-100, VD: 10 = 10%)
                            </c:when>
                            <c:otherwise>
                                <i class="fas fa-info-circle" style="margin-right: 10px;"></i>Nhập số tiền (VD: 10000 = 10.000₫)
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="invalid-feedback" id="discount_value_error"></div>
                </div>
                
                <%-- Điều kiện áp dụng --%>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">
                        <i class="fas fa-shopping-cart" style="margin-right: 10px;"></i>Đơn hàng tối thiểu
                    </label>
                    <input type="number" class="form-control" name="min_order_value" 
                           value="${v.min_order_value}" 
                           step="1000"
                           min="0"
                           placeholder="Không yêu cầu">
                    <div class="form-text">Bỏ trống nếu không yêu cầu giá trị đơn hàng tối thiểu</div>
                </div>

                <div class="col-md-6">
                    <label class="form-label fw-semibold">
                        <i class="fas fa-tag" style="margin-right: 10px;"></i>Giảm giá tối đa
                    </label>
                    <input type="number" class="form-control" name="max_discount" 
                           value="${v.max_discount}" 
                           step="1000"
                           min="0"
                           placeholder="Không giới hạn">
                    <div class="form-text">Áp dụng cho giảm theo % - giới hạn số tiền giảm tối đa</div>
                </div>
                
                <%-- Thời gian hiệu lực --%>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">
                        <i class="fas fa-calendar-alt" style="margin-right: 10px;"></i>Ngày bắt đầu <span class="text-danger">*</span>
                    </label>
                    <input type="datetime-local" class="form-control" name="start_date" 
                           value="${v.start_dateAsLocalDateTimeString}" 
                           required>
                </div>
                
                <div class="col-md-6">
                    <label class="form-label fw-semibold">
                        <i class="fas fa-calendar-times" style="margin-right: 10px;"></i>Ngày kết thúc <span class="text-danger">*</span>
                    </label>
                    <input type="datetime-local" class="form-control" name="end_date" 
                           value="${v.end_dateAsLocalDateTimeString}" 
                           required>
                </div>
                
                <%-- Mô tả và Giới hạn sử dụng --%>
                <div class="col-md-6">
                    <label class="form-label fw-semibold">
                        <i class="fas fa-align-left" style="margin-right: 10px;"></i>Mô tả
                    </label>
                    <textarea class="form-control" name="description" rows="3" 
                              placeholder="Mô tả về voucher...">${v.description}</textarea>
                </div>
                
                <div class="col-md-6">
                    <label class="form-label fw-semibold">
                        <i class="fas fa-users" style="margin-right: 10px;"></i>Giới hạn lượt sử dụng
                    </label>
                    <input type="number" class="form-control" name="usage_limit" 
                           value="${v.usage_limit}" 
                           step="1"
                           min="1"
                           placeholder="Không giới hạn">
                    <div class="form-text">Bỏ trống nếu không giới hạn số lượt sử dụng</div>
                    <c:if test="${not empty v.voucher_id && v.used_count != null}">
                        <div class="mt-2">
                            <span class="badge bg-info text-white px-3 py-2">
                                <i class="fas fa-chart-line" style="margin-right: 5px;"></i>Đã sử dụng: ${v.used_count} lượt
                            </span>
                        </div>
                    </c:if>
                </div>
                
                <%-- Trạng thái --%>
                <div class="col-12">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="isActive" 
                               id="isActive" ${v.isActive || empty v.voucher_id ? 'checked' : ''}>
                        <label class="form-check-label fw-semibold" for="isActive">
                            Kích hoạt (Cho phép sử dụng voucher này)
                        </label>
                    </div>
                </div>
                
                <%-- Actions --%>
                <div class="col-12">
                    <hr class="my-4">
                    <div class="d-flex gap-3">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save" style="margin-right: 10px;"></i>Lưu Voucher
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/vouchers" class="btn btn-outline-secondary">
                            <i class="fas fa-times" style="margin-right: 10px;"></i>Hủy
                        </a>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>

<script>
// Auto uppercase code input
document.querySelector('input[name="code"]').addEventListener('input', function(e) {
    e.target.value = e.target.value.toUpperCase();
});

// Validate discount value based on discount type
const discountTypeRadios = document.querySelectorAll('input[name="discount_type"]');
const discountValueInput = document.getElementById('discount_value');
const discountValueHelp = document.getElementById('discount_value_help');
const discountValueError = document.getElementById('discount_value_error');

function getSelectedDiscountType() {
    const selected = document.querySelector('input[name="discount_type"]:checked');
    return selected ? selected.value : 'AMOUNT';
}

function updateDiscountValueValidation() {
    const discountType = getSelectedDiscountType();
    const discountValue = parseFloat(discountValueInput.value);
    
    // Reset validation
    discountValueInput.classList.remove('is-invalid');
    discountValueError.textContent = '';
    
    if (discountType === 'PERCENT') {
        discountValueInput.setAttribute('max', '100');
        discountValueHelp.innerHTML = '<i class="fas fa-info-circle" style="margin-right: 10px;"></i>Nhập % (0-100, VD: 10 = 10%)';
        if (discountValueInput.value && (isNaN(discountValue) || discountValue < 0 || discountValue > 100)) {
            discountValueInput.classList.add('is-invalid');
            discountValueError.textContent = 'Giá trị giảm giá theo % phải từ 0 đến 100';
        }
    } else if (discountType === 'AMOUNT') {
        discountValueInput.removeAttribute('max');
        discountValueHelp.innerHTML = '<i class="fas fa-info-circle" style="margin-right: 10px;"></i>Nhập số tiền (VD: 10000 = 10.000₫)';
        if (discountValueInput.value && (isNaN(discountValue) || discountValue < 0)) {
            discountValueInput.classList.add('is-invalid');
            discountValueError.textContent = 'Giá trị giảm giá phải lớn hơn 0';
        }
    }
}

if (discountTypeRadios.length > 0 && discountValueInput) {
    // Add change event to all radio buttons
    discountTypeRadios.forEach(radio => {
        radio.addEventListener('change', updateDiscountValueValidation);
    });
    
    discountValueInput.addEventListener('input', updateDiscountValueValidation);
    discountValueInput.addEventListener('blur', updateDiscountValueValidation);
    
    // Initial validation
    updateDiscountValueValidation();
    
    // Form submission validation
    document.querySelector('form').addEventListener('submit', function(e) {
        const discountType = getSelectedDiscountType();
        const discountValue = parseFloat(discountValueInput.value);
        
        if (discountType === 'PERCENT' && (isNaN(discountValue) || discountValue < 0 || discountValue > 100)) {
            e.preventDefault();
            discountValueInput.classList.add('is-invalid');
            discountValueError.textContent = 'Giá trị giảm giá theo % phải từ 0 đến 100';
            discountValueInput.focus();
            return false;
        } else if (discountType === 'AMOUNT' && (isNaN(discountValue) || discountValue < 0)) {
            e.preventDefault();
            discountValueInput.classList.add('is-invalid');
            discountValueError.textContent = 'Giá trị giảm giá phải lớn hơn 0';
            discountValueInput.focus();
            return false;
        }
    });
}
</script>
