<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<style>
    .order-filter-nav .nav-link { 
        color: #666; 
        border-bottom: 2px solid transparent; 
        border-radius: 0; 
        padding: 0.75rem 1.5rem;
        font-weight: 500;
        transition: all 0.3s ease;
    }
    .order-filter-nav .nav-link:hover {
        color: var(--bs-primary);
        background: rgba(0, 102, 51, 0.05);
    }
    .order-filter-nav .nav-link.active { 
        color: var(--bs-primary); 
        border-bottom-color: var(--bs-primary); 
        background-color: transparent; 
    }
    .order-card {
        border: 1px solid #e0e0e0;
        border-radius: 12px;
        transition: all 0.3s ease;
    }
    .order-card:hover {
        box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
</style>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="h4 mb-0">Đơn hàng của tôi</h2>
            <span class="badge bg-primary">
                ${not empty orders ? orders.size() : 0} đơn hàng
            </span>
        </div>

        <ul class="nav nav-tabs order-filter-nav mb-4">
            <c:set var="cs" value="${empty currentStatus ? 'Tất cả' : currentStatus}" />
            <li class="nav-item">
                <a class="nav-link ${cs eq 'Tất cả' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/user/orders">
                    <i class="bi bi-grid"></i> Tất cả
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${cs eq 'Chờ xác nhận' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/user/orders?status=Chờ xác nhận">
                    <i class="bi bi-clock"></i> Chờ xác nhận
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${cs eq 'Đang giao' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/user/orders?status=Đang giao">
                    <i class="bi bi-truck"></i> Đang giao
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${cs eq 'Hoàn thành' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/user/orders?status=Hoàn thành">
                    <i class="bi bi-check-circle"></i> Hoàn thành
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link ${cs eq 'Đã hủy' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/user/orders?status=Đã hủy">
                    <i class="bi bi-x-circle"></i> Đã hủy
                </a>
            </li>
        </ul>

        <c:if test="${not empty sessionScope.orderSuccess}">
            <div class="alert alert-success alert-dismissible fade show">
                <i class="bi bi-check-circle-fill"></i> ${sessionScope.orderSuccess}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="orderSuccess" scope="session"/>
        </c:if>
        
        <c:if test="${not empty sessionScope.orderError}">
            <div class="alert alert-danger alert-dismissible fade show">
                <i class="bi bi-exclamation-triangle-fill"></i> ${sessionScope.orderError}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="orderError" scope="session"/>
        </c:if>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="text-center py-5">
                    <i class="bi bi-inbox display-1 text-muted"></i>
                    <p class="text-muted mt-3">Không có đơn hàng nào trong mục này.</p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                        Bắt đầu mua sắm
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="accordion" id="orderAccordion">
                    <c:forEach var="order" items="${orders}" varStatus="loop">
                        <div class="accordion-item order-card mb-3">
                            <h2 class="accordion-header" id="heading-${order.order_id}">
                                <button class="accordion-button collapsed" type="button" 
                                        data-bs-toggle="collapse" 
                                        data-bs-target="#collapse-${order.order_id}">
                                    <div class="w-100 d-flex justify-content-between align-items-center pe-3">
                                        <div>
                                            <strong>Đơn hàng #${order.order_id}</strong>
                                            <small class="text-muted ms-2">
                                                <i class="bi bi-calendar"></i>
                                                <fmt:formatDate value="${order.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </small>
                                        </div>
                                        <div class="d-flex gap-2">
                                            <span class="badge bg-primary">
                                                <fmt:formatNumber value="${order.total_amount}" pattern="#,##0₫"/>
                                            </span>
                                            <c:choose>
                                                <c:when test="${order.order_status eq 'Chờ xác nhận'}">
                                                    <span class="badge bg-warning text-dark">${order.order_status}</span>
                                                </c:when>
                                                <c:when test="${order.order_status eq 'Đang giao'}">
                                                    <span class="badge bg-info">${order.order_status}</span>
                                                </c:when>
                                                <c:when test="${order.order_status eq 'Hoàn thành'}">
                                                    <span class="badge bg-success">${order.order_status}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${order.order_status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </button>
                            </h2>
                            <div id="collapse-${order.order_id}" 
                                 class="accordion-collapse collapse" 
                                 data-bs-parent="#orderAccordion">
                                <div class="accordion-body">
                                    <div class="row g-3 mb-3">
                                        <div class="col-md-6">
                                            <h6><i class="bi bi-person"></i> Thông tin người nhận</h6>
                                            <p class="mb-1"><strong>Họ tên:</strong> ${order.fullname}</p>
                                            <p class="mb-1"><strong>SĐT:</strong> ${order.phone}</p>
                                            <p class="mb-0"><strong>Địa chỉ:</strong> ${order.address}</p>
                                        </div>
                                        <div class="col-md-6">
                                            <h6><i class="bi bi-credit-card"></i> Thanh toán</h6>
                                            <p class="mb-1"><strong>Phương thức:</strong> ${order.payment_method}</p>
                                            <p class="mb-0">
                                                <strong>Trạng thái:</strong> 
                                                <span class="badge bg-${order.payment_status eq 'Đã thanh toán' ? 'success' : 'warning'}">
                                                    ${order.payment_status}
                                                </span>
                                            </p>
                                        </div>
                                    </div>
                                    
                                    <h6><i class="bi bi-box-seam"></i> Sản phẩm</h6>
                                    <ul class="list-group list-group-flush mb-3">
                                        <c:forEach var="detail" items="${order.orderDetails}">
                                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                                <div>
                                                    <strong>${detail.product_name}</strong>
                                                    <c:if test="${not empty detail.size_name}">
                                                        <span class="text-muted ms-2">(${detail.size_name})</span>
                                                    </c:if>
                                                    <c:if test="${not empty detail.toppings}">
                                                        <br><small class="text-muted">+ ${detail.toppings}</small>
                                                    </c:if>
                                                </div>
                                                <div class="text-end">
                                                    <div class="text-muted small">x${detail.quantity}</div>
                                                    <strong class="text-primary">
                                                        <fmt:formatNumber value="${detail.lineTotal}" pattern="#,##0₫"/>
                                                    </strong>
                                                </div>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                    
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <c:if test="${order.order_status eq 'Chờ xác nhận'}">
                                                <form method="post" 
                                                      action="${pageContext.request.contextPath}/user/profile" 
                                                      style="display: inline;">
                                                    <input type="hidden" name="action" value="cancelOrder"/>
                                                    <input type="hidden" name="orderId" value="${order.order_id}"/>
                                                    <button type="submit" 
                                                            class="btn btn-sm btn-outline-danger" 
                                                            onclick="return confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')">
                                                        <i class="bi bi-x-circle"></i> Huỷ đơn
                                                    </button>
                                                </form>
                                            </c:if>
                                            <c:if test="${order.order_status eq 'Hoàn thành'}">
                                                <a href="${pageContext.request.contextPath}/user/reorder?orderId=${order.order_id}" 
                                                   class="btn btn-sm btn-primary">
                                                    <i class="bi bi-arrow-repeat"></i> Mua lại
                                                </a>
                                            </c:if>
                                        </div>
                                        <div class="text-end">
                                            <div class="text-muted small">Tổng cộng</div>
                                            <h5 class="mb-0 text-primary">
                                                <fmt:formatNumber value="${order.total_amount}" pattern="#,##0₫"/>
                                            </h5>
                                        </div>
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