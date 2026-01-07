<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<footer class="bg-dark text-white mt-5">
  <div class="container py-4">
    <div class="row">
      <!-- Brand -->
      <div class="col-md-4 mb-3 mb-md-0">
        <h5 class="fw-bold mb-2">AloTra</h5>
        <p class="small text-white-50 mb-3">
          Hệ thống trà sữa chất lượng cao, phục vụ tận tâm khách hàng.
        </p>

        <div class="d-flex align-items-center gap-3">
          <a href="#" class="text-white" aria-label="Facebook" title="Facebook">
            <i class="bi bi-facebook fs-5"></i>
          </a>
          <a href="#" class="text-white" aria-label="Instagram" title="Instagram">
            <i class="bi bi-instagram fs-5"></i>
          </a>
          <a href="#" class="text-white" aria-label="TikTok" title="TikTok">
            <i class="bi bi-tiktok fs-5"></i>
          </a>
        </div>
      </div>

      <!-- Links -->
      <div class="col-md-4 mb-3 mb-md-0">
        <h6 class="fw-semibold mb-2">Liên kết</h6>
        <ul class="list-unstyled small mb-0">
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/home" class="text-white-50 text-decoration-none">
              Trang chủ
            </a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/products" class="text-white-50 text-decoration-none">
              Sản phẩm
            </a>
          </li>
          <li class="mb-2">
            <a href="${pageContext.request.contextPath}/about" class="text-white-50 text-decoration-none">
              Giới thiệu
            </a>
          </li>
          <li>
            <a href="${pageContext.request.contextPath}/contact" class="text-white-50 text-decoration-none">
              Liên hệ
            </a>
          </li>
        </ul>
      </div>

      <!-- Contact -->
      <div class="col-md-4">
        <h6 class="fw-semibold mb-2">Liên hệ</h6>
        <ul class="list-unstyled small text-white-50 mb-0">
          <li class="mb-2">
            <i class="bi bi-geo-alt-fill me-2"></i>
            123 Đường ABC, Q1, TP.HCM
          </li>
          <li class="mb-2">
            <i class="bi bi-telephone-fill me-2"></i>
            0909 123 456
          </li>
          <li>
            <i class="bi bi-envelope-fill me-2"></i>
            contact@alotra.vn
          </li>
        </ul>
      </div>
    </div>

    <hr class="border-secondary my-3" />

    <div class="row">
      <div class="col text-center small text-white-50">
        &copy; 2025 AloTra. All rights reserved. | Made with ❤️ by 24TX810029
      </div>
    </div>
  </div>
</footer>
