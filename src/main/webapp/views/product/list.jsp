<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script>const contextPath='${contextPath}';</script>
<h1 class="h5">Danh sách hàng hóa</h1>
<!-- TODO filter form: cate/supplier/price -->
<div id="grid" class="row row-cols-2 row-cols-md-4 g-3"></div>
<script>loadMore();</script>
