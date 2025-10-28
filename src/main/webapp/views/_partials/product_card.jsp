<%-- Trong file _partials/product_card.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- Xóa các dòng <c:set> cũ ở đây.
     Biến 'p' đã có sẵn từ requestScope. --%>

<div class="col">
  <div class="card h-100">
 <img class="card-img-top" src="${p.thumbnail}" alt="${p.product_name}" 
     style="height: 200px; object-fit: cover; display: block;"/>
    <div class="card-body">
      <h6 class="card-title mb-1">${p.product_name}</h6>
      <div class="small text-muted">Giá: <strong>${p.price}</strong></div>
    </div>
    <div class="card-footer bg-transparent border-0">
      <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/p?id=${p.product_id}">Xem</a>
      <button class="btn btn-sm btn-primary" data-id="${p.product_id}" onclick="addToCart(this)">Thêm giỏ</button>
    </div>
  </div>
</div>