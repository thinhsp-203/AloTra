<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Thông tin cá nhân</title>
</head>
<body>
    <h2>Chào mừng, ${username}</h2>
    <p>Thông tin phiên làm việc:</p>
    <ul>
        <li>Session ID: ${sessionId}</li>
        <li>Thời gian tồn tại tối đa: ${sessionTimeout} giây</li>
    </ul>
    
    <p>
        <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
    </p>
</body>
</html>