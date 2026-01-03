<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<style>
.checkout-container {
    max-width: 1200px;
    margin: 0 auto;
}

.section-card {
    background: white;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    padding: 1.5rem;
    margin-bottom: 1rem;
}

.section-header {
    font-weight: 600;
    font-size: 1rem;
    margin-bottom: 1rem;
    padding-bottom: 0.75rem;
    border-bottom: 1px solid #e9ecef;
}

.cart-item-simple {
    display: flex;
    gap: 1rem;
    padding: 1rem 0;
    border-bottom: 1px solid #f0f0f0;
}

.cart-item-simple:last-child {
    border-bottom: none;
}

.item-image {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 8px;
    flex-shrink: 0;
}

.item-details {
    flex-grow: 1;
}

.item-name {
    font-weight: 600;
    font-size: 0.95rem;
    margin-bottom: 0.25rem;
}

.item-options {
    font-size: 0.85rem;
    color: #666;
}

.item-actions {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.quantity-mini {
    display: inline-flex;
    align-items: center;
    gap: 0.25rem;
}

.quantity-mini button {
    width: 28px;
    height: 28px;
    padding: 0;
    border-radius: 6px;
    border: 1px solid #dee2e6;
    background: white;
    font-size: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
}

.quantity-mini input {
    width: 40px;
    text-align: center;
    border: 1px solid #dee2e6;
    border-radius: 6px;
    padding: 0.25rem;
    font-weight: 600;
}

.btn-icon {
    width: 28px;
    height: 28px;
    padding: 0;
    border-radius: 6px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
}

.price-mini {
    font-weight: 600;
    color: var(--bs-primary);
    font-size: 0.95rem;
    white-space: nowrap;
}

.payment-option {
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    padding: 0.75rem;
    margin-bottom: 0.5rem;
    cursor: pointer;
    transition: all 0.2s;
}

.payment-option:hover {
    border-color: var(--bs-primary);
}

.payment-option.active {
    border-color: var(--bs-primary);
    background: rgba(0, 102, 51, 0.05);
}

.payment-option input[type="radio"] {
    margin-right: 0.5rem;
}

.summary-row {
    display: flex;
    justify-content: space-between;
    padding: 0.5rem 0;
    font-size: 0.95rem;
}

.summary-row.total {
    border-top: 2px solid #e9ecef;
    padding-top: 1rem;
    margin-top: 0.5rem;
    font-weight: 600;
    font-size: 1.1rem;
}

.form-control-sm {
    font-size: 0.9rem;
    padding: 0.5rem 0.75rem;
}

.sticky-sidebar {
    position: sticky;
    top: 20px;
}
.voucher-input-group {
    display: flex;
    gap: 0.5rem;
}

.voucher-input-group input {
    flex: 1;
}

.voucher-message {
    margin-top: 0.5rem;
    font-size: 0.85rem;
}

.voucher-list {
    max-height: 400px;
    overflow-y: auto;
}

.voucher-item {
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    padding: 1rem;
    margin-bottom: 0.75rem;
    cursor: pointer;
    transition: all 0.2s;
    background: white;
}

.voucher-item:hover {
    border-color: var(--bs-primary);
    box-shadow: 0 2px 8px rgba(0, 102, 51, 0.1);
}

.voucher-item.selected {
    border-color: var(--bs-primary);
    background: rgba(0, 102, 51, 0.05);
}

.voucher-item.disabled {
    opacity: 0.6;
    cursor: not-allowed;
    background: #f5f5f5;
}

.voucher-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 0.5rem;
}

.voucher-code {
    font-weight: 700;
    font-size: 1.1rem;
    color: var(--bs-primary);
}

.voucher-discount {
    font-weight: 700;
    font-size: 1.1rem;
    color: #28a745;
}

.voucher-description {
    font-size: 0.9rem;
    color: #666;
    margin-bottom: 0.5rem;
}

.voucher-info {
    display: flex;
    flex-wrap: wrap;
    gap: 0.75rem;
    font-size: 0.85rem;
    color: #666;
}

.voucher-info-item {
    display: flex;
    align-items: center;
    gap: 0.25rem;
}

.voucher-info-item i {
    color: var(--bs-primary);
}

.voucher-warning {
    margin-top: 0.5rem;
    padding: 0.5rem;
    background: #fff3cd;
    border-radius: 4px;
    font-size: 0.85rem;
    color: #856404;
}
</style>

<div class="checkout-container">
    <h1 class="h4 mb-4">Thanh toán</h1>

    <c:if test="${not empty sessionScope.checkoutError}">
      <div class="alert alert-danger alert-dismissible fade show">
        <i class="bi bi-exclamation-triangle-fill"></i> ${sessionScope.checkoutError}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
      <c:remove var="checkoutError" scope="session"/>
    </c:if>

    <c:if test="${empty sessionScope.CART}">
      <div class="alert alert-info text-center py-5">
        <i class="bi bi-cart-x display-4 d-block mb-3"></i>
        <h5>Giỏ hàng của bạn đang trống</h5>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">
          Khám phá sản phẩm
        </a>
      </div>
    </c:if>

    <c:if test="${not empty sessionScope.CART}">
        <c:set var="total" value="${0}" />
        <c:forEach var="item" items="${sessionScope.CART}">
            <c:set var="total" value="${total + item.lineTotal}" />
        </c:forEach>

        <form method="post" action="${pageContext.request.contextPath}/checkout" id="checkoutForm">
            <div class="row g-3">
                <!-- Left Column -->
                <div class="col-lg-7">
                    <!-- Delivery Info -->
                    <div class="section-card">
                        <div class="section-header">
                            <i class="bi bi-geo-alt"></i> Địa chỉ giao hàng
                        </div>
                        <div class="row g-2">
                            <div class="col-md-6">
                                <input class="form-control form-control-sm" name="fullname" 
                                       placeholder="Họ tên *" required 
                                       value="${sessionScope.currentUser.fullname}"/>
                            </div>
                            <div class="col-md-6">
                                <input class="form-control form-control-sm" name="phone" 
                                       placeholder="Số điện thoại *" required 
                                       value="${sessionScope.currentUser.phone}"/>
                            </div>
                            <div class="col-12">
                                <textarea class="form-control form-control-sm" name="address" 
                                          placeholder="Địa chỉ giao hàng *" required 
                                          rows="2">${sessionScope.currentUser.address}</textarea>
                            </div>
                            <div class="col-12">
                                <input class="form-control form-control-sm" name="note" 
                                       placeholder="Ghi chú đơn hàng (không bắt buộc)"/>
                            </div>
                        </div>
                    </div>

                    <!-- Payment Methods -->
                    <div class="section-card">
                        <div class="section-header">
                            <i class="bi bi-credit-card"></i> Phương thức thanh toán
                        </div>
                        
                        <label class="payment-option active">
                            <input type="radio" name="payment" value="COD" checked>
                            <i class="bi bi-cash"></i> Thanh toán tiền mặt (COD)
                        </label>
                        
                        <label class="payment-option">
                            <input type="radio" name="payment" value="VNPAY">
                            <i class="bi bi-wallet2"></i> Ví VNPAY
                        </label>
                        
                        <label class="payment-option">
                            <input type="radio" name="payment" value="MOMO">
                            <i class="bi bi-credit-card"></i> Ví MOMO
                        </label>
                        
                        <label class="payment-option">
                            <input type="radio" name="payment" value="CARDVISA">
                            <i class="bi bi-phone"></i> Thẻ ngân hàng/Thẻ tín dụng
                        </label>
                    </div>

                    <!-- VOUCHER SECTION -->
                    <div class="section-card">
                        <div class="section-header">
                            <i class="bi bi-tag"></i> Mã giảm giá
                        </div>
                        
                        <!-- Input để nhập mã thủ công (nếu có) -->
                        <div class="voucher-input-group mb-3">
                            <input class="form-control form-control-sm" 
                                   name="voucher" 
                                   id="voucher-code-input" 
                                   placeholder="Nhập mã giảm giá (nếu có)"/>
                            <button class="btn btn-outline-primary btn-sm" 
                                    type="button" 
                                    id="apply-voucher-btn">
                                Áp dụng
                            </button>
                        </div>
                        <div id="voucher-message" class="voucher-message"></div>
                        
                        <!-- Danh sách voucher khả dụng -->
                        <c:choose>
                            <c:when test="${empty availableVouchers}">
                                <div class="text-center text-muted py-3">
                                    <i class="bi bi-inbox" style="font-size: 2rem;"></i>
                                    <p class="mb-0 mt-2">Hiện tại không có mã giảm giá nào</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="voucher-list">
                                    <c:forEach var="vInfo" items="${availableVouchers}">
                                        <c:set var="v" value="${vInfo.voucher()}"/>
                                        <div class="voucher-item ${vInfo.canUse() ? '' : 'disabled'}" 
                                             data-code="${v.code}"
                                             <c:if test="${vInfo.canUse()}">onclick="selectVoucher(this, '${v.code}')"</c:if>>
                                            <div class="voucher-header">
                                                <div class="voucher-code">${v.code}</div>
                                                <div class="voucher-discount">${vInfo.discountDisplay()}</div>
                                            </div>
                                            <c:if test="${not empty v.description}">
                                                <div class="voucher-description">${v.description}</div>
                                            </c:if>
                                            <div class="voucher-info">
                                                <c:if test="${not empty v.min_order_value}">
                                                    <div class="voucher-info-item">
                                                        <i class="bi bi-cart-check"></i>
                                                        <span>Đơn tối thiểu: <fmt:formatNumber value="${v.min_order_value}" pattern="#,##0" />₫</span>
                                                    </div>
                                                </c:if>
                                                <c:choose>
                                                    <c:when test="${v.usage_limit != null}">
                                                        <div class="voucher-info-item">
                                                            <i class="bi bi-people"></i>
                                                            <span>Còn lại: ${vInfo.remainingUses()} lượt</span>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="voucher-info-item">
                                                            <i class="bi bi-people"></i>
                                                            <span>Không giới hạn lượt</span>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                                <div class="voucher-info-item">
                                                    <i class="bi bi-calendar-event"></i>
                                                    <span>
                                                        <fmt:formatDate value="${v.start_dateAsDate}" pattern="dd/MM/yyyy"/> - 
                                                        <fmt:formatDate value="${v.end_dateAsDate}" pattern="dd/MM/yyyy"/>
                                                    </span>
                                                </div>
                                            </div>
                                            <c:if test="${not vInfo.canUse()}">
                                                <div class="voucher-warning">
                                                    <i class="bi bi-exclamation-triangle"></i> 
                                                    Đơn hàng chưa đủ điều kiện (cần tối thiểu <fmt:formatNumber value="${v.min_order_value}" pattern="#,##0" />₫)
                                                </div>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Right Column -->
                <div class="col-lg-5">
                    <div class="sticky-sidebar">
                        <!-- Cart Items -->
                        <div class="section-card">
                            <div class="section-header">
                                <i class="bi bi-bag-check"></i> Giỏ hàng của bạn 
                                <span class="badge bg-primary">${sessionScope.CART.size()} món</span>
                            </div>
                            
                            <c:forEach var="item" items="${sessionScope.CART}">
                                <div class="cart-item-simple" data-product-id="${item.productId}" 
                                     data-size="${item.sizeName}" data-toppings="${item.toppingsCsv}">
                                    <img src="${item.thumbnail}" class="item-image" alt="${item.productName}">
                                    
                                    <div class="item-details">
                                        <div class="item-name">${item.productName}</div>
                                        <div class="item-options">
                                            <c:if test="${not empty item.sizeName && item.sizeName ne 'Mặc định'}">
                                                Kích cỡ: ${item.sizeName}
                                            </c:if>
                                            <c:if test="${not empty item.toppingsCsv}">
                                                <br>Đá: ${item.toppingsCsv}
                                            </c:if>
                                        </div>
                                    </div>
                                    
                                    <div class="item-actions d-flex flex-column align-items-end gap-2">
                                        <div class="price-mini">
                                            <fmt:formatNumber value="${item.lineTotal}" pattern="#,##0₫"/>
                                        </div>
                                        <div class="quantity-mini">
                                            <button type="button" class="qty-decrease btn btn-sm btn-outline-secondary">−</button>
                                            <input type="number" value="${item.quantity}" readonly>
                                            <button type="button" class="qty-increase btn btn-sm btn-outline-secondary">+</button>
                                        </div>
                                        <div class="d-flex gap-1">
                                            <button type="button" class="btn btn-sm btn-outline-primary btn-icon edit-item-btn" title="Sửa">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button type="button" class="btn btn-sm btn-outline-danger btn-icon remove-item-btn" title="Xóa">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>

                        <!-- Order Summary -->
                        <div class="section-card">
                            <div class="section-header">
                                <i class="bi bi-receipt"></i> Thông tin thanh toán
                            </div>
                            
                            <div class="summary-row">
                                <span>Tổng tiền tạm tính</span>
                                <strong id="subtotal-display">
                                    <fmt:formatNumber value="${total}" pattern="#,##0₫"/>
                                </strong>
                            </div>
                            
                            <div class="summary-row text-muted">
                                <span>Phí vận chuyển</span>
                                <strong>0 đ</strong>
                            </div>
                            
                            <div class="summary-row text-success">
                                <span>Mã giảm giá</span>
                                <strong id="discount-display">0 đ</strong>
                            </div>
                            
                            <div class="summary-row total">
                                <span>Tổng tiền (Đã có VAT)</span>
                                <span class="text-primary" id="grand-total-display">
                                    <fmt:formatNumber value="${total}" pattern="#,##0₫"/>
                                </span>
                            </div>
                            
                            <button type="submit" class="btn btn-primary w-100 mt-3">
                                TIẾN HÀNH THANH TOÁN
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </c:if>
</div>

<!-- Edit Modal (giữ nguyên) -->
<div class="modal fade" id="editItemModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Chỉnh sửa sản phẩm</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body" id="editModalContent">
                <div class="text-center"><div class="spinner-border"></div></div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="button" class="btn btn-primary" id="updateItemBtn">Cập nhật</button>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    const contextPath = '${pageContext.request.contextPath}';
    const currencyFormatter = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' });

    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Payment method selection
    document.querySelectorAll('.payment-option').forEach(option => {
        option.addEventListener('click', function() {
            document.querySelectorAll('.payment-option').forEach(o => o.classList.remove('active'));
            this.classList.add('active');
            this.querySelector('input[type="radio"]').checked = true;
        });
    });

    // Quantity controls
    document.querySelectorAll('.cart-item-simple').forEach(card => {
        const qtyInput = card.querySelector('.quantity-mini input');
        const decreaseBtn = card.querySelector('.qty-decrease');
        const increaseBtn = card.querySelector('.qty-increase');

        const updateQuantity = (change) => {
            let newQty = parseInt(qtyInput.value) + change;
            if (newQty < 1) return;
            
            const params = new URLSearchParams({
                productId: card.dataset.productId,
                size: card.dataset.size || "Mặc định",
                toppings: card.dataset.toppings || "",
                quantity: newQty
            });

            fetch(contextPath + '/cart/update', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: params
            })
            .then(r => r.json())
            .then(data => {
                if (data.ok) location.reload();
            });
        };
        
        decreaseBtn.addEventListener('click', () => updateQuantity(-1));
        increaseBtn.addEventListener('click', () => updateQuantity(1));
    });

    // Remove item
    document.querySelectorAll('.remove-item-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const card = this.closest('.cart-item-simple');
            if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) return;
            
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = contextPath + '/cart/remove';
            form.innerHTML = '<input name="productId" value="' + card.dataset.productId + '">' +
                           '<input name="size" value="' + (card.dataset.size || 'Mặc định') + '">' +
                           '<input name="toppings" value="' + (card.dataset.toppings || '') + '">';
            document.body.appendChild(form);
            form.submit();
        });
    });

    // Function để chọn voucher từ danh sách
    window.selectVoucher = function(element, code) {
        // Bỏ chọn các voucher khác
        document.querySelectorAll('.voucher-item').forEach(item => {
            item.classList.remove('selected');
        });
        
        // Chọn voucher này
        element.classList.add('selected');
        
        // Điền mã vào input
        document.getElementById('voucher-code-input').value = code;
        
        // Tự động áp dụng voucher
        document.getElementById('apply-voucher-btn').click();
    };
    
    // VOUCHER APPLICATION
    document.getElementById('apply-voucher-btn')?.addEventListener('click', function() {
        const code = document.getElementById('voucher-code-input').value.trim();
        const msgEl = document.getElementById('voucher-message');
        const discountEl = document.getElementById('discount-display');
        const totalEl = document.getElementById('grand-total-display');
        const subtotalEl = document.getElementById('subtotal-display');
        
        if (!code) {
            msgEl.className = 'voucher-message text-warning';
            msgEl.textContent = 'Vui lòng nhập mã giảm giá';
            return;
        }
        
        // Show loading
        this.disabled = true;
        this.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Đang kiểm tra...';
        
        fetch(contextPath + '/api/voucher', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'code=' + encodeURIComponent(code)
        })
        .then(r => r.json())
        .then(data => {
            if (data.ok) {
                msgEl.className = 'voucher-message text-success';
                msgEl.innerHTML = '<i class="bi bi-check-circle-fill"></i> ' + data.message;
                
                // Format discount amount
                const discountAmount = parseFloat(data.discount) || 0;
                const discountFormatted = new Intl.NumberFormat('vi-VN').format(discountAmount);
                discountEl.textContent = '-' + discountFormatted + '₫';
                discountEl.parentElement.classList.remove('text-success');
                discountEl.parentElement.classList.add('text-danger');
                
                // Format new total
                const newTotal = parseFloat(data.newTotal) || 0;
                const newTotalFormatted = new Intl.NumberFormat('vi-VN').format(newTotal);
                totalEl.textContent = newTotalFormatted + '₫';
                
                // Đánh dấu voucher đã chọn trong danh sách
                document.querySelectorAll('.voucher-item').forEach(item => {
                    if (item.dataset.code === code) {
                        item.classList.add('selected');
                    }
                });
            } else {
                msgEl.className = 'voucher-message text-danger';
                msgEl.innerHTML = '<i class="bi bi-x-circle-fill"></i> ' + data.message;
                discountEl.textContent = '0₫';
                discountEl.parentElement.classList.remove('text-danger');
                discountEl.parentElement.classList.add('text-success');
                totalEl.textContent = subtotalEl.textContent;
                
                // Bỏ chọn voucher nếu có
                document.querySelectorAll('.voucher-item').forEach(item => {
                    item.classList.remove('selected');
                });
            }
        })
        .catch(error => {
            console.error('Error applying voucher:', error);
            msgEl.className = 'voucher-message text-danger';
            msgEl.innerHTML = '<i class="bi bi-exclamation-triangle-fill"></i> Không thể áp dụng mã giảm giá';
        })
        .finally(() => {
            this.disabled = false;
            this.textContent = 'Áp dụng';
        });
    });

    // EDIT MODAL - Giữ nguyên code đã fix trước đó
    let currentEditingItem = null;
    const editItemModal = new bootstrap.Modal(document.getElementById('editItemModal'));
    
    document.querySelectorAll('.edit-item-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const card = this.closest('.cart-item-simple');
            currentEditingItem = card;
            const pid = card.dataset.productId;
            
            document.getElementById('editModalContent').innerHTML = 
                '<div class="text-center my-5"><div class="spinner-border"></div></div>';
            editItemModal.show();
            
            fetch(contextPath + '/api/product-details?id=' + pid)
                .then(r => {
                    if (!r.ok) throw new Error('HTTP ' + r.status);
                    return r.json();
                })
                .then(data => {
                    if (data.ok) {
                        renderEditModal(data, card);
                    } else {
                        document.getElementById('editModalContent').innerHTML = 
                            '<div class="alert alert-danger">Lỗi: ' + (data.message || 'Không thể tải') + '</div>';
                    }
                })
                .catch(err => {
                    console.error(err);
                    document.getElementById('editModalContent').innerHTML = 
                        '<div class="alert alert-danger">Không thể tải thông tin sản phẩm</div>';
                });
        });
    });

    function renderEditModal(data, card) {
        const content = document.getElementById('editModalContent');
        const currentSize = card.dataset.size || "Mặc định";
        const currentToppingsStr = card.dataset.toppings || "";

        let sizesHtml = '';
        if (data.sizes && data.sizes.length > 0) {
            sizesHtml = data.sizes.map((s, i) => {
                const checked = (s.name === currentSize) ? 'checked' : '';
                const adj = new Intl.NumberFormat('vi-VN').format(s.priceAdjustment);
                return '<div class="col-auto">' +
                    '<input type="radio" class="btn-check" name="edit-size" id="edit-size-' + i + '" ' +
                    'value="' + escapeHtml(s.name) + '" data-price-adj="' + s.priceAdjustment + '" ' + checked + '>' +
                    '<label class="btn btn-outline-primary btn-size" for="edit-size-' + i + '">' +
                    escapeHtml(s.name) + '<div class="small fw-normal">' + 
                    (s.priceAdjustment >= 0 ? '+' : '') + adj + ' đ</div></label></div>';
            }).join('');
        }

        let toppingsHtml = '';
        if (data.toppings && data.toppings.length > 0) {
            toppingsHtml = data.toppings.map(t => {
                const regex = new RegExp(t.name.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&') + '( x(\\d+))?');
                const match = currentToppingsStr.match(regex);
                const qty = match ? (match[2] ? parseInt(match[2]) : 1) : 0;
                
                return '<div class="d-flex justify-content-between align-items-center mb-2">' +
                    '<div>' + escapeHtml(t.name) + ' <small class="text-muted">(+' + 
                    currencyFormatter.format(t.price) + ')</small></div>' +
                    '<div class="input-group" style="width: 100px;">' +
                    '<button class="btn btn-outline-secondary btn-sm" type="button" ' +
                    'onclick="updateEditModalToppingQty(' + t.id + ', -1)">-</button>' +
                    '<input type="text" class="form-control form-control-sm text-center" value="' + qty + '" readonly ' +
                    'id="edit-topping-qty-' + t.id + '" data-topping-id="' + t.id + '" data-price="' + t.price + '">' +
                    '<button class="btn btn-outline-secondary btn-sm" type="button" ' +
                    'onclick="updateEditModalToppingQty(' + t.id + ', 1)">+</button>' +
                    '</div></div>';
            }).join('');
        }

        const sizesBlock = sizesHtml ? '<div class="mb-3"><h6>Size</h6><div class="row g-2">' + sizesHtml + '</div></div>' : '';
        const toppingsBlock = toppingsHtml ? '<div class="mb-3"><h6>Topping</h6>' + toppingsHtml + '</div>' : '';

        content.innerHTML = '<div class="row g-4">' +
            '<div class="col-md-5"><img src="' + escapeHtml(data.product.thumbnail) + '" ' +
            'class="img-fluid rounded" alt="' + escapeHtml(data.product.name) + '"></div>' +
            '<div class="col-md-7">' +
            '<h4>' + escapeHtml(data.product.name) + '</h4>' +
            '<p class="h5 text-primary fw-bold mb-3" id="edit-base-price" data-price="' + data.product.basePrice + '">' +
            currencyFormatter.format(data.product.basePrice) + '</p>' +
            '<div style="max-height: 300px; overflow-y: auto;">' +
            sizesBlock + toppingsBlock +
            (!sizesBlock && !toppingsBlock ? '<p class="text-muted">Sản phẩm này không có tùy chọn.</p>' : '') +
            '</div></div></div>';

        content.querySelectorAll('input[name="edit-size"]').forEach(r => 
            r.addEventListener('change', updateEditModalPrice));
        updateEditModalPrice();
    }

    window.updateEditModalToppingQty = (id, change) => {
        const input = document.getElementById('edit-topping-qty-' + id);
        if (!input) return;
        let qty = parseInt(input.value) + change;
        if (qty >= 0) {
            input.value = qty;
            updateEditModalPrice();
        }
    };

    function updateEditModalPrice() {
        const el = document.getElementById('edit-base-price');
        if (!el) return;
        
        const base = parseFloat(el.dataset.price) || 0;
        const sizeInput = document.querySelector('input[name="edit-size"]:checked');
        const sizeAdj = sizeInput ? (parseFloat(sizeInput.dataset.priceAdj) || 0) : 0;
        
        let toppingsCost = 0;
        document.querySelectorAll('#editModalContent input[data-topping-id]').forEach(inp => {
            toppingsCost += (parseFloat(inp.dataset.price) || 0) * (parseInt(inp.value) || 0);
        });
        
        document.getElementById('updateItemBtn').textContent = 
            'Cập nhật - ' + currencyFormatter.format(base + sizeAdj + toppingsCost);
    }

    document.getElementById('updateItemBtn').addEventListener('click', function() {
        if (!currentEditingItem) return;

        const newSize = document.querySelector('input[name="edit-size"]:checked');
        const newToppings = Array.from(document.querySelectorAll('#editModalContent input[data-topping-id]'))
            .filter(inp => parseInt(inp.value) > 0)
            .map(inp => inp.dataset.toppingId + ':' + inp.value)
            .join(',');

        const params = new URLSearchParams({
            oldProductId: currentEditingItem.dataset.productId,
            oldSize: currentEditingItem.dataset.size || "Mặc định",
            oldToppingsCsv: currentEditingItem.dataset.toppings || "",
            quantity: currentEditingItem.querySelector('.quantity-mini input').value,
            newSize: newSize ? newSize.value : "Mặc định",
            newToppings: newToppings
        });

        fetch(contextPath + '/cart/update-item', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: params
        })
        .then(r => r.json())
        .then(data => {
            if (data.ok) location.reload();
            else alert('Lỗi: ' + (data.message || 'Không cập nhật được'));
        })
        .catch(err => {
            console.error(err);
            alert('Có lỗi xảy ra');
        });
        
        editItemModal.hide();
    });
});
</script>