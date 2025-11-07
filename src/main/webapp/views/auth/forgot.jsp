<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <div class="card shadow-lg border-0">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <i class="bi bi-key display-4 text-primary"></i>
                        <h2 class="h4 mt-3 mb-0">Quên mật khẩu?</h2>
                        <p class="text-muted small">Nhập email để nhận hướng dẫn đặt lại mật khẩu</p>
                    </div>

                    <c:if test="${not empty msg}">
                        <div class="alert alert-info alert-dismissible fade show">
                            <i class="bi bi-info-circle"></i> ${msg}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle"></i> ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form method="post" action="${pageContext.request.contextPath}/auth/forgot">
                        <div class="mb-3">
                            <label class="form-label">Email đăng ký</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                                <input type="email" class="form-control" name="email" 
                                       placeholder="example@email.com" required autofocus />
                            </div>
                        </div>
                        
                        <button class="btn btn-primary w-100 mb-3" type="submit">
                            <i class="bi bi-send"></i> Gửi hướng dẫn
                        </button>
                    </form>
                    
                    <div class="text-center">
                        <hr class="my-3">
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-secondary w-100">
                            <i class="bi bi-arrow-left"></i> Về trang đăng nhập
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>