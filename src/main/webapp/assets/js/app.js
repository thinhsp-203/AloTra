(function () {
    'use strict';

    var body = document.body || document.getElementsByTagName('body')[0];
    var contextPath = (body && body.getAttribute('data-context-path')) || '';

    function postForm(url, params, cb) {
        var xhr = new XMLHttpRequest();
        xhr.open('POST', url, true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                if (xhr.status >= 200 && xhr.status < 300) {
                    try { cb(null, JSON.parse(xhr.responseText)); } catch (e) { cb(e); }
                } else {
                    cb(new Error('HTTP ' + xhr.status));
                }
            }
        };
        xhr.send(params);
    }

    document.addEventListener('DOMContentLoaded', function() {
        // Voucher auto-apply logic
        const codeInput = document.getElementById('voucher-code');
        if (codeInput) {
            let debounceTimeout;
            codeInput.addEventListener('input', function() {
                clearTimeout(debounceTimeout);
                const code = codeInput.value.trim();
                const messageDiv = document.getElementById('voucher-message');
                const discountDisplay = document.getElementById('discount-display');
                const totalDisplay = document.getElementById('grand-total-display');
                const subtotalDisplay = document.getElementById('subtotal-display');

                if (!code) {
                    messageDiv.textContent = '';
                    discountDisplay.textContent = '0₫';
                    totalDisplay.textContent = subtotalDisplay.textContent;
                    return;
                }

                debounceTimeout = setTimeout(() => {
                    messageDiv.className = 'mt-2 small text-muted';
                    messageDiv.textContent = 'Đang kiểm tra mã...';
                    postForm(contextPath + '/api/voucher/apply', 'code=' + encodeURIComponent(code), function(err, data) {
                        if (err || !data) {
                            messageDiv.className = 'mt-2 small text-danger';
                            messageDiv.textContent = 'Có lỗi xảy ra, vui lòng thử lại.';
                            return;
                        }
                        if (data.ok) {
                            messageDiv.className = 'mt-2 small text-success';
                            discountDisplay.textContent = data.discountFormatted;
                            totalDisplay.textContent = data.newTotalFormatted;
                        } else {
                            messageDiv.className = 'mt-2 small text-danger';
                            discountDisplay.textContent = '0₫';
                            totalDisplay.textContent = subtotalDisplay.textContent;
                        }
                        messageDiv.textContent = data.message;
                    });
                }, 500);
            });
        }
        
        // Add to cart from detail page form
        const form = document.getElementById('addToCartForm');
        if (form) {
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                const formData = new FormData(form);
                const params = new URLSearchParams(formData).toString();
                handleAddToCartRequest(params);
            });
        }
    });

    // Add to cart from list page buttons
    window.addToCart = function (btn) {
        try {
            var id = btn.getAttribute('data-id');
            handleAddToCartRequest('productId=' + encodeURIComponent(id));
        } catch (e) { console.error(e); showToast('Có lỗi khi thêm vào giỏ'); }
    };

    function handleAddToCartRequest(params) {
        postForm(contextPath + '/cart/add', params, function (err, data) {
            if (err) { showToast('Đã có lỗi xảy ra.'); console.error(err); return; }
            if (data && data.redirect) { window.location.href = data.redirect; return; }
            if (data && data.ok) {
                showToast('Đã thêm sản phẩm vào giỏ!');
                updateCartUI(data.cartSize, data.newItem);
            } else {
                showToast((data && data.message) || 'Thêm giỏ hàng thất bại');
            }
        });
    }

    function updateCartUI(cartSize, newItem) {
        var cartCount = document.getElementById('cart-item-count');
        if (cartCount) { cartCount.textContent = cartSize; }
        var listContainer = document.getElementById('cart-item-list');
        if (!listContainer) return;
        var emptyMsg = listContainer.querySelector('.empty-cart-message');
        if (emptyMsg) { listContainer.innerHTML = ''; }
        if (newItem) {
            var formattedPrice = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(newItem.lineTotal);
            var itemHtml = '<li><a class="dropdown-item cart-dropdown-item" href="' + contextPath + '/p?id=' + newItem.productId + '"><img src="' + newItem.thumbnail + '" alt="' + newItem.productName + '"><div class="cart-dropdown-item-info"><span class="cart-dropdown-item-name">' + newItem.productName + '</span><strong class="text-primary">' + formattedPrice + '</strong></div></a></li>';
            listContainer.insertAdjacentHTML('afterbegin', itemHtml);
        }
        var dropdownFooter = document.querySelector('.cart-dropdown-footer span');
        if (dropdownFooter) { dropdownFooter.textContent = cartSize + ' sản phẩm trong giỏ'; }
    }
    
    function showToast(message) {
        var toastLiveExample = document.getElementById('liveToast');
        if (toastLiveExample) {
            var toastBody = toastLiveExample.querySelector('.toast-body');
            if (toastBody) { toastBody.textContent = message; }
            var toast = new bootstrap.Toast(toastLiveExample);
            toast.show();
        }
    }
})();