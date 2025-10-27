<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h2>Thêm Category</h2>
<form action="${pageContext.request.contextPath}/admin/category/add"
      method="post" enctype="multipart/form-data">
  <label>Tên danh mục:</label>
  <input name="name" required />
  <br/><br/>
  <label>Ảnh đại diện:</label>
  <input type="file" name="icon" accept="image/*" />
  <br/><br/>
  <button type="submit">Lưu</button>
</form>
