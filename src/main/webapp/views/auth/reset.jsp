<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <div class="card shadow-lg border-0">
                <div class="card-body p-4">
                    <div class="text-center mb-4">
                        <i class="bi bi-shield-lock display-4 text-primary"></i>
                        <h2 class="h4 mt-3 mb-0">Đặt lại mật khẩu</h2>
                        <p class="text-muted small">Nhập mật khẩu mới cho tài khoản của bạn</p>
                    </div>

                    <c:if test="${invalid}">
                        <div class="alert alert-danger">
                            <i class="bi bi-exclamation-triangle"></i> 
                            Liên kết đặt lại mật khẩu không hợp lệ hoặc đã hết hạn.
                        </div>
                        <a href="${pageContext.request.contextPath}/auth/forgot" class="btn btn-primary w-100">
                            Yêu cầu liên kết mới
                        </a>
                    </c:if>

                    <c:if test="${not empty error}">
                        <div class="alert alert-warning alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle"></i> ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <c:if test="${!invalid}">
                        <form method="post" action="${pageContext.request.contextPath}/auth/reset" id="resetForm">
                            <input type="hidden" name="token" value="${token}"/>
                            
                            <div class="mb-3">
                                <label class="form-label">Mật khẩu mới <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <input type="password" class="form-control" name="password" 
                                           id="password" required minlength="6" maxlength="100"/>
                                    <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <small class="text-muted">Tối thiểu 6 ký tự</small>
                                <div class="progress mt-2" style="height: 5px;">
                                    <div class="progress-bar" id="passwordStrength" style="width: 0%"></div>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label class="form-label">Xác nhận mật khẩu <span class="text-danger">*</span></label>
                                <input type="password" class="form-control" name="confirm" 
                                       id="confirm" required minlength="6"/>
                                <small class="form-text text-danger d-none" id="passwordMismatch">
                                    Mật khẩu không khớp!
                                </small>
                            </div>
                            
                            <button class="btn btn-primary w-100 mb-3" type="submit">
                                <i class="bi bi-check-circle"></i> Đặt lại mật khẩu
                            </button>
                        </form>
                    </c:if>

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

<script>
// Password visibility toggle
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

// Password strength
document.getElementById('password')?.addEventListener('input', function() {
    const password = this.value;
    const strengthBar = document.getElementById('passwordStrength');
    
    let strength = 0;
    let strengthClass = '';
    
    if (password.length >= 6) strength += 20;
    if (password.length >= 10) strength += 20;
    if (/[a-z]/.test(password)) strength += 20;
    if (/[A-Z]/.test(password)) strength += 20;
    if (/[0-9]/.test(password)) strength += 10;
    if (/[^A-Za-z0-9]/.test(password)) strength += 10;
    
    if (strength < 40) {
        strengthClass = 'bg-danger';
    } else if (strength < 70) {
        strengthClass = 'bg-warning';
    } else {
        strengthClass = 'bg-success';
    }
    
    strengthBar.style.width = strength + '%';
    strengthBar.className = 'progress-bar ' + strengthClass;
});

// Password match validation
document.getElementById('confirm')?.addEventListener('input', function() {
    const password = document.getElementById('password').value;
    const confirm = this.value;
    const mismatchMsg = document.getElementById('passwordMismatch');
    
    if (confirm && password !== confirm) {
        this.classList.add('is-invalid');
        mismatchMsg.classList.remove('d-none');
    } else {
        this.classList.remove('is-invalid');
        mismatchMsg.classList.add('d-none');
    }
});

// Form validation
document.getElementById('resetForm')?.addEventListener('submit', function(e) {
    const password = document.getElementById('password').value;
    const confirm = document.getElementById('confirm').value;
    
    if (password !== confirm) {
        e.preventDefault();
        alert('Mật khẩu xác nhận không khớp!');
        return false;
    }
    
    if (password.length < 6) {
        e.preventDefault();
        alert('Mật khẩu phải có ít nhất 6 ký tự!');
        return false;
    }
    
    return true;
});
</script>