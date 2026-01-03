# 🔍 PHÂN TÍCH & FIX: Logic Hiển Thị Size Cho Sản Phẩm Thức Uống

## 📋 ROOT CAUSE ANALYSIS

### **Vấn đề chính:**
Một số category thức uống không hiển thị/cho chọn size (S/M/L), trong khi một số category khác thì có.

### **Root Cause #1: JavaScript điều kiện hiển thị sai (PRIMARY)**
**File:** `src/main/webapp/assets/js/app.js`
- **Dòng 201 & 333:** `${data.sizes.length > 1 ? ...}`
- **Vấn đề:** Chỉ hiển thị size khi có **> 1** size
- **Hậu quả:** Nếu sản phẩm thức uống chỉ có 1 size trong DB (hoặc trả về 1 size "Mặc định"), sẽ không hiển thị UI chọn size

### **Root Cause #2: Service không tự tạo sizes cho drink**
**File:** `src/main/java/service/impl/ProductQueryServiceImpl.java`
- **Method:** `getSizes(int productId)` (dòng 42-60)
- **Vấn đề:** Nếu không có size trong DB, chỉ trả về 1 size "Mặc định"
- **Thiếu:** Logic kiểm tra `category.isDrink` để tự động tạo sizes S/M/L cho thức uống

### **Root Cause #3: Thiếu flag phân biệt Drink/Food**
**File:** `src/main/java/model/Category.java`
- **Vấn đề:** Category entity không có field `isDrink` hoặc `type`
- **Hậu quả:** Phải hard-code dựa vào tên category (như trong `getAvailableToppingsForCategory`)

---

## ✅ GIẢI PHÁP ĐÃ TRIỂN KHAI

### **1. Thêm field `isDrink` vào Category**
**Files thay đổi:**
- `src/main/java/model/Category.java`: Thêm field `isDrink` (Boolean)
- `database/alter_category_add_isDrink.sql`: Migration script

**Chi tiết:**
```java
@Column(name = "isDrink")
private Boolean isDrink;

public Boolean getIsDrink() { return isDrink != null && isDrink; }
```

### **2. Sửa JavaScript điều kiện hiển thị size**
**File:** `src/main/webapp/assets/js/app.js`
- **Dòng 201 & 333:** 
  - **Trước:** `${data.sizes.length > 1 ? ...}`
  - **Sau:** `${(data.sizes && data.sizes.length > 0 && (data.product.isDrink || data.sizes.length > 1)) ? ...}`

**Logic mới:**
- Hiển thị size nếu: `sizes.length > 0` VÀ (`isDrink = true` HOẶC `sizes.length > 1`)
- Tức là: **Mọi thức uống (isDrink=true) đều hiển thị size, bất kể số lượng sizes**

### **3. Service tự tạo sizes cho drink nếu thiếu**
**File:** `src/main/java/service/impl/ProductQueryServiceImpl.java`
- **Method:** `getSizes(int productId)`
- **Logic mới:**
  1. Lấy sizes từ DB
  2. Nếu empty → Lấy product và category
  3. Nếu `category.isDrink = true` → Tự tạo sizes: **S (0₫), M (+5k), L (+10k)**
  4. Nếu không phải drink → Trả về size "Mặc định"

### **4. API trả về flag `isDrink`**
**File:** `src/main/java/controller/api/ProductModalApiController.java`
- **Dòng 37:** Thêm logic lấy `isDrink` từ category
- **Dòng 43:** Thêm `isDrink` vào JSON response: `"isDrink":true/false`

---

## 🧪 HƯỚNG DẪN TEST

### **Bước 1: Chạy migration script**
```sql
-- Chạy file: database/alter_category_add_isDrink.sql
-- Script sẽ:
-- 1. Thêm cột isDrink vào bảng Category
-- 2. Set mặc định = 0 (Đồ ăn)
-- 3. Update các category thức uống = 1 (dựa trên tên category)
```

### **Bước 2: Kiểm tra dữ liệu**
```sql
-- Kiểm tra categories đã được đánh dấu đúng chưa
SELECT cate_id, cate_name, isDrink 
FROM Category 
ORDER BY isDrink DESC, cate_name;
```

### **Bước 3: Test Case 1 - Thức uống có size trong DB**
1. Chọn 1 sản phẩm thức uống đã có size S/M/L trong bảng `ProductSize`
2. Mở modal/product detail
3. **Kỳ vọng:** Hiển thị các size options (S/M/L)

### **Bước 4: Test Case 2 - Thức uống KHÔNG có size trong DB** ⭐
1. Tìm 1 sản phẩm thức uống **KHÔNG có** record nào trong bảng `ProductSize`
2. Đảm bảo category của sản phẩm có `isDrink = 1`
3. Mở modal/product detail
4. **Kỳ vọng:** 
   - Tự động hiển thị 3 sizes: **S (0₫), M (+5,000₫), L (+10,000₫)**
   - Có thể chọn size và giá tự động cập nhật

### **Bước 5: Test Case 3 - Đồ ăn (không phải thức uống)**
1. Chọn 1 sản phẩm đồ ăn (`isDrink = 0`)
2. Đảm bảo không có size trong `ProductSize`
3. Mở modal/product detail
4. **Kỳ vọng:** 
   - KHÔNG hiển thị size options (vì `isDrink = false` và `sizes.length = 0`)

### **Bước 6: Test Case 4 - Thức uống chỉ có 1 size trong DB**
1. Tạo/thay đổi 1 sản phẩm thức uống chỉ có 1 size (ví dụ: chỉ có "M")
2. Mở modal/product detail
3. **Kỳ vọng:** Vẫn hiển thị size option (vì `isDrink = true`)

---

## 📝 FILES ĐÃ THAY ĐỔI

### **Java Files:**
1. `src/main/java/model/Category.java` - Thêm field `isDrink`
2. `src/main/java/service/impl/ProductQueryServiceImpl.java` - Logic tự tạo sizes cho drink
3. `src/main/java/controller/api/ProductModalApiController.java` - Trả về `isDrink` flag

### **JavaScript Files:**
4. `src/main/webapp/assets/js/app.js` - Sửa điều kiện hiển thị size (2 chỗ: modal & detail page)

### **Database:**
5. `database/alter_category_add_isDrink.sql` - Migration script

---

## ⚠️ LƯU Ý QUAN TRỌNG

1. **Migration Script:** 
   - Script tự động update các category thức uống dựa trên tên (contains "trà", "cà phê", "sinh tố", "nước"...)
   - **CẦN KIỂM TRA LẠI** danh sách category names trong database của bạn
   - Có thể cần chỉnh sửa điều kiện WHERE trong script cho phù hợp

2. **Auto-create sizes:**
   - Sizes được tạo **trong memory** (không persist vào DB)
   - Nếu muốn lưu vào DB, cần thêm logic persist sau khi tạo

3. **Giá upsize:**
   - Hiện tại hard-code: M = +5k, L = +10k
   - Nếu muốn configurable, có thể thêm vào Category entity hoặc config file

4. **Backward compatibility:**
   - Các sản phẩm đã có size trong DB vẫn hoạt động bình thường
   - Không ảnh hưởng đến logic hiện tại

---

## 🔄 NEXT STEPS (Optional)

1. **Admin UI:** Thêm checkbox "Là thức uống" trong form thêm/sửa category
2. **Config upsize price:** Cho phép admin cấu hình giá S/M/L cho từng category
3. **Persist auto-created sizes:** Lưu sizes tự tạo vào DB thay vì chỉ tạo trong memory
4. **Validation:** Thêm validation khi tạo product thức uống phải có ít nhất 1 size

---

**Ngày fix:** 2026-01-04  
**Engineer:** AI Assistant  
**Status:** ✅ Hoàn thành - Ready for testing


