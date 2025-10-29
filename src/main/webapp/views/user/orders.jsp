<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<style>
    .order-filter-nav .nav-link { color: #666; border-bottom: 2px solid transparent; border-radius: 0; }
    .order-filter-nav .nav-link.active { color: var(--bs-primary); border-bottom-color: var(--bs-primary); background-color: transparent; }
</style>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <h2 class="h4 mb-4">Đơn hàng của tôi</h2>

        <ul class="nav nav-tabs order-filter-nav mb-4">
            <c:set var="cs" value="${empty currentStatus ? 'Tất cả' : currentStatus}" />
            <li class="nav-item"><a class="nav-link ${cs eq 'Tất cả' ? 'active' : ''}" href="${pageContext.request.contextPath}/user/orders">Tất cả</a></li>
            <li class="nav-item"><a class="nav-link ${cs eq 'Chờ xác nhận' ? 'active' : ''}" href="${pageContext.request.contextPath}/user/orders?status=Chờ xác nhận">Chờ xác nhận</a></li>
            <li class="nav-item"><a class="nav-link ${cs eq 'Đang giao' ? 'active' : ''}" href="${pageContext.request.contextPath}/user/orders?status=Đang giao">Đang giao</a></li>
            <li class="nav-item"><a class="nav-link ${cs eq 'Hoàn thành' ? 'active' : ''}" href="${pageContext.request.contextPath}/user/orders?status=Hoàn thành">Hoàn thành</a></li>
            <li class="nav-item"><a class="nav-link ${cs eq 'Đã hủy' ? 'active' : ''}" href="${pageContext.request.contextPath}/user/orders?status=Đã hủy">Đã hủy</a></li>
        </ul>

        <c:if test="${not empty sessionScope.orderSuccess}"><div class="alert alert-success alert-dismissible fade show">${sessionScope.orderSuccess}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><c:remove var="orderSuccess" scope="session"/></c:if>
        <c:if test="${not empty sessionScope.orderError}"><div class="alert alert-danger alert-dismissible fade show">${sessionScope.orderError}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div><c:remove var="orderError" scope="session"/></c:if>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="alert alert-info"><i class="bi bi-inbox"></i> Không có đơn hàng nào trong mục này.</div>
            </c:when>
            <c:otherwise>
                <div class="accordion" id="orderAccordion">
                    <c:forEach var="order" items="${orders}" varStatus="loop">
                        <div class="accordion-item">
                            <h2 class="accordion-header" id="heading-${order.order_id}">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#collapse-${order.order_id}">
                                    <div class="w-100 d-flex justify-content-between align-items-center pe-3">
                                        <span>Đơn hàng #${order.order_id} - <fmt:formatDate value="${order.createdDate}" pattern="dd/MM/yyyy"/></span>
                                        <span class="badge bg-primary"><fmt:formatNumber value="${order.total_amount}" pattern="#,##0₫"/></span>
                                        <span class="badge bg-info">${order.order_status}</span>
                                    </div>
                                </button>
                            </h2>
                            <div id="collapse-${order.order_id}" class="accordion-collapse collapse" data-bs-parent="#orderAccordion">
                                <div class="accordion-body">
                                    <%-- Nội dung chi tiết đơn hàng --%>
                                    <ul class="list-group list-group-flush mb-3">
                                        <c:forEach var="detail" items="${order.orderDetails}">
                                            <li class="list-group-item">${detail.product_name} x ${detail.quantity}</li>
                                        </c:forEach>
                                    </ul>
                                    <div class="text-end">
                                        <c:if test="${order.order_status eq 'Chờ xác nhận'}">
                                            <form method="post" action="${pageContext.request.contextPath}/user/profile" style="display: inline;">
                                                <input type="hidden" name="action" value="cancelOrder"/><input type="hidden" name="orderId" value="${order.order_id}"/>
                                                <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')">Huỷ đơn</button>
                                            </form>
                                        </c:if>
                                        <c:if test="${order.order_status eq 'Hoàn thành'}">
                                            <a href="${pageContext.request.contextPath}/user/reorder?orderId=${order.order_id}" class="btn btn-sm btn-primary">Mua lại</a>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>