# ✅ NOTIFICATION DELETE FEATURE COMPLETE

## 📋 YÊU CẦU

Cho phép user xóa thông báo trong hệ thống:
- ✅ User chỉ được xóa thông báo của chính mình
- ✅ Xóa xong UI cập nhật ngay (reload)
- ✅ Không làm ảnh hưởng phần tạo thông báo hiện có
- ✅ Thêm nút "Xóa" cho từng thông báo
- ✅ Thêm nút "Xóa tất cả"

---

## ✅ GIẢI PHÁP

### **1. Thêm Field isDeleted (Soft Delete)**

**File:** `src/main/java/model/Notification.java`

Thêm field:
```java
@Column(nullable = false)
private Boolean isDeleted = false;
```

**Lý do:** Dùng soft delete để an toàn, có thể khôi phục nếu cần.

### **2. Migration SQL**

**File:** `database/add_isDeleted_to_notification.sql`

Script thêm cột `isDeleted` vào bảng `Notification`:
```sql
ALTER TABLE [dbo].[Notification]
ADD [isDeleted] [bit] NOT NULL DEFAULT 0;
```

### **3. Cập nhật DAO**

**File:** `src/main/java/dao/NotificationDao.java` và `src/main/java/dao/impl/NotificationDaoImpl.java`

**Thay đổi:**
1. **Filter isDeleted trong queries:**
   - `findByUserId()`: Thêm điều kiện `AND (n.isDeleted = false OR n.isDeleted IS NULL)`
   - `findRecentByUserId()`: Thêm điều kiện `AND (n.isDeleted = false OR n.isDeleted IS NULL)`
   - `countUnreadByUserId()`: Thêm điều kiện `AND (n.isDeleted = false OR n.isDeleted IS NULL)`
   - `markAllAsRead()`: Thêm điều kiện `AND (n.isDeleted = false OR n.isDeleted IS NULL)`

2. **Thêm methods:**
   - `markAsDeleted(Integer id, Integer userId)`: Xóa 1 thông báo (check owner)
   - `markAllAsDeleted(Integer userId)`: Xóa tất cả thông báo của user

**Security:** `markAsDeleted()` kiểm tra `notification.getUser().getId().equals(userId)` trước khi xóa.

### **4. Cập nhật Service**

**File:** `src/main/java/service/NotificationService.java` và `src/main/java/service/impl/NotificationServiceImpl.java`

**Thêm methods:**
- `deleteNotification(Integer notificationId, Integer userId)`: Xóa 1 thông báo
- `deleteAllNotifications(Integer userId)`: Xóa tất cả thông báo

**Cập nhật:** `createNotification()` set `isDeleted = false` khi tạo mới.

### **5. Cập nhật Controller**

**File:** `src/main/java/controller/user/NotificationController.java`

**Thêm actions trong doPost():**
- `action=delete`: Xóa 1 thông báo (param: id)
- `action=deleteAll`: Xóa tất cả thông báo

**Security:** 
- Kiểm tra `currentUser` từ session
- Pass `currentUser.getId()` vào service (service/DAO sẽ check owner)

### **6. Cập nhật JSP**

**File:** `src/main/webapp/views/user/notifications.jsp`

**Thay đổi:**
1. **Nút "Xóa tất cả":**
   - Thêm form với `action=deleteAll`
   - Có confirm dialog: "Bạn có chắc muốn xóa tất cả thông báo?"
   - Icon: `bi-trash`, màu: `btn-outline-danger`

2. **Nút "Xóa" cho từng thông báo:**
   - Thêm form với `action=delete` và `id=${notif.id}`
   - Có confirm dialog: "Bạn có chắc muốn xóa thông báo này?"
   - Icon: `bi-trash`, màu: `text-danger`
   - Đặt bên cạnh nút "Đánh dấu đã đọc"

---

## 📄 FILES CHANGED

### **Modified Files:**
1. `src/main/java/model/Notification.java` - Thêm field isDeleted
2. `src/main/java/dao/NotificationDao.java` - Thêm methods delete
3. `src/main/java/dao/impl/NotificationDaoImpl.java` - Implement delete + filter isDeleted
4. `src/main/java/service/NotificationService.java` - Thêm methods delete
5. `src/main/java/service/impl/NotificationServiceImpl.java` - Implement delete
6. `src/main/java/controller/user/NotificationController.java` - Thêm endpoints delete
7. `src/main/webapp/views/user/notifications.jsp` - Thêm nút xóa

### **New Files:**
8. `database/add_isDeleted_to_notification.sql` - Migration script

---

## 🔒 SECURITY

### **Owner Check:**
- ✅ DAO `markAsDeleted()` kiểm tra: `notification.getUser().getId().equals(userId)`
- ✅ Service chỉ pass `currentUser.getId()` từ session
- ✅ Controller kiểm tra `currentUser != null` trước khi xử lý

### **CSRF Protection:**
- ✅ Dùng POST method (không phải GET)
- ✅ Validate session user (không có session = redirect login)
- ✅ Không có CSRF token riêng (dự án chưa dùng CSRF token)

---

## 🎯 KẾT QUẢ

### **Functionality:**
1. ✅ User có thể xóa 1 thông báo
2. ✅ User có thể xóa tất cả thông báo
3. ✅ User chỉ xóa được thông báo của chính mình
4. ✅ Thông báo đã xóa không hiển thị trong list
5. ✅ Thông báo đã xóa không được tính vào unread count
6. ✅ UI reload sau khi xóa (đơn giản, dễ maintain)

### **UI/UX:**
- ✅ Nút "Xóa tất cả" ở header (bên cạnh "Đánh dấu tất cả đã đọc")
- ✅ Nút "Xóa" (icon thùng rác) cho từng thông báo
- ✅ Confirm dialog trước khi xóa (tránh xóa nhầm)
- ✅ Layout không bị vỡ, responsive

---

## 🧪 HƯỚNG DẪN TEST

### **Test Case 1: Xóa 1 thông báo**
1. Đăng nhập với User A
2. Vào `/user/notifications`
3. Click nút "Xóa" (icon thùng rác) trên 1 thông báo
4. **Kiểm tra:**
   - ✅ Hiển thị confirm dialog: "Bạn có chắc muốn xóa thông báo này?"
   - ✅ Click OK → Thông báo biến mất khỏi list
   - ✅ Reload trang → Thông báo không còn hiển thị
   - ✅ Unread count không còn tính thông báo đã xóa

### **Test Case 2: Xóa tất cả**
1. Đăng nhập với User A (có ít nhất 2 thông báo)
2. Vào `/user/notifications`
3. Click nút "Xóa tất cả"
4. **Kiểm tra:**
   - ✅ Hiển thị confirm dialog: "Bạn có chắc muốn xóa tất cả thông báo?"
   - ✅ Click OK → Tất cả thông báo biến mất
   - ✅ Reload trang → Hiển thị "Chưa có thông báo nào"
   - ✅ Unread count = 0

### **Test Case 3: Security - Không xóa được thông báo của user khác**
1. Đăng nhập với User A
2. Ghi nhớ ID của 1 thông báo (giả sử ID = 1)
3. Logout và đăng nhập với User B
4. Thử POST `/user/notifications` với `action=delete&id=1` (thông báo của User A)
5. **Kiểm tra:**
   - ✅ Không xóa được (DAO check `notification.getUser().getId() != currentUser.getId()`)
   - ✅ Thông báo của User A vẫn còn trong DB
   - ✅ User B không thấy thông báo của User A

### **Test Case 4: Không đăng nhập**
1. Logout (hoặc không đăng nhập)
2. Thử POST `/user/notifications` với `action=delete&id=1`
3. **Kiểm tra:**
   - ✅ Redirect về `/login`
   - ✅ Không xóa được thông báo nào

### **Test Case 5: Thông báo đã xóa không hiển thị**
1. User A có thông báo ID = 1 (chưa xóa)
2. Xóa thông báo ID = 1
3. Vào `/user/notifications`
4. **Kiểm tra:**
   - ✅ Thông báo ID = 1 không hiển thị trong list
   - ✅ Query chỉ lấy `isDeleted = false`
   - ✅ Unread count không tính thông báo đã xóa

### **Test Case 6: Tạo thông báo mới sau khi xóa**
1. Xóa 1 vài thông báo
2. Tạo thông báo mới (ví dụ: admin tạo promotion mới)
3. **Kiểm tra:**
   - ✅ Thông báo mới hiển thị bình thường
   - ✅ `isDeleted = false` khi tạo mới
   - ✅ Không bị ảnh hưởng bởi thông báo đã xóa

---

## 📝 MIGRATION INSTRUCTIONS

**Bước 1: Chạy migration SQL**
```sql
-- Chạy script: database/add_isDeleted_to_notification.sql
USE AloTra;
GO
ALTER TABLE [dbo].[Notification]
ADD [isDeleted] [bit] NOT NULL DEFAULT 0;
GO
```

**Bước 2: Restart application**
- Restart Tomcat/server để load entity mới

**Bước 3: Test**
- Kiểm tra thông báo hiển thị bình thường
- Test xóa 1 thông báo
- Test xóa tất cả

---

**Status:** ✅ Hoàn thành  
**Date:** 2026-01-04  
**Total files changed:** 8 files (1 new, 7 modified)

