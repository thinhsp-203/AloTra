<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
  <title><c:out value="${pageTitle != null ? pageTitle : 'AloTra'}"/></title>
  <link href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css" rel="stylesheet"/>
  <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet"/>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">
  <jsp:include page="/views/_partials/navbar.jsp"/>
  <main class="container py-4"><sitemesh:write property='body'/></main>
  <jsp:include page="/views/_partials/footer.jsp"/>
  <script src="${pageContext.request.contextPath}/assets/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>
