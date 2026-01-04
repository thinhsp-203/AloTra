# 🔧 BUILD ERROR FIX: ClassNotFoundException

## ❌ LỖI

```
java.lang.ClassNotFoundException: service.impl.WishlistServiceImpl
java.lang.ClassNotFoundException: service.impl.UserServiceImpl
java.lang.ClassNotFoundException: model.Settings
```

**Nguyên nhân:**
- File Java tồn tại trong source code
- Nhưng class chưa được compile vào `target/classes`
- Runtime không tìm thấy class → ClassNotFoundException

## ✅ GIẢI PHÁP

### **Cách 1: Rebuild bằng Maven (Khuyến nghị)**

```bash
# Clean và compile lại
mvn clean compile

# Hoặc clean, compile và package
mvn clean package

# Hoặc nếu dùng Maven wrapper
./mvnw clean compile
```

### **Cách 2: Rebuild bằng IDE**

**Eclipse/IntelliJ:**
1. **Clean Project:**
   - Eclipse: `Project → Clean → Clean all projects`
   - IntelliJ: `Build → Rebuild Project`

2. **Build lại:**
   - Eclipse: `Project → Build All` (hoặc tự động build nếu enabled)
   - IntelliJ: `Build → Build Project`

3. **Restart IDE** (nếu cần)

### **Cách 3: Kiểm tra Build Output**

1. Kiểm tra thư mục `target/classes/service/impl/`:
   - Có file `WishlistServiceImpl.class` không?
   - Có file `UserServiceImpl.class` không?

2. Nếu không có:
   - Clean project
   - Build lại
   - Kiểm tra compile errors trong console

### **Cách 4: Restart Tomcat/Server**

Sau khi rebuild:
1. Stop Tomcat/server
2. Clean deploy directory (nếu có)
3. Start lại server

---

## 📋 KIỂM TRA

### **Files tồn tại:**
- ✅ `src/main/java/service/impl/WishlistServiceImpl.java` - TỒN TẠI
- ✅ `src/main/java/service/impl/UserServiceImpl.java` - TỒN TẠI
- ✅ `src/main/java/model/Settings.java` - TỒN TẠI (sử dụng Lombok)
- ✅ Không có lỗi linter/syntax

### **Cần kiểm tra:**
- ❓ Class files trong `target/classes/service/impl/`
- ❓ Compile errors trong build log
- ❓ IDE build settings

---

## 🔍 NGUYÊN NHÂN THƯỜNG GẶP

1. **Clean build** - Xóa `target/` directory nhưng chưa rebuild
2. **Incremental build failed** - Build một phần bị lỗi, một số class chưa compile
3. **IDE sync issue** - IDE chưa sync với file system
4. **Classpath issue** - Build output directory không đúng
5. **Lombok annotation processing** - Settings.java sử dụng Lombok, cần annotation processor chạy đúng

---

## ⚠️ LƯU Ý

- Đây KHÔNG phải lỗi code logic
- File Java tồn tại và đúng cú pháp
- Vấn đề là BUILD/COMPILE
- Chỉ cần rebuild là OK

---

**Status:** 🔧 Cần rebuild project  
**Solution:** `mvn clean compile` hoặc IDE Clean + Rebuild

