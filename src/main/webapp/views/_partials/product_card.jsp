<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<div class="card h-100 product-card">
    <a href="${pageContext.request.contextPath}/p?id=${p.product_id}" class="card-link text-decoration-none text-dark">
        <div class="card-img-container">
            <img class="card-img-top" src="${p.thumbnail}" alt="${p.product_name}">
        </div>
        <div class="card-body d-flex flex-column text-center">
            <h6 class="card-title">${p.product_name}</h6>
            <p class="card-text fw-bold text-primary mt-auto">
                <fmt:formatNumber value="${p.price}" pattern="#,##0"/> đ
            </p>
        </div>
    </a>
    <div class="card-footer bg-transparent border-0 pb-3">
        <button class="btn btn-primary w-100" 
                data-bs-toggle="modal" 
                data-bs-target="#productModal" 
                data-product-id="${p.product_id}">
            Đặt mua
        </button>
    </div>
</div>