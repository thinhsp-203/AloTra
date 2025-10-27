<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<h2>Sửa Category</h2>
<form action="${pageContext.request.contextPath}/admin/category/edit" method="post" enctype="multipart/form-data">
  <input type="hidden" name="id" value="${category.id}"/>
  <label>Tên danh mục:</label>
  <input name="name" value="${category.name}" required />
  <br/><br/>
  <label>Ảnh hiện tại:</label>
  <c:if test="${not empty category.icon}">
    <img src="${pageContext.request.contextPath}/${category.icon}" width="120"/>
  </c:if>
  <br/><br/>
  <label>Chọn ảnh mới (tuỳ chọn):</label>
  <input type="file" name="icon" accept="image/*"/>
  <br/><br/>
  <button type="submit">Cập nhật</button>
</form>
