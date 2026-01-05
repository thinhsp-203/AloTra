# ĐỀ XUẤT CHUẨN HÓA TÊN FILE & FOLDER
## Dự án AloTra - Java Web (Servlet + JSP/JSTL + JPA)

---

## 📋 NGUYÊN TẮC CHUẨN HÓA

1. **Package**: lowercase, kebab-case nếu cần, rõ nghĩa theo module/chức năng
2. **Class**: PascalCase, suffix đúng vai trò (Controller/Service/Dao/Entity)
3. **JSP**: lowercase, kebab-case (-), có prefix module nếu cần
4. **Folder JSP**: tách rõ theo module (admin, user, product, auth, ...)

---

## 🎯 CẤU TRÚC FOLDER/PACKAGE CHUẨN

```
src/main/java/
├── controller/
│   ├── admin/          # Admin controllers
│   ├── api/            # API endpoints
│   ├── auth/           # Authentication
│   ├── cart/           # Shopping cart
│   ├── category/       # Category management
│   ├── order/          # Order processing
│   ├── payment/        # Payment gateway
│   ├── product/        # Product pages
│   ├── user/           # User profile
│   └── web/            # Web utilities (download, etc)
├── service/
│   └── impl/           # Service implementations
├── dao/
│   └── impl/           # DAO implementations
├── model/              # JPA Entities
├── dto/                # Data Transfer Objects
├── filter/             # Servlet Filters
├── config/             # Configuration
└── utils/              # Utilities

src/main/webapp/views/
├── _partials/          # Reusable components
├── admin/              # Admin pages
│   └── reports/        # Report subpages
├── auth/               # Auth pages
├── user/               # User pages
├── product/            # Product pages
├── order/              # Order pages
├── home.jsp            # Homepage
├── login.jsp           # Login
├── register.jsp        # Register
├── promotions.jsp      # Promotions list
└── stores.jsp          # Stores list

WEB-INF/decorators/     # Sitemesh decorators
```

---

## 📊 BẢNG MAPPING: TÊN CŨ → TÊN MỚI

### 🔵 DAO LAYER (Chuẩn hóa: tất cả dùng "Dao" suffix)

| TÊN CŨ | TÊN MỚI | LÝ DO |
|--------|---------|-------|
| `dao/BannerRepository.java` | `dao/BannerDao.java` | Chuẩn hóa suffix "Dao" thay vì "Repository" |
| `dao/CategoryRepository.java` | `dao/CategoryDao.java` | Chuẩn hóa suffix "Dao" |
| `dao/OrderRepository.java` | `dao/OrderDao.java` | Chuẩn hóa suffix "Dao" |
| `dao/ProductQueryRepository.java` | `dao/ProductQueryDao.java` | Chuẩn hóa suffix "Dao" |
| `dao/PromotionRepository.java` | `dao/PromotionDao.java` | Chuẩn hóa suffix "Dao" |
| `dao/UserRepository.java` | `dao/UserDao.java` | Chuẩn hóa suffix "Dao" |
| `dao/VoucherRepository.java` | `dao/VoucherDao.java` | Chuẩn hóa suffix "Dao" |
| `dao/impl/BannerRepositoryImpl.java` | `dao/impl/BannerDaoImpl.java` | Chuẩn hóa suffix "DaoImpl" |
| `dao/impl/CategoryRepositoryImpl.java` | `dao/impl/CategoryDaoImpl.java` | Chuẩn hóa suffix "DaoImpl" |
| `dao/impl/OrderRepositoryImpl.java` | `dao/impl/OrderDaoImpl.java` | Chuẩn hóa suffix "DaoImpl" |
| `dao/impl/ProductQueryRepositoryImpl.java` | `dao/impl/ProductQueryDaoImpl.java` | Chuẩn hóa suffix "DaoImpl" |
| `dao/impl/PromotionRepositoryImpl.java` | `dao/impl/PromotionDaoImpl.java` | Chuẩn hóa suffix "DaoImpl" |
| `dao/impl/UserRepositoryImpl.java` | `dao/impl/UserDaoImpl.java` | Chuẩn hóa suffix "DaoImpl" |
| `dao/impl/VoucherRepositoryImpl.java` | `dao/impl/VoucherDaoImpl.java` | Chuẩn hóa suffix "DaoImpl" |

### 🟢 CONTROLLER LAYER

| TÊN CŨ | TÊN MỚI | LÝ DO |
|--------|---------|-------|
| `controller/product/ProductsListController.java` | `controller/product/ProductListController.java` | Singular "Product" đúng chuẩn Java naming |
| `controller/product/ProductPageController.java` | `controller/product/ProductListController.java` | "List" rõ nghĩa hơn "Page", trùng với file trên → cần merge logic |
| `controller/admin/UserListController.java` | `controller/admin/AdminUserListController.java` | Thêm prefix "Admin" để đồng nhất với các controller khác |
| `controller/user/ReOrderController.java` | `controller/user/ReorderController.java` | Viết liền "Reorder" đúng chuẩn Java camelCase |
| `controller/web/DownloadImageController.java` | `controller/web/ImageDownloadController.java` | Động từ + danh từ phù hợp hơn với convention |

**Lưu ý:** `controller/product/ProductsListController.java` và `controller/product/ProductPageController.java` có vẻ trùng chức năng, cần kiểm tra và merge nếu cần.

### 🟡 SERVICE LAYER (Không đổi - đã chuẩn)

Service layer đã tuân thủ đúng convention, không cần thay đổi.

### 🟠 JSP FILES

#### Admin JSPs

| TÊN CŨ | TÊN MỚI | LÝ DO |
|--------|---------|-------|
| `views/admin/add-category.jsp` | `views/admin/category-form.jsp` | Prefix "category-" cho consistency, "form" rõ hơn "add" |
| `views/admin/edit-category.jsp` | `views/admin/category-edit.jsp` | Prefix "category-" để nhóm với các file category khác |
| `views/admin/list-category.jsp` | `views/admin/category-list.jsp` | Prefix "category-" cho consistency |
| `views/admin/list-user.jsp` | `views/admin/user-list.jsp` | Prefix "user-" để nhóm với user-form.jsp |
| `views/admin/user_form.jsp` | `views/admin/user-form.jsp` | Kebab-case thống nhất (đã đúng, chỉ để reference) |
| `views/admin/banner_form.jsp` | `views/admin/banner-form.jsp` | Kebab-case thống nhất |
| `views/admin/product_form.jsp` | `views/admin/product-form.jsp` | Kebab-case thống nhất |
| `views/admin/topping_form.jsp` | `views/admin/topping-form.jsp` | Kebab-case thống nhất |
| `views/admin/voucher_form.jsp` | `views/admin/voucher-form.jsp` | Kebab-case thống nhất |
| `views/admin/order_detail.jsp` | `views/admin/order-detail.jsp` | Kebab-case thống nhất |
| `views/admin/payment_config.jsp` | `views/admin/payment-config.jsp` | Kebab-case thống nhất |

#### User JSPs

| TÊN CŨ | TÊN MỚI | LÝ DO |
|--------|---------|-------|
| `views/user/change_password.jsp` | `views/user/change-password.jsp` | Kebab-case thống nhất |
| `views/user/point_history.jsp` | `views/user/point-history.jsp` | Kebab-case thống nhất |

#### Product JSPs

| TÊN CŨ | TÊN MỚI | LÝ DO |
|--------|---------|-------|
| `views/product/list.jsp` | `views/product/list.jsp` | Giữ nguyên (đã chuẩn) |

#### Root JSPs

| TÊN CŨ | TÊN MỚI | LÝ DO |
|--------|---------|-------|
| `views/promotion_detail.jsp` | `views/promotion-detail.jsp` | Kebab-case thống nhất |
| `views/store_detail.jsp` | `views/store-detail.jsp` | Kebab-case thống nhất |

---

## ⚠️ LƯU Ý QUAN TRỌNG

### 1. **URL Patterns & Servlet Mappings**
- **KHÔNG** thay đổi `@WebServlet(urlPatterns)` trong các Controller
- **KHÔNG** thay đổi các URL trong JSP (href, form action, etc.)
- Chỉ đổi tên class/file, không đổi logic mapping

### 2. **Package Declaration & Imports**
- Cập nhật `package` declaration trong tất cả file đổi tên
- Cập nhật `import` statements trong các file liên quan
- Cập nhật `web.xml` nếu có servlet-mapping explicit

### 3. **Reference Updates**
Cần cập nhật references trong:
- Service implementations (inject DAO)
- Controller classes (inject Service)
- Test files (nếu có)
- Configuration files

### 4. **DAO → Repository Confusion**
Hiện tại có cả `Dao` và `Repository` suffix. **Chuẩn hóa về `Dao`** vì:
- Đã có nhiều file dùng `Dao` (7 files)
- `Dao` là convention phổ biến trong Java Web
- Giữ consistency với `DaoImpl` suffix

### 5. **Controller Naming Conflicts**
- `ProductsListController` và `ProductPageController` có vẻ trùng chức năng → Cần kiểm tra và merge nếu cần trước khi đổi tên
- `AdminDashboardReportController` vs `AdminReportController` → Cần kiểm tra xem có trùng không

---

## 📝 CHECKLIST THỰC HIỆN

### Bước 1: Backup
- [ ] Tạo branch mới: `feature/naming-standardization`
- [ ] Commit hiện trạng trước khi thay đổi

### Bước 2: Refactor DAO Layer
- [ ] Đổi tên interface `*Repository` → `*Dao`
- [ ] Đổi tên implementation `*RepositoryImpl` → `*DaoImpl`
- [ ] Cập nhật package declarations
- [ ] Cập nhật tất cả imports trong Service layer

### Bước 3: Refactor Controller Layer
- [ ] Đổi tên các Controller theo mapping
- [ ] Cập nhật package declarations
- [ ] Cập nhật imports trong JSP (nếu có)

### Bước 4: Refactor JSP Layer
- [ ] Đổi tên JSP files theo mapping
- [ ] Cập nhật includes/forwards trong Controllers
- [ ] Cập nhật Sitemesh decorator patterns (nếu có)

### Bước 5: Testing
- [ ] Compile project: `mvn clean compile`
- [ ] Test từng module: admin, user, product, order
- [ ] Verify URL patterns vẫn hoạt động
- [ ] Verify JSP rendering

### Bước 6: Documentation
- [ ] Cập nhật README.md (nếu có)
- [ ] Cập nhật API documentation (nếu có)

---

## 🔍 THỐNG KÊ

- **Tổng số file cần đổi:** ~35 files
  - DAO: 14 files
  - Controller: 5 files
  - JSP: 16 files

- **Ưu tiên:**
  1. **Cao:** DAO layer (inconsistency nghiêm trọng)
  2. **Trung:** JSP naming (consistency)
  3. **Thấp:** Controller naming (chỉ một vài file)

---

## ✅ KẾT LUẬN

Sau khi chuẩn hóa:
- ✅ Package naming: nhất quán, rõ nghĩa
- ✅ Class naming: đúng suffix convention
- ✅ JSP naming: kebab-case thống nhất
- ✅ Folder structure: tách rõ theo module

**Không ảnh hưởng:**
- ❌ Kiến trúc MVC
- ❌ URL patterns
- ❌ Business logic
- ❌ Database schema

