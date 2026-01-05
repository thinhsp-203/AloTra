# BÁO CÁO VI PHẠM KIẾN TRÚC MVC

**Ngày:** 2025-01-27  
**Mục đích:** Phát hiện và sửa các vi phạm kiến trúc MVC + DAO + Service

---

## 🔍 CÁC VI PHẠM ĐÃ PHÁT HIỆN

### 1. Controller sử dụng trực tiếp EntityManager/JpaUtil

#### ❌ AdminPaymentConfigController.java
- **Dòng 3-4, 28, 92, 145:** Import và sử dụng `JpaUtil`, `EntityManager`
- **Dòng 33-36, 43, 98-124, 151-156:** Thao tác DB trực tiếp trong Controller
- **Dòng 100, 103:** Tạo Entity trực tiếp (`new PaymentConfig()`)
- **Dòng 110-118:** Set field Entity trực tiếp

#### ❌ CheckoutController.java
- **Dòng 8-9, 119:** Import và sử dụng `JpaUtil`, `EntityManager`
- **Dòng 119-135:** Logic rollback nằm trong Controller

#### ❌ LoyaltyController.java
- **Dòng 87-95:** Sử dụng `EntityManager` để refresh user từ DB

---

### 2. Controller tạo Entity trực tiếp và set field

#### ❌ AdminProductController.java
- **Dòng 136-138:** `new Product()`
- **Dòng 141-157:** Set field Entity trực tiếp
- **Dòng 152-154:** `new Category()` và set field

#### ❌ AdminStoreController.java
- **Dòng 70:** `new Store()`
- **Dòng 73-82:** Set field Entity trực tiếp

#### ❌ CategoryAddController.java
- **Dòng 39:** `new Category()`
- **Dòng 40-42:** Set field Entity trực tiếp

---

## 📋 KẾ HOẠCH SỬA

### Phase 1: Tạo Service cho PaymentConfig
- Tạo `PaymentConfigService` interface
- Tạo `PaymentConfigServiceImpl`
- Di chuyển logic từ Controller → Service

### Phase 2: Sửa CheckoutController
- Di chuyển logic rollback → `OrderService`

### Phase 3: Sửa LoyaltyController
- Di chuyển logic refresh user → `LoyaltyService` hoặc `UserService`

### Phase 4: Sửa các Controller tạo Entity
- Di chuyển logic tạo Entity và set field → Service
- Controller chỉ truyền parameters

---

## ✅ TIÊU CHUẨN SAU KHI SỬA

- Controller KHÔNG import DAO/DAOImpl
- Controller KHÔNG new Entity để xử lý logic
- Controller KHÔNG dùng EntityManager/JpaUtil
- Controller CHỈ import Service
- Service xử lý nghiệp vụ và gọi DAO
- DAO chỉ xử lý DB

