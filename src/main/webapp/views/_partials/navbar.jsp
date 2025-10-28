<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<style>
    .cart-dropdown .dropdown-menu {
        width: 400px;
        padding: 0;
        border-radius: 0.25rem;
        box-shadow: 0 0.5rem 1rem rgba(0,0,0,0.15);
    }
    .cart-dropdown-header {
        padding: 0.75rem 1rem;
        background-color: #f8f9fa;
        color: #6c757d;
        font-size: 0.9rem;
    }
    .cart-dropdown-item {
        display: flex;
        align-items: center;
        padding: 0.75rem 1rem;
        text-decoration: none;
        color: #212529;
    }
    .cart-dropdown-item:hover {
        background-color: #f1f1f1;
    }
    .cart-dropdown-item img {
        width: 50px;
        height: 50px;
        object-fit: cover;
        margin-right: 1rem;
        border: 1px solid #dee2e6;
    }
    .cart-dropdown-item-info {
        flex-grow: 1;
        overflow: hidden;
    }
    .cart-dropdown-item-name {
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        display: block;
    }
    .cart-dropdown-footer {
        padding: 0.75rem 1rem;
        background-color: #f8f9fa;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
</style>

<nav class="navbar navbar-expand-lg bg-body-tertiary border-bottom sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/home">AloTra</a>
        <form class="d-flex mx-auto" style="min-width: 40%;" action="${pageContext.request.contextPath}/products" method="get">
            <input class="form-control me-2" type="search" name="q" placeholder="Tìm trà sữa, cà phê..." value="${param.q}"/>
            <button class="btn btn-primary" type="submit"><i class="bi bi-search"></i> Tìm</button>
        </form>

        <ul class="navbar-nav ms-auto mb-2 mb-lg-0 align-items-center">
            <li class="nav-item dropdown cart-dropdown">
                <a class="nav-link" href="#" id="cartDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    <i class="bi bi-cart-fill fs-5"></i>
                    <span class="badge rounded-pill bg-danger" id="cart-item-count">${not empty sessionScope.CART ? fn:length(sessionScope.CART) : 0}</span>
                </a>
                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="cartDropdown">
                    <li class="cart-dropdown-header">Sản Phẩm Mới Thêm</li>
                    <li><hr class="dropdown-divider my-0"></li>
                    <c:choose>
                        <c:when test="${not empty sessionScope.CART}">
                            <c:forEach var="item" items="${sessionScope.CART}" begin="0" end="4">
                                <li>
                                    <a class="dropdown-item cart-dropdown-item" href="${pageContext.request.contextPath}/p?id=${item.productId}">
                                        <img src="${item.thumbnail}" alt="${item.productName}">
                                        <div class="cart-dropdown-item-info">
                                            <span class="cart-dropdown-item-name">${item.productName}</span>
                                            <strong class="text-primary"><fmt:formatNumber value="${item.lineTotal}" pattern="#,##0₫"/></strong>
                                        </div>
                                    </a>
                                </li>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <li class="p-3 text-center text-muted">Chưa có sản phẩm</li>
                        </c:otherwise>
                    </c:choose>
                    <li><hr class="dropdown-divider my-0"></li>
                    <li class="cart-dropdown-footer">
                        <span class="small">${fn:length(sessionScope.CART)} Thêm Hàng Vào Giỏ</span>
                        <a href="${pageContext.request.contextPath}/cart/view" class="btn btn-primary btn-sm">Xem Giỏ Hàng</a>
                    </li>
                </ul>
            </li>

            <c:choose>
                <c:when test="${empty sessionScope.currentUser}">
                    <li class="nav-item ms-2">
                        <a class="nav-link" href="${pageContext.request.contextPath}/register">Đăng ký</a>
                    </li>
                    <li class="nav-item">
                        <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="nav-item dropdown ms-2">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-person-circle"></i> Chào, ${sessionScope.currentUser.username}
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/profile">Tài Khoản</a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user/orders">Đơn Mua</a></li>
                            <c:if test="${sessionScope.currentUser.roleid == 1 || sessionScope.currentUser.roleid == 2}">
                                <li><hr class="dropdown-divider"></li>
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin">Trang Quản Trị</a></li>
                            </c:if>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Đăng Xuất</a></li>
                        </ul>
                    </li>
                </c:otherwise>
            </c:choose>
        </ul>
    </div>
</nav>