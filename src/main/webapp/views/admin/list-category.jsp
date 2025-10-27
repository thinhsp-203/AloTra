<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<h2>Categories</h2>
<p><a href="${pageContext.request.contextPath}/admin/category/add">Thêm danh mục</a></p>
<style>
  table.category { border-collapse: separate; border-spacing: 6px; }
  table.category th, table.category td { padding: 6px; }
</style>

<table class="category" border="1">
  <tr><th>#</th><th>Icon</th><th>Tên</th><th>Hành động</th></tr>
  <c:forEach items="${cateList}" var="cate" varStatus="st">
    <tr>
      <td>${st.index + 1}</td>
      <td><c:if test="${not empty cate.icon}">
        <img src="${pageContext.request.contextPath}/${cate.icon}" width="120"/>
      </c:if></td>
      <td>${cate.name}</td>
      <td>
        <a href="${pageContext.request.contextPath}/admin/category/edit?id=${cate.id}">Sửa</a> |
        <a href="${pageContext.request.contextPath}/admin/category/delete?id=${cate.id}">Xóa</a>
      </td>
    </tr>
  </c:forEach>
</table>
