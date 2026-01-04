# 🔧 FIX: Migration Error for isDeleted Column

## ❌ Vấn đề

Hibernate đang tự động tạo schema và cố gắng thêm cột `isDeleted NOT NULL` vào bảng `Notification` đã có dữ liệu, nhưng SQL Server không cho phép điều này nếu không có DEFAULT value.

**Error:**
```
ALTER TABLE only allows columns to be added that can contain nulls, or have a DEFAULT definition specified...
Column 'isDeleted' cannot be added to non-empty table 'Notification'
```

## ✅ Giải pháp

### **Option 1: Chạy Migration Script Thủ Công (KHUYẾN NGHỊ)**

1. **Chạy script migration TRƯỚC KHI start application:**
   ```sql
   -- File: database/add_isDeleted_to_notification.sql
   ```

2. **Script đã được cập nhật để:**
   - Thêm column as NULLABLE trước
   - Update existing records = 0
   - Add DEFAULT constraint
   - Set NOT NULL (an toàn vì đã có default và data)

3. **Sau khi chạy script, restart application**

### **Option 2: Tắt Auto DDL (Nếu không cần)**

Nếu bạn không muốn Hibernate tự động update schema, tìm file `persistence.xml` hoặc `application.properties` và set:
```properties
hibernate.hbm2ddl.auto=none
# hoặc
hibernate.hbm2ddl.auto=validate
```

### **Option 3: Entity Nullable Tạm Thời (Đã áp dụng)**

Entity đã được sửa để column `nullable = true` tạm thời. Sau khi chạy migration script, bạn có thể:
1. Update script để set NOT NULL
2. Hoặc giữ nguyên nullable = true (vẫn hoạt động bình thường với default value)

---

## 📝 HƯỚNG DẪN CHẠY MIGRATION

### **Bước 1: Backup Database (Nếu cần)**
```sql
BACKUP DATABASE AloTra TO DISK = 'C:\backup\AloTra_backup.bak';
```

### **Bước 2: Chạy Migration Script**
1. Mở SQL Server Management Studio (SSMS)
2. Connect đến database AloTra
3. Mở file `database/add_isDeleted_to_notification.sql`
4. Execute script

Hoặc dùng command line:
```bash
sqlcmd -S localhost -d AloTra -i database/add_isDeleted_to_notification.sql
```

### **Bước 3: Verify**
```sql
-- Kiểm tra column đã được thêm
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'Notification' AND COLUMN_NAME = 'isDeleted';

-- Kiểm tra data
SELECT id, isDeleted, COUNT(*) 
FROM Notification 
GROUP BY id, isDeleted;
```

### **Bước 4: Restart Application**
- Restart Tomcat/server
- Kiểm tra log không còn lỗi DDL

---

## 🔄 NẾU ĐÃ CHẠY SAI VÀ CỘT ĐÃ TỒN TẠI

Nếu cột đã được tạo sai (nullable nhưng cần NOT NULL), chạy script cleanup:

```sql
-- Cleanup script (chỉ chạy nếu cột đã tồn tại nhưng sai cấu hình)
USE AloTra;
GO

-- Nếu cột tồn tại nhưng nullable
IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[Notification]') AND name = 'isDeleted')
BEGIN
    -- Update NULL values
    UPDATE [dbo].[Notification] SET [isDeleted] = 0 WHERE [isDeleted] IS NULL;
    
    -- Drop và recreate với đúng config (nếu cần)
    ALTER TABLE [dbo].[Notification] DROP CONSTRAINT [DF_Notification_isDeleted];
    ALTER TABLE [dbo].[Notification] ALTER COLUMN [isDeleted] [bit] NOT NULL;
    ALTER TABLE [dbo].[Notification] ADD CONSTRAINT [DF_Notification_isDeleted] DEFAULT 0 FOR [isDeleted];
    
    PRINT 'Column isDeleted fixed.';
END
GO
```

---

**Status:** ✅ Fixed  
**Date:** 2026-01-04

