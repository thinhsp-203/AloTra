<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:forEach var="product" items="${products}">
    <div class="col product-item"> 
        <c:set var="p" value="${product}" scope="request"/>
        <jsp:include page="/views/_partials/product_card.jsp" />
    </div>
</c:forEach>