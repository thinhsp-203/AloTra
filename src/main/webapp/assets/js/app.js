// Hàm hiển thị modal yêu cầu đăng nhập (toàn cục)
function showLoginRequiredModal() {
    const loginModalEl = document.getElementById('loginRequiredModal');
    if (loginModalEl) {
        const loginModal = new bootstrap.Modal(loginModalEl);
        loginModal.show();
    }
}

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
					    <div class="row g-4">
					        <div class="col-md-5">
					            <img src="${escapeHtml(data.product.thumbnail)}" 
					                 class="img-fluid rounded" 
					                 alt="${escapeHtml(data.product.name)}"
					                 style="position: sticky; top: 0;">
					        </div>
					        <div class="col-md-7">
					            <div style="max-height: calc(80vh - 250px); overflow-y: auto; padding-right: 10px;">
					                <div class="d-flex justify-content-between align-items-start mb-2">
					                    <h4 id="modalProductName">${escapeHtml(data.product.name)}</h4>
					                    <button class="btn btn-outline-danger btn-sm btn-wishlist ms-2" 
					                            data-product-id="${data.product.id}"
					                            title="Thêm vào yêu thích"
					                            type="button">
					                        <i class="bi bi-heart"></i>
					                    </button>
					                </div>
					                <p class="h5 text-primary fw-bold mb-4" id="modalBasePrice" data-price="${data.product.basePrice}">
					                    ${currencyFormatter.format(data.product.basePrice)}
					                </p>
					                <hr>
					                ${data.sizes.length > 1 ? `
					                    <div class="mb-3 option-group">
					                        <h6>Chọn kích cỡ</h6>
					                        <div class="row g-2">${sizesHtml}</div>
					                    </div>
					                ` : ''}
					                ${createOptionGroup('Độ ngọt', 'sweetness', ['Ít', 'Bình thường', 'Nhiều'])}
					                ${teaOptionsHtml}
					                ${createOptionGroup('Mức đá', 'ice', ['Ít', 'Bình thường', 'Nhiều'])}
					                ${toppingsHtml}
					            </div>
					        </div>
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
                // Thay vì redirect ngay, hiển thị modal yêu cầu đăng nhập
                showLoginRequiredModal();
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
    
    // Function để show thêm sản phẩm ở trang product list (theo logic của trang home)
    window.showMoreProducts = function(button) {
        const container = document.getElementById('productsContainer');
        const items = container.querySelectorAll('.hidden-product-item');
        items.forEach(item => {
            item.style.display = 'block';
            item.classList.add('animate__animated', 'animate__fadeIn');
        });
        button.style.display = 'none';
        
        // Update wishlist hearts sau khi show thêm sản phẩm
        if (typeof updateWishlistHearts === 'function') {
            updateWishlistHearts();
        }
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
        
		// --- PRODUCT LIST PAGE LOGIC ---
		if (document.getElementById('grid')) {
		    var page = 0;
		    var isLoading = false;
		    var hasMore = true;
		    var searchKeyword = "";
		    var selectedCate = "";
		    
		    // Lấy giá trị từ biến JavaScript được set bởi JSP
		    var scripts = document.getElementsByTagName('script');
		    for (var i = 0; i < scripts.length; i++) {
		        var scriptContent = scripts[i].textContent || scripts[i].innerText;
		        if (scriptContent.indexOf('searchKeyword') > -1) {
		            var match = scriptContent.match(/searchKeyword\s*=\s*"([^"]*)"/);
		            if (match) searchKeyword = match[1];
		            match = scriptContent.match(/selectedCate\s*=\s*"([^"]*)"/);
		            if (match) selectedCate = match[1];
		            break;
		        }
		    }
		    
		    console.log("Loaded - Search:", searchKeyword, "Category:", selectedCate);
		    
		    window.loadMore = function() {
		        if (isLoading || !hasMore) return;
		        isLoading = true;
		        
		        const loadingEl = document.getElementById('loading');
		        const btnLoadMoreEl = document.getElementById('btnLoadMore');
		        const noResultsEl = document.getElementById('no-results');
		        const gridEl = document.getElementById('grid');
		        
		        if(loadingEl) loadingEl.style.display = 'block';
		        if(btnLoadMoreEl) btnLoadMoreEl.style.display = 'none';
		        if(noResultsEl) noResultsEl.style.display = 'none';
		        
		        const params = new URLSearchParams({ page: page, size: 12 });
		        if (searchKeyword && searchKeyword.trim() !== "") {
		            params.append('q', searchKeyword.trim());
		        }
		        if (selectedCate && selectedCate.trim() !== "") {
		            params.append('cate', selectedCate.trim());
		        }
		        
		        console.log("Request URL:", contextPath + '/products/page?' + params.toString());
		        
		        get(contextPath + '/products/page?' + params.toString(), (err, data) => {
		            isLoading = false;
		            if(loadingEl) loadingEl.style.display = 'none';
		            
		            if (err || !data) {
		                console.error("Load error:", err);
		                showToast('Không thể tải sản phẩm', true);
		                return;
		            }
		            
		            console.log("Loaded data:", data);
		            
		            // Update search result count
		            const resultCountEl = document.getElementById('search-result-count');
		            if (resultCountEl && page === 0) {
		                resultCountEl.textContent = 'Tìm thấy ' + data.total + ' sản phẩm';
		            }
		            
		            // Show no results message
		            if (data.total === 0 && page === 0) {
		                if(noResultsEl) {
		                    const keywordEl = document.getElementById('no-results-keyword');
		                    if (keywordEl && searchKeyword) {
		                        keywordEl.textContent = searchKeyword;
		                    }
		                    noResultsEl.style.display = 'block';
		                }
		                return;
		            }
		            
					// Render products
					data.items.forEach(item => {
					    const card = `
					    <div class="col">
					        <div class="card h-100 product-card">
					            <a href="${contextPath}/p?id=${item.id}" class="card-link text-decoration-none text-dark">
					                <div class="card-img-container">
					                    <img class="card-img-top" src="${escapeHtml(item.thumb)}" alt="${escapeHtml(item.name)}">
					                </div>
					                <div class="card-body d-flex flex-column text-center">
					                    <h6 class="card-title">${escapeHtml(item.name)}</h6>
					                    <p class="card-text fw-bold text-primary mt-auto">
					                        ${currencyFormatter.format(item.price)}
					                    </p>
					                </div>
					            </a>
					            <div class="card-footer bg-transparent border-0 pb-3">
					                <button class="btn btn-primary w-100" 
					                        data-bs-toggle="modal" 
					                        data-bs-target="#productModal" 
					                        data-product-id="${item.id}">
					                    Đặt mua
					                </button>
					            </div>
					        </div>
					    </div>`;
					    gridEl.insertAdjacentHTML('beforeend', card);
					});
		            hasMore = data.hasMore;
		            page++;
		            
		            if (hasMore && btnLoadMoreEl) {
		                btnLoadMoreEl.style.display = 'block';
		            }
		        });
		    };
		    
		    loadMore(); // Initial load
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
	// --- SEARCH AUTOCOMPLETE ---
	(function() {
	    const searchInput = document.getElementById('searchInput');
	    const searchSuggestions = document.getElementById('searchSuggestions');
	    const suggestionsList = document.getElementById('suggestionsList');
	    
	    if (!searchInput || !searchSuggestions) return;
	    
	    // Load search history from localStorage
	    function getSearchHistory() {
	        const history = localStorage.getItem('searchHistory');
	        return history ? JSON.parse(history) : [];
	    }
	    
	    // Save search to history
	    function saveToHistory(query) {
	        if (!query || query.trim() === '') return;
	        
	        let history = getSearchHistory();
	        query = query.trim();
	        
	        // Remove if already exists
	        history = history.filter(item => item !== query);
	        
	        // Add to beginning
	        history.unshift(query);
	        
	        // Keep only last 10 searches
	        history = history.slice(0, 10);
	        
	        localStorage.setItem('searchHistory', JSON.stringify(history));
	    }
	    
	    // Clear search history
	    window.clearSearchHistory = function() {
	        localStorage.removeItem('searchHistory');
	        renderSuggestions([]);
	    };
	    
	    // Render suggestions
	    function renderSuggestions(history) {
	        if (history.length === 0) {
	            suggestionsList.innerHTML = '<div class="suggestion-item text-muted"><i class="bi bi-info-circle"></i><span class="suggestion-text">Chưa có lịch sử tìm kiếm</span></div>';
	            return;
	        }
	        
	        suggestionsList.innerHTML = history.map(item => `
	            <div class="suggestion-item" onclick="selectSuggestion('${item.replace(/'/g, "\\'")}')">
	                <i class="bi bi-clock-history"></i>
	                <span class="suggestion-text">${escapeHtml(item)}</span>
	            </div>
	        `).join('');
	    }
	    
	    // Select suggestion
	    window.selectSuggestion = function(query) {
	        searchInput.value = query;
	        searchSuggestions.classList.remove('show');
	        searchInput.form.submit();
	    };
	    
	    // Show suggestions on focus
	    searchInput.addEventListener('focus', function() {
	        const history = getSearchHistory();
	        renderSuggestions(history);
	        searchSuggestions.classList.add('show');
	    });
	    
	    // Hide suggestions on blur (with delay for click events)
	    searchInput.addEventListener('blur', function() {
	        setTimeout(() => {
	            searchSuggestions.classList.remove('show');
	        }, 200);
	    });
	    
	    // Filter suggestions as user types
	    searchInput.addEventListener('input', function() {
	        const query = this.value.toLowerCase();
	        const history = getSearchHistory();
	        
	        if (query === '') {
	            renderSuggestions(history);
	        } else {
	            const filtered = history.filter(item => item.toLowerCase().includes(query));
	            renderSuggestions(filtered);
	        }
	    });
	    
	    // Save search on form submit
	    searchInput.form.addEventListener('submit', function() {
	        const query = searchInput.value.trim();
	        if (query !== '') {
	            saveToHistory(query);
	        }
	    });
	    
	    // Close suggestions when clicking outside
	    document.addEventListener('click', function(e) {
	        if (!searchInput.contains(e.target) && !searchSuggestions.contains(e.target)) {
	            searchSuggestions.classList.remove('show');
	        }
	    });
	    
	    // Helper function
	    function escapeHtml(text) {
	        const map = {'&': '&amp;','<': '&lt;','>': '&gt;','"': '&quot;',"'": '&#039;'};
	        return text ? text.replace(/[&<>"']/g, m => map[m]) : '';
	    }
	})
})();
/*
 * ========================================
 * SB ADMIN 2 - JAVASCRIPT
 * ========================================
 */
document.addEventListener("DOMContentLoaded", function() {
    
    // 1. Sidebar Toggle (Nút gạch ở Topbar)
    const sidebarToggleTop = document.getElementById('sidebarToggleTop');
    if (sidebarToggleTop) {
        sidebarToggleTop.addEventListener('click', function(e) {
            e.preventDefault();
            document.body.classList.toggle('sidebar-toggled');
            document.getElementById('accordionSidebar').classList.toggle('toggled');
        });
    }

    // 2. Sidebar Toggle (Nút mũi tên ở Sidebar)
    const sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function(e) {
            e.preventDefault();
            document.body.classList.toggle('sidebar-toggled');
            document.getElementById('accordionSidebar').classList.toggle('toggled');
        });
    }

    // 3. Scroll to Top Button
    const scrollTopButton = document.querySelector('.scroll-to-top');
    const mainContent = document.querySelector('.main-content'); // Vùng cuộn chính
    
    if (scrollTopButton && mainContent) {
        // Hiển thị nút khi cuộn
        mainContent.addEventListener('scroll', function() {
            if (mainContent.scrollTop > 100) {
                scrollTopButton.style.display = 'block';
            } else {
                scrollTopButton.style.display = 'none';
            }
        });

        // Click để cuộn lên
        scrollTopButton.addEventListener('click', function(e) {
            e.preventDefault();
            mainContent.scrollTo({
                top: 0,
                behavior: 'smooth'
            });
        });
    }

});
/*
 * ========================================
 * LOGIC WISHLIST (MỚI)
 * ========================================
 */
document.addEventListener("DOMContentLoaded", function() {
    const contextPath = document.body.dataset.contextPath || '';
    
    // 1. Tô màu các nút đã "thích" khi tải trang
    function updateWishlistHearts() {
        const contextPathForWishlist = document.body.dataset.contextPath || '';
        fetch(`${contextPathForWishlist}/api/wishlist/ids`)
            .then(resp => resp.json())
            .then(data => {
                const wishlistIds = data.wishlistIds || [];
                document.querySelectorAll('.btn-wishlist').forEach(btn => {
                    const productId = btn.dataset.productId;
                    const icon = btn.querySelector('i');
                    if (productId && wishlistIds.includes(parseInt(productId))) {
                        btn.classList.add('active');
                        if (icon && icon.classList.contains('bi-heart')) {
                            icon.classList.remove('bi-heart');
                            icon.classList.add('bi-heart-fill');
                        }
                    } else {
                        btn.classList.remove('active');
                        if (icon && icon.classList.contains('bi-heart-fill')) {
                            icon.classList.remove('bi-heart-fill');
                            icon.classList.add('bi-heart');
                        }
                    }
                });
            })
            .catch(err => console.error("Lỗi khi lấy ID wishlist:", err));
    }
    
    // Export hàm ra global scope để có thể gọi từ jQuery
    window.updateWishlistHearts = updateWishlistHearts;
    
    updateWishlistHearts(); // Chạy khi tải trang

    // 2. Gắn sự kiện click cho TẤT CẢ các nút wishlist (kể cả modal)
    // Dùng event delegation
    document.body.addEventListener('click', function(e) {
        // Tìm nút .btn-wishlist gần nhất mà user click
        const wishlistBtn = e.target.closest('.btn-wishlist');
        
        if (wishlistBtn) {
            e.preventDefault(); // Ngăn hành vi mặc định
            e.stopPropagation(); // Ngăn các sự kiện khác
            
            const productId = wishlistBtn.dataset.productId;
            if (!productId) {
                console.error("Product ID không tồn tại");
                return;
            }
            
            fetch(`${contextPath}/api/wishlist/toggle?productId=${productId}`, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json'
                }
            })
            .then(resp => resp.json().then(data => ({ status: resp.status, data: data })))
            .then(result => {
                // Kiểm tra status code 401 hoặc error về đăng nhập
                if (result.status === 401 || (result.data.status === 'error' && result.data.message && result.data.message.includes('đăng nhập'))) {
                    showLoginRequiredModal();
                    return;
                }
                
                // Xử lý thành công
                const data = result.data;
                console.log(data.message);
                
                // Update tất cả các nút wishlist có cùng productId (trên card, modal, detail page)
                const allWishlistBtns = document.querySelectorAll(`.btn-wishlist[data-product-id="${productId}"]`);
                
                if (data.status === 'added') {
                    allWishlistBtns.forEach(btn => {
                        btn.classList.add('active');
                        const icon = btn.querySelector('i');
                        if (icon && icon.classList.contains('bi-heart')) {
                            icon.classList.remove('bi-heart');
                            icon.classList.add('bi-heart-fill');
                        }
                    });
                } else if (data.status === 'removed') {
                    allWishlistBtns.forEach(btn => {
                        btn.classList.remove('active');
                        const icon = btn.querySelector('i');
                        if (icon && icon.classList.contains('bi-heart-fill')) {
                            icon.classList.remove('bi-heart-fill');
                            icon.classList.add('bi-heart');
                        }
                    });
                    
                    // Nếu đang ở trang wishlist, xóa card
                    const card = wishlistBtn.closest('.col');
                    if (card && window.location.pathname.includes('/user/wishlist')) {
                        card.remove();
                    }
                }
            })
            .catch(err => {
                console.error("Lỗi toggle wishlist:", err);
            });
        }
    });

    // Cần gọi lại updateWishlistHearts mỗi khi modal được hiển thị
    // (Vì modal được load động)
    const productModal = document.getElementById('productModal');
    if (productModal) {
        productModal.addEventListener('shown.bs.modal', function () {
             updateWishlistHearts();
        });
    }

});