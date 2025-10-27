(function () {
  'use strict';

  // Lấy contextPath an toàn (gán vào <body data-context-path="${pageContext.request.contextPath}">)
  var body = document.body || document.getElementsByTagName('body')[0];
  var contextPath = (body && body.getAttribute('data-context-path')) || '';

  function postForm(url, params, cb) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', url, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
    xhr.onreadystatechange = function () {
      if (xhr.readyState === 4) {
        if (xhr.status >= 200 && xhr.status < 300) {
          try {
            cb(null, JSON.parse(xhr.responseText));
          } catch (e) {
            cb(e);
          }
        } else {
          cb(new Error('HTTP ' + xhr.status));
        }
      }
    };
    xhr.send(params);
  }

  window.addToCart = function (btn) {
    try {
      var id = btn.getAttribute('data-id');
      var size = btn.getAttribute('data-size') || '';
      var toppings = btn.getAttribute('data-toppings') || ''; // "1,3,5"
      var qty = btn.getAttribute('data-qty') || '1';

      var params = 'productId=' + encodeURIComponent(id)
        + '&size=' + encodeURIComponent(size)
        + '&toppings=' + encodeURIComponent(toppings)
        + '&qty=' + encodeURIComponent(qty);

      postForm(contextPath + '/cart/add', params, function (err, data) {
        if (err) { alert('Lỗi mạng'); return; }
        if (data && data.ok) {
          alert('Đã thêm vào giỏ');
        } else {
          alert((data && data.message) || 'Thêm giỏ hàng thất bại');
        }
      });
    } catch (e) {
      console && console.error && console.error(e);
      alert('Có lỗi khi thêm vào giỏ');
    }
  };
})();
