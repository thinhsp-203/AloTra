<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<h1 class="h3 mb-4 text-gray-800">Chi tiết đơn hàng
	#${order.order_id}</h1>

<div class="row">
	<div class="col-lg-8">
		<div class="card shadow mb-4">
			<div class="card-header py-3">
				<h6 class="m-0 font-weight-bold text-primary">Thông tin khách
					hàng</h6>
			</div>
			<div class="card-body">
				<div class="row">
					<div class="col-6 mb-2">
						<strong>Họ và tên:</strong> ${order.fullname}
					</div>
					<div class="col-6 mb-2">
						<strong>Số điện thoại:</strong> ${order.phone}
					</div>
					<div class="col-12 mb-2">
						<strong>Địa chỉ:</strong> ${order.address}
					</div>
					<c:if test="${not empty order.note}">
						<div class="col-12">
							<strong>Ghi chú:</strong> <em>${order.note}</em>
						</div>
					</c:if>
				</div>
			</div>
		</div>

		<div class="card shadow mb-4">
			<div class="card-header py-3">
				<h6 class="m-0 font-weight-bold text-primary">Sản phẩm đã đặt</h6>
			</div>
			<div class="card-body p-0">
				<div class="table-responsive">
					<table class="table table-sm mb-0">
						<thead>
							<tr>
								<th>Sản phẩm</th>
								<th>Size</th>
								<th>Topping</th>
								<th>Số lượng</th>
								<th>Đơn giá</th>
								<th>Thành tiền</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="item" items="${details}">
								<tr>
									<td>${item.product_name}</td>
									<td>${empty item.size_name ? '-' : item.size_name}</td>
									<td>${empty item.toppings ? '-' : item.toppings}</td>
									<td>${item.quantity}</td>
									<td><fmt:formatNumber value="${item.price}"
											pattern="#,##0 '₫'" /></td>
									<td class="fw-bold"><fmt:formatNumber
											value="${item.price * item.quantity}" pattern="#,##0 '₫'" />
									</td>
								</tr>
							</c:forEach>
						</tbody>
						<tfoot class="table-light">
							<tr>
								<th colspan="5" class="text-end">Tổng cộng:</th>
								<th class="text-primary"><fmt:formatNumber
										value="${order.total_amount}" pattern="#,##0 '₫'" /></th>
							</tr>
						</tfoot>
					</table>
				</div>
			</div>
		</div>
	</div>

	<div class="col-lg-4">
		<div class="card shadow mb-4">
			<div class="card-header py-3">
				<h6 class="m-0 font-weight-bold text-primary">Trạng thái đơn
					hàng</h6>
			</div>
			<div class="card-body">
				<form method="post"
					action="${pageContext.request.contextPath}/admin/orders">
					<input type="hidden" name="action" value="updateStatus"> <input
						type="hidden" name="orderId" value="${order.order_id}">

					<div class="mb-3">
						<label class="form-label">Trạng thái hiện tại</label> <select
							class="form-select" name="status">
							<option value="Chờ xác nhận"
								${order.order_status eq 'Chờ xác nhận' ? 'selected' : ''}>Chờ
								xác nhận</option>
							<option value="Đang chuẩn bị"
								${order.order_status eq 'Đang chuẩn bị' ? 'selected' : ''}>Đang
								chuẩn bị</option>
							<option value="Đang giao"
								${order.order_status eq 'Đang giao' ? 'selected' : ''}>Đang
								giao</option>
							<option value="Hoàn thành"
								${order.order_status eq 'Hoàn thành' ? 'selected' : ''}>Hoàn
								thành</option>
							<option value="Đã hủy"
								${order.order_status eq 'Đã hủy' ? 'selected' : ''}>Đã
								hủy</option>
						</select>
					</div>
					<button type="submit" class="btn btn-primary w-100">Cập
						nhật trạng thái</button>
				</form>
			</div>
		</div>

		<div class="card shadow mb-4">
			<div class="card-header py-3">
				<h6 class="m-0 font-weight-bold text-primary">Thanh toán</h6>
			</div>
			<div class="card-body">
				<form method="post"
					action="${pageContext.request.contextPath}/admin/orders">
					<input type="hidden" name="action" value="updatePayment"> <input
						type="hidden" name="orderId" value="${order.order_id}">

					<div class="mb-2">
						<strong>Phương thức:</strong> ${order.payment_method}
					</div>
					<div class="mb-3">
						<label class="form-label">Trạng thái thanh toán</label> <select
							class="form-select" name="paymentStatus">
							<option value="Chưa thanh toán"
								${order.payment_status eq 'Chưa thanh toán' ? 'selected' : ''}>Chưa
								thanh toán</option>
							<option value="Đã thanh toán"
								${order.payment_status eq 'Đã thanh toán' ? 'selected' : ''}>Đã
								thanh toán</option>
						</select>
					</div>
					<button type="submit" class="btn btn-success w-100">Cập
						nhật thanh toán</button>
				</form>

				<hr class="my-3">

				<div class="small text-muted">
					<div>
						<strong>Ngày đặt:</strong>
						<fmt:formatDate value="${order.createdDateAsDate}"
							pattern="dd/MM/yyyy HH:mm" />
					</div>
					<div>
						<strong>Cập nhật:</strong>
						<fmt:formatDate value="${order.updatedDateAsDate}"
							pattern="dd/MM/yyyy HH:mm" />
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
