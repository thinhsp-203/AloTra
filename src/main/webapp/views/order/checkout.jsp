<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<style>
.cart-item-card {
    border: 1px solid #e0e0e0;
    border-radius: 12px;
    transition: box-shadow 0.3s ease;
    margin-bottom: 1rem;
}
.cart-item-card:hover {
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.item-thumbnail {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 8px;
}
.quantity-control {
    display: inline-flex;
    align-items: center;
    border: 1px solid #dee2e6;
    border-radius: 8px;
    overflow: hidden;
}
.quantity-control button {
    border: none;
    background: #f8f9fa;
    padding: 0.5rem 0.75rem;
    cursor: pointer;
    transition: background 0.2s;
}
.quantity-control button:hover {
    background: #e9ecef;
}
.quantity-control input {
    border: none;
    text-align: center;
    width: 50px;
    padding: 0.5rem;
}
.payment-method-card {
    border: 2px solid #e0e0e0;
    border-radius: 12px;
    padding: 1rem;
    cursor: pointer;
    transition: all 0.3s ease;
}
.payment-method-card:hover {
    border-color: var(--bs-primary);
    background: rgba(0, 102, 51, 0.05);
}
.payment-method-card.active {
    border-color: var(--bs-primary);
    background: rgba(0, 102, 51, 0.1);
}
</style>

<h1 class="h4 mb-4">
    <i class="bi bi-cart-check text-primary"></i> Thanh toán
</h1>

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
    <p class="text-muted mb-3">Hãy thêm sản phẩm vào giỏ hàng để tiếp tục</p>
    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
      <i class="bi bi-shop"></i> Khám phá sản phẩm
    </a>
  </div>
</c:if>

<c:if test="${not empty sessionScope.CART}">
    <c:set var="total" value="${0}" />
    <c:forEach var="item" items="${sessionScope.CART}">
        <c:set var="total" value="${total + item.lineTotal}" />
    </c:forEach>

    <form method="post" action="${pageContext.request.contextPath}/checkout" id="checkoutForm">
        <div class="row g-4">
            <div class="col-lg-8">
                <div class="card mb-4">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-basket"></i> Giỏ hàng của bạn 
                            <span class="badge bg-primary">${sessionScope.CART.size()} sản phẩm</span>
                        </h5>
                    </div>
                    <div class="card-body p-2">
                        <c:forEach var="item" items="${sessionScope.CART}" varStatus="status">
                            <div class="cart-item-card p-3" data-product-id="${item.productId}" 
                                 data-size="${item.sizeName}" data-toppings="${item.toppingsCsv}">
                                <div class="row align-items-center">
                                    <div class="col-auto">
                                        <img src="${item.thumbnail}" class="item-thumbnail" alt="${item.productName}">
                                    </div>
                                    <div class="col">
                                        <h6 class="mb-1">${item.productName}</h6>
                                        <div class="text-muted small">
                                            <c:if test="${not empty item.sizeName}">
                                                <span class="badge bg-secondary">${item.sizeName}</span>
                                            </c:if>
                                            <c:if test="${not empty item.toppingsCsv}">
                                                <br><i class="bi bi-plus-circle"></i> ${item.toppingsCsv}
                                            </c:if>
                                        </div>
                                        <div class="mt-2">
                                            <button type="button" class="btn btn-sm btn-outline-primary edit-item-btn">
                                                <i class="bi bi-pencil"></i> Chỉnh sửa
                                            </button>
                                            <button type="button" class="btn btn-sm btn-outline-danger remove-item-btn">
                                                <i class="bi bi-trash"></i> Xóa
                                            </button>
                                        </div>
                                    </div>
                                    <div class="col-auto text-end">
                                        <div class="fw-bold text-primary mb-2">
                                            <fmt:formatNumber value="${item.lineTotal}" pattern="#,##0₫"/>
                                        </div>
                                        <div class="quantity-control">
                                            <button type="button" class="qty-decrease">-</button>
                                            <input type="number" value="${item.quantity}" min="1" max="99" readonly>
                                            <button type="button" class="qty-increase">+</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <div class="card">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-person-circle"></i> Thông tin giao hàng
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label">Họ tên <span class="text-danger">*</span></label>
                                <input class="form-control" name="fullname" required value="${sessionScope.currentUser.fullname}"/>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Điện thoại <span class="text-danger">*</span></label>
                                <input class="form-control" name="phone" required value="${sessionScope.currentUser.phone}"/>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Địa chỉ giao hàng <span class="text-danger">*</span></label>
                                <textarea class="form-control" name="address" required rows="3">${sessionScope.currentUser.address}</textarea>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Ghi chú (không bắt buộc)</label>
                                <textarea class="form-control" name="note" rows="2" placeholder="Yêu cầu đặc biệt..."></textarea>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="col-lg-4">
                <div class="card mb-3">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-credit-card"></i> Phương thức thanh toán
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="payment-method-card mb-2 active" data-method="COD">
                            <input type="radio" name="payment" value="COD" id="payment-cod" checked hidden>
                            <label for="payment-cod" class="d-flex align-items-center w-100 mb-0 cursor-pointer">
                                <i class="bi bi-cash-coin fs-3 me-3 text-success"></i>
                                <div class="flex-grow-1">
                                    <strong>Thanh toán khi nhận hàng</strong>
                                    <div class="text-muted small">Tiền mặt khi giao hàng</div>
                                </div>
                            </label>
                        </div>
                        <div class="payment-method-card mb-2" data-method="VNPAY">
                            <input type="radio" name="payment" value="VNPAY" id="payment-vnpay" hidden>
                            <label for="payment-vnpay" class="d-flex align-items-center w-100 mb-0 cursor-pointer">
                                <i class="bi bi-credit-card fs-3 me-3 text-primary"></i>
                                <div class="flex-grow-1">
                                    <strong>VNPAY</strong>
                                    <div class="text-muted small">Thanh toán qua cổng VNPAY</div>
                                </div>
                            </label>
                        </div>
                    </div>
                </div>

                <div class="card mb-3">
                    <div class="card-body">
                        <label class="form-label">
                            <i class="bi bi-tag"></i> Mã giảm giá
                        </label>
                        <div class="input-group">
                            <input class="form-control" name="voucher" id="voucher-code" placeholder="Nhập mã giảm giá"/>
                            <button class="btn btn-outline-secondary" type="button" id="apply-voucher-btn">
                                Áp dụng
                            </button>
                        </div>
                        <div id="voucher-message" class="mt-2 small"></div>
                    </div>
                </div>

                <div class="card position-sticky" style="top: 80px;">
                    <div class="card-header bg-light">
                        <h5 class="card-title mb-0">
                            <i class="bi bi-receipt"></i> Tóm tắt đơn hàng
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="d-flex justify-content-between mb-2">
                            <span>Tạm tính</span>
                            <strong id="subtotal-display">
                                <fmt:formatNumber value="${total}" pattern="#,##0₫"/>
                            </strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2 text-success">
                            <span>Giảm giá</span>
                            <strong id="discount-display">0₫</strong>
                        </div>
                        <div class="d-flex justify-content-between mb-2 text-muted">
                            <span>Phí vận chuyển</span>
                            <strong>Miễn phí</strong>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between mb-3">
                            <strong class="h5 mb-0">Tổng cộng</strong>
                            <strong class="h5 mb-0 text-primary" id="grand-total-display">
                                <fmt:formatNumber value="${total}" pattern="#,##0₫"/>
                            </strong>
                        </div>
                        <button type="submit" class="btn btn-primary btn-lg w-100">
                            <i class="bi bi-check-circle"></i> Đặt hàng
                        </button>
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary w-100 mt-2">
                            <i class="bi bi-arrow-left"></i> Tiếp tục mua sắm
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </form>
</c:if>

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

    // Payment method selection
    document.querySelectorAll('.payment-method-card').forEach(card => {
        card.addEventListener('click', function() {
            document.querySelectorAll('.payment-method-card').forEach(c => c.classList.remove('active'));
            this.classList.add('active');
            this.querySelector('input[type="radio"]').checked = true;
        });
    });

    // Quantity controls for each item
    document.querySelectorAll('.cart-item-card').forEach(card => {
        const qtyInput = card.querySelector('.quantity-control input');
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

    // Remove item button
    document.querySelectorAll('.remove-item-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const card = this.closest('.cart-item-card');
            if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) return;
            
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = contextPath + '/cart/remove';
            
            form.innerHTML = `
                <input name="productId" value="${card.dataset.productId}">
                <input name="size" value="${card.dataset.size || 'Mặc định'}">
                <input name="toppings" value="${card.dataset.toppings || ''}">
            `;
            document.body.appendChild(form);
            form.submit();
        });
    });

    // --- EDIT ITEM MODAL LOGIC ---
    let currentEditingItem = null;
    const editItemModal = new bootstrap.Modal(document.getElementById('editItemModal'));
    
    document.querySelectorAll('.edit-item-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const card = this.closest('.cart-item-card');
            currentEditingItem = card;
            const pid = card.dataset.productId;
            
            document.getElementById('editModalContent').innerHTML = '<div class="text-center my-5"><div class="spinner-border"></div></div>';
            editItemModal.show();
            
            fetch(`${contextPath}/api/product-details?id=${pid}`)
                .then(r => r.json())
                .then(data => {
                    if (data.ok) {
                        renderEditModal(data, card);
                    } else {
                        document.getElementById('editModalContent').innerHTML = '<p class="text-danger">Không thể tải thông tin.</p>';
                    }
                });
        });
    });

    function renderEditModal(data, card) {
        const content = document.getElementById('editModalContent');
        const currentSize = card.dataset.size || "Mặc định";
        const currentToppingsStr = card.dataset.toppings || "";
        const currentQuantity = card.querySelector('.quantity-control input').value;

        let sizesHtml = data.sizes.map((s, index) => {
        	  const isChecked = (s.name === currentSize);
        	  const priceAdjText = new Intl.NumberFormat('vi-VN').format(s.priceAdjustment);
        	  return ''
        	    + '<div class="col-auto">'
        	    +   '<input type="radio" class="btn-check" name="edit-size" id="edit-size-' + index + '"'
        	    +   ' value="' + s.name + '" data-price-adj="' + s.priceAdjustment + '"' + (isChecked ? ' checked' : '') + '>'
        	    +   '<label class="btn btn-outline-primary btn-size" for="edit-size-' + index + '">'
        	    +     s.name
        	    +     '<div class="small fw-normal">' + (s.priceAdjustment >= 0 ? '+' : '') + priceAdjText + ' đ</div>'
        	    +   '</label>'
        	    + '</div>';
        	}).join('');

        	let toppingsHtml = data.toppings.map((t) => {
        	  const regex = new RegExp(t.name.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&') + '( x(\\d+))?');
        	  const match = currentToppingsStr.match(regex);
        	  const currentQty = match ? (match[2] ? parseInt(match[2]) : 1) : 0;
        	  return ''
        	    + '<div class="d-flex justify-content-between align-items-center mb-2">'
        	    +   '<div>' + t.name + ' <small class="text-muted">(+' + currencyFormatter.format(t.price) + ')</small></div>'
        	    +   '<div class="input-group" style="width: 100px;">'
        	    +     '<button class="btn btn-outline-secondary btn-sm" type="button" onclick="updateEditModalToppingQty(' + t.id + ', -1)">-</button>'
        	    +     '<input type="text" class="form-control form-control-sm text-center" value="' + currentQty + '" readonly id="edit-topping-qty-' + t.id + '" data-topping-id="' + t.id + '" data-price="' + t.price + '">'
        	    +     '<button class="btn btn-outline-secondary btn-sm" type="button" onclick="updateEditModalToppingQty(' + t.id + ', 1)">+</button>'
        	    +   '</div>'
        	    + '</div>';
        	}).join('');

        	let sizesBlock    = sizesHtml    ? '<div class="mb-3"><h6>Size</h6><div class="row g-2">' + sizesHtml    + '</div></div>' : '';
        	let toppingsBlock = toppingsHtml ? '<div class="mb-3"><h6>Topping</h6>' + toppingsHtml + '</div>' : '';

        	let finalHtml = ''
        	  + '<div class="row g-4">'
        	  +   '<div class="col-md-5"><img src="' + data.product.thumbnail + '" class="img-fluid rounded"></div>'
        	  +   '<div class="col-md-7">'
        	  +     '<h4>' + data.product.name + '</h4>'
        	  +     '<p class="h5 text-primary fw-bold mb-3" id="edit-base-price" data-price="' + data.product.basePrice + '">'
        	  +        currencyFormatter.format(data.product.basePrice) + '</p>'
        	  +     '<div style="max-height: 250px; overflow-y: auto; padding-right: 10px;">'
        	  +        sizesBlock + toppingsBlock
        	  +     '</div>'
        	  +   '</div>'
        	  + '</div>';
        	content.innerHTML = finalHtml;


        content.querySelectorAll('input[name="edit-size"]').forEach(radio => radio.addEventListener('change', updateEditModalPrice));
        updateEditModalPrice();
    }

    window.updateEditModalToppingQty = (id, change) => {
        const qtyInput = document.getElementById(`edit-topping-qty-${id}`);
        let currentQty = parseInt(qtyInput.value) + change;
        if (currentQty >= 0) {
            qtyInput.value = currentQty;
            updateEditModalPrice();
        }
    };

    function updateEditModalPrice() {
        const basePriceEl = document.getElementById('edit-base-price');
        if (!basePriceEl) return;
        const basePrice = parseFloat(basePriceEl.dataset.price) || 0;
        const sizeInput = document.querySelector('input[name="edit-size"]:checked');
        const sizeAdj = sizeInput ? (parseFloat(sizeInput.dataset.priceAdj) || 0) : 0;
        let toppingsPrice = 0;
        document.querySelectorAll('#editModalContent input[data-topping-id]').forEach(input => {
            toppingsPrice += (parseFloat(input.dataset.price) || 0) * (parseInt(input.value) || 0);
        });
        const finalPrice = basePrice + sizeAdj + toppingsPrice;
        document.getElementById('updateItemBtn').textContent =
        	  'Cập nhật - ' + currencyFormatter.format(finalPrice);
    }

    document.getElementById('updateItemBtn').addEventListener('click', function() {
        if (!currentEditingItem) return;

        const params = new URLSearchParams({
            oldProductId: currentEditingItem.dataset.productId,
            oldSize: currentEditingItem.dataset.size || "Mặc định",
            oldToppingsCsv: currentEditingItem.dataset.toppings || "",
            quantity: currentEditingItem.querySelector('.quantity-control input').value,
            newSize: document.querySelector('input[name="edit-size"]:checked')?.value || "Mặc định",
            newToppings: Array.from(document.querySelectorAll('#editModalContent input[data-topping-id]'))
                            .filter(input => parseInt(input.value) > 0)
                            .map(input => `${input.dataset.toppingId}:${input.value}`)
                            .join(',')
        });

        fetch(contextPath + '/cart/update-item', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: params
        })
        .then(r => r.json())
        .then(data => {
            if (data.ok) {
                location.reload();
            } else {
                alert('Lỗi: ' + data.message);
            }
        });
        editItemModal.hide();
    });

    // Voucher application
    document.getElementById('apply-voucher-btn')?.addEventListener('click', function() {
        const code = document.getElementById('voucher-code').value.trim();
        if (!code) return;
        fetch(contextPath + '/api/voucher/apply', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'code=' + encodeURIComponent(code)
        })
        .then(r => r.json())
        .then(data => {
            const msgEl = document.getElementById('voucher-message');
            if (data.ok) {
                msgEl.className = 'mt-2 small text-success';
                msgEl.textContent = data.message;
                document.getElementById('discount-display').textContent = '-' + data.discountFormatted;
                document.getElementById('grand-total-display').textContent = data.newTotalFormatted;
            } else {
                msgEl.className = 'mt-2 small text-danger';
                msgEl.textContent = data.message;
            }
        });
    });
});
</script>