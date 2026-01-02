<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<fmt:setLocale value="vi_VN" />
<fmt:setTimeZone value="Asia/Ho_Chi_Minh" />

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<%-- Header --%>
<div class="d-flex justify-content-between align-items-center mb-4" style="margin-right: 20px;">
	<div>
		<h1 class="h3 mb-1 text-gray-800">
			<i class="fas fa-tachometer-alt text-primary" style="margin-right: 10px;"></i>Dashboard
		</h1>
		<p class="text-muted mb-0">Tổng quan doanh thu, đơn hàng và khách hàng</p>
	</div>
	<fmt:formatDate value="<%=new java.util.Date()%>" pattern="EEEE, dd MMMM yyyy" var="todayFormatted"/>
	<span class="text-muted"><c:out value="${todayFormatted}"/></span>
</div>

<%-- Alert Messages --%>
<c:if test="${stats.pendingOrders > 0}">
	<div class="alert alert-warning alert-dismissible fade show shadow-sm" role="alert">
		<i class="fas fa-exclamation-triangle" style="margin-right: 10px;"></i>
		<strong>Có <strong class="text-danger">${stats.pendingOrders}</strong> đơn hàng đang chờ xác nhận.</strong>
		<a href="${pageContext.request.contextPath}/admin/orders?status=Chờ xác nhận" class="alert-link" style="margin-left: 10px;">
			Xem ngay <i class="fas fa-arrow-right" style="margin-left: 5px;"></i>
		</a>
		<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
	</div>
</c:if>

<!-- ========== DOANH THU HÔM NAY / TUẦN / THÁNG ========== -->
<div class="row mb-4">
	<div class="col-xl-3 col-md-6 mb-4">
		<div class="card border-left-primary shadow h-100 py-2">
			<div class="card-body">
				<div class="row no-gutters align-items-center">
					<div class="col mr-2">
						<div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
							Doanh thu hôm nay
						</div>
						<div class="h5 mb-0 font-weight-bold text-gray-800">
							<fmt:formatNumber value="${stats.revenueToday}" pattern="#,##0" />₫
						</div>
						<small class="text-muted">
							<i class="fas fa-calendar-day" style="margin-right: 5px;"></i>Hôm nay
						</small>
					</div>
					<div class="col-auto">
						<i class="fas fa-coins fa-2x text-gray-300"></i>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="col-xl-3 col-md-6 mb-4">
		<div class="card border-left-success shadow h-100 py-2">
			<div class="card-body">
				<div class="row no-gutters align-items-center">
					<div class="col mr-2">
						<div class="text-xs font-weight-bold text-success text-uppercase mb-1">
							Doanh thu tuần này
						</div>
						<div class="h5 mb-0 font-weight-bold text-gray-800">
							<fmt:formatNumber value="${stats.revenueWeek}" pattern="#,##0" />₫
						</div>
						<small class="text-muted">
							<i class="fas fa-calendar-week" style="margin-right: 5px;"></i>Tuần này
						</small>
					</div>
					<div class="col-auto">
						<i class="fas fa-chart-line fa-2x text-gray-300"></i>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="col-xl-3 col-md-6 mb-4">
		<div class="card border-left-info shadow h-100 py-2">
			<div class="card-body">
				<div class="row no-gutters align-items-center">
					<div class="col mr-2">
						<div class="text-xs font-weight-bold text-info text-uppercase mb-1">
							Doanh thu tháng này
						</div>
						<div class="h5 mb-0 font-weight-bold text-gray-800">
							<fmt:formatNumber value="${stats.revenueMonth}" pattern="#,##0" />₫
						</div>
						<small class="text-muted">
							<i class="fas fa-calendar-alt" style="margin-right: 5px;"></i>Tháng này
						</small>
					</div>
					<div class="col-auto">
						<i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="col-xl-3 col-md-6 mb-4">
		<div class="card border-left-warning shadow h-100 py-2">
			<div class="card-body">
				<div class="row no-gutters align-items-center">
					<div class="col mr-2">
						<div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
							Tổng doanh thu
						</div>
						<div class="h5 mb-0 font-weight-bold text-gray-800">
							<fmt:formatNumber value="${stats.totalRevenue}" pattern="#,##0" />₫
						</div>
						<small class="text-muted">
							<i class="fas fa-infinity" style="margin-right: 5px;"></i>Tất cả thời gian
						</small>
					</div>
					<div class="col-auto">
						<i class="fas fa-trophy fa-2x text-gray-300"></i>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- ========== ĐƠN HÀNG VÀ KHÁCH HÀNG ========== -->
<div class="row mb-4">
	<div class="col-xl-3 col-md-6 mb-4">
		<div class="card border-left-danger shadow h-100 py-2">
			<div class="card-body">
				<div class="row no-gutters align-items-center">
					<div class="col mr-2">
						<div class="text-xs font-weight-bold text-danger text-uppercase mb-1">
							Đơn hàng hôm nay
						</div>
						<div class="h5 mb-0 font-weight-bold text-gray-800">${stats.ordersToday}</div>
						<small class="text-muted">
							<i class="fas fa-shopping-cart" style="margin-right: 5px;"></i>Đã đặt hôm nay
						</small>
					</div>
					<div class="col-auto">
						<i class="fas fa-clipboard-list fa-2x text-gray-300"></i>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="col-xl-3 col-md-6 mb-4">
		<div class="card border-left-warning shadow h-100 py-2">
			<div class="card-body">
				<div class="row no-gutters align-items-center">
					<div class="col mr-2">
						<div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
							Chờ xác nhận
						</div>
						<div class="h5 mb-0 font-weight-bold text-gray-800">${stats.pendingOrders}</div>
						<small class="text-muted">
							<i class="fas fa-clock" style="margin-right: 5px;"></i>Cần xử lý
						</small>
					</div>
					<div class="col-auto">
						<i class="fas fa-hourglass-half fa-2x text-gray-300"></i>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="col-xl-3 col-md-6 mb-4">
		<div class="card border-left-info shadow h-100 py-2">
			<div class="card-body">
				<div class="row no-gutters align-items-center">
					<div class="col mr-2">
						<div class="text-xs font-weight-bold text-info text-uppercase mb-1">
							Đang xử lý
						</div>
						<div class="h5 mb-0 font-weight-bold text-gray-800">${stats.processingOrders}</div>
						<small class="text-muted">
							<i class="fas fa-cog" style="margin-right: 5px;"></i>Đang chuẩn bị
						</small>
					</div>
					<div class="col-auto">
						<i class="fas fa-shipping-fast fa-2x text-gray-300"></i>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="col-xl-3 col-md-6 mb-4">
		<div class="card border-left-success shadow h-100 py-2">
			<div class="card-body">
				<div class="row no-gutters align-items-center">
					<div class="col mr-2">
						<div class="text-xs font-weight-bold text-success text-uppercase mb-1">
							Khách hàng
						</div>
						<div class="h5 mb-0 font-weight-bold text-gray-800">${stats.totalCustomers}</div>
						<small class="text-muted">
							<i class="fas fa-users" style="margin-right: 5px;"></i>Tổng: ${stats.totalCustomers}
							<span class="text-success" style="margin-left: 10px;">+${stats.newCustomersThisMonth} tháng này</span>
						</small>
					</div>
					<div class="col-auto">
						<i class="fas fa-user-friends fa-2x text-gray-300"></i>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- ========== BIỂU ĐỒ DOANH THU ========== -->
<div class="row mb-4">
	<%-- Doanh thu theo giờ trong ngày (quan trọng cho F&B) --%>
	<div class="col-xl-6 col-lg-6 mb-4">
		<div class="card shadow-sm border-0 mb-4">
			<div class="card-header bg-white border-bottom py-3">
				<h6 class="m-0 font-weight-bold text-primary">
					<i class="fas fa-clock" style="margin-right: 10px;"></i>Doanh thu theo giờ hôm nay
				</h6>
			</div>
			<div class="card-body">
				<div class="chart-area" style="height: 250px;">
					<canvas id="hourlyRevenueChart"></canvas>
				</div>
			</div>
		</div>
	</div>

	<%-- Doanh thu 7 ngày qua --%>
	<div class="col-xl-6 col-lg-6 mb-4">
		<div class="card shadow-sm border-0 mb-4">
			<div class="card-header bg-white border-bottom py-3">
				<h6 class="m-0 font-weight-bold text-primary">
					<i class="fas fa-chart-area" style="margin-right: 10px;"></i>Doanh thu 7 ngày qua
				</h6>
			</div>
			<div class="card-body">
				<div class="chart-area" style="height: 250px;">
					<canvas id="dailyRevenueChart"></canvas>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="row mb-4">
	<%-- Doanh thu 6 tháng gần nhất --%>
	<div class="col-xl-8 col-lg-7">
		<div class="card shadow-sm border-0 mb-4">
			<div class="card-header bg-white border-bottom py-3">
				<h6 class="m-0 font-weight-bold text-primary">
					<i class="fas fa-chart-bar" style="margin-right: 10px;"></i>Doanh thu 6 tháng gần nhất
				</h6>
			</div>
			<div class="card-body">
				<div class="chart-area" style="height: 300px;">
					<canvas id="revenueChart"></canvas>
				</div>
			</div>
		</div>
	</div>

	<%-- Top 10 sản phẩm bán chạy tháng này --%>
	<div class="col-xl-4 col-lg-5">
		<div class="card shadow-sm border-0 mb-4">
			<div class="card-header bg-white border-bottom py-3">
				<h6 class="m-0 font-weight-bold text-primary">
					<i class="fas fa-fire" style="margin-right: 10px;"></i>Top 10 sản phẩm bán chạy (tháng này)
				</h6>
			</div>
			<div class="card-body p-0">
				<div class="table-responsive" style="max-height: 300px; overflow-y: auto;">
					<table class="table table-hover align-middle mb-0">
						<thead class="table-light sticky-top">
							<tr>
								<th class="ps-4">#</th>
								<th>Sản phẩm</th>
								<th class="text-end pe-4">Đã bán</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty stats.topProducts && fn:length(stats.topProducts) > 0}">
									<c:forEach var="item" items="${stats.topProducts}" varStatus="status">
										<tr class="border-bottom">
											<td class="ps-4">
												<span class="badge bg-primary text-white px-3 py-2">${status.index + 1}</span>
											</td>
											<td>
												<span class="fw-semibold fs-5">${item[0]}</span>
											</td>
											<td class="text-end pe-4">
												<span class="badge bg-success text-white px-3 py-2">${item[1]} sp</span>
											</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan="3" class="text-center py-5">
											<div class="py-4">
												<i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
												<h5 class="text-muted mb-2">Không có dữ liệu</h5>
												<p class="text-muted small mb-0">Chưa có sản phẩm nào được bán trong tháng này</p>
											</div>
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>

<%-- Đơn hàng mới nhất --%>
<div class="row mb-4">
	<div class="col-12 mb-4">
		<div class="card shadow-sm border-0 mb-4">
			<div class="card-header bg-white border-bottom py-3">
				<h6 class="m-0 font-weight-bold text-primary">
					<i class="fas fa-shopping-bag" style="margin-right: 10px;"></i>Đơn hàng mới nhất
				</h6>
			</div>
			<div class="card-body p-0">
				<div class="table-responsive">
					<table class="table table-hover align-middle mb-0">
						<thead class="table-light">
							<tr>
								<th class="ps-4">Mã đơn</th>
								<th>Khách hàng</th>
								<th>Tổng tiền</th>
								<th>Trạng thái</th>
								<th class="pe-4">Thời gian</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${not empty stats.recentOrders && fn:length(stats.recentOrders) > 0}">
									<c:forEach var="order" items="${stats.recentOrders}">
										<tr class="border-bottom">
											<td class="ps-4">
												<span class="fw-semibold">#${order.order_id}</span>
											</td>
											<td>
												<span class="fw-semibold">${order.fullname}</span>
											</td>
											<td>
												<strong class="text-success fs-5">
													<fmt:formatNumber value="${order.total_amount}" pattern="#,##0" />₫
												</strong>
											</td>
											<td>
												<c:choose>
													<c:when test="${order.order_status == 'Chờ xác nhận'}">
														<span class="badge bg-warning text-white px-3 py-2">${order.order_status}</span>
													</c:when>
													<c:when test="${order.order_status == 'Đang xử lý'}">
														<span class="badge bg-info text-white px-3 py-2">${order.order_status}</span>
													</c:when>
													<c:when test="${order.order_status == 'Hoàn thành'}">
														<span class="badge bg-success text-white px-3 py-2">${order.order_status}</span>
													</c:when>
													<c:when test="${order.order_status == 'Đã hủy'}">
														<span class="badge bg-danger text-white px-3 py-2">${order.order_status}</span>
													</c:when>
													<c:otherwise>
														<span class="badge bg-secondary text-white px-3 py-2">${order.order_status}</span>
													</c:otherwise>
												</c:choose>
											</td>
											<td class="pe-4">
												<small class="text-muted">
													<fmt:formatDate value="${order.createdDateAsDate}" pattern="dd/MM/yyyy HH:mm"/>
												</small>
											</td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise>
									<tr>
										<td colspan="5" class="text-center py-5">
											<div class="py-4">
												<i class="fas fa-inbox fa-4x text-muted mb-3 d-block"></i>
												<h5 class="text-muted mb-2">Không có đơn hàng nào</h5>
												<p class="text-muted small mb-0">Chưa có đơn hàng nào được đặt</p>
											</div>
										</td>
									</tr>
								</c:otherwise>
							</c:choose>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    // ========== BIỂU ĐỒ DOANH THU THEO GIỜ ==========
    const hourlyData = [
        <c:forEach var="item" items="${stats.hourlyRevenue}">
            { hour: ${item[0]}, revenue: ${item[1]} },
        </c:forEach>
    ];
    
    // Tạo array đầy đủ 24 giờ (fill 0 cho giờ không có dữ liệu)
    const hourlyLabels = [];
    const hourlyRevenueData = [];
    for (let i = 0; i < 24; i++) {
        hourlyLabels.push(i + 'h');
        const found = hourlyData.find(d => d.hour === i);
        hourlyRevenueData.push(found ? found.revenue : 0);
    }
    
    const hourlyCanvas = document.getElementById('hourlyRevenueChart');
    if (hourlyCanvas) {
        new Chart(hourlyCanvas.getContext('2d'), {
            type: 'line',
            data: {
                labels: hourlyLabels,
                datasets: [{
                    label: 'Doanh thu (₫)',
                    data: hourlyRevenueData,
                    borderColor: 'rgba(0, 102, 51, 1)',
                    backgroundColor: 'rgba(0, 102, 51, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return new Intl.NumberFormat('vi-VN').format(value) + ' ₫';
                            }
                        }
                    }
                },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return 'Doanh thu: ' + new Intl.NumberFormat('vi-VN').format(context.parsed.y) + ' ₫';
                            }
                        }
                    }
                }
            }
        });
    }
    
    // ========== BIỂU ĐỒ DOANH THU 7 NGÀY QUA ==========
    const dailyData = [
        <c:forEach var="item" items="${stats.dailyRevenue}">
            { date: "${item[0]}", revenue: ${item[1]} },
        </c:forEach>
    ];
    
    const dailyLabels = dailyData.map(item => {
        const date = new Date(item.date);
        return date.getDate() + '/' + (date.getMonth() + 1);
    });
    const dailyRevenueData = dailyData.map(item => item.revenue);
    
    const dailyCanvas = document.getElementById('dailyRevenueChart');
    if (dailyCanvas) {
        new Chart(dailyCanvas.getContext('2d'), {
            type: 'bar',
            data: {
                labels: dailyLabels,
                datasets: [{
                    label: 'Doanh thu',
                    data: dailyRevenueData,
                    backgroundColor: 'rgba(78, 115, 223, 0.7)',
                    borderColor: 'rgba(78, 115, 223, 1)',
                    borderWidth: 1,
                    borderRadius: 5
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return new Intl.NumberFormat('vi-VN').format(value) + ' ₫';
                            }
                        }
                    }
                },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return 'Doanh thu: ' + new Intl.NumberFormat('vi-VN').format(context.parsed.y) + ' ₫';
                            }
                        }
                    }
                }
            }
        });
    }
    
    // ========== BIỂU ĐỒ DOANH THU 6 THÁNG GẦN NHẤT ==========
    const revenueData = [
        <c:forEach var="item" items="${stats.monthlyRevenue}">
            { month: "Tháng ${item[1]}/${item[0]}", revenue: ${item[2]} },
        </c:forEach>
    ].reverse();

    const labels = revenueData.map(item => item.month);
    const data = revenueData.map(item => item.revenue);
    const canvas = document.getElementById('revenueChart');

    if (canvas) {
        new Chart(canvas.getContext('2d'), {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Doanh thu',
                    data: data,
                    backgroundColor: 'rgba(0, 102, 51, 0.7)',
                    borderColor: 'rgba(0, 102, 51, 1)',
                    borderWidth: 1,
                    borderRadius: 5
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return new Intl.NumberFormat('vi-VN').format(value) + ' ₫';
                            }
                        }
                    }
                },
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const v = context.parsed.y;
                                return 'Doanh thu: ' + new Intl.NumberFormat('vi-VN').format(v) + ' ₫';
                            }
                        }
                    }
                }
            }
        });
    }
});
</script>