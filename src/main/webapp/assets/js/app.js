(function () {
    'use strict';

    const body = document.body;
    const contextPath = body.dataset.contextPath || '';

    // --- UTILITY FUNCTIONS ---
    function post(url, params, callback) {
        const xhr = new XMLHttpRequest();
        xhr.open('POST', url, true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
        xhr.onload = function () {
            if (xhr.status >= 200 && xhr.status < 300) {
                try {
                    callback(null, JSON.parse(xhr.responseText));
                } catch (e) {
                    callback(e);
                }
            } else {
                callback(new Error(`HTTP ${xhr.status}`));
            }
        };
        xhr.onerror = () => callback(new Error('Network request failed'));
        xhr.send(params);
    }

    function get(url, callback) {
        const xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        xhr.onload = function () {
            if (xhr.status >= 200 && xhr.status < 300) {
                try {
                    callback(null, JSON.parse(xhr.responseText));
                } catch (e) {
                    callback(e);
                }
            } else {
                callback(new Error(`HTTP ${xhr.status}`));
            }
        };
        xhr.onerror = () => callback(new Error('Network request failed'));
        xhr.send();
    }
    
    function showToast(message, isError = false) {
        const toastEl = document.getElementById('liveToast');
        if (!toastEl) return;
        const toastBody = toastEl.querySelector('.toast-body');
        const toastHeader = toastEl.querySelector('.toast-header strong');
        toastBody.textContent = message;
        toastHeader.className = isError ? 'me-auto text-danger' : 'me-auto text-success';
        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    }

    const currencyFormatter = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' });

    // --- PRODUCT MODAL LOGIC ---
    const productModal = document.getElementById('productModal');
    if (productModal) {
        productModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const productId = button.dataset.productId;
            const modalContent = document.getElementById('productModalContent');
            const addToCartBtn = document.getElementById('modalAddToCartBtn');

            // Set loading state
            modalContent.innerHTML = '<div class="col-12 text-center"><div class="spinner-border"></div></div>';
            addToCartBtn.disabled = true;

            // Fetch product data
            get(`${contextPath}/api/product-details?id=${productId}`, (err, data) => {
                if (err || !data || !data.ok) {
                    modalContent.innerHTML = '<p class="text-danger">Không thể tải thông tin sản phẩm.</p>';
                    return;
                }
                
                // Build modal HTML
                let sizesHtml = data.sizes.map((s, index) => `
                    <div class="col">
                        <input type="radio" class="btn-check" name="size" id="size-${s.name}" value="${s.name}" data-price-adj="${s.priceAdjustment}" ${index === 0 ? 'checked' : ''}>
                        <label class="btn btn-outline-primary w-100" for="size-${s.name}">${s.name}</label>
                    </div>
                `).join('');

                let toppingsHtml = data.toppings.map(t => `
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" name="topping" value="${t.id}" id="topping-${t.id}" data-price="${t.price}">
                        <label class="form-check-label d-flex justify-content-between" for="topping-${t.id}">
                            <span>${t.name}</span>
                            <span>+${currencyFormatter.format(t.price)}</span>
                        </label>
                    </div>
                `).join('');

                modalContent.innerHTML = `
                    <div class="col-md-5">
                        <img src="${data.product.thumbnail}" class="img-fluid rounded">
                    </div>
                    <div class="col-md-7">
                        <h4 id="modalProductName">${data.product.name}</h4>
                        <p class="h5 text-primary fw-bold" id="modalBasePrice" data-price="${data.product.basePrice}">${currencyFormatter.format(data.product.basePrice)}</p>
                        <hr>
                        <div class="mb-3">
                            <h6>Kích cỡ</h6>
                            <div class="row row-cols-3 g-2">${sizesHtml}</div>
                        </div>
                         <div class="mb-3">
                            <h6>Topping</h6>
                            <div id="modalToppingsList">${toppingsHtml}</div>
                        </div>
                        <div class="d-flex align-items-center">
                           <h6>Số lượng</h6>
                           <div class="input-group ms-auto" style="width: 120px;">
                               <button class="btn btn-outline-secondary" type="button" id="modal-quantity-minus">-</button>
                               <input type="text" class="form-control text-center" id="modal-quantity" value="1" readonly>
                               <button class="btn btn-outline-secondary" type="button" id="modal-quantity-plus">+</button>
                           </div>
                        </div>
                    </div>
                `;

                // Add event listeners for dynamic price update
                modalContent.querySelectorAll('input').forEach(input => {
                    input.addEventListener('change', updateModalPrice);
                });
                
                document.getElementById('modal-quantity-plus').addEventListener('click', () => {
                    const qtyInput = document.getElementById('modal-quantity');
                    qtyInput.value = parseInt(qtyInput.value) + 1;
                    updateModalPrice();
                });
                
                 document.getElementById('modal-quantity-minus').addEventListener('click', () => {
                    const qtyInput = document.getElementById('modal-quantity');
                    let currentQty = parseInt(qtyInput.value);
                    if (currentQty > 1) {
                         qtyInput.value = currentQty - 1;
                         updateModalPrice();
                    }
                });

                // Initial price calculation
                updateModalPrice();
                addToCartBtn.disabled = false;
                
                // Update "Add to Cart" button action
                addToCartBtn.onclick = function() {
                    const params = new URLSearchParams({
                        productId: productId,
                        quantity: document.getElementById('modal-quantity').value,
                        size: modalContent.querySelector('input[name="size"]:checked').value,
                        ...Object.fromEntries(Array.from(modalContent.querySelectorAll('input[name="topping"]:checked')).map(cb => ['topping', cb.value]))
                    }).toString();
                    
                    handleAddToCartRequest(params);
                    const modalInstance = bootstrap.Modal.getInstance(productModal);
                    modalInstance.hide();
                };
            });
        });

        function updateModalPrice() {
            const basePrice = parseFloat(document.getElementById('modalBasePrice').dataset.price) || 0;
            const sizeAdj = parseFloat(modalContent.querySelector('input[name="size"]:checked')?.dataset.priceAdj) || 0;
            let toppingsPrice = 0;
            modalContent.querySelectorAll('input[name="topping"]:checked').forEach(cb => {
                toppingsPrice += parseFloat(cb.dataset.price) || 0;
            });
            const quantity = parseInt(document.getElementById('modal-quantity').value) || 1;
            
            const finalPrice = (basePrice + sizeAdj + toppingsPrice) * quantity;
            document.getElementById('modalAddToCartBtn').textContent = `Thêm vào giỏ - ${currencyFormatter.format(finalPrice)}`;
        }
    }

    // --- CART LOGIC ---
    function handleAddToCartRequest(params) {
        post(contextPath + '/cart/add', params, (err, data) => {
            if (err) { showToast('Đã có lỗi xảy ra.', true); console.error(err); return; }
            if (data && data.redirect) { window.location.href = data.redirect; return; }
            if (data && data.ok) {
                showToast('Đã thêm sản phẩm vào giỏ!');
                document.getElementById('cart-item-count').textContent = data.cartSize;
            } else {
                showToast((data && data.message) || 'Thêm giỏ hàng thất bại', true);
            }
        });
    }

})();