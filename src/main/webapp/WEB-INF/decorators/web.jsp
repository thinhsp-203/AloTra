<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!doctype html>
<html lang="vi">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
    <title><c:out value="${pageTitle != null ? pageTitle : 'AloTra'}"/></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link href="${pageContext.request.contextPath}/assets/css/custom.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet"/>
</head>
<body data-context-path="${pageContext.request.contextPath}">

    <jsp:include page="/views/_partials/navbar.jsp"/>
    <main class="container py-4">
        <sitemesh:write property='body'/>
    </main>
    <jsp:include page="/views/_partials/footer.jsp"/>

    <div class="modal fade" id="productModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Tùy chỉnh sản phẩm</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="productModalContent" class="row">
                        <div class="col-12 text-center"><div class="spinner-border"></div></div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary w-100" id="modalAddToCartBtn">Thêm vào giỏ</button>
                </div>
            </div>
        </div>
    </div>
    
    <div class="toast-container position-fixed bottom-0 end-0 p-3">
        <div id="liveToast" class="toast" role="alert">
            <div class="toast-header">
                <strong class="me-auto text-success"><i class="bi bi-check-circle-fill"></i> Thông báo</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast"></button>
            </div>
            <div class="toast-body"></div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>