<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<div class="row g-4">
    <div class="col-md-3">
        <jsp:include page="/views/user/_sidebar.jsp"/>
    </div>
    
    <div class="col-md-9">
        <h2 class="h4 mb-4">Đơn hàng của tôi</h2>

        <c:choose>
            <c:when test="${empty orders}">
                <div class="alert alert-info">
                    <i class="bi bi-inbox"></i> Bạn chưa có đơn hàng nào.
                    <a href="${pageContext.request.contextPath}/products" class="alert-link">Đặt hàng ngay</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="row g-3">
                    <c:forEach var="order" items="${orders}">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-body">
                                    <div class="row align-items-center">
                                        <div class="col-md-2">
                                            <h6 class="mb-1">Đơn hàng #${order.order_id}</h6>
                                            <small class="text-muted">
                                                <fmt:formatDate value="${order.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </small>
                                        </div>
                                        
                                        <div class="col-md-3">
                                            <div class="small text-muted">Khách hàng</div>
                                            <div>${order.fullname}</div>
                                            <div class="small">${order.phone}</div>
                                        </div>
                                        
                                       <div class="col-md-2">
                                            <div class="small text-muted">Tổng tiền</div>
                                            <div class="fw-bold text-primary">
                                               <fmt:formatNumber value="${order.total_amount}" pattern="#,##0₫"/>
                                            </div>
                                        </div>
                                        
                                        <div class="col-md-2">
                                            <div class="small text-muted">Thanh toán</div>
                                            <span class="badge bg-${order.payment_status eq 'Đã thanh toán' ? 'success' : 'warning'}">
                                                ${order.payment_status}
                                            </span>
                                        </div>
                                        
                                        <div class="col-md-2">
                                            <div class="small text-muted">Trạng thái</div>
                                            <c:choose>
                                                <c:when test="${order.order_status eq 'Chờ xác nhận'}">
                                                    <span class="badge bg-secondary">${order.order_status}</span>
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
                                                    <span class="badge bg-danger">${order.order_status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        
                                        <div class="col-md-1 text-end">
                                            <button class="btn btn-sm btn-outline-secondary" 
                                                    type="button" 
                                                    data-bs-toggle="collapse" 
                                                    data-bs-target="#order-${order.order_id}"
                                                    aria-expanded="false">
                                                <i class="bi bi-chevron-down"></i>
                                            </button>
                                        </div>
                                    </div>
                                    
                                    <!-- Chi tiết đơn hàng (collapse) -->
                                    <div class="collapse mt-3" id="order-${order.order_id}">
                                        <hr>
                                        
                                        <!-- Danh sách sản phẩm -->
                                        <c:if test="${not empty order.orderDetails}">
                                            <h6 class="small text-muted mb-2">Chi tiết sản phẩm</h6>
                                            <div class="table-responsive mb-3">
                                                <table class="table table-sm table-borderless">
                                                    <thead class="table-light">
                                                        <tr>
                                                            <th>Sản phẩm</th>
                                                            <th>Size</th>
                                                            <th>Topping</th>
                                                            <th class="text-center">SL</th>
                                                            <th class="text-end">Đơn giá</th>
                                                            <th class="text-end">Thành tiền</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="detail" items="${order.orderDetails}">
                                                            <tr>
                                                                <td>${detail.product_name}</td>
                                                                <td>${empty detail.size_name ? '-' : detail.size_name}</td>
                                                                <td class="small">${empty detail.toppings ? '-' : detail.toppings}</td>
                                                                <td class="text-center">${detail.quantity}</td>
                                                                <td class="text-end">
                                                                    <fmt:formatNumber value="${detail.price}" pattern="#,##0₫"/>
                                                                </td>
                                                                <td class="text-end fw-bold">
                                                                    <c:set var="lineTotal" value="${detail.price.multiply(detail.quantity)}" />
                                                                    <fmt:formatNumber value="${lineTotal}" pattern="#,##0₫"/>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </c:if>
                                        
                                        <div class="row">
                                            <div class="col-md-6">
                                                <h6 class="small text-muted">Địa chỉ giao hàng</h6>
                                                <p class="mb-2">${order.address}</p>
                                                <c:if test="${not empty order.note}">
                                                    <h6 class="small text-muted mt-3">Ghi chú</h6>
                                                    <p class="mb-0"><em>${order.note}</em></p>
                                                </c:if>
                                            </div>
                                            
                                            <div class="col-md-6">
                                                <h6 class="small text-muted">Phương thức thanh toán</h6>
                                                <p class="mb-2">${order.payment_method}</p>
                                                
                                                <h6 class="small text-muted mt-3">Thời gian</h6>
                                                <div class="small">
                                                    <div><strong>Ngày đặt:</strong> 
                                                        <fmt:formatDate value="${order.createdDate}" pattern="dd/MM/yyyy HH:mm"/>
                                                    </div>
                                                    <c:if test="${not empty order.updatedDate}">
                                                        <div><strong>Cập nhật:</strong> 
                                                            <fmt:formatDate value="${order.updatedDate}" pattern="dd/MM/yyyy HH:mm"/>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

        <div class="mt-4">
            <a href="${pageContext.request.contextPath}/user/profile" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Quay lại trang cá nhân
            </a>
        </div>
    </div>
</div>

<style>
.badge { 
    font-size: 0.85rem; 
    padding: 0.35rem 0.65rem; 
}
.table-responsive {
    max-height: 400px;
    overflow-y: auto;
}
.collapse {
    transition: height 0.3s ease;
}
</style>