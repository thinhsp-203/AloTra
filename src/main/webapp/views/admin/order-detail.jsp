<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<%-- Header with Order ID --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
    <div>
        <h1 class="h3 mb-1 text-gray-800">
            <i class="fas fa-receipt text-primary" style="margin-right: 10px;"></i>Chi tiết đơn hàng #${order.order_id}
        </h1>
        <p class="text-muted mb-0">
            <i class="far fa-calendar" style="margin-right: 10px;"></i>
            Ngày đặt: <fmt:formatDate value="${order.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
        </p>
    </div>
    <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline-secondary">
        <i class="fas fa-arrow-left" style="margin-right: 10px;"></i>Quay lại danh sách
    </a>
</div>

<%-- Alert Messages --%>
<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-check-circle" style="margin-right: 10px;"></i><strong>Thành công!</strong> ${sessionScope.success}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="success" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.error}">
    <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
        <i class="fas fa-exclamation-circle" style="margin-right: 10px;"></i><strong>Lỗi!</strong> ${sessionScope.error}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
    <c:remove var="error" scope="session"/>
</c:if>

<div class="row g-4">
    <%-- Left Column: Customer Info & Products --%>
    <div class="col-lg-8">
        <%-- Customer Information Card --%>
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-gradient-primary text-white py-3" 
                 style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                <h6 class="m-0 font-weight-bold">
                    <i class="fas fa-user-circle" style="margin-right: 10px;"></i>Thông tin khách hàng
                </h6>
            </div>
            <div class="card-body p-4">
                <div class="row g-4">
                    <div class="col-md-6">
                        <div class="d-flex align-items-center">
                            <i class="fas fa-user text-primary me-5" style="font-size: 1.2rem; width: 28px; text-align: center; flex-shrink: 0;"></i>
                            <div class="fs-5">
                                <strong>Họ và tên:</strong> ${order.fullname}
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="d-flex align-items-center">
                            <i class="fas fa-phone text-success me-5" style="font-size: 1.2rem; width: 28px; text-align: center; flex-shrink: 0;"></i>
                            <div class="fs-5">
                                <strong>Số điện thoại:</strong> ${order.phone}
                            </div>
                        </div>
                    </div>
                    <div class="col-12">
                        <div class="d-flex align-items-start">
                            <i class="fas fa-map-marker-alt text-warning me-5" style="font-size: 1.2rem; width: 28px; text-align: center; flex-shrink: 0; padding-top: 2px;"></i>
                            <div class="flex-grow-1 fs-5">
                                <strong>Địa chỉ giao hàng:</strong> ${order.address}
                            </div>
                        </div>
                    </div>
                    <c:if test="${not empty order.note}">
                        <div class="col-12">
                            <div class="d-flex align-items-start p-3 bg-info bg-opacity-10 rounded border-start border-info border-3">
                                <i class="fas fa-sticky-note text-info me-5" style="font-size: 1.2rem; width: 28px; text-align: center; flex-shrink: 0; padding-top: 2px;"></i>
                                <div class="flex-grow-1 fs-5">
                                    <strong>Ghi chú đặc biệt:</strong> <em class="text-dark">${order.note}</em>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <%-- Order Items Card --%>
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 font-weight-bold text-primary">
                    <i class="fas fa-utensils" style="margin-right: 10px;"></i>Sản phẩm đã đặt
                    <span class="ms-2">${fn:length(details)} món</span>
                </h6>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4" style="width: 5%;">#</th>
                                <th>Sản phẩm</th>
                                <th style="width: 100px;" class="text-center">Size</th>
                                <th style="width: 180px;">Topping</th>
                                <th style="width: 100px;" class="text-center">Số lượng</th>
                                <th style="width: 130px;" class="text-end">Đơn giá</th>
                                <th style="width: 140px;" class="text-end pe-4">Thành tiền</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${details}" varStatus="loop">
                                <tr class="border-bottom">
                                    <td class="ps-4">
                                        ${loop.index + 1}
                                    </td>
                                    <td>
                                        <strong>${item.product_name}</strong>
                                    </td>
                                    <td class="text-center">
                                        <c:choose>
                                            <c:when test="${not empty item.size_name}">
                                                ${item.size_name}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">-</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${not empty item.toppings}">
                                                <small class="text-muted">${item.toppings}</small>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">-</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        ${item.quantity}
                                    </td>
                                    <td class="text-end">
                                        <fmt:formatNumber value="${item.price}" pattern="#,##0₫"/>
                                    </td>
                                    <td class="text-end fw-bold text-success pe-4">
                                        <fmt:formatNumber value="${item.price * item.quantity}" pattern="#,##0₫"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                        <tfoot class="table-light">
                            <%-- Sử dụng helper methods từ Orders model --%>
                            <c:set var="subtotal" value="${order.subtotal}"/>
                            <c:set var="estimatedShipping" value="${order.estimatedShippingFee}"/>
                            <c:set var="discountAmount" value="${order.estimatedDiscount}"/>
                            
                            <tr>
                                <td colspan="6" class="text-end fw-bold ps-4">
                                    <span class="fs-5">Tổng sản phẩm:</span>
                                </td>
                                <td class="text-end pe-4">
                                    <span class="fs-5 fw-bold">
                                        <fmt:formatNumber value="${subtotal}" pattern="#,##0₫"/>
                                    </span>
                                </td>
                            </tr>
                            <c:if test="${discountAmount != null && discountAmount > 0}">
                                <tr>
                                    <td colspan="6" class="text-end fw-bold text-danger ps-4">
                                        <span class="fs-5">Giảm giá:</span>
                                    </td>
                                    <td class="text-end text-danger pe-4">
                                        <span class="fs-5 fw-bold">
                                            -<fmt:formatNumber value="${discountAmount}" pattern="#,##0₫"/>
                                        </span>
                                    </td>
                                </tr>
                            </c:if>
                            <tr>
                                <td colspan="6" class="text-end fw-bold ps-4">
                                    <span class="fs-5">Phí vận chuyển:</span>
                                </td>
                                <td class="text-end pe-4">
                                    <span class="fs-5 fw-bold">
                                        <fmt:formatNumber value="${estimatedShipping}" pattern="#,##0₫"/>
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="6" class="text-end fw-bold ps-4">
                                    <span class="fs-5">Tổng cộng:</span>
                                </td>
                                <td class="text-end pe-4">
                                    <span class="fs-4 fw-bold text-success">
                                        <fmt:formatNumber value="${order.total_amount}" pattern="#,##0₫"/>
                                    </span>
                                </td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <%-- Right Column: Status & Payment --%>
    <div class="col-lg-4">
        <%-- Order Status Card --%>
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 font-weight-bold text-primary">
                    <i class="fas fa-tasks" style="margin-right: 10px;"></i>Trạng thái đơn hàng
                </h6>
            </div>
            <div class="card-body p-4">
                <div class="mb-4">
                    <label class="form-label fw-semibold mb-3">Trạng thái hiện tại</label>
                    <c:choose>
                        <c:when test="${order.order_status eq 'Chờ xác nhận'}">
                            <div class="alert alert-secondary mb-0 text-center py-3">
                                <i class="fas fa-hourglass-half fa-2x mb-2"></i>
                                <div class="fw-bold">${order.order_status}</div>
                            </div>
                        </c:when>
                        <c:when test="${order.order_status eq 'Đang chuẩn bị'}">
                            <div class="alert alert-info mb-0 text-center py-3">
                                <i class="fas fa-utensils fa-2x mb-2"></i>
                                <div class="fw-bold">${order.order_status}</div>
                            </div>
                        </c:when>
                        <c:when test="${order.order_status eq 'Đang giao'}">
                            <div class="alert alert-primary mb-0 text-center py-3">
                                <i class="fas fa-motorcycle fa-2x mb-2"></i>
                                <div class="fw-bold">${order.order_status}</div>
                            </div>
                        </c:when>
                        <c:when test="${order.order_status eq 'Hoàn thành'}">
                            <div class="alert alert-success mb-0 text-center py-3">
                                <i class="fas fa-check-circle fa-2x mb-2"></i>
                                <div class="fw-bold">${order.order_status}</div>
                            </div>
                        </c:when>
                        <c:when test="${order.order_status eq 'Hủy bởi khách'}">
                            <div class="alert alert-warning mb-0 text-center py-3">
                                <i class="fas fa-user-times fa-2x mb-2"></i>
                                <div class="fw-bold">${order.order_status}</div>
                            </div>
                        </c:when>
                        <c:when test="${order.order_status eq 'Hủy bởi shop'}">
                            <div class="alert alert-danger mb-0 text-center py-3">
                                <i class="fas fa-store-slash fa-2x mb-2"></i>
                                <div class="fw-bold">${order.order_status}</div>
                            </div>
                        </c:when>
                        <c:when test="${order.order_status eq 'Từ chối'}">
                            <div class="alert alert-danger mb-0 text-center py-3">
                                <i class="fas fa-ban fa-2x mb-2"></i>
                                <div class="fw-bold">${order.order_status}</div>
                            </div>
                        </c:when>
                        <c:when test="${order.order_status eq 'Hủy Đơn'}">
                            <%-- Fallback cho status cũ --%>
                            <div class="alert alert-danger mb-0 text-center py-3">
                                <i class="fas fa-times-circle fa-2x mb-2"></i>
                                <div class="fw-bold">${order.order_status}</div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-secondary mb-0 text-center py-3">
                                <i class="fas fa-question-circle fa-2x mb-2"></i>
                                <div class="fw-bold">${order.order_status}</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <%-- UI theo workflow mới --%>
                <c:choose>
                    <%-- Trạng thái: Chờ xác nhận -> Hiển thị 2 nút: Xác nhận / Từ chối --%>
                    <c:when test="${order.order_status eq 'Chờ xác nhận'}">
                        <div class="d-grid gap-2">
                            <form method="post" action="${pageContext.request.contextPath}/admin/orders/confirm" 
                                  onsubmit="return confirm('Xác nhận đơn hàng #${order.order_id}?')" style="margin: 0;">
                                <input type="hidden" name="orderId" value="${order.order_id}">
                                <button type="submit" class="btn btn-success w-100 btn-lg">
                                    <i class="fas fa-check-circle" style="margin-right: 10px;"></i>Xác nhận đơn
                                </button>
                            </form>
                            
                            <form method="post" action="${pageContext.request.contextPath}/admin/orders/reject"
                                  onsubmit="return confirm('Bạn có chắc chắn muốn từ chối đơn hàng #${order.order_id}?')" style="margin: 0;">
                                <input type="hidden" name="orderId" value="${order.order_id}">
                                <button type="submit" class="btn btn-danger w-100 btn-lg">
                                    <i class="fas fa-times-circle" style="margin-right: 10px;"></i>Không nhận đơn
                                </button>
                            </form>
                        </div>
                    </c:when>
                    
                    <%-- Trạng thái: Đang chuẩn bị -> Dropdown cập nhật: Đang giao / Hoàn thành / Hủy bởi shop --%>
                    <c:when test="${order.order_status eq 'Đang chuẩn bị'}">
                        <form method="post" action="${pageContext.request.contextPath}/admin/orders/status/update">
                            <input type="hidden" name="orderId" value="${order.order_id}">
                            
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Cập nhật trạng thái</label>
                                <select class="form-select form-select-lg" name="status" required>
                                    <option value="Đang giao">Đang giao</option>
                                    <option value="Hoàn thành">Hoàn thành</option>
                                    <option value="Hủy bởi shop">Hủy bởi shop</option>
                                </select>
                            </div>
                            
                            <button type="submit" class="btn btn-primary w-100 btn-lg">
                                <i class="fas fa-save" style="margin-right: 10px;"></i>Cập nhật trạng thái
                            </button>
                        </form>
                    </c:when>
                    
                    <%-- Trạng thái: Đang giao -> Chỉ có thể chuyển sang Hoàn thành hoặc Hủy bởi shop --%>
                    <c:when test="${order.order_status eq 'Đang giao'}">
                        <form method="post" action="${pageContext.request.contextPath}/admin/orders/status/update">
                            <input type="hidden" name="orderId" value="${order.order_id}">
                            
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Cập nhật trạng thái</label>
                                <select class="form-select form-select-lg" name="status" required>
                                    <option value="Hoàn thành">Hoàn thành</option>
                                    <option value="Hủy bởi shop">Hủy bởi shop</option>
                                </select>
                            </div>
                            
                            <button type="submit" class="btn btn-primary w-100 btn-lg">
                                <i class="fas fa-save" style="margin-right: 10px;"></i>Cập nhật trạng thái
                            </button>
                        </form>
                    </c:when>
                    
                    <%-- Final states: Hoàn thành, Hủy bởi khách, Hủy bởi shop, Từ chối -> Chỉ xem --%>
                    <c:when test="${order.order_status eq 'Hoàn thành' or order.order_status eq 'Hủy bởi khách' or order.order_status eq 'Hủy bởi shop' or order.order_status eq 'Từ chối' or order.order_status eq 'Hủy Đơn'}">
                        <div class="alert alert-info border-info">
                            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>
                            <strong>Đơn hàng đã kết thúc</strong>
                            <p class="mb-0 mt-2 small">Không thể cập nhật trạng thái cho đơn hàng đã hoàn thành hoặc đã hủy.</p>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Trạng thái (chỉ xem)</label>
                            <select class="form-select form-select-lg" disabled>
                                <option value="${order.order_status}" selected>${order.order_status}</option>
                            </select>
                        </div>
                    </c:when>
                    
                    <%-- Fallback: các trạng thái khác (nếu có) --%>
                    <c:otherwise>
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle" style="margin-right: 10px;"></i>
                            Trạng thái không xác định: ${order.order_status}
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <%-- Payment Information Card --%>
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-header bg-white border-bottom py-3">
                <h6 class="m-0 font-weight-bold text-primary">
                    <i class="fas fa-money-bill-wave" style="margin-right: 10px;"></i>Thông tin thanh toán
                </h6>
            </div>
            <div class="card-body p-4">
                <div class="mb-4">
                    <label class="form-label fw-semibold mb-2">Phương thức thanh toán</label>
                    <div>
                        <c:choose>
                            <c:when test="${order.payment_method eq 'COD'}">
                                <span class="badge bg-secondary text-white fs-6 px-3 py-2">
                                    <i class="fas fa-money-bill" style="margin-right: 5px;"></i>Thanh toán khi nhận hàng (COD)
                                </span>
                            </c:when>
                            <c:when test="${order.payment_method eq 'Online'}">
                                <span class="badge bg-primary text-white fs-6 px-3 py-2">
                                    <i class="fas fa-credit-card" style="margin-right: 5px;"></i>Thanh toán online
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge bg-secondary fs-6 px-3 py-2">${order.payment_method}</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="mb-4">
                    <label class="form-label fw-semibold mb-2">Trạng thái thanh toán</label>
                    <c:choose>
                        <c:when test="${order.payment_status eq 'Đã thanh toán'}">
                            <div class="alert alert-success mb-0 text-center py-3">
                                <i class="fas fa-check-circle fa-2x mb-2"></i>
                                <div class="fw-bold">${order.payment_status}</div>
                            </div>
                        </c:when>
                        <c:when test="${order.payment_status eq 'Đã hoàn tiền'}">
                            <div class="alert alert-info mb-0 text-center py-3">
                                <i class="fas fa-undo fa-2x mb-2"></i>
                                <div class="fw-bold">${order.payment_status}</div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning mb-0 text-center py-3">
                                <i class="fas fa-clock fa-2x mb-2"></i>
                                <div class="fw-bold">${order.payment_status}</div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
                
                <c:choose>
                    <%-- Final states: không thể cập nhật payment status --%>
                    <c:when test="${order.order_status eq 'Hoàn thành' or order.order_status eq 'Hủy bởi khách' or order.order_status eq 'Hủy bởi shop' or order.order_status eq 'Từ chối' or order.order_status eq 'Hủy Đơn'}">
                        <div class="alert alert-info border-info">
                            <i class="fas fa-info-circle" style="margin-right: 10px;"></i>
                            <strong>Đơn hàng đã kết thúc</strong>
                            <p class="mb-0 mt-2 small">Không thể cập nhật trạng thái thanh toán cho đơn hàng đã hoàn thành hoặc đã hủy.</p>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Trạng thái (chỉ xem)</label>
                            <select class="form-select form-select-lg" disabled>
                                <option value="${order.payment_status}" selected>${order.payment_status}</option>
                            </select>
                            <div class="form-text mt-2">
                                <%-- Chỉ hiển thị thông báo khi đơn hàng BỊ HỦY (không phải Hoàn thành) --%>
                                <c:if test="${(order.order_status eq 'Hủy bởi khách' or order.order_status eq 'Hủy bởi shop' or order.order_status eq 'Từ chối' or order.order_status eq 'Hủy Đơn') && order.payment_method eq 'Online' && order.payment_status eq 'Đã thanh toán'}">
                                    <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>
                                    Đơn hàng đã thanh toán online, trạng thái đã được tự động chuyển sang "Đã hoàn tiền" khi hủy.
                                </c:if>
                                <c:if test="${(order.order_status eq 'Hủy bởi khách' or order.order_status eq 'Hủy bởi shop' or order.order_status eq 'Từ chối' or order.order_status eq 'Hủy Đơn') && order.payment_method eq 'COD' && order.payment_status eq 'Chưa thanh toán'}">
                                    <i class="fas fa-info-circle text-info" style="margin-right: 5px;"></i>
                                    Đơn hàng COD đã hủy, trạng thái là "Chưa thanh toán" vì chưa thu tiền.
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${pageContext.request.contextPath}/admin/orders/payment/update">
                            <input type="hidden" name="orderId" value="${order.order_id}">
                            
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Cập nhật trạng thái thanh toán</label>
                                <select class="form-select form-select-lg" name="paymentStatus" required>
                                    <option value="Chưa thanh toán" 
                                            ${order.payment_status eq 'Chưa thanh toán' ? 'selected' : ''}>
                                        Chưa thanh toán
                                    </option>
                                    <option value="Đã thanh toán" 
                                            ${order.payment_status eq 'Đã thanh toán' ? 'selected' : ''}>
                                        Đã thanh toán
                                    </option>
                                    <c:if test="${order.payment_method eq 'Online'}">
                                        <option value="Đã hoàn tiền" 
                                                ${order.payment_status eq 'Đã hoàn tiền' ? 'selected' : ''}>
                                            Đã hoàn tiền
                                        </option>
                                    </c:if>
                                    <option value="Thất bại" 
                                            ${order.payment_status eq 'Thất bại' ? 'selected' : ''}>
                                        Thất bại
                                    </option>
                                </select>
                            </div>
                            
                            <button type="submit" class="btn btn-success w-100 btn-lg">
                                <i class="fas fa-save" style="margin-right: 10px;"></i>Cập nhật thanh toán
                            </button>
                        </form>
                    </c:otherwise>
                </c:choose>

                <hr class="my-4">

                <div class="small text-muted">
                    <div class="mb-3 d-flex align-items-center">
                        <i class="fas fa-calendar-alt text-primary me-5" style="width: 28px; text-align: center; flex-shrink: 0;"></i>
                        <div>
                            <div class="fw-semibold">Ngày đặt</div>
                            <div><fmt:formatDate value="${order.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></div>
                        </div>
                    </div>
                    <div class="d-flex align-items-center">
                        <i class="fas fa-edit text-primary me-5" style="width: 28px; text-align: center; flex-shrink: 0;"></i>
                        <div>
                            <div class="fw-semibold">Cập nhật lần cuối</div>
                            <div><fmt:formatDate value="${order.updatedDateAsDate}" pattern="dd/MM/yyyy HH:mm"/></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
