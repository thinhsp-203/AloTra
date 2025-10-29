<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="col">
  <div class="card h-100 text-center">
    <a href="${pageContext.request.contextPath}/p?id=${p.product_id}">
        <img class="card-img-top p-3" src="${p.thumbnail}" alt="${p.product_name}" style="height: 200px; object-fit: contain;">
    </a>
    <div class="card-body">
      <h6 class="card-title">${p.product_name}</h6>
      <p class="card-text text-muted"><fmt:formatNumber value="${p.price}" pattern="#,##0"/> đ</p>
    </div>
    <div class="card-footer bg-transparent border-0 pb-3">
      <button class="btn btn-primary w-100" 
              data-bs-toggle="modal" 
              data-bs-target="#productModal" 
              data-product-id="${p.product_id}">
          Đặt mua
      </button>
    </div>
  </div>
</div>