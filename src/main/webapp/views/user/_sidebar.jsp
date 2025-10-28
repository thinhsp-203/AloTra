<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="list-group">
    <a href="${pageContext.request.contextPath}/user/profile" 
       class="list-group-item list-group-item-action ${requestScope['javax.servlet.forward.request_uri'].endsWith('/profile') ? 'active' : ''}">
       <i class="bi bi-person-fill me-2"></i> Hồ Sơ
    </a>
    <a href="${pageContext.request.contextPath}/user/orders" 
       class="list-group-item list-group-item-action ${requestScope['javax.servlet.forward.request_uri'].endsWith('/orders') ? 'active' : ''}">
       <i class="bi bi-receipt me-2"></i> Đơn Mua
    </a>
    <a href="${pageContext.request.contextPath}/logout" class="list-group-item list-group-item-action">
       <i class="bi bi-box-arrow-right me-2"></i> Đăng xuất
    </a>
</div>