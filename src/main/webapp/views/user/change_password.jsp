<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<style>
.change-password-form .input-group {
    max-width: 900px;
    width: 100%;
}

.change-password-form .form-control-lg {
    font-size: 1.25rem;
    padding: 1rem 1.25rem;
    height: calc(3rem + 2px);
}
</style>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <h2 class="h4 mb-4">
            <i class="bi bi-shield-lock text-primary"></i> Đổi mật khẩu
        </h2>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="bi bi-check-circle"></i> ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="bi bi-exclamation-triangle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <div class="card">
            <div class="card-header bg-light">
                <h5 class="card-title mb-0">
                    <i class="bi bi-key"></i> Thay đổi mật khẩu
                </h5>
            </div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/user/change-password" 
                      id="changePasswordForm" class="change-password-form">
                    <input type="hidden" name="action" value="changePassword">
                    
                    <div class="mb-3">
                        <label class="form-label">Mật khẩu hiện tại <span class="text-danger">*</span></label>
                        <div class="input-group input-group-lg">
                            <input type="password" class="form-control form-control-lg" name="oldPassword" 
                                   id="oldPassword" required>
                            <button class="btn btn-outline-secondary" type="button" 
                                    onclick="togglePasswordVisibility('oldPassword')">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label">Mật khẩu mới <span class="text-danger">*</span></label>
                        <div class="input-group input-group-lg">
                            <input type="password" class="form-control form-control-lg" name="newPassword" 
                                   id="newPassword" minlength="6" maxlength="100" required>
                            <button class="btn btn-outline-secondary" type="button" 
                                    onclick="togglePasswordVisibility('newPassword')">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                        <small class="text-muted">Tối thiểu 6 ký tự</small>
                        <div class="progress mt-2" style="height: 5px; max-width: 900px;">
                            <div class="progress-bar" id="newPasswordStrength" style="width: 0%"></div>
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <label class="form-label">Xác nhận mật khẩu mới <span class="text-danger">*</span></label>
                        <div class="input-group input-group-lg">
                            <input type="password" class="form-control form-control-lg" name="confirmPassword" 
                                   id="confirmPassword" minlength="6" required>
                            <button class="btn btn-outline-secondary" type="button" 
                                    onclick="togglePasswordVisibility('confirmPassword')">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                        <small class="form-text text-danger d-none" id="passwordMismatchMsg">
                            Mật khẩu không khớp!
                        </small>
                    </div>
                    
                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-key"></i> Đổi mật khẩu
                        </button>
                        <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left"></i> Quay lại
                        </a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
function togglePasswordVisibility(fieldId) {
    const field = document.getElementById(fieldId);
    if (!field) return;
    const inputGroup = field.closest('.input-group');
    if (!inputGroup) return;
    const btn = inputGroup.querySelector('button');
    if (!btn) return;
    const icon = btn.querySelector('i');
    if (!icon) return;
    if (field.type === 'password') {
        field.type = 'text';
        icon.classList.remove('bi-eye');
        icon.classList.add('bi-eye-slash');
    } else {
        field.type = 'password';
        icon.classList.remove('bi-eye-slash');
        icon.classList.add('bi-eye');
    }
}

document.getElementById('newPassword')?.addEventListener('input', function() {
    const password = this.value;
    const strengthBar = document.getElementById('newPasswordStrength');
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

document.getElementById('confirmPassword')?.addEventListener('input', function() {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = this.value;
    const mismatchMsg = document.getElementById('passwordMismatchMsg');
    if (confirmPassword && newPassword !== confirmPassword) {
        this.classList.add('is-invalid');
        mismatchMsg.classList.remove('d-none');
    } else {
        this.classList.remove('is-invalid');
        mismatchMsg.classList.add('d-none');
    }
});

document.getElementById('changePasswordForm')?.addEventListener('submit', function(e) {
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    if (newPassword !== confirmPassword) {
        e.preventDefault();
        alert('Mật khẩu xác nhận không khớp!');
        document.getElementById('confirmPassword').focus();
        return false;
    }
    if (newPassword.length < 6) {
        e.preventDefault();
        alert('Mật khẩu mới phải có ít nhất 6 ký tự!');
        document.getElementById('newPassword').focus();
        return false;
    }
    return true;
});
</script>

