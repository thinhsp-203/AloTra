<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- XÓA TẤT CẢ PHẦN STYLE INLINE CŨ --%>
<div class="container my-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-lg">
                <div class="card-body p-4">
                    <h2 class="card-title text-center mb-4">Đăng nhập</h2>
                    
                    <c:if test="${not empty sessionScope.success}">
                        <p class="alert alert-success text-center">${sessionScope.success}</p>
                        <c:remove var="success" scope="session"></c:remove>
                    </c:if>
                    
                    <c:if test="${not empty alert}">
                        <p class="alert alert-danger text-center">${alert}</p>
                    </c:if>

                    <form action="login" method="post">
                        <div class="mb-3">
                            <input type="text" class="form-control" name="username" placeholder="Tên đăng nhập" required autofocus>
                        </div>
                        <div class="mb-3">
                            <input type="password" class="form-control" name="password" placeholder="Mật khẩu" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100 mb-2">Đăng nhập</button>
                    </form>
                    
                    <div class="text-center mt-3">
                        <a href="${pageContext.request.contextPath}/auth/forgot" class="d-block small text-muted mb-2">Quên mật khẩu?</a>
                        <p class="text-muted mb-0">Chưa có tài khoản? <a href="register">Đăng ký</a></p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>