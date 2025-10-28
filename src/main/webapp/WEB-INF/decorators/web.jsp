<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title><c:out value="${pageTitle != null ? pageTitle : 'AloTra'}"/></title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
  <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet"/>
</head>
<body data-context-path="${pageContext.request.contextPath}">
	<%-- THÊM ĐOẠN NÀY --%>
	  <div class="toast-container position-fixed bottom-0 end-0 p-3">
	    <div id="liveToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
	      <div class="toast-header">
	        <strong class="me-auto text-success"><i class="bi bi-check-circle-fill"></i> Giỏ hàng</strong>
	        <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
	      </div>
	      <div class="toast-body" id="toast-body-content">
	        Sản phẩm đã được thêm thành công!
	      </div>
	    </div>
	  </div>
	  <%-- KẾT THÚC PHẦN THÊM --%>
  <jsp:include page="/views/_partials/navbar.jsp"/>
  <main class="container py-4"><sitemesh:write property='body'/></main>
  <jsp:include page="/views/_partials/footer.jsp"/>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
  <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>