# AloTra - Hệ Thống Website Quản Lý & Bán Trà Sữa Online

**AloTra** là một ứng dụng web thương mại điện tử chuyên biệt cho cửa hàng trà sữa, được xây dựng trên nền tảng Java Web (Servlet/JSP). Hệ thống cung cấp giải pháp toàn diện từ việc đặt hàng trực tuyến cho khách hàng đến quản trị doanh thu, sản phẩm và đơn hàng cho người quản lý.

---

## 🚀 Tổng Quan Dự Án

* **Tên dự án:** AloTra
* **Mô hình:** MVC (Model-View-Controller)
* **Ngôn ngữ:** Java
* **Cơ sở dữ liệu:** SQL Server
* **Công cụ Build:** Maven
* **Server:** Apache Tomcat 10.x

---

## 🛠 Công Nghệ Sử Dụng

### Backend
* **Java Servlet API:** Xử lý request/response.
* **JSP (JavaServer Pages) & JSTL:** Hiển thị giao diện động.
* **JPA (Java Persistence API) / Hibernate:** ORM để làm việc với cơ sở dữ liệu.
* **JavaMail:** Gửi email xác thực OTP, lấy lại mật khẩu.
* **Gson:** Xử lý dữ liệu JSON cho API.

### Frontend
* **HTML5 / CSS3 / JavaScript**
* **Bootstrap 4:** Framework giao diện responsive.
* **SB Admin 2:** Theme trang quản trị.
* **jQuery / AJAX:** Xử lý bất đồng bộ (Thêm giỏ hàng, tải sản phẩm).
* **CKEditor:** Soạn thảo văn bản cho mô tả sản phẩm.

### Database
* **Microsoft SQL Server:** Lưu trữ dữ liệu hệ thống.

---

## 🌟 Chức Năng Chính

### 1. Phân Hệ Khách Hàng (Client)
* **Tài khoản:** Đăng ký, Đăng nhập, Quên mật khẩu (OTP qua Email), Cập nhật hồ sơ.
* **Sản phẩm:** Xem danh sách, tìm kiếm, lọc theo danh mục, xem chi tiết, xem topping.
* **Giỏ hàng:** Thêm/Sửa/Xóa sản phẩm, tính tổng tiền tự động.
* **Thanh toán:**
    * Thanh toán khi nhận hàng (COD).
    * Thanh toán online (tích hợp VNPay - *đang phát triển*).
    * Sử dụng mã giảm giá (Voucher).
* **Cá nhân:** Xem lịch sử đơn hàng, Mua lại (Re-order), Quản lý danh sách yêu thích (Wishlist).

### 2. Phân Hệ Quản Trị (Admin & Staff)
* **Dashboard:** Thống kê doanh thu, số lượng đơn hàng, người dùng mới (Biểu đồ trực quan).
* **Quản lý Sản phẩm:** Thêm, xóa, sửa, upload ảnh, quản lý giá và tồn kho.
* **Quản lý Danh mục & Topping:** Cấu hình menu đồ uống.
* **Quản lý Đơn hàng:** Xem chi tiết, cập nhật trạng thái (Chờ xác nhận -> Đang giao -> Hoàn thành/Hủy).
* **Quản lý Người dùng:** Phân quyền (Admin/Staff/Customer), khóa tài khoản.
* **Marketing:** Quản lý Banner slider, Mã giảm giá (Voucher).
* **Báo cáo:** Xuất báo cáo doanh thu theo ngày/tháng.
* **Cấu hình:** Cài đặt thông tin website, cấu hình thanh toán.

---

## 📂 Cấu Trúc Thư Mục

```text
AloTra/
├── database/               # Script tạo CSDL (AloTra.sql)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── config/     # Cấu hình JPA, Context Listener
│   │   │   ├── controller/ # Servlet điều hướng (Admin, API, Auth, Web...)
│   │   │   ├── dao/        # Data Access Object (Repository)
│   │   │   ├── filter/     # Bộ lọc bảo mật (Auth, Role check)
│   │   │   ├── model/      # Entity mapped với DB
│   │   │   ├── service/    # Logic nghiệp vụ
│   │   │   └── utils/      # Tiện ích (Email, Hash Pass, Constant)
│   │   │
│   │   ├── resources/
│   │   │   └── META-INF/   # Cấu hình persistence.xml
│   │   │
│   │   └── webapp/
│   │       ├── assets/     # CSS, JS, Vendor lib (Bootstrap, ChartJS)
│   │       ├── uploads/    # Ảnh sản phẩm, avatar người dùng
│   │       ├── views/      # Các file JSP hiển thị
│   │       └── WEB-INF/    # Cấu hình web.xml, sitemesh
│   │
└── pom.xml                 # Khai báo thư viện Maven
