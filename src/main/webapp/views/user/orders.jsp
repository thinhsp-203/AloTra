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
        <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="h4 mb-0">
                <i class="bi bi-receipt text-primary"></i> Đơn hàng của tôi
            </h2>
            <span class="badge bg-primary fs-6">
                ${not empty orders ? orders.size() : 0} đơn hàng
            </span>
        </div>

        <!-- Search Bar -->
        <div class="card mb-3">
            <div class="card-body p-3">
                <form method="get" action="${pageContext.request.contextPath}/user/orders" class="row g-2">
                    <div class="col-md-8">
                        <input type="text" class="form-control" name="keyword" 
                               placeholder="Tìm theo mã đơn, tên, số điện thoại..." 
                               value="${keyword}">
                    </div>
                    <div class="col-md-4">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="bi bi-search"></i> Tìm kiếm
                        </button>
                    </div>
                    <c:if test="${not empty keyword}">
                        <div class="col-12">
                            <a href="${pageContext.request.contextPath}/user/orders" 
                               class="btn btn-sm btn-outline-secondary">
                                <i class="bi bi-x-circle"></i> Xóa bộ lọc
                            </a>
                        </div>
                    </c:if>
                </form>
            </div>
        </div>

        <!-- Order Status Tabs -->
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
                <a class="nav-link ${cs eq 'Đang chuẩn bị' ? 'active' : ''}" 
                   href="${pageContext.request.contextPath}/user/orders?status=Đang chuẩn bị">
                    <i class="bi bi-box-seam"></i> Đang chuẩn bị
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
                    <h5 class="mt-3 text-muted">Không có đơn hàng nào</h5>
                    <p class="text-muted">
                        <c:choose>
                            <c:when test="${not empty keyword}">
                                Không tìm thấy đơn hàng phù hợp với "<strong>${keyword}</strong>"
                            </c:when>
                            <c:otherwise>
                                Bạn chưa có đơn hàng nào trong mục này
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                        <i class="bi bi-shop"></i> Bắt đầu mua sắm
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="accordion" id="orderAccordion">
                    <c:forEach var="order" items="${orders}">
                        <div class="accordion-item order-card mb-3">
                            <h2 class="accordion-header">
                                <button class="accordion-button collapsed" type="button" 
                                        data-bs-toggle="collapse" 
                                        data-bs-target="#collapse-${order.order_id}">
                                    <div class="w-100 d-flex justify-content-between align-items-center pe-3">
                                        <div>
                                            <strong class="text-primary">#${order.order_id}</strong>
                                            <small class="text-muted ms-2">
                                                <i class="bi bi-calendar"></i>
                                                <fmt:formatDate value="${order.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </small>
                                        </div>
                                        <div class="d-flex gap-2 align-items-center">
                                            <span class="badge bg-primary fs-6">
                                                <fmt:formatNumber value="${order.total_amount}" pattern="#,##0₫"/>
                                            </span>
                                            <c:choose>
                                                <c:when test="${order.order_status eq 'Chờ xác nhận'}">
                                                    <span class="badge bg-warning text-dark">${order.order_status}</span>
                                                </c:when>
                                                <c:when test="${order.order_status eq 'Đang chuẩn bị'}">
                                                    <span class="badge bg-info">${order.order_status}</span>
                                                </c:when>
                                                <c:when test="${order.order_status eq 'Đang giao'}">
                                                    <span class="badge bg-primary">${order.order_status}</span>
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
                                    <!-- Order Info -->
                                    <div class="row g-3 mb-3">
                                        <div class="col-md-6">
                                            <h6><i class="bi bi-person-circle"></i> Thông tin người nhận</h6>
                                            <p class="mb-1"><strong>Họ tên:</strong> ${order.fullname}</p>
                                            <p class="mb-1"><strong>SĐT:</strong> ${order.phone}</p>
                                            <p class="mb-0"><strong>Địa chỉ:</strong> ${order.address}</p>
                                            <c:if test="${not empty order.note}">
                                                <p class="mb-0 mt-2"><strong>Ghi chú:</strong> <em>${order.note}</em></p>
                                            </c:if>
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
                                    
                                    <!-- Order Items -->
                                    <h6><i class="bi bi-box-seam"></i> Sản phẩm</h6>
                                    <div class="table-responsive">
                                        <table class="table table-sm">
                                            <thead class="table-light">
                                                <tr>
                                                    <th>Sản phẩm</th>
                                                    <th>Size</th>
                                                    <th>Topping</th>
                                                    <th class="text-center">SL</th>
                                                    <th class="text-end">Giá</th>
                                                    <th class="text-end">Tổng</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="detail" items="${order.orderDetails}">
                                                    <tr>
                                                        <td><strong>${detail.product_name}</strong></td>
                                                        <td>${empty detail.size_name ? '-' : detail.size_name}</td>
                                                        <td><small>${empty detail.toppings ? '-' : detail.toppings}</small></td>
                                                        <td class="text-center">${detail.quantity}</td>
                                                        <td class="text-end">
                                                            <fmt:formatNumber value="${detail.price}" pattern="#,##0₫"/>
                                                        </td>
                                                        <td class="text-end fw-bold text-primary">
                                                            <fmt:formatNumber value="${detail.lineTotal}" pattern="#,##0₫"/>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                            <tfoot class="table-light">
                                                <tr>
                                                    <th colspan="5" class="text-end">Tổng cộng:</th>
                                                    <th class="text-end text-primary fs-5">
                                                        <fmt:formatNumber value="${order.total_amount}" pattern="#,##0₫"/>
                                                    </th>
                                                </tr>
                                            </tfoot>
                                        </table>
                                    </div>
                                    
                                    <!-- Order Actions -->
                                    <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top">
                                        <div>
                                            <c:if test="${order.order_status eq 'Chờ xác nhận'}">
                                                <form method="post" 
                                                      action="${pageContext.request.contextPath}/user/profile" 
                                                      style="display: inline;"
                                                      onsubmit="return confirm('Bạn có chắc chắn muốn hủy đơn hàng #${order.order_id}?')">
                                                    <input type="hidden" name="action" value="cancelOrder"/>
                                                    <input type="hidden" name="orderId" value="${order.order_id}"/>
                                                    <button type="submit" class="btn btn-outline-danger">
                                                        <i class="bi bi-x-circle"></i> Hủy đơn
                                                    </button>
                                                </form>
                                            </c:if>
                                            <c:if test="${order.order_status eq 'Hoàn thành'}">
                                                <a href="${pageContext.request.contextPath}/user/reorder?orderId=${order.order_id}" 
                                                   class="btn btn-primary">
                                                    <i class="bi bi-arrow-repeat"></i> Mua lại
                                                </a>
                                            </c:if>
                                        </div>
                                        <div class="text-muted small">
                                            <i class="bi bi-clock-history"></i>
                                            Cập nhật: <fmt:formatDate value="${order.updatedDateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
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