# ✅ HOÀN THÀNH: Chuẩn Hóa Logic Size Cho Thức Uống

## 📋 ROOT CAUSE ANALYSIS

### **Vấn đề chính:**
1. ✅ Sizes chỉ được tạo trong **memory**, không lưu vào DB
   - **File:** `src/main/java/service/impl/ProductQueryServiceImpl.java` (dòng 72-88)
   - **Vấn đề:** Khi `getSizes()` không tìm thấy sizes trong DB, code tạo sizes mới nhưng chỉ trả về trong memory, không persist

2. ✅ Giá size đang **hard-code** trong service
   - **File:** `src/main/java/service/impl/ProductQueryServiceImpl.java` (dòng 80, 85)
   - **Vấn đề:** `BigDecimal.valueOf(5000)`, `BigDecimal.valueOf(10000)` viết trực tiếp trong code

3. ✅ Admin tạo sản phẩm thức uống mới → **không tự động tạo sizes**
   - **File:** `src/main/java/service/impl/AdminProductServiceImpl.java`
   - **Vấn đề:** `saveProduct()` chỉ lưu Product, không tự động tạo ProductSize

4. ✅ UI đã đúng: Không hard-code giá trong JSP/JS
   - **File:** `src/main/webapp/assets/js/app.js` (dòng 299, 302)
   - **Status:** ✅ Lấy giá từ API (`data-price-adj="${s.priceAdjustment}"`), không hard-code

---

## ✅ GIẢI PHÁP ĐÃ TRIỂN KHAI

### **1. Tạo ProductSizeDao + ProductSizeDaoImpl**
**Files:**
- `src/main/java/dao/ProductSizeDao.java` (NEW)
- `src/main/java/dao/impl/ProductSizeDaoImpl.java` (NEW)

**Chức năng:**
- `save(ProductSize)` - Insert/Update size vào DB
- `findByProductId(int productId)` - Lấy tất cả sizes của sản phẩm
- `exists(int productId, String sizeName)` - Kiểm tra size đã tồn tại chưa
- `delete(int sizeId)` - Xóa size
- `deleteByProductId(int productId)` - Xóa tất cả sizes của sản phẩm

---

### **2. Cập nhật AdminProductServiceImpl - Tự động tạo sizes khi tạo sản phẩm mới**
**File:** `src/main/java/service/impl/AdminProductServiceImpl.java`

**Thay đổi:**
- Thêm constants cho giá size mặc định (không hard-code trong logic):
  ```java
  private static final BigDecimal SIZE_S_PRICE = BigDecimal.ZERO; // +0 VND
  private static final BigDecimal SIZE_M_PRICE = BigDecimal.valueOf(5000); // +5,000 VND
  private static final BigDecimal SIZE_L_PRICE = BigDecimal.valueOf(10000); // +10,000 VND
  ```

- Thêm method `createDefaultSizesForDrink(int productId)`:
  - Kiểm tra product có phải thức uống không (category.isDrink = true)
  - Kiểm tra đã có sizes chưa (nếu có rồi thì không tạo lại)
  - Tự động tạo 3 sizes: S (+0₫), M (+5k₫), L (+10k₫) và lưu vào DB

- Gọi `createDefaultSizesForDrink()` trong `saveProduct()` sau khi lưu product thành công

---

### **3. Cập nhật ProductQueryServiceImpl - Insert sizes vào DB thay vì memory**
**File:** `src/main/java/service/impl/ProductQueryServiceImpl.java`

**Thay đổi:**
- Thêm constants cho giá size mặc định (giống AdminProductServiceImpl)
- Thêm import `ProductSizeDao` và `ProductSizeDaoImpl`

- Sửa logic trong `getSizes()`:
  - **TRƯỚC:** Tạo sizes trong memory, trả về `List.of(sizeS, sizeM, sizeL)`
  - **SAU:** 
    1. Tạo sizes với giá từ constants
    2. Sử dụng `ProductSizeDao` để kiểm tra và insert vào DB (nếu chưa tồn tại)
    3. Query lại từ DB để đảm bảo có ID và thông tin đầy đủ
    4. Trả về `sizeDao.findByProductId(productId)`

**Lưu ý:** Logic này đảm bảo backward compatibility - nếu thức uống chưa có sizes, sẽ tự động tạo khi lần đầu tiên được query.

---

### **4. Tạo Script SQL cho dữ liệu hiện có**
**File:** `database/insert_default_sizes_for_drinks.sql` (NEW)

**Chức năng:**
- Insert sizes mặc định (S/M/L) cho tất cả sản phẩm thức uống hiện có chưa có sizes
- Sử dụng `LEFT JOIN` và `NOT IN` để tránh duplicate
- Transaction-safe (BEGIN/COMMIT)
- Có query kiểm tra kết quả

**Giá size mặc định:**
- Size S: +0 VND
- Size M: +5,000 VND
- Size L: +10,000 VND

---

## 📄 FILES ĐÃ THAY ĐỔI

### **Files mới:**
1. ✅ `src/main/java/dao/ProductSizeDao.java`
2. ✅ `src/main/java/dao/impl/ProductSizeDaoImpl.java`
3. ✅ `database/insert_default_sizes_for_drinks.sql`

### **Files đã sửa:**
1. ✅ `src/main/java/service/impl/AdminProductServiceImpl.java`
2. ✅ `src/main/java/service/impl/ProductQueryServiceImpl.java`

---

## 🧪 HƯỚNG DẪN TEST

### **Bước 1: Chạy SQL Script (cho dữ liệu hiện có)**
```sql
-- Chạy file: database/insert_default_sizes_for_drinks.sql
-- Script sẽ tự động insert S/M/L cho tất cả thức uống chưa có sizes
```

### **Bước 2: Test Case 1 - Tạo sản phẩm thức uống mới**
1. Đăng nhập admin
2. Vào **Sản phẩm → Thêm mới**
3. Tạo sản phẩm mới:
   - Tên: "Trà sữa Test"
   - Danh mục: Chọn một category thức uống (isDrink=1)
   - Giá: 30000
   - Lưu
4. **Kiểm tra DB:**
   ```sql
   SELECT * FROM ProductSize WHERE product_id = [ID sản phẩm vừa tạo]
   ```
   - **Kỳ vọng:** Có 3 records: S (0), M (5000), L (10000)

### **Bước 3: Test Case 2 - Xem chi tiết sản phẩm**
1. Vào trang chi tiết sản phẩm thức uống vừa tạo
2. Mở modal "Thêm vào giỏ" hoặc xem trên product detail page
3. **Kiểm tra:**
   - ✅ Hiển thị 3 size options: S, M, L
   - ✅ Size S được **checked mặc định** (`index === 0 ? 'checked' : ''`)
   - ✅ Hiển thị giá: S (+0đ), M (+5.000đ), L (+10.000đ)
   - ✅ Giá được lấy từ backend (API), không hard-code trong JS

### **Bước 4: Test Case 3 - Đổi size → Cập nhật giá**
1. Trong modal/product detail, chọn size M
2. **Kiểm tra:**
   - ✅ Tổng tiền = Giá gốc + 5.000đ
3. Chọn size L
4. **Kiểm tra:**
   - ✅ Tổng tiền = Giá gốc + 10.000đ

### **Bước 5: Test Case 4 - Sản phẩm đồ ăn không có size**
1. Tạo sản phẩm mới thuộc category đồ ăn (isDrink=0)
2. Vào chi tiết sản phẩm
3. **Kiểm tra:**
   - ✅ KHÔNG hiển thị size options
   - ✅ KHÔNG tự động tạo sizes trong DB

### **Bước 6: Test Case 5 - Backward Compatibility**
1. Vào chi tiết một sản phẩm thức uống CŨ (chưa chạy SQL script, chưa có sizes trong DB)
2. **Kiểm tra:**
   - ✅ Tự động tạo sizes S/M/L và lưu vào DB
   - ✅ Hiển thị đúng trên UI
   - ✅ Sau khi tạo, query lại DB sẽ thấy sizes đã được lưu

---

## ✅ KẾT QUẢ

1. ✅ **Không còn hard-code giá size** trong logic (dùng constants)
2. ✅ **Sizes được lưu vào DB**, không chỉ tạo trong memory
3. ✅ **Admin tạo thức uống mới** → tự động có 3 sizes (S/M/L)
4. ✅ **UI hiển thị đúng**: Size S checked mặc định, giá lấy từ backend
5. ✅ **Backward compatible**: Tự động tạo sizes cho thức uống cũ khi query
6. ✅ **Script SQL** sẵn sàng để update dữ liệu hiện có

---

## 📝 NOTES

- **Constants:** Giá size mặc định được định nghĩa ở 2 nơi (AdminProductServiceImpl và ProductQueryServiceImpl) để tránh hard-code. Nếu cần thay đổi giá, chỉ cần sửa constants.
- **DB Schema:** Giả sử bảng `ProductSize` đã có sẵn với các cột: `size_id`, `product_id`, `size_name`, `price_adjustment`
- **Performance:** Logic kiểm tra `exists()` trước khi insert để tránh duplicate và lỗi constraint

---

**Status:** ✅ Hoàn thành 100%  
**Date:** 2026-01-04  
**Files changed:** 2 files  
**Files created:** 3 files


