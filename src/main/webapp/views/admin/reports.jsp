<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<h1 class="h3 mb-4 text-gray-800">Báo cáo</h1>

<div class="row">
  <div class="col-12">
    <div class="card shadow mb-4">
      <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Doanh thu theo tháng</h6>
      </div>
      <div class="card-body">
        <table class="table table-sm table-hover">
          <thead><tr><th>Năm</th><th>Tháng</th><th>Doanh thu</th></tr></thead>
          <tbody>
            <c:forEach var="r" items="${rev}">
              <tr><td>${r[0]}</td><td>${r[1]}</td><td>${r[2]}</td></tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  
  <div class="col-lg-6">
    <div class="card shadow mb-4">
      <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Top bán chạy</h6>
      </div>
      <div class="card-body">
        <table class="table table-sm table-hover">
          <thead><tr><th>Sản phẩm</th><th>Số lượng</th></tr></thead>
          <tbody>
            <c:forEach var="t" items="${top}">
              <tr><td>${t[0]}</td><td>${t[1]}</td></tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  
  <div class="col-lg-6">
    <div class="card shadow mb-4">
      <div class="card-header py-3">
        <h6 class="m-0 font-weight-bold text-primary">Tồn kho hiện tại</h6>
      </div>
      <div class="card-body">
        <table class="table table-sm table-hover">
          <thead><tr><th>Sản phẩm</th><th>Kho</th></tr></thead>
          <tbody>
            <c:forEach var="s" items="${stock}">
              <tr><td>${s[0]}</td><td>${s[1]}</td></tr>
            </c:forEach>
          
</tbody>
        </table>
      </div>
    </div>
  </div>
</div>