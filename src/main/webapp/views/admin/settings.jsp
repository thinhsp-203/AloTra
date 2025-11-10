<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h1 class="h3 mb-4 text-gray-800">Cài đặt Website</h1>

<c:if test="${not empty sessionScope.success}">
  <div class="alert alert-success alert-dismissible fade show">
    ${sessionScope.success}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="success" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
  <div class="alert alert-danger alert-dismissible fade show">
    ${sessionScope.error}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="error" scope="session"/>
</c:if>

<div class="row">
    <div class="col-lg-8">
        <div class="card shadow mb-4">
            <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Logo & Banner</h6>
            </div>
            <div class="card-body">
                <form method="POST" action="${pageContext.request.contextPath}/admin/settings">
                    <div class="mb-3">
                        <label for="logoUrl" class="form-label">Logo URL</label>
                        <input type="text" class="form-control" id="logoUrl" name="LOGO_URL" 
                               value="${settings['LOGO_URL']}" 
                               placeholder="https://.../logo.png">
                        <div class="form-text">Dán link ảnh logo. Sẽ hiển thị ở góc trên bên trái (navbar).</div>
                    </div>

                    <div class="mb-3">
                        <label for="bannerUrl" class="form-label">Banner Trang chủ URL</label>
                        <input type="text" class="form-control" id="bannerUrl" name="BANNER_URL" 
                               value="${settings['BANNER_URL']}" 
                               placeholder="https://.../banner-home.jpg">
                        <div class="form-text">Ảnh nền lớn hiển thị ở trang chủ.</div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="bannerText" class="form-label">Chữ trên Banner</label>
                        <input type="text" class="form-control" id="bannerText" name="BANNER_TEXT" 
                               value="${settings['BANNER_TEXT']}" 
                               placeholder="Ví dụ: Khuyến mãi 50%">
                        <div class="form-text">Dòng chữ lớn hiển thị đè lên ảnh banner.</div>
                    </div>
                    
                    <hr>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save fa-sm"></i> Lưu Cài đặt
                    </button>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-4">
         <div class="card shadow-sm">
             <div class="card-header py-3">
                <h6 class="m-0 font-weight-bold text-primary">Xem trước Logo</h6>
             </div>
             <div class="card-body text-center">
                 <c:if test="${not empty settings['LOGO_URL']}">
                     <img src="${settings['LOGO_URL']}" alt="Logo" style="max-height: 80px; max-width: 100%;">
                 </c:if>
                 <c:if test="${empty settings['LOGO_URL']}">
                     <span class="text-muted">Chưa có logo</span>
                 </c:if>
             </div>
         </div>
    </div>
</div>