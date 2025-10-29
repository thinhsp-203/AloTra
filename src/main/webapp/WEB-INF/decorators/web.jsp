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
    <%-- Đảm bảo bạn đã có các file CSS này trong project --%>
    <link href="${pageContext.request.contextPath}/assets/css/custom.css" rel="stylesheet"/>
    <link href="${pageContext.request.contextPath}/assets/css/app.css" rel="stylesheet"/>
</head>
<body data-context-path="${pageContext.request.contextPath}">

    <%-- Thanh điều hướng và Chân trang --%>
    <jsp:include page="/views/_partials/navbar.jsp"/>
    <main class="container py-4">
        <sitemesh:write property='body'/>
    </main>
    <jsp:include page="/views/_partials/footer.jsp"/>

    <%-- MODAL (POP-UP) ĐỂ TÙY CHỈNH SẢN PHẨM --%>
    <div class="modal fade" id="productModal" tabindex="-1" aria-labelledby="productModalTitle" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="productModalTitle">Tùy chỉnh sản phẩm</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div id="productModalContent" class="row">
                        <%-- Nội dung chi tiết sản phẩm sẽ được JavaScript tải động vào đây --%>
                        <div class="col-12 text-center">
                            <div class="spinner-border" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <%-- Nút thêm vào giỏ hàng, giá tiền sẽ được JavaScript cập nhật --%>
                    <button type="button" class="btn btn-primary w-100" id="modalAddToCartBtn" disabled>Thêm vào giỏ</button>
                </div>
            </div>
        </div>
    </div>
    
    <%-- Pop-up thông báo nhanh (Toast) --%>
    <div class="toast-container position-fixed bottom-0 end-0 p-3">
        <div id="liveToast" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header">
                <strong class="me-auto text-success"><i class="bi bi-check-circle-fill"></i> Thông báo</strong>
                <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
            <div class="toast-body" id="toast-body-content">
                <%-- Nội dung thông báo sẽ được JavaScript chèn vào đây --%>
            </div>
        </div>
    </div>

    <%-- Scripts --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>