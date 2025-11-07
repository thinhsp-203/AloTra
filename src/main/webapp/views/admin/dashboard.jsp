<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<div class="d-sm-flex align-items-center justify-content-between mb-4">
  <h1 class="h3 mb-0 text-gray-800">Dashboard</h1>
</div>

<div class="row">

  <div class="col-xl-3 col-md-6 mb-4">
    <div class="card border-left-primary shadow h-100 py-2">
      <div class="card-body">
        <div class="row no-gutters align-items-center">
          <div class="col mr-2">
            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
              Tổng doanh thu</div>
            <div class="h5 mb-0 font-weight-bold text-gray-800">
              <fmt:formatNumber value="${stats.totalRevenue}" pattern="#,##0₫"/>
            </div>
          </div>
          <div class="col-auto">
            <i class="fas fa-calendar fa-2x text-gray-300"></i>
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
              Tổng đơn hàng</div>
            <div class="h5 mb-0 font-weight-bold text-gray-800">${stats.totalOrders}</div>
          </div>
          <div class="col-auto">
            <i class="fas fa-dollar-sign fa-2x text-gray-300"></i>
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
              Khách hàng</div>
            <div class="h5 mb-0 font-weight-bold text-gray-800">${stats.totalCustomers}</div>
          </div>
          <div class="col-auto">
            <i class="fas fa-users fa-2x text-gray-300"></i>
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
              Đơn chờ xử lý</div>
            <div class="h5 mb-0 font-weight-bold text-gray-800">${stats.pendingOrders}</div>
          </div>
          <div class="col-auto">
            <i class="fas fa-comments fa-2x text-gray-300"></i>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="row">

  <div class="col-xl-8 col-lg-7">
    <div class="card shadow mb-4">
      <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
        <h6 class="m-0 font-weight-bold text-primary">Doanh thu 6 tháng gần nhất</h6>
      </div>
      <div class="card-body">
        <div class="chart-area" style="height: 300px;">
          <canvas id="revenueChart"></canvas>
        </div>
      </div>
    </div>
  </div>

  <div class="col-xl-4 col-lg-5">
    <div class="card shadow mb-4">
      <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Top 5 sản phẩm bán chạy</h6>
      </div>
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th class="border-0">#</th>
                <th class="border-0">Sản phẩm</th>
                <th class="border-0 text-end">Đã bán</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="item" items="${stats.topProducts}" varStatus="status">
                <tr>
                  <td><strong>#${status.index + 1}</strong></td>
                  <td class="fw-semibold">${item[0]}</td>
                  <td class="text-end">
                    <span class="badge text-bg-success">${item[1]} sp</span>
                  </td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
// Script cho Biểu đồ
document.addEventListener("DOMContentLoaded", function() {
    const revenueData = [
        <c:forEach var="item" items="${stats.monthlyRevenue}">
            { month: "Tháng ${item[1]}/${item[0]}", revenue: ${item[2]} },
        </c:forEach>
    ].reverse();

    const labels = revenueData.map(item => item.month);
    const data = revenueData.map(item => item.revenue);
    const ctx = document.getElementById('revenueChart');
    
    if (ctx) {
        new Chart(ctx.getContext('2d'), {
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
                    legend: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                let label = context.dataset.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                if (context.parsed.y !== null) {
                                    label += new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(context.parsed.y);
                                }
                                return label;
                            }
                        }
                    }
                }
            }
        });
    }
});
</script>