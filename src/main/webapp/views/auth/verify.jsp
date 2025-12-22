<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <title>Xác thực OTP - AloTra</title>
    </head>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header text-center">
                        <h4>Nhập Mã Xác Thực</h4>
                    </div>
                    <div class="card-body">
                        
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">${error}</div>
                        </c:if>
                        
                        <div class="alert alert-info">
                            Mã OTP đã được gửi đến email: <b>${param.email != null ? param.email : email}</b>
                        </div>

                        <form action="${pageContext.request.contextPath}/verify-otp" method="post">
                            <input type="hidden" name="email" value="${param.email != null ? param.email : email}">
                            <input type="hidden" name="action" value="${param.action != null ? param.action : 'forgot'}">

                            <div class="form-group">
                                <label>Mã OTP (6 số):</label>
                                <input type="text" name="otp" class="form-control" placeholder="Nhập mã OTP..." required>
                            </div>
                            
                            <button type="submit" class="btn btn-primary btn-block mt-3">Xác nhận</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>