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
        toastHeader.innerHTML = isError ? '<i class="bi bi-exclamation-triangle-fill"></i> Lỗi' : '<i class="bi bi-check-circle-fill"></i> Thành công';
        const toast = new bootstrap.Toast(toastEl);
        toast.show();
    }

    const currencyFormatter = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' });

    function escapeHtml(text) {
        const map = {'&': '&amp;','<': '&lt;','>': '&gt;','"': '&quot;',"'": '&#039;'};
        return text ? text.replace(/[&<>"']/g, m => map[m]) : '';
    }

	// --- PRODUCT MODAL LOGIC ---
	    const productModal = document.getElementById('productModal');
	    if (productModal) {
	        let modalData = null; 
	        
	        productModal.addEventListener('show.bs.modal', function (event) {
	            const button = event.relatedTarget;
	            const productId = button.dataset.productId;
	            const modalContent = document.getElementById('productModalContent');
	            const addToCartBtn = document.getElementById('modalAddToCartBtn');

	            modalContent.innerHTML = '<div class="col-12 text-center my-5"><div class="spinner-border text-primary" style="width: 3rem; height: 3rem;"></div><p class="mt-2">Đang tải...</p></div>';
	            addToCartBtn.disabled = true;

	            get(`${contextPath}/api/product-details?id=${productId}`, (err, data) => {
	                if (err || !data || !data.ok) {
	                    modalContent.innerHTML = '<p class="text-danger">Không thể tải thông tin sản phẩm.</p>';
	                    return;
	                }
	                
	                modalData = data; 
	                
	                let sizesHtml = data.sizes.map((s, index) => `
	                    <div class="col-auto">
	                        <input type="radio" class="btn-check" name="size" id="modal-size-${index}" value="${escapeHtml(s.name)}" data-price-adj="${s.priceAdjustment}" ${s.priceAdjustment == 0 ? 'checked' : ''}>
	                        <label class="btn btn-outline-primary btn-size" for="modal-size-${index}">
	                            ${escapeHtml(s.name)}
	                            <div class="small fw-normal">${s.priceAdjustment == 0 ? '0 đ' : (s.priceAdjustment > 0 ? '+' : '') + new Intl.NumberFormat('vi-VN').format(s.priceAdjustment) + ' đ'}</div>
	                        </label>
	                    </div>
	                `).join('');

	                // === START FIX: Conditionally render toppings ===
	                let toppingsHtml = '';
	                if (data.toppings && data.toppings.length > 0) {
	                    const toppingItems = data.toppings.map(t => `
	                         <div class="d-flex justify-content-between align-items-center mb-2">
	                            <div>${escapeHtml(t.name)} <small class="text-muted">(+${currencyFormatter.format(t.price)})</small></div>
	                            <div class="input-group" style="width: 100px;">
	                               <button class="btn btn-outline-secondary btn-sm" type="button" onclick="updateModalToppingQty(${t.id}, -1)">-</button>
	                               <input type="text" class="form-control form-control-sm text-center" value="0" readonly id="modal-topping-qty-${t.id}" data-topping-id="${t.id}" data-price="${t.price}">
	                               <button class="btn btn-outline-secondary btn-sm" type="button" onclick="updateModalToppingQty(${t.id}, 1)">+</button>
	                            </div>
	                        </div>
	                    `).join('');
	                    toppingsHtml = `<div class="mb-3"><h6>Chọn Topping</h6><div id="modalToppingsList">${toppingItems}</div></div>`;
	                }
	                // === END FIX ===
	                
	                const createOptionGroup = (title, name, options) => `
	                    <div class="mb-3">
	                        <h6>${title}</h6>
	                        <div class="row row-cols-3 g-2">
	                            ${options.map((opt, index) => `
	                                <div class="col">
	                                    <input type="radio" class="btn-check" name="${name}" id="modal-${name}-${index}" value="${opt}" ${index === 1 ? 'checked' : ''}>
	                                    <label class="btn btn-outline-primary w-100" for="modal-${name}-${index}">${opt}</label>
	                                </div>
	                            `).join('')}
	                        </div>
	                    </div>
	                `;
	                
	                let teaOptionsHtml = '';
	                if (data.product.categoryName && data.product.categoryName.toLowerCase().includes('trà')) {
	                    teaOptionsHtml = createOptionGroup('Mức trà', 'tea', ['Ít', 'Bình thường', 'Nhiều']);
	                }

	                modalContent.innerHTML = `
	                    <div class="col-md-5">
	                        <img src="${escapeHtml(data.product.thumbnail)}" class="img-fluid rounded" alt="${escapeHtml(data.product.name)}">
	                    </div>
	                    <div class="col-md-7">
	                        <h4 id="modalProductName">${escapeHtml(data.product.name)}</h4>
	                        <p class="h5 text-primary fw-bold" id="modalBasePrice" data-price="${data.product.basePrice}">${currencyFormatter.format(data.product.basePrice)}</p>
	                        <hr>
	                        ${data.sizes.length > 1 ? `<div class="mb-3"><h6>Chọn kích cỡ</h6><div class="row g-2">${sizesHtml}</div></div>` : ''}
	                        ${createOptionGroup('Độ ngọt', 'sweetness', ['Ít', 'Bình thường', 'Nhiều'])}
	                        ${teaOptionsHtml}
	                        ${createOptionGroup('Mức đá', 'ice', ['Ít', 'Bình thường', 'Nhiều'])}
	                        ${toppingsHtml}
	                    </div>
	                `;

                modalContent.querySelectorAll('input[type="radio"]').forEach(input => input.addEventListener('change', updateModalPrice));
                window.updateModalToppingQty = (id, change) => {
                    const qtyInput = document.getElementById(`modal-topping-qty-${id}`);
                    let currentQty = parseInt(qtyInput.value) + change;
                    if (currentQty >= 0) {
                        qtyInput.value = currentQty;
                        updateModalPrice();
                    }
                };
                
                updateModalPrice();
                addToCartBtn.disabled = false;
                
                addToCartBtn.onclick = function() {
                    const params = new URLSearchParams({ productId: productId, quantity: 1 });
                    const sizeInput = modalContent.querySelector('input[name="size"]:checked');
                    if (sizeInput) params.append('size', sizeInput.value);
                    const toppings = [];
                    modalContent.querySelectorAll('input[data-topping-id]').forEach(input => {
                        const qty = parseInt(input.value);
                        if(qty > 0) toppings.push(`${input.dataset.toppingId}:${qty}`);
                    });
                    if (toppings.length > 0) params.append('topping', toppings.join(','));
                    handleAddToCartRequest(params.toString());
                    bootstrap.Modal.getInstance(productModal).hide();
                };
            });
        });

        function updateModalPrice() {
            if (!modalData) return;
            const basePrice = parseFloat(document.getElementById('modalBasePrice').dataset.price) || 0;
            const sizeInput = document.querySelector('input[name="size"]:checked');
            const sizeAdj = sizeInput ? (parseFloat(sizeInput.dataset.priceAdj) || 0) : 0;
            let toppingsPrice = 0;
            document.querySelectorAll('#modalToppingsList input[data-topping-id]').forEach(input => {
                toppingsPrice += (parseFloat(input.dataset.price) || 0) * (parseInt(input.value) || 0);
            });
            const finalPrice = basePrice + sizeAdj + toppingsPrice;
            document.getElementById('modalAddToCartBtn').textContent = `Thêm vào giỏ - ${currencyFormatter.format(finalPrice)}`;
        }
    }

    // --- CART LOGIC ---
    function handleAddToCartRequest(params) {
        post(contextPath + '/cart/add', params, (err, data) => {
            if (err) { 
                showToast('Đã có lỗi xảy ra.', true); 
                return; 
            }
            if (data && data.redirect) { 
                window.location.href = data.redirect; 
                return; 
            }
            if (data && data.ok) {
                showToast(`Đã thêm "${data.newItem.productName}" vào giỏ!`);
                const cartCountEl = document.getElementById('cart-item-count');
                if (cartCountEl) {
                    cartCountEl.textContent = data.cartSize;
                }
            } else {
                showToast((data && data.message) || 'Thêm giỏ hàng thất bại', true);
            }
        });
    }

    // --- PRODUCT DETAIL PAGE LOGIC ---
    function initializeProductDetailPage() {
        const container = document.getElementById('product-detail-container');
        if (!container) return; 

        const productId = container.dataset.productId;
        const optionsContainer = document.getElementById('product-options');
        const addToCartBtn = document.getElementById('detailAddToCartBtn');

        get(`${contextPath}/api/product-details?id=${productId}`, (err, data) => {
            if (err || !data || !data.ok) {
                optionsContainer.innerHTML = '<p class="text-danger">Không thể tải thông tin sản phẩm.</p>';
                return;
            }

            let sizesHtml = data.sizes.map((s, index) => `
                <div class="col-auto">
                    <input type="radio" class="btn-check" name="size" id="detail-size-${index}" value="${escapeHtml(s.name)}" data-price-adj="${s.priceAdjustment}" ${index === 0 ? 'checked' : ''}>
                    <label class="btn btn-outline-primary btn-size" for="detail-size-${index}">
                        ${escapeHtml(s.name)}
                        <div class="small fw-normal">${s.priceAdjustment >= 0 ? '+' : ''}${currencyFormatter.format(s.priceAdjustment)}</div>
                    </label>
                </div>
            `).join('');

            let toppingsHtml = data.toppings.map(t => `
                 <div class="d-flex justify-content-between align-items-center mb-3 option-item">
                    <div>${escapeHtml(t.name)} <br> <small class="text-primary fw-bold">+${currencyFormatter.format(t.price)}</small></div>
                    <div class="input-group" style="width: 120px;">
                       <button class="btn btn-outline-secondary" type="button" onclick="updateDetailToppingQty(${t.id}, -1)">-</button>
                       <input type="text" class="form-control text-center" value="0" readonly id="detail-topping-qty-${t.id}" data-topping-id="${t.id}" data-price="${t.price}">
                       <button class="btn btn-outline-secondary" type="button" onclick="updateDetailToppingQty(${t.id}, 1)">+</button>
                    </div>
                </div>
            `).join('');
            
            const createOptionGroup = (title, name, options) => `
                <div class="mb-4 option-group">
                    <h6>${title}</h6>
                    <div class="d-flex gap-2">
                        ${options.map((opt, index) => `
                            <div>
                                <input type="radio" class="btn-check" name="${name}" id="detail-${name}-${index}" value="${opt}" ${index === 1 ? 'checked' : ''}>
                                <label class="btn btn-outline-primary" for="detail-${name}-${index}">${opt}</label>
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
            
            let teaOptionsHtml = '';
            if (data.product.categoryName && data.product.categoryName.toLowerCase().includes('trà')) {
                teaOptionsHtml = createOptionGroup('Mức trà', 'tea', ['Ít', 'Bình thường', 'Nhiều']);
            }

            optionsContainer.innerHTML = `
                ${data.sizes.length > 1 ? `<div class="mb-4 option-group"><h6>Chọn kích cỡ</h6><div class="row g-2">${sizesHtml}</div></div>` : ''}
                ${createOptionGroup('Mức đá', 'ice', ['Ít', 'Bình thường', 'Nhiều'])}
                ${createOptionGroup('Độ ngọt', 'sweetness', ['Ít', 'Bình thường', 'Nhiều', 'Không'])}
                ${teaOptionsHtml}
                ${toppingsHtml ? `<div class="mb-4 option-group"><h6>Chọn Topping</h6>${toppingsHtml}</div>` : ''}
            `;

            optionsContainer.querySelectorAll('input, button').forEach(el => {
                el.addEventListener('change', updateDetailPagePrice);
                el.addEventListener('click', updateDetailPagePrice);
            });
            
            window.updateDetailToppingQty = (id, change) => {
                const qtyInput = document.getElementById(`detail-topping-qty-${id}`);
                let currentQty = parseInt(qtyInput.value) + change;
                if (currentQty >= 0) {
                    qtyInput.value = currentQty;
                    updateDetailPagePrice();
                }
            };
            
            updateDetailPagePrice();
            addToCartBtn.disabled = false;
            
            addToCartBtn.onclick = function() {
                const params = new URLSearchParams({ productId: productId, quantity: 1 });
                const sizeInput = optionsContainer.querySelector('input[name="size"]:checked');
                if (sizeInput) params.append('size', sizeInput.value);
                const toppings = [];
                optionsContainer.querySelectorAll('input[data-topping-id]').forEach(input => {
                    const qty = parseInt(input.value);
                    if(qty > 0) toppings.push(`${input.dataset.toppingId}:${qty}`);
                });
                if (toppings.length > 0) params.append('topping', toppings.join(','));
                handleAddToCartRequest(params.toString());
            };
        });
    }

    function updateDetailPagePrice() {
        const basePrice = parseFloat(document.getElementById('product-base-price').dataset.price) || 0;
        const sizeInput = document.querySelector('#product-options input[name="size"]:checked');
        const sizeAdj = sizeInput ? (parseFloat(sizeInput.dataset.priceAdj) || 0) : 0;
        let toppingsPrice = 0;
        document.querySelectorAll('#product-options input[data-topping-id]').forEach(input => {
            toppingsPrice += (parseFloat(input.dataset.price) || 0) * (parseInt(input.value) || 0);
        });
        const finalPrice = basePrice + sizeAdj + toppingsPrice;
        document.getElementById('detailAddToCartBtn').textContent = `Thêm vào giỏ hàng : ${currencyFormatter.format(finalPrice)}`;
    }

    // --- SUGGESTION SLIDER (SWIPER) ---
    function initializeSwiperSlider() {
        if (typeof Swiper === 'undefined') return; // Kiểm tra Swiper đã tải chưa
        const swiperContainer = document.querySelector('.suggestion-swiper');
        if (!swiperContainer) return;

        new Swiper(swiperContainer, {
            loop: true,
            slidesPerView: 2,
            spaceBetween: 10,
            autoplay: {
                delay: 4000,
                disableOnInteraction: false,
            },
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev',
            },
            breakpoints: {
                768: {
                    slidesPerView: 3,
                    spaceBetween: 20,
                },
                992: {
                    slidesPerView: 5,
                    spaceBetween: 20,
                },
            },
        });
    }

    // --- OTHER LOGIC ---
    window.showMoreHomepage = function(type, button) {
        const container = button.previousElementSibling;
        const items = container.querySelectorAll(`.hidden-${type}-item`);
        items.forEach(item => {
            item.style.display = 'block';
            item.classList.add('animate__animated', 'animate__fadeIn');
        });
        button.style.display = 'none';
    }

    // --- INITIALIZATION ---
    document.addEventListener("DOMContentLoaded", function(){
        // Menu dropdown hover
        const dropdowns = document.querySelectorAll('.main-nav .dropdown');
        dropdowns.forEach(function(dropdown) {
            dropdown.addEventListener('mouseenter', function () {
                let dropdownInstance = new bootstrap.Dropdown(this.querySelector('.dropdown-toggle'));
                dropdownInstance.show();
            });
            dropdown.addEventListener('mouseleave', function () {
                let dropdownInstance = new bootstrap.Dropdown(this.querySelector('.dropdown-toggle'));
                dropdownInstance.hide();
            });
        });
        
        // Chạy logic cho trang danh sách sản phẩm
        if (document.getElementById('grid')) {
            var page = 0;
            var isLoading = false;
            var hasMore = true;
            var searchKeyword = document.body.dataset.searchKeyword || "";
            var selectedCate = document.body.dataset.selectedCate || "";
            
            window.loadMore = function() {
                if (isLoading || !hasMore) return;
                isLoading = true;
                const loadingEl = document.getElementById('loading');
                const btnLoadMoreEl = document.getElementById('btnLoadMore');
                if(loadingEl) loadingEl.style.display = 'block';
                if(btnLoadMoreEl) btnLoadMoreEl.style.display = 'none';
                
                const params = new URLSearchParams({ page: page, size: 12 });
                if (searchKeyword) params.append('q', searchKeyword);
                if (selectedCate) params.append('cate', selectedCate);
                
                get(`${contextPath}/products/page?${params}`, (err, data) => {
                    isLoading = false;
                    if(loadingEl) loadingEl.style.display = 'none';
                    if (err || !data) {
                        showToast('Không thể tải sản phẩm', true);
                        return;
                    }
                    
                    const grid = document.getElementById('grid');
                    data.items.forEach(item => {
                        const card = `<div class="col">
                            <a href="${contextPath}/p?id=${item.id}" class="card-link">
                                <div class="card h-100 product-card">
                                    <div class="card-img-container">
                                        <img class="card-img-top" src="${escapeHtml(item.thumb)}" alt="${escapeHtml(item.name)}">
                                    </div>
                                    <div class="card-body d-flex flex-column">
                                        <h6 class="card-title flex-grow-1">${escapeHtml(item.name)}</h6>
                                        <p class="card-text fw-bold text-primary">${currencyFormatter.format(item.price)}</p>
                                    </div>
                                </div>
                            </a>
                            <div class="card-footer bg-transparent border-0">
                                <button class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#productModal" data-product-id="${item.id}">
                                    Đặt mua
                                </button>
                            </div>
                        </div>`;
                        grid.insertAdjacentHTML('beforeend', card);
                    });
                    
                    hasMore = data.hasMore;
                    page++;
                    if (hasMore && btnLoadMoreEl) {
                        btnLoadMoreEl.style.display = 'block';
                    }
                });
            };
            loadMore(); // Tải lần đầu
        }

        // Chạy logic cho trang chi tiết sản phẩm
        if (document.getElementById('product-detail-container')) {
            initializeProductDetailPage();
            initializeSwiperSlider();
        }

        // Chạy logic cho trang checkout (voucher)
        const voucherInput = document.getElementById('voucher-code');
        if(voucherInput) {
            voucherInput.addEventListener('change', function() {
                const code = this.value.trim();
                const msgEl = document.getElementById('voucher-message');
                const discountEl = document.getElementById('discount-display');
                const totalEl = document.getElementById('grand-total-display');
                const subtotalEl = document.getElementById('subtotal-display');

                if(!code) {
                  msgEl.innerHTML = '';
                  discountEl.textContent = '0₫';
                  totalEl.textContent = subtotalEl.textContent;
                  return;
                }
                post(contextPath + '/api/voucher/apply', `code=${encodeURIComponent(code)}`, (err, data) => {
                  if(err || !data) {
                    msgEl.className = 'mt-2 small text-danger';
                    msgEl.textContent = 'Lỗi kết nối.';
                    return;
                  }
                  if(data.ok) {
                    msgEl.className = 'mt-2 small text-success';
                    msgEl.textContent = data.message;
                    discountEl.textContent = `-${data.discountFormatted}`;
                    totalEl.textContent = data.newTotalFormatted;
                  } else {
                    msgEl.className = 'mt-2 small text-danger';
                    msgEl.textContent = data.message;
                    discountEl.textContent = '0₫';
                    totalEl.textContent = subtotalEl.textContent;
                  }
                });
            });
        }
    });
})();