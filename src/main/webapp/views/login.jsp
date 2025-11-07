<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <div class="card shadow-lg border-0">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <i class="bi bi-person-circle display-4 text-primary"></i>
                        <h2 class="h4 mt-3 mb-0">Đăng nhập</h2>
                        <p class="text-muted small">Chào mừng bạn quay trở lại!</p>
                    </div>
                    
                    <c:if test="${not empty sessionScope.success}">
                        <div class="alert alert-success alert-dismissible fade show">
                            <i class="bi bi-check-circle"></i> ${sessionScope.success}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="success" scope="session"/>
                    </c:if>
                    
                    <c:if test="${not empty alert}">
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle"></i> ${alert}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <form action="login" method="post">
                        <div class="mb-3">
                            <label class="form-label">Tên đăng nhập</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-person"></i></span>
                                <input type="text" 
                                       class="form-control" 
                                       name="username" 
                                       placeholder="Nhập tên đăng nhập" 
                                       value="${username}"
                                       required 
                                       autofocus>
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Mật khẩu</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-lock"></i></span>
                                <input type="password" 
                                       class="form-control" 
                                       name="password" 
                                       id="password"
                                       placeholder="Nhập mật khẩu" 
                                       required>
                                <button class="btn btn-outline-secondary" 
                                        type="button" 
                                        id="togglePassword">
                                    <i class="bi bi-eye"></i>
                                </button>
                            </div>
                        </div>
                        
                        <div class="form-check mb-3">
                            <input class="form-check-input" 
                                   type="checkbox" 
                                   name="rememberMe" 
                                   id="rememberMe">
                            <label class="form-check-label small" for="rememberMe">
                                Ghi nhớ đăng nhập
                            </label>
                        </div>
                        
                        <button type="submit" class="btn btn-primary w-100 mb-3">
                            <i class="bi bi-box-arrow-in-right"></i> Đăng nhập
                        </button>
                    </form>
                    
                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/auth/forgot" 
                           class="d-block small text-decoration-none mb-2">
                            <i class="bi bi-question-circle"></i> Quên mật khẩu?
                        </a>
                        <hr class="my-3">
                        <p class="text-muted small mb-2">Chưa có tài khoản?</p>
                        <a href="register" class="btn btn-outline-primary w-100">
                            <i class="bi bi-person-plus"></i> Đăng ký ngay
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
// Toggle password visibility
document.getElementById('togglePassword')?.addEventListener('click', function() {
    const password = document.getElementById('password');
    const icon = this.querySelector('i');
    if (password.type === 'password') {
        password.type = 'text';
        icon.classList.remove('bi-eye');
        icon.classList.add('bi-eye-slash');
    } else {
        password.type = 'password';
        icon.classList.remove('bi-eye-slash');
        icon.classList.add('bi-eye');
    }
});
</script>