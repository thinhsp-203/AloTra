<nav class="navbar navbar-expand-lg bg-body-tertiary border-bottom">
  <div class="container">
    <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/home">AloTra</a>
    <form class="d-flex ms-auto" action="${pageContext.request.contextPath}/search" method="get">
      <input class="form-control me-2" type="search" name="q" placeholder="Tìm trà sữa..." />
      <button class="btn btn-primary" type="submit">Tìm</button>
    </form>
  </div>
</nav>
