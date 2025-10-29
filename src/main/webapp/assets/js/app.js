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

    // === START VOUCHER LOGIC ===
    document.addEventListener('DOMContentLoaded', function() {
        const codeInput = document.getElementById('voucher-code');
        if (codeInput) {
            let debounceTimeout;

            codeInput.addEventListener('input', function() {
                // Hủy timeout cũ nếu người dùng gõ tiếp
                clearTimeout(debounceTimeout);

                const code = codeInput.value.trim();
                const messageDiv = document.getElementById('voucher-message');
                const discountDisplay = document.getElementById('discount-display');
                const totalDisplay = document.getElementById('grand-total-display');
                const subtotalDisplay = document.getElementById('subtotal-display');

                // Nếu ô trống, reset lại giá và thông báo
                if (!code) {
                    messageDiv.textContent = '';
                    discountDisplay.textContent = '0₫';
                    totalDisplay.textContent = subtotalDisplay.textContent;
                    return;
                }

                // Đặt timeout mới
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
                }, 500); // Chờ 500ms sau khi người dùng ngừng gõ
            });
        }
    });
    // === END VOUCHER LOGIC ===

    // Các hàm xử lý giỏ hàng và sản phẩm giữ nguyên
    window.addToCart = function (btn) {
        try {
            var id = btn.getAttribute('data-id');
            handleAddToCartRequest('productId=' + encodeURIComponent(id));
        } catch (e) { console.error(e); showToast('Có lỗi khi thêm vào giỏ'); }
    };
    
    document.addEventListener('DOMContentLoaded', function() {
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

    function handleAddToCartRequest(params) {
        postForm(contextPath + '/cart/add', params, function (err, data) {
            if (err) { showToast('Đã có lỗi xảy ra. Vui lòng thử lại.'); console.error(err); return; }
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

  function showToast(message) {
    var toastLiveExample = document.getElementById('liveToast');
    if (toastLiveExample) {
      var toastBody = toastLiveExample.querySelector('.toast-body');
      if (toastBody) {
        toastBody.textContent = message;
      }
      var toast = new bootstrap.Toast(toastLiveExample);
      toast.show();
    }
  }
  
  // ... (rest of the file like loadMore remains the same)
  function renderProducts(items) {
      var grid = document.getElementById('grid');
      if (!grid) return;
      var html = items.map(function(p) {
          return '<div class="col">' +
              '<div class="card h-100">' +
              '<img class="card-img-top" src="' + p.thumb + '" alt="' + p.name + '"/>' +
              '<div class="card-body">' +
              '<h6 class="card-title mb-1">' + p.name + '</h6>' +
              '<div class="small text-muted">Giá: <strong>' + p.price + '</strong></div>' +
              '</div>' +
              '<div class="card-footer bg-transparent border-0">' +
              '<a class="btn btn-sm btn-outline-primary" href="' + contextPath + '/p?id=' + p.id + '">Xem</a> ' +
              '<button class="btn btn-sm btn-primary" data-id="' + p.id + '" onclick="addToCart(this)">Thêm giỏ</button>' +
              '</div>' +
              '</div>' +
              '</div>';
      }).join('');
      grid.innerHTML += html;
  }

  window.loadMore = function() {
      if (isLoading || !hasMore) return;
      isLoading = true;
      
      var loadingEl = document.getElementById('loading');
      var btnLoadMore = document.getElementById('btnLoadMore');
      if (loadingEl) loadingEl.style.display = 'block';
      if (btnLoadMore) btnLoadMore.style.display = 'none';
      
      var xhr = new XMLHttpRequest();
      var url = contextPath + '/products/page?page=' + page;
      
      if (typeof searchKeyword !== 'undefined' && searchKeyword) {
        url += '&q=' + encodeURIComponent(searchKeyword);
      }
      
      if (typeof selectedCate !== 'undefined' && selectedCate) {
        url += '&cate=' + encodeURIComponent(selectedCate);
      }
      
      if (typeof selectedSupplier !== 'undefined' && selectedSupplier) {
        url += '&supplier=' + encodeURIComponent(selectedSupplier);
      }
      
    xhr.open('GET', url, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (loadingEl) loadingEl.style.display = 'none';
            
            if (xhr.status === 200) {
                var data = JSON.parse(xhr.responseText);
                if (data.items && data.items.length > 0) {
                    renderProducts(data.items);
                    page++;
                }
                hasMore = data.hasMore;
                
                if (btnLoadMore && hasMore) {
                    btnLoadMore.style.display = 'inline-block';
                }
            } else {
                console.error("Lỗi khi tải sản phẩm. Status: " + xhr.status);
            }
            isLoading = false;
        }
    };
    xhr.send();
  };
})();