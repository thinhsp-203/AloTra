<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h1 class="h5 mb-3">${empty p ? 'Thêm' : 'Sửa'} sản phẩm</h1>
<form method="post" action="${pageContext.request.contextPath}/admin/products/save">
  <input type="hidden" name="id" value="${p.product_id}"/>
  <div class="row g-3">
    <div class="col-md-8">
      <div class="mb-2"><label class="form-label">Tên</label>
        <input class="form-control" name="product_name" value="${p.product_name}" required/></div>
      <div class="mb-2"><label class="form-label">Mô tả</label>
        <textarea class="form-control" name="description" rows="5">${p.description}</textarea></div>
      <div class="mb-2"><label class="form-label">Ảnh thumbnail (URL)</label>
        <input class="form-control" name="thumbnail" value="${p.thumbnail}"/></div>
    </div>
    <div class="col-md-4">
      <div class="mb-2"><label class="form-label">Giá</label>
        <input class="form-control" name="price" type="number" step="100" value="${p.price}" required/></div>
      <div class="mb-2"><label class="form-label">Giảm (%)</label>
        <input class="form-control" name="discount" type="number" step="1" value="${p.discount}"/></div>
      <div class="mb-2"><label class="form-label">Kho</label>
        <input class="form-control" name="stock" type="number" step="1" value="${p.stock}"/></div>
      <div class="mb-2"><label class="form-label">Danh mục</label>
        <select class="form-select" name="cate_id">
          <c:forEach var="c" items="${categories}">
             <%-- Sửa lại các thuộc tính ở đây --%>
             <option value="${c.id}" ${p.category.id == c.id ? 'selected' : ''}>${c.name}</option>
          </c:forEach>
        </select></div>
      <div class="mb-2"><label class="form-label">Nhà cung cấp</label>
        <select class="form-select" name="supplier_id">
          <c:forEach var="s" items="${suppliers}">
            <option value="${s.supplier_id}" ${p.supplier.supplier_id==s.supplier_id?'selected':''}>${s.supplier_name}</option>
          </c:forEach>
        </select></div>
      <div class="form-check mb-2">
        <input class="form-check-input" type="checkbox" name="isActive" ${p.isActive?'checked':''} id="a1"/>
        <label class="form-check-label" for="a1">Hiển thị</label>
      </div>
      <div class="form-check mb-3">
        <input class="form-check-input" type="checkbox" name="isFeatured" ${p.isFeatured?'checked':''} id="a2"/>
        <label class="form-check-label" for="a2">Nổi bật</label>
      </div>
      <button class="btn btn-primary">Lưu</button>
      <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/products">Hủy</a>
    </div>
  </div>
</form>
