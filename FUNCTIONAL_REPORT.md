# BÁO CÁO CHỨC NĂNG VÀ CÔNG NGHỆ
## Hệ thống AloTra - Website Bán Trà Sữa Online

---

## CHƯƠNG 1. GIỚI THIỆU DỰ ÁN

### 1.1. Mục tiêu hệ thống AloTra

AloTra là một hệ thống website thương mại điện tử chuyên biệt cho cửa hàng trà sữa, được xây dựng trên nền tảng Java Web. Hệ thống cung cấp giải pháp toàn diện từ việc đặt hàng trực tuyến cho khách hàng đến quản trị doanh thu, sản phẩm và đơn hàng cho người quản lý.

**Mục tiêu chính:**
- Tạo môi trường mua sắm trực tuyến thuận tiện cho khách hàng
- Quản lý toàn bộ hoạt động kinh doanh của cửa hàng trà sữa
- Tối ưu hóa quy trình đặt hàng, thanh toán và giao hàng
- Hỗ trợ marketing thông qua hệ thống khuyến mãi, voucher và điểm tích lũy
- Cung cấp báo cáo thống kê doanh thu và hiệu quả kinh doanh

### 1.2. Đối tượng sử dụng

Hệ thống AloTra phục vụ ba nhóm đối tượng chính:

1. **Khách hàng (Guest/User):**
   - Khách hàng chưa đăng ký tài khoản (Guest)
   - Khách hàng đã đăng ký và đăng nhập (User/Customer)
   - Có nhu cầu mua sắm trà sữa trực tuyến

2. **Quản trị viên (Admin):**
   - Người quản lý toàn bộ hệ thống
   - Có quyền cao nhất trong hệ thống
   - Quản lý sản phẩm, đơn hàng, người dùng, báo cáo

3. **Nhân viên (Staff):**
   - Theo cấu hình hiện tại, nhân viên có quyền tương đương khách hàng
   - Không có quyền truy cập vào khu vực quản trị

### 1.3. Phạm vi hệ thống

**Phạm vi chức năng:**
- Quản lý tài khoản người dùng (đăng ký, đăng nhập, quên mật khẩu)
- Duyệt và tìm kiếm sản phẩm
- Quản lý giỏ hàng và đặt hàng
- Thanh toán (COD, thanh toán online - đang phát triển)
- Quản lý đơn hàng và theo dõi trạng thái
- Hệ thống khuyến mãi và voucher
- Điểm tích lũy và đổi quà
- Quản lý sản phẩm, danh mục, topping
- Quản lý banner và khuyến mãi
- Báo cáo thống kê doanh thu

**Phạm vi kỹ thuật:**
- Ứng dụng web chạy trên Apache Tomcat
- Cơ sở dữ liệu SQL Server
- Giao diện responsive hỗ trợ đa thiết bị

---

## CHƯƠNG 2. CÁC VAI TRÒ TRONG HỆ THỐNG

### 2.1. Guest (Khách chưa đăng nhập)

#### 2.1.1. Mô tả vai trò

Guest là người dùng chưa đăng ký hoặc chưa đăng nhập vào hệ thống. Họ có thể duyệt và xem thông tin sản phẩm nhưng không thể thực hiện các thao tác mua hàng.

#### 2.1.2. Danh sách chức năng

**Xem thông tin công khai:**
- Xem trang chủ (`/home`, `/trang-chu`, `/`)
  - Xem sản phẩm nổi bật
  - Xem sản phẩm mới nhất
  - Xem danh mục sản phẩm
  - Xem banner quảng cáo
  - Xem khuyến mãi đang diễn ra
  - Xem danh sách cửa hàng

- Xem danh sách sản phẩm (`/products`)
  - Duyệt tất cả sản phẩm
  - Tìm kiếm sản phẩm theo từ khóa
  - Lọc sản phẩm theo danh mục
  - Sắp xếp sản phẩm (giá, mới nhất, bán chạy)
  - Phân trang kết quả

- Xem chi tiết sản phẩm (`/p?id=...`)
  - Xem thông tin chi tiết sản phẩm
  - Xem các size và giá tương ứng
  - Xem danh sách topping (nếu là đồ uống)
  - Xem đánh giá và bình luận của khách hàng
  - Xem sản phẩm liên quan
  - Xem sản phẩm đã xem gần đây (lưu trong cookie)

- Xem danh sách khuyến mãi (`/promotions`, `/khuyen-mai`)
  - Xem tất cả khuyến mãi đang hoạt động
  - Xem chi tiết từng khuyến mãi

- Xem danh sách cửa hàng (`/stores`)
  - Xem thông tin các cửa hàng
  - Xem chi tiết cửa hàng (địa chỉ, giờ mở cửa)

- Xem trang giới thiệu (`/about`, `/ve-chung-toi`)
  - Xem thông tin về công ty

**Quản lý tài khoản:**
- Đăng ký tài khoản (`/register`)
  - Tạo tài khoản mới
  - Xác thực email qua OTP
  - Kích hoạt tài khoản (`/verify`)

- Đăng nhập (`/login`)
  - Đăng nhập bằng username/email và password
  - Lưu thông tin đăng nhập (Remember me)

- Quên mật khẩu (`/forgot`)
  - Yêu cầu reset mật khẩu qua email
  - Đặt lại mật khẩu mới (`/reset`)

#### 2.1.3. Giới hạn quyền

- **Không thể:**
  - Thêm sản phẩm vào giỏ hàng (sẽ được yêu cầu đăng nhập)
  - Đặt hàng
  - Xem lịch sử đơn hàng
  - Quản lý hồ sơ cá nhân
  - Sử dụng voucher
  - Tích điểm và đổi quà
  - Đánh giá sản phẩm
  - Thêm vào danh sách yêu thích

- **Bị chặn bởi Filter:**
  - Các URL `/user/*`, `/checkout`, `/cart/*` sẽ tự động redirect về trang đăng nhập
  - Các URL `/admin/*` sẽ bị chặn hoàn toàn

---

### 2.2. User (Khách hàng đã đăng nhập)

#### 2.2.1. Mô tả vai trò

User là khách hàng đã đăng ký và đăng nhập vào hệ thống. Họ có đầy đủ quyền để mua sắm, quản lý đơn hàng và sử dụng các tính năng cá nhân hóa.

#### 2.2.2. Danh sách chức năng

**Tất cả chức năng của Guest, cộng thêm:**

**Mua sắm:**
- Thêm sản phẩm vào giỏ hàng (`/cart/add`)
  - Chọn size (S, M, L, XL...)
  - Chọn mức độ ngọt (đường)
  - Chọn mức độ đá
  - Chọn topping (nếu là đồ uống)
  - Chọn số lượng
  - Xem giá được tính tự động

- Xem và quản lý giỏ hàng (`/checkout`)
  - Xem danh sách sản phẩm trong giỏ
  - Cập nhật số lượng
  - Xóa sản phẩm khỏi giỏ hàng
  - Tính tổng tiền tự động
  - Áp dụng mã giảm giá (voucher)
  - Chọn phương thức vận chuyển (tiêu chuẩn/ưu tiên)

- Đặt hàng (`/checkout` - POST)
  - Nhập thông tin giao hàng (họ tên, số điện thoại, địa chỉ)
  - Chọn phương thức thanh toán (COD hoặc online)
  - Áp dụng voucher (nếu có)
  - Xác nhận đơn hàng
  - Nhận thông báo đơn hàng đã được đặt

**Quản lý đơn hàng:**
- Xem lịch sử đơn hàng (`/user/orders`)
  - Xem tất cả đơn hàng đã đặt
  - Lọc đơn hàng theo trạng thái
  - Tìm kiếm đơn hàng
  - Xem chi tiết từng đơn hàng

- Hủy đơn hàng (`/user/orders` - POST)
  - Chỉ có thể hủy đơn hàng ở trạng thái "Chờ xác nhận"

- Mua lại đơn hàng cũ (`/user/reorder`)
  - Thêm lại tất cả sản phẩm từ đơn hàng cũ vào giỏ hàng

**Quản lý cá nhân:**
- Xem và cập nhật hồ sơ (`/user/profile`)
  - Xem thông tin cá nhân
  - Cập nhật họ tên, số điện thoại, địa chỉ
  - Upload avatar

- Đổi mật khẩu (`/user/change-password`)
  - Đổi mật khẩu với xác thực mật khẩu cũ

**Danh sách yêu thích:**
- Xem danh sách yêu thích (`/user/wishlist`)
  - Xem tất cả sản phẩm đã thêm vào yêu thích

- Thêm/Xóa yêu thích (`/api/wishlist/toggle`)
  - Toggle sản phẩm vào/ra khỏi danh sách yêu thích

**Đánh giá sản phẩm:**
- Xem đánh giá (`/p?id=...`)
  - Xem tất cả đánh giá đã được duyệt

- Viết đánh giá (`/user/review`)
  - Chỉ có thể đánh giá sản phẩm đã mua
  - Mỗi sản phẩm chỉ được đánh giá một lần
  - Đánh giá cần được admin duyệt trước khi hiển thị

**Voucher và khuyến mãi:**
- Xem danh sách voucher khả dụng (`/checkout`)
  - Xem voucher phù hợp với giá trị đơn hàng
  - Xem điều kiện sử dụng voucher

- Áp dụng voucher (`/api/voucher`)
  - Nhập mã voucher
  - Xem giá giảm được áp dụng
  - Tự động tính lại tổng tiền

**Điểm tích lũy:**
- Xem điểm tích lũy (`/user/loyalty`)
  - Xem số điểm hiện có
  - Xem lịch sử tích điểm và đổi quà (`/user/point-history`)

- Đổi quà (`/user/rewards`)
  - Xem danh sách quà có thể đổi
  - Đổi quà bằng điểm tích lũy
  - Xem chi tiết quà đã đổi

**Thông báo:**
- Xem thông báo (`/user/notifications`)
  - Xem tất cả thông báo
  - Đánh dấu đã đọc
  - Xóa thông báo

- Nhận thông báo tự động:
  - Thông báo đơn hàng mới
  - Thông báo trạng thái đơn hàng thay đổi
  - Thông báo tích điểm

#### 2.2.3. Các nghiệp vụ chính

**Nghiệp vụ mua hàng:**
1. Duyệt sản phẩm → Chọn sản phẩm → Chọn size/topping → Thêm vào giỏ hàng
2. Xem giỏ hàng → Cập nhật số lượng → Áp dụng voucher → Chọn phương thức vận chuyển
3. Nhập thông tin giao hàng → Xác nhận đơn hàng → Nhận thông báo

**Nghiệp vụ quản lý đơn hàng:**
- Theo dõi trạng thái đơn hàng (Chờ xác nhận → Đã xác nhận → Đang giao → Hoàn thành)
- Hủy đơn hàng (chỉ khi ở trạng thái "Chờ xác nhận")
- Mua lại đơn hàng cũ

**Nghiệp vụ tích điểm:**
- Tự động tích điểm khi đơn hàng hoàn thành (1 điểm = 1000 VND)
- Đổi quà bằng điểm tích lũy
- Xem lịch sử giao dịch điểm

---

### 2.3. Admin (Quản trị viên)

#### 2.3.1. Mô tả vai trò

Admin là người quản lý toàn bộ hệ thống, có quyền cao nhất. Chỉ có roleId = 1 mới được truy cập vào khu vực quản trị (`/admin/*`).

#### 2.3.2. Danh sách chức năng quản trị

**Dashboard và báo cáo:**
- Xem dashboard (`/admin/dashboard`)
  - Thống kê tổng quan: doanh thu, số đơn hàng, số khách hàng mới
  - Biểu đồ doanh thu theo thời gian
  - Top sản phẩm bán chạy
  - Top khách hàng

- Xem báo cáo (`/admin/reports`)
  - Báo cáo doanh thu theo ngày/tháng
  - Báo cáo đơn hàng
  - Báo cáo sản phẩm
  - Báo cáo khách hàng
  - Xuất báo cáo Excel

**Quản lý sản phẩm:**
- Xem danh sách sản phẩm (`/admin/products`)
  - Xem tất cả sản phẩm (kể cả đã vô hiệu hóa)
  - Tìm kiếm và lọc sản phẩm
  - Phân trang

- Thêm sản phẩm (`/admin/products/add`)
  - Nhập thông tin sản phẩm (tên, mô tả, giá, giảm giá)
  - Chọn danh mục
  - Upload ảnh sản phẩm
  - Tự động tạo size mặc định nếu là đồ uống

- Sửa sản phẩm (`/admin/products/edit`)
  - Cập nhật thông tin sản phẩm
  - Thay đổi ảnh sản phẩm
  - Xóa ảnh cũ khi upload ảnh mới

- Xóa sản phẩm (`/admin/products/delete`)
  - Hard delete: Xóa hoàn toàn sản phẩm và tất cả dữ liệu liên quan
  - Xóa OrderDetail, Review, WishlistItem, ProductSize, ViewHistory

- Vô hiệu hóa/Kích hoạt sản phẩm (`/admin/products/disable`, `/admin/products/enable`)
  - Soft delete: Ẩn sản phẩm khỏi danh sách công khai
  - Sản phẩm vẫn tồn tại trong DB nhưng không hiển thị cho khách hàng

**Quản lý danh mục:**
- Xem danh sách danh mục (`/admin/categories`)
- Thêm danh mục (`/admin/categories/add`)
  - Tên danh mục, icon, loại (đồ uống hay không)
- Sửa danh mục (`/admin/categories/edit`)
- Xóa danh mục (`/admin/categories/delete`)

**Quản lý Topping:**
- Xem danh sách topping (`/admin/toppings`)
  - Xem tất cả topping (kể cả không khả dụng)
- Thêm topping (`/admin/toppings/add`)
  - Tên topping, giá, danh mục áp dụng, trạng thái khả dụng
- Sửa topping (`/admin/toppings/edit`)
- Xóa topping (`/admin/toppings/delete`)

**Quản lý đơn hàng:**
- Xem danh sách đơn hàng (`/admin/orders`)
  - Xem tất cả đơn hàng
  - Tìm kiếm đơn hàng
  - Lọc theo trạng thái
  - Phân trang

- Xem chi tiết đơn hàng (`/admin/orders/detail`)
  - Xem thông tin khách hàng
  - Xem danh sách sản phẩm trong đơn
  - Xem tổng tiền, voucher, phí ship

- Cập nhật trạng thái đơn hàng (`/admin/orders/update-status`)
  - Chuyển trạng thái: Chờ xác nhận → Đã xác nhận → Đang giao → Hoàn thành
  - Hoặc hủy đơn hàng

- Cập nhật trạng thái thanh toán (`/admin/orders/update-payment`)
  - Đánh dấu đã thanh toán hoặc chưa thanh toán

**Quản lý người dùng:**
- Xem danh sách người dùng (`/admin/users`)
  - Xem tất cả người dùng (kể cả đã vô hiệu hóa)
  - Tìm kiếm người dùng
  - Lọc theo vai trò (Admin/Staff/Customer)
  - Phân trang

- Thêm người dùng (`/admin/users/add`)
  - Tạo tài khoản mới
  - Phân quyền (Admin/Staff/Customer)
  - Set trạng thái active/inactive

- Sửa người dùng (`/admin/users/edit`)
  - Cập nhật thông tin cá nhân
  - Thay đổi vai trò (chỉ cho phép với Customer và Staff, không cho phép thay đổi role của Admin)
  - Kích hoạt/Vô hiệu hóa tài khoản

- Xóa người dùng (`/admin/users/delete`)
  - Soft delete: Set `isActive = false`
  - Hard delete: Xóa hoàn toàn (cần xóa các dữ liệu liên quan)

**Quản lý Voucher:**
- Xem danh sách voucher (`/admin/vouchers`)
- Thêm voucher (`/admin/vouchers/add`)
  - Mã voucher, loại giảm giá (%, số tiền), giá trị giảm
  - Giảm giá tối đa (nếu là %)
  - Đơn tối thiểu, số lượt sử dụng
  - Ngày bắt đầu, ngày kết thúc
- Sửa voucher (`/admin/vouchers/edit`)
- Xóa voucher (`/admin/vouchers/delete`)

**Quản lý Banner:**
- Xem danh sách banner (`/admin/banners`)
- Thêm banner (`/admin/banners/add`)
  - Upload ảnh banner
  - Link URL, thứ tự hiển thị
  - Trạng thái active/inactive
- Sửa banner (`/admin/banners/edit`)
- Xóa banner (`/admin/banners/delete`)

**Quản lý Khuyến mãi:**
- Xem danh sách khuyến mãi (`/admin/promotions`)
- Thêm khuyến mãi (`/admin/promotions/add`)
  - Tiêu đề, mô tả, ảnh
  - Ngày bắt đầu, ngày kết thúc
  - Trạng thái active/inactive
- Sửa khuyến mãi (`/admin/promotions/edit`)
- Xóa khuyến mãi (`/admin/promotions/delete`)

**Quản lý Quà tặng (Reward):**
- Xem danh sách quà tặng (`/admin/rewards`)
- Thêm quà tặng (`/admin/rewards/add`)
  - Tên quà, mô tả, ảnh
  - Số điểm cần để đổi
  - Số lượng tồn kho
  - Trạng thái active/inactive
- Sửa quà tặng (`/admin/rewards/edit`)
- Xóa quà tặng (`/admin/rewards/delete`)

**Quản lý Cửa hàng:**
- Xem danh sách cửa hàng (`/admin/stores`)
- Thêm cửa hàng (`/admin/stores/add`)
  - Tên cửa hàng, địa chỉ, số điện thoại
  - Giờ mở cửa, giờ đóng cửa
  - Trạng thái active/inactive
- Sửa cửa hàng (`/admin/stores/edit`)
- Xóa cửa hàng (`/admin/stores/delete`)

**Quản lý Trang Giới thiệu:**
- Xem danh sách (`/admin/about`)
- Thêm nội dung (`/admin/about/add`)
- Sửa nội dung (`/admin/about/edit`)
- Xóa nội dung (`/admin/about/delete`)

**Duyệt đánh giá:**
- Xem đánh giá chưa duyệt (thông qua quản lý sản phẩm)
- Duyệt hoặc từ chối đánh giá

#### 2.3.3. Các dữ liệu Admin quản lý

**Dữ liệu chính:**
- **Sản phẩm:** Thông tin sản phẩm, giá, ảnh, danh mục, size, trạng thái
- **Danh mục:** Tên, icon, loại (đồ uống hay không)
- **Topping:** Tên, giá, danh mục áp dụng, trạng thái khả dụng
- **Đơn hàng:** Tất cả đơn hàng, trạng thái, thanh toán
- **Người dùng:** Thông tin tài khoản, vai trò, trạng thái
- **Voucher:** Mã giảm giá, điều kiện, số lượt sử dụng
- **Banner:** Ảnh quảng cáo, link, thứ tự hiển thị
- **Khuyến mãi:** Thông tin khuyến mãi, thời gian
- **Quà tặng:** Quà đổi điểm, số điểm cần, tồn kho
- **Cửa hàng:** Thông tin cửa hàng, địa chỉ, giờ làm việc
- **Trang giới thiệu:** Nội dung về công ty

**Báo cáo và thống kê:**
- Doanh thu theo ngày/tháng/năm
- Số lượng đơn hàng
- Top sản phẩm bán chạy
- Top khách hàng
- Số khách hàng mới
- Tỷ lệ đơn hàng hoàn thành/hủy

---

## CHƯƠNG 3. CÔNG NGHỆ SỬ DỤNG

### 3.1. Backend

**Java Servlet API:**
- Sử dụng Jakarta Servlet API 6.0.0
- Xử lý HTTP request/response
- Quản lý session và cookie
- Upload file (multipart/form-data)

**JSP (JavaServer Pages):**
- Sử dụng Jakarta JSP API 3.1.1
- Tạo giao diện động
- Tích hợp với Java code và JSTL

**JSTL (JavaServer Pages Standard Tag Library):**
- Sử dụng Jakarta JSTL 3.0.1
- Thư viện: `jakarta.servlet.jsp.jstl-api` và `jakarta.servlet.jsp.jstl`
- Các thẻ: `<c:forEach>`, `<c:if>`, `<c:choose>`, `<fmt:formatNumber>`, `<fn:length>`

**JPA (Java Persistence API) / Hibernate:**
- Sử dụng Hibernate Core 6.4.4.Final
- ORM (Object-Relational Mapping) để làm việc với database
- Entity Manager để quản lý persistence context
- JPQL (Java Persistence Query Language) để truy vấn
- Transaction management

**JavaMail:**
- Sử dụng javax.mail 1.6.2
- Gửi email xác thực OTP
- Gửi email reset password
- SMTP configuration

**Gson:**
- Sử dụng Gson 2.10.1
- Xử lý JSON cho AJAX API
- Serialize/Deserialize Java objects

**BCrypt:**
- Sử dụng jbcrypt 0.4
- Hash và verify password
- Bảo mật mật khẩu người dùng

**Apache POI:**
- Sử dụng poi-ooxml 5.2.3
- Xuất báo cáo Excel
- Tạo file .xlsx cho báo cáo doanh thu

**Lombok:**
- Sử dụng Lombok 1.18.32
- Tự động generate getter/setter
- Giảm boilerplate code

**SLF4J:**
- Sử dụng SLF4J API 2.0.7 và Simple 2.0.7
- Logging framework
- Ghi log hoạt động hệ thống

**Sitemesh:**
- Sử dụng Sitemesh 3.2.0
- Decorator pattern cho JSP
- Tạo layout chung cho các trang
- Tách biệt header/footer/sidebar

### 3.2. Frontend

**HTML5 / CSS3:**
- Cấu trúc và style cơ bản
- Semantic HTML
- Responsive design

**JavaScript:**
- Vanilla JavaScript
- DOM manipulation
- Event handling
- AJAX requests (Fetch API)

**Bootstrap:**
- Bootstrap 5 (theo yêu cầu, có thể đang dùng Bootstrap 4 trong thực tế)
- Framework CSS responsive
- Grid system, components, utilities
- Modal, dropdown, carousel

**jQuery / AJAX:**
- Xử lý bất đồng bộ
- Thêm sản phẩm vào giỏ hàng không reload trang
- Toggle wishlist
- Tìm kiếm sản phẩm
- Áp dụng voucher
- Cập nhật thông báo

### 3.3. ORM / Persistence

**JPA Configuration:**
- File: `src/main/resources/META-INF/persistence.xml`
- Persistence Unit: "AloTra"
- Provider: Hibernate JPA
- Transaction Type: RESOURCE_LOCAL

**Entity Manager:**
- Singleton pattern cho EntityManagerFactory
- Mỗi DAO tự quản lý EntityManager
- Tự động đóng EntityManager trong finally block

**DAO Pattern:**
- Data Access Object pattern
- Tách biệt logic truy cập database
- Mỗi entity có DAO riêng (UserDao, ProductDao, OrderDao...)

**Service Layer:**
- Business logic layer
- Gọi DAO để truy cập database
- Không sử dụng EntityManager trực tiếp

### 3.4. Database

**Hệ quản trị CSDL:**
- Microsoft SQL Server
- JDBC Driver: mssql-jdbc 12.6.1.jre11
- Dialect: SQLServerDialect

**Kết nối:**
- URL: `jdbc:sqlserver://VNF-PC001\SQLEXPRESS:1433;databaseName=AloTra`
- Username/Password: Cấu hình trong persistence.xml
- Encoding: UTF-8 (sendStringParametersAsUnicode=true)

**Cấu hình:**
- `hibernate.hbm2ddl.auto = update`: Tự động cập nhật schema
- `hibernate.show_sql = false`: Không hiển thị SQL trong console
- `hibernate.format_sql = true`: Format SQL khi log

### 3.5. Server triển khai

**Apache Tomcat:**
- Phiên bản: 10.x (theo README)
- Servlet Container
- JSP Engine
- Web Application Server

**Deployment:**
- File WAR (Web Application Archive)
- Deploy vào thư mục `webapps` của Tomcat
- Context path: `/AloTra`

### 3.6. Công cụ phát triển

**Build Tool:**
- Maven
- File: `pom.xml`
- Quản lý dependencies
- Compile và package project

**Java Version:**
- Java 17
- `maven.compiler.source = 17`
- `maven.compiler.target = 17`

**Encoding:**
- UTF-8
- `project.build.sourceEncoding = UTF-8`
- Hỗ trợ tiếng Việt

---

## CHƯƠNG 4. CƠ SỞ DỮ LIỆU

### 4.1. Hệ quản trị CSDL

Hệ thống sử dụng **Microsoft SQL Server** làm hệ quản trị cơ sở dữ liệu. SQL Server được chọn vì:
- Hỗ trợ tốt cho ứng dụng doanh nghiệp
- Tích hợp tốt với Java/JPA
- Hỗ trợ Unicode (NVARCHAR) cho tiếng Việt
- Transaction và ACID properties
- Performance tốt cho ứng dụng web

### 4.2. Các bảng chính trong hệ thống

**1. User (Người dùng)**
- Lưu trữ thông tin tài khoản người dùng
- Các trường: id, email, username, password (hashed), fullname, phone, address, roleid, isActive, loyalty_points, avatar, createdDate
- Quan hệ: One-to-Many với Orders, Review, WishlistItem, PointTransaction, Notification

**2. Product (Sản phẩm)**
- Lưu trữ thông tin sản phẩm
- Các trường: product_id, product_name, description, price, discount, thumbnail, category_id, isActive, rating, views
- Quan hệ: Many-to-One với Category, One-to-Many với OrderDetail, Review, WishlistItem, ProductSize

**3. Category (Danh mục)**
- Phân loại sản phẩm
- Các trường: id, name, icon, isDrink
- Quan hệ: One-to-Many với Product

**4. Orders (Đơn hàng)**
- Lưu trữ thông tin đơn hàng
- Các trường: order_id, user_id, fullname, phone, address, note, total_amount, payment_method, payment_status, order_status, createdDate, updatedDate
- Quan hệ: Many-to-One với User, One-to-Many với OrderDetail, PointTransaction

**5. OrderDetail (Chi tiết đơn hàng)**
- Lưu trữ từng sản phẩm trong đơn hàng
- Các trường: detail_id, order_id, product_id, product_name, size_name, quantity, price, toppings
- Quan hệ: Many-to-One với Orders và Product

**6. CartItem (Giỏ hàng)**
- Lưu trữ tạm thời trong session (không phải Entity)
- Thông tin: productId, productName, quantity, unitPrice, sizeName, sugarLevel, iceLevel, toppings, lineTotal

**7. Review (Đánh giá)**
- Lưu trữ đánh giá của khách hàng về sản phẩm
- Các trường: review_id, user_id, product_id, rating, comment, isApproved, createdDate
- Quan hệ: Many-to-One với User và Product

**8. WishlistItem (Danh sách yêu thích)**
- Lưu trữ sản phẩm yêu thích của người dùng
- Các trường: wishlist_id, user_id, product_id
- Quan hệ: Many-to-One với User và Product

**9. Voucher (Mã giảm giá)**
- Lưu trữ thông tin mã giảm giá
- Các trường: voucher_id, code, discount_type, discount_value, max_discount, min_order_value, usage_limit, used_count, start_date, end_date, isActive
- Quan hệ: Độc lập

**10. Promotion (Khuyến mãi)**
- Lưu trữ thông tin khuyến mãi
- Các trường: promotion_id, title, description, imageUrl, start_date, end_date, isActive
- Quan hệ: Độc lập

**11. Banner (Banner quảng cáo)**
- Lưu trữ banner hiển thị trên trang chủ
- Các trường: banner_id, imageUrl, linkUrl, display_order, isActive
- Quan hệ: Độc lập

**12. Topping (Topping)**
- Lưu trữ thông tin topping cho đồ uống
- Các trường: topping_id, topping_name, price, category_name, isAvailable
- Quan hệ: Many-to-One với Category (qua category_name)

**13. ProductSize (Size sản phẩm)**
- Lưu trữ các size và giá tương ứng
- Các trường: size_id, product_id, size_name, price_adjustment
- Quan hệ: Many-to-One với Product

**14. Store (Cửa hàng)**
- Lưu trữ thông tin cửa hàng
- Các trường: store_id, name, address, phone, open_time, close_time, isActive
- Quan hệ: Độc lập

**15. Reward (Quà tặng)**
- Lưu trữ quà tặng đổi điểm
- Các trường: reward_id, name, description, imageUrl, points_required, stock, isActive
- Quan hệ: One-to-Many với PointTransaction

**16. PointTransaction (Giao dịch điểm)**
- Lưu trữ lịch sử tích điểm và đổi quà
- Các trường: transaction_id, user_id, order_id, reward_id, points, type (EARN/REDEEM), description, balance_after, createdDate
- Quan hệ: Many-to-One với User, Orders, Reward

**17. Notification (Thông báo)**
- Lưu trữ thông báo cho người dùng
- Các trường: notification_id, user_id, message, link, isRead, isDeleted, createdDate
- Quan hệ: Many-to-One với User

**18. AboutUs (Trang giới thiệu)**
- Lưu trữ nội dung trang giới thiệu
- Các trường: about_id, title, content, image, display_order, isActive
- Quan hệ: Độc lập

**19. Settings (Cài đặt)**
- Lưu trữ cấu hình hệ thống
- Các trường: setting_id, key, value
- Quan hệ: Độc lập

### 4.3. Quan hệ tổng quát giữa các bảng

**Quan hệ One-to-Many:**
- User → Orders (một user có nhiều đơn hàng)
- User → Review (một user có nhiều đánh giá)
- User → WishlistItem (một user có nhiều sản phẩm yêu thích)
- User → PointTransaction (một user có nhiều giao dịch điểm)
- User → Notification (một user có nhiều thông báo)
- Category → Product (một danh mục có nhiều sản phẩm)
- Product → OrderDetail (một sản phẩm có trong nhiều đơn hàng)
- Product → Review (một sản phẩm có nhiều đánh giá)
- Product → WishlistItem (một sản phẩm được nhiều user yêu thích)
- Product → ProductSize (một sản phẩm có nhiều size)
- Orders → OrderDetail (một đơn hàng có nhiều chi tiết)
- Orders → PointTransaction (một đơn hàng có thể tạo giao dịch điểm)
- Reward → PointTransaction (một quà tặng có thể được đổi nhiều lần)

**Quan hệ Many-to-One:**
- OrderDetail → Orders (nhiều chi tiết thuộc một đơn hàng)
- OrderDetail → Product (nhiều chi tiết tham chiếu một sản phẩm)
- Review → User (nhiều đánh giá của một user)
- Review → Product (nhiều đánh giá về một sản phẩm)
- WishlistItem → User (nhiều item yêu thích của một user)
- WishlistItem → Product (nhiều user yêu thích một sản phẩm)
- Product → Category (nhiều sản phẩm thuộc một danh mục)
- ProductSize → Product (nhiều size thuộc một sản phẩm)

**Các bảng độc lập:**
- Voucher, Promotion, Banner, Store, AboutUs, Settings không có quan hệ trực tiếp với các bảng khác (ngoại trừ được sử dụng trong business logic)

**Cascade và Foreign Key:**
- Khi xóa User: Cần xử lý các bản ghi liên quan (Orders, Review, WishlistItem...)
- Khi xóa Product: Cần xóa OrderDetail, Review, WishlistItem, ProductSize (hard delete)
- Khi xóa Orders: Có thể cascade xóa OrderDetail (tùy cấu hình)

---

## KẾT LUẬN

Hệ thống AloTra là một ứng dụng web thương mại điện tử hoàn chỉnh, được xây dựng trên nền tảng Java Web với kiến trúc MVC rõ ràng. Hệ thống phân quyền rõ ràng giữa Guest, User và Admin, đảm bảo bảo mật và trải nghiệm người dùng tốt. Công nghệ sử dụng hiện đại và phù hợp với yêu cầu của một hệ thống thương mại điện tử.

**Điểm mạnh:**
- Kiến trúc rõ ràng: Controller-Service-DAO
- Phân quyền chặt chẽ qua Filter
- Hỗ trợ đầy đủ các chức năng thương mại điện tử
- Giao diện responsive
- Bảo mật tốt (password hashing, session management)

**Hướng phát triển:**
- Tích hợp thanh toán online (VNPay)
- Tối ưu hóa hiệu năng
- Mở rộng tính năng cho Staff role
- Mobile app

---

**Tài liệu được tạo tự động từ source code dự án AloTra**

