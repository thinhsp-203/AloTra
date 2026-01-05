# KẾ HOẠCH CHUẨN HÓA TÊN FILE, FOLDER, PACKAGE - DỰ ÁN ALOTRA

## 1. PHÂN TÍCH CẤU TRÚC HIỆN TẠI

### 1.1. Java Packages (src/main/java/stnw/)

```
stnw/
├── config/              ✅ OK (không đổi)
├── controller/         ⚠️ CẦN CHUẨN HÓA
│   ├── admin/          ✅ OK
│   ├── api/            ✅ OK
│   ├── auth/            ✅ OK
│   ├── cart/            ✅ OK
│   ├── category/        ✅ OK
│   ├── order/           ✅ OK
│   ├── payment/         ✅ OK
│   ├── product/         ✅ OK
│   ├── user/            ✅ OK
│   ├── web/             ⚠️ Tên không rõ nghĩa
│   ├── AboutController.java      ⚠️ Nên ở subfolder
│   ├── HomeController.java       ⚠️ Nên ở subfolder
│   ├── LoginController.java      ⚠️ Nên ở auth/
│   ├── LogoutController.java     ⚠️ Nên ở auth/
│   ├── PromotionController.java  ⚠️ Nên ở promotion/
│   ├── RegisterController.java   ⚠️ Nên ở auth/
│   └── StoreController.java      ⚠️ Nên ở store/
├── dao/                 ✅ OK (không đổi)
├── dto/                 ✅ OK (không đổi)
├── filter/              ✅ OK (không đổi)
├── model/               ✅ OK (không đổi)
├── service/             ✅ OK (không đổi)
└── utils/               ✅ OK (không đổi)
```

### 1.2. JSP Views (src/main/webapp/views/)

**VẤN ĐỀ PHÁT HIỆN:**
- ❌ **DUPLICATE FILES**: Có cả `banner_form.jsp` và `banner-form.jsp`, `order_detail.jsp` và `order-detail.jsp`
- ❌ **KHÔNG NHẤT QUÁN**: Mix giữa underscore (`_`) và dash (`-`)
  - `banner_form.jsp` vs `banner-form.jsp`
  - `order_detail.jsp` vs `order-detail.jsp`
  - `change_password.jsp` vs `change-password.jsp`
  - `point_history.jsp` vs `point-history.jsp`
- ❌ **TÊN KHÔNG RÕ NGHĨA**: `store_detail.jsp` ở root, nên ở `store/`
- ⚠️ **THIẾU TỔ CHỨC**: Một số file ở root nên ở subfolder

**CẤU TRÚC HIỆN TẠI:**
```
views/
├── _partials/          ✅ OK
├── admin/              ⚠️ CẦN CHUẨN HÓA (có duplicate)
├── auth/               ✅ OK
├── order/              ✅ OK
├── product/            ✅ OK
├── user/               ⚠️ CẦN CHUẨN HÓA (có duplicate)
├── about.jsp           ⚠️ Nên ở root hoặc about/
├── home.jsp            ✅ OK
├── login.jsp           ⚠️ Nên ở auth/
├── promotions.jsp      ⚠️ Nên ở promotion/
├── promotion_detail.jsp ⚠️ Nên ở promotion/
├── register.jsp        ⚠️ Nên ở auth/
├── stores.jsp          ⚠️ Nên ở store/
└── store_detail.jsp    ⚠️ Nên ở store/
```

## 2. ĐỀ XUẤT CẤU TRÚC CHUẨN

### 2.1. Nguyên tắc đặt tên

**Java Package:**
- lowercase, rõ nghĩa
- Ví dụ: `controller.home`, `controller.promotion`, `controller.store`

**Java Class:**
- PascalCase
- Controller: `*Controller`
- Service: `*Service`
- DAO: `*Dao`
- Entity: PascalCase (không suffix)

**JSP File:**
- lowercase + dash (`-`)
- Ví dụ: `banner-form.jsp`, `order-detail.jsp`, `change-password.jsp`

**JSP Folder:**
- lowercase, rõ module
- Ví dụ: `admin/`, `auth/`, `store/`, `promotion/`

### 2.2. Cấu trúc đề xuất

```
src/main/java/stnw/controller/
├── admin/              (giữ nguyên)
├── api/                (giữ nguyên)
├── auth/               (di chuyển LoginController, LogoutController, RegisterController vào đây)
├── cart/               (giữ nguyên)
├── category/           (giữ nguyên)
├── home/               (mới: di chuyển HomeController)
├── order/              (giữ nguyên)
├── payment/            (giữ nguyên)
├── product/            (giữ nguyên)
├── promotion/          (mới: di chuyển PromotionController)
├── store/              (mới: di chuyển StoreController)
└── user/               (giữ nguyên)

src/main/webapp/views/
├── _partials/          (giữ nguyên)
├── admin/              (chuẩn hóa: chỉ dùng dash)
├── auth/               (di chuyển login.jsp, register.jsp vào đây)
├── cart/               (nếu có)
├── category/           (nếu có)
├── home/               (di chuyển home.jsp vào đây)
├── order/              (giữ nguyên)
├── product/            (giữ nguyên)
├── promotion/          (mới: di chuyển promotions.jsp, promotion-detail.jsp)
├── store/              (mới: di chuyển stores.jsp, store-detail.jsp)
└── user/               (chuẩn hóa: chỉ dùng dash)
```

## 3. BẢNG MAPPING ĐỔI TÊN

### 3.1. Java Controllers

| TÊN CŨ | TÊN MỚI | LÝ DO |
|--------|---------|-------|
| `controller/HomeController.java` | `controller/home/HomeController.java` | Tổ chức theo module, rõ nghĩa hơn |
| `controller/AboutController.java` | `controller/about/AboutController.java` | Tổ chức theo module |
| `controller/LoginController.java` | `controller/auth/LoginController.java` | Đã có folder auth/, nên đưa vào đây |
| `controller/LogoutController.java` | `controller/auth/LogoutController.java` | Đã có folder auth/, nên đưa vào đây |
| `controller/RegisterController.java` | `controller/auth/RegisterController.java` | Đã có folder auth/, nên đưa vào đây |
| `controller/PromotionController.java` | `controller/promotion/PromotionController.java` | Tổ chức theo module |
| `controller/StoreController.java` | `controller/store/StoreController.java` | Tổ chức theo module |
| `controller/web/ImageDownloadController.java` | `controller/web/ImageDownloadController.java` | Giữ nguyên (web là utility) |
| `controller/web/DownloadImageController.java` | `controller/web/DownloadImageController.java` | Giữ nguyên (web là utility) |
| `controller/user/ReOrderController.java` | `controller/user/ReorderController.java` | Chuẩn hóa: ReOrder → Reorder (PascalCase đúng) |

### 3.2. JSP Files - Admin Module

| TÊN CŨ | TÊN MỚI | LÝ DO |
|--------|---------|-------|
| `admin/banner_form.jsp` | `admin/banner-form.jsp` | Chuẩn hóa: underscore → dash |
| `admin/banner-form.jsp` | **XÓA** (duplicate) | File duplicate, giữ banner-form.jsp |
| `admin/order_detail.jsp` | `admin/order-detail.jsp` | Chuẩn hóa: underscore → dash |
| `admin/order-detail.jsp` | **XÓA** (duplicate) | File duplicate, giữ order-detail.jsp |
| `admin/product_form.jsp` | `admin/product-form.jsp` | Chuẩn hóa: underscore → dash |
| `admin/product-form.jsp` | **XÓA** (duplicate) | File duplicate, giữ product-form.jsp |
| `admin/user_form.jsp` | `admin/user-form.jsp` | Chuẩn hóa: underscore → dash |
| `admin/user-form.jsp` | **XÓA** (duplicate) | File duplicate, giữ user-form.jsp |
| `admin/topping_form.jsp` | `admin/topping-form.jsp` | Chuẩn hóa: underscore → dash |
| `admin/topping-form.jsp` | **XÓA** (duplicate) | File duplicate, giữ topping-form.jsp |
| `admin/voucher_form.jsp` | `admin/voucher-form.jsp` | Chuẩn hóa: underscore → dash |
| `admin/voucher-form.jsp` | **XÓA** (duplicate) | File duplicate, giữ voucher-form.jsp |
| `admin/category-form.jsp` | `admin/category-form.jsp` | Giữ nguyên (đã đúng) |
| `admin/category-edit.jsp` | `admin/category-edit.jsp` | Giữ nguyên (đã đúng) |
| `admin/category-list.jsp` | `admin/category-list.jsp` | Giữ nguyên (đã đúng) |
| `admin/add-category.jsp` | `admin/category-add.jsp` | Nhất quán: category-* prefix |
| `admin/edit-category.jsp` | `admin/category-edit.jsp` | Nhất quán: category-* prefix (đã có, xóa duplicate) |
| `admin/list-category.jsp` | `admin/category-list.jsp` | Nhất quán: category-* prefix (đã có, xóa duplicate) |
| `admin/list-user.jsp` | `admin/user-list.jsp` | Nhất quán: user-* prefix (đã có, xóa duplicate) |
| `admin/about_form.jsp` | `admin/about-form.jsp` | Chuẩn hóa: underscore → dash |
| `admin/payment_config.jsp` | `admin/payment-config.jsp` | Chuẩn hóa: underscore → dash |
| `admin/payment-config.jsp` | **XÓA** (duplicate) | File duplicate, giữ payment-config.jsp |

### 3.3. JSP Files - User Module

| TÊN CŨ | TÊN MỚI | LÝ DO |
|--------|---------|-------|
| `user/change_password.jsp` | `user/change-password.jsp` | Chuẩn hóa: underscore → dash |
| `user/change-password.jsp` | **XÓA** (duplicate) | File duplicate, giữ change-password.jsp |
| `user/point_history.jsp` | `user/point-history.jsp` | Chuẩn hóa: underscore → dash |
| `user/point-history.jsp` | **XÓA** (duplicate) | File duplicate, giữ point-history.jsp |

### 3.4. JSP Files - Root & Other Modules

| TÊN CŨ | TÊN MỚI | LÝ DO |
|--------|---------|-------|
| `home.jsp` | `home/home.jsp` | Tổ chức theo module |
| `login.jsp` | `auth/login.jsp` | Đã có folder auth/, nên đưa vào đây |
| `register.jsp` | `auth/register.jsp` | Đã có folder auth/, nên đưa vào đây |
| `about.jsp` | `about/about.jsp` | Tổ chức theo module |
| `promotions.jsp` | `promotion/list.jsp` | Tổ chức theo module, rõ nghĩa hơn |
| `promotion_detail.jsp` | `promotion/detail.jsp` | Tổ chức theo module, rõ nghĩa hơn |
| `promotion-detail.jsp` | **XÓA** (duplicate) | File duplicate, giữ promotion/detail.jsp |
| `stores.jsp` | `store/list.jsp` | Tổ chức theo module, rõ nghĩa hơn |
| `store_detail.jsp` | `store/detail.jsp` | Tổ chức theo module, rõ nghĩa hơn |
| `store-detail.jsp` | **XÓA** (duplicate) | File duplicate, giữ store/detail.jsp |

## 4. CÁC THAM CHIẾU CẦN CẬP NHẬT

### 4.1. Import Package (Java)

**File cần cập nhật:**
- Tất cả file import `stnw.controller.HomeController` → `stnw.controller.home.HomeController`
- Tất cả file import `stnw.controller.AboutController` → `stnw.controller.about.AboutController`
- Tất cả file import `stnw.controller.LoginController` → `stnw.controller.auth.LoginController`
- Tất cả file import `stnw.controller.LogoutController` → `stnw.controller.auth.LogoutController`
- Tất cả file import `stnw.controller.RegisterController` → `stnw.controller.auth.RegisterController`
- Tất cả file import `stnw.controller.PromotionController` → `stnw.controller.promotion.PromotionController`
- Tất cả file import `stnw.controller.StoreController` → `stnw.controller.store.StoreController`
- Tất cả file import `stnw.controller.user.ReOrderController` → `stnw.controller.user.ReorderController`

### 4.2. Forward/Include Path (Java Controllers)

**File cần cập nhật:**

1. **AdminBannerController.java**
   - `/views/admin/banner_form.jsp` → `/views/admin/banner-form.jsp`

2. **AdminOrderController.java**
   - `/views/admin/order_detail.jsp` → `/views/admin/order-detail.jsp`

3. **UserProfileController.java**
   - `/views/user/change_password.jsp` → `/views/user/change-password.jsp`

4. **LoyaltyController.java**
   - `/views/user/point_history.jsp` → `/views/user/point-history.jsp`

5. **Các controller forward đến:**
   - `/views/home.jsp` → `/views/home/home.jsp`
   - `/views/login.jsp` → `/views/auth/login.jsp`
   - `/views/register.jsp` → `/views/auth/register.jsp`
   - `/views/about.jsp` → `/views/about/about.jsp`
   - `/views/promotions.jsp` → `/views/promotion/list.jsp`
   - `/views/promotion_detail.jsp` → `/views/promotion/detail.jsp`
   - `/views/stores.jsp` → `/views/store/list.jsp`
   - `/views/store_detail.jsp` → `/views/store/detail.jsp`

### 4.3. Sitemesh Decorator Path

**File: `WEB-INF/sitemesh3.xml`**
- Không cần thay đổi (decorator path không phụ thuộc vào JSP path)

**File: `WEB-INF/decorators/web.jsp`**
- Không cần thay đổi (include path là absolute: `/views/_partials/navbar.jsp`)

**File: `WEB-INF/decorators/admin.jsp`**
- Không cần thay đổi (không có forward/include JSP)

### 4.4. JSP Include/Forward

**Cần kiểm tra các file JSP có:**
- `<jsp:include page="..."/>`
- `<jsp:forward page="..."/>`
- `request.getRequestDispatcher("...")`

**File cần kiểm tra:**
- Tất cả file trong `views/admin/`
- Tất cả file trong `views/user/`
- `views/home.jsp` (sẽ thành `views/home/home.jsp`)
- `views/login.jsp` (sẽ thành `views/auth/login.jsp`)
- `views/register.jsp` (sẽ thành `views/auth/register.jsp`)

### 4.5. URL Pattern (WebServlet Annotation)

**File cần kiểm tra:**
- `HomeController.java` - có thể có URL pattern `/home` hoặc `/`
- `LoginController.java` - có thể có URL pattern `/login`
- `RegisterController.java` - có thể có URL pattern `/register`
- `PromotionController.java` - có thể có URL pattern `/promotions`
- `StoreController.java` - có thể có URL pattern `/stores`

**Lưu ý:** URL pattern KHÔNG đổi, chỉ đổi package và forward path.

## 5. CHECKLIST TEST SAU KHI RENAME

### 5.1. Test Home Page
- [ ] Truy cập `/` hoặc `/home` → Hiển thị `views/home/home.jsp`
- [ ] Navbar hiển thị đúng
- [ ] Footer hiển thị đúng
- [ ] Product list hiển thị đúng

### 5.2. Test Authentication
- [ ] Truy cập `/login` → Hiển thị `views/auth/login.jsp`
- [ ] Truy cập `/register` → Hiển thị `views/auth/register.jsp`
- [ ] Login thành công → Redirect đúng
- [ ] Register thành công → Redirect đúng
- [ ] Verify OTP → Hiển thị `views/auth/verify.jsp`
- [ ] Forgot password → Hiển thị `views/auth/forgot.jsp`
- [ ] Reset password → Hiển thị `views/auth/reset.jsp`

### 5.3. Test Admin Module
- [ ] Truy cập `/admin/dashboard` → Hiển thị `views/admin/dashboard.jsp`
- [ ] Truy cập `/admin/banners` → Hiển thị `views/admin/banners.jsp`
- [ ] Truy cập `/admin/banners/create` → Hiển thị `views/admin/banner-form.jsp`
- [ ] Truy cập `/admin/orders` → Hiển thị `views/admin/orders.jsp`
- [ ] Truy cập `/admin/orders/detail?id=1` → Hiển thị `views/admin/order-detail.jsp`
- [ ] Truy cập `/admin/products` → Hiển thị `views/admin/products.jsp`
- [ ] Truy cập `/admin/products/create` → Hiển thị `views/admin/product-form.jsp`
- [ ] Truy cập `/admin/users` → Hiển thị `views/admin/user-list.jsp`
- [ ] Truy cập `/admin/users/create` → Hiển thị `views/admin/user-form.jsp`
- [ ] Truy cập `/admin/categories` → Hiển thị `views/admin/category-list.jsp`
- [ ] Truy cập `/admin/categories/add` → Hiển thị `views/admin/category-add.jsp`
- [ ] Truy cập `/admin/categories/edit?id=1` → Hiển thị `views/admin/category-edit.jsp`
- [ ] Truy cập `/admin/payment-config` → Hiển thị `views/admin/payment-config.jsp`
- [ ] Truy cập `/admin/about` → Hiển thị `views/admin/about.jsp`
- [ ] Truy cập `/admin/about/edit` → Hiển thị `views/admin/about-form.jsp`

### 5.4. Test User Module
- [ ] Truy cập `/user/profile` → Hiển thị `views/user/profile.jsp`
- [ ] Truy cập `/user/change-password` → Hiển thị `views/user/change-password.jsp`
- [ ] Truy cập `/user/orders` → Hiển thị `views/user/orders.jsp`
- [ ] Truy cập `/user/loyalty` → Hiển thị `views/user/loyalty.jsp`
- [ ] Truy cập `/user/point-history` → Hiển thị `views/user/point-history.jsp`
- [ ] Truy cập `/user/rewards` → Hiển thị `views/user/rewards.jsp`
- [ ] Truy cập `/user/wishlist` → Hiển thị `views/user/wishlist.jsp`
- [ ] Truy cập `/user/notifications` → Hiển thị `views/user/notifications.jsp`

### 5.5. Test Product Module
- [ ] Truy cập `/p?id=1` → Hiển thị `views/product/detail.jsp`
- [ ] Truy cập `/products` → Hiển thị `views/product/list.jsp`
- [ ] Product card hiển thị đúng
- [ ] Product detail hiển thị đúng

### 5.6. Test Promotion Module
- [ ] Truy cập `/promotions` → Hiển thị `views/promotion/list.jsp`
- [ ] Truy cập `/promotions/detail?id=1` → Hiển thị `views/promotion/detail.jsp`

### 5.7. Test Store Module
- [ ] Truy cập `/stores` → Hiển thị `views/store/list.jsp`
- [ ] Truy cập `/stores/detail?id=1` → Hiển thị `views/store/detail.jsp`

### 5.8. Test About Module
- [ ] Truy cập `/about` → Hiển thị `views/about/about.jsp`

### 5.9. Test Order Module
- [ ] Truy cập `/checkout` → Hiển thị `views/order/checkout.jsp`
- [ ] Checkout flow hoạt động đúng

### 5.10. Test API Endpoints
- [ ] `/api/wishlist/*` → Hoạt động đúng
- [ ] `/api/voucher/*` → Hoạt động đúng
- [ ] `/api/product-modal/*` → Hoạt động đúng
- [ ] `/api/store-search/*` → Hoạt động đúng
- [ ] `/api/notification/*` → Hoạt động đúng

### 5.11. Test Decorator
- [ ] Admin pages có decorator `admin.jsp`
- [ ] Web pages có decorator `web.jsp`
- [ ] API endpoints không có decorator

## 6. LƯU Ý QUAN TRỌNG

1. **KHÔNG đổi URL pattern** trong `@WebServlet` annotation
2. **KHÔNG đổi tên biến** trong request/session/model
3. **KHÔNG đổi logic nghiệp vụ**
4. **CHỈ đổi** package name, file name, folder structure
5. **XÓA các file duplicate** sau khi đã chuẩn hóa
6. **Test kỹ** các forward/include path sau khi rename
7. **Backup** trước khi thực hiện rename

## 7. THỨ TỰ THỰC HIỆN

### Phase 1: Chuẩn hóa JSP (ít rủi ro)
1. Xóa các file duplicate trong `admin/`
2. Đổi tên các file có underscore → dash trong `admin/`
3. Đổi tên các file có underscore → dash trong `user/`
4. Cập nhật forward path trong controllers

### Phase 2: Di chuyển JSP (rủi ro trung bình)
1. Di chuyển `home.jsp` → `home/home.jsp`
2. Di chuyển `login.jsp`, `register.jsp` → `auth/`
3. Di chuyển `promotions.jsp`, `promotion_detail.jsp` → `promotion/`
4. Di chuyển `stores.jsp`, `store_detail.jsp` → `store/`
5. Di chuyển `about.jsp` → `about/about.jsp`
6. Cập nhật forward path trong controllers

### Phase 3: Di chuyển Controllers (rủi ro cao)
1. Di chuyển `HomeController` → `controller/home/`
2. Di chuyển `AboutController` → `controller/about/`
3. Di chuyển `LoginController`, `LogoutController`, `RegisterController` → `controller/auth/`
4. Di chuyển `PromotionController` → `controller/promotion/`
5. Di chuyển `StoreController` → `controller/store/`
6. Đổi tên `ReOrderController` → `ReorderController`
7. Cập nhật import trong tất cả các file

### Phase 4: Test & Fix
1. Chạy toàn bộ checklist test
2. Fix các lỗi phát sinh
3. Verify không có broken link

---

**Tài liệu này được tạo tự động bởi AI Assistant**
**Ngày tạo: 2024**
**Version: 1.0**

