<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<h1 class="h3 mb-4 text-gray-800">${empty p ? 'Thêm' : 'Sửa'} sản phẩm</h1>

<div class="card shadow mb-4">
  <div class="card-header py-3">
    <h6 class="m-0 font-weight-bold text-primary">Thông tin sản phẩm</h6>
  </div>
  <div class="card-body">
    <form method="post" action="${pageContext.request.contextPath}/admin/products/save">
      <input type="hidden" name="id" value="${p.product_id}"/>
      <div class="row g-3">
        <div class="col-md-8">
          <div class="mb-3"><label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
            <input class="form-control" name="product_name" value="${p.product_name}" required/></div>
          <div class="mb-3"><label class="form-label">Mô tả</label>
            <textarea class="form-control" name="description" rows="5">${p.description}</textarea></div>
          <div class="mb-3"><label class="form-label">Ảnh thumbnail (URL)</label>
            <input class="form-control" name="thumbnail" value="${p.thumbnail}"/></div>
        </div>
        <div class="col-md-4">
          <div class="mb-3"><label class="form-label">Giá <span class="text-danger">*</span></label>
      <input class="form-control" name="price" type="number" step="100" value="${p.price}" required/></div>
          <div class="mb-3"><label class="form-label">Giảm giá (%)</label>
            <input class="form-control" name="discount" type="number" step="1" value="${p.discount}"/></div>
          <div class="mb-3"><label class="form-label">Tồn kho</label>
            <input class="form-control" name="stock" type="number" step="1" value="${p.stock}"/></div>
          <div class="mb-3"><label class="form-label">Danh mục</label>
            <select class="form-select" name="cate_id">
              <c:forEach var="c" items="${categories}">
                <option value="${c.id}" 
${p.category != null && p.category.id == c.id ? 'selected' : ''}>${c.name}</option>
              </c:forEach>
            </select></div>
          <div class="mb-3"><label class="form-label">Nhà cung cấp</label>
            <select class="form-select" name="supplier_id">
              <c:forEach var="s" items="${suppliers}">
                <option value="${s.supplier_id}" ${p.supplier != null && p.supplier.supplier_id == s.supplier_id ?
'selected' : ''}>${s.supplier_name}</option>
              </c:forEach>
            </select></div>
          <div class="form-check mb-2">
            <input class="form-check-input" type="checkbox" name="isActive" ${p.isActive != null && p.isActive ?
'checked' : ''} id="a1"/>
            <label class="form-check-label" for="a1">Hiển thị</label>
          </div>
          <div class="form-check mb-3">
            <input class="form-check-input" type="checkbox" name="isFeatured" ${p.isFeatured != null && p.isFeatured ?
'checked' : ''} id="a2"/>
            <label class="form-check-label" for="a2">Nổi bật</label>
          </div>
        </div>
        
        <div class="col-12">
          <hr>
          <button class="btn btn-primary" type="submit">
             <i class="fas fa-save fa-sm"></i> Lưu lại
          </button>
          <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/products">Hủy</a>
        </div>
        
      </div>
    </form>
  </div>
</div>