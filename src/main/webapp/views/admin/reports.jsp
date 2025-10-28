<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<h1 class="h5 mb-3">Báo cáo</h1>

<h2 class="h6">Doanh thu theo tháng</h2>
<table class="table table-sm">
  <thead><tr><th>Năm</th><th>Tháng</th><th>Doanh thu</th></tr></thead>
  <tbody>
    <c:forEach var="r" items="${rev}">
      <tr><td>${r[0]}</td><td>${r[1]}</td><td>${r[2]}</td></tr>
    </c:forEach>
  </tbody>
</table>

<h2 class="h6 mt-4">Top bán chạy</h2>
<table class="table table-sm">
  <thead><tr><th>Sản phẩm</th><th>Số lượng</th></tr></thead>
  <tbody>
    <c:forEach var="t" items="${top}">
      <tr><td>${t[0]}</td><td>${t[1]}</td></tr>
    </c:forEach>
  </tbody>
</table>

<h2 class="h6 mt-4">Tồn kho hiện tại</h2>
<table class="table table-sm">
  <thead><tr><th>Sản phẩm</th><th>Kho</th></tr></thead>
  <tbody>
    <c:forEach var="s" items="${stock}">
      <tr><td>${s[0]}</td><td>${s[1]}</td></tr>
    </c:forEach>
  </tbody>
</table>
