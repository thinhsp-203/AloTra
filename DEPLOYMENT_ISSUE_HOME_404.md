# VẤN ĐỀ: 404 Error khi truy cập /AloTra/home

## 🔍 PHÂN TÍCH

### Code đúng ✅
- **HomeController.java**: Có `@WebServlet(urlPatterns = {"/home", "/trang-chu", ""})`
- **Package**: `stnw.controller.home` - ĐÚNG
- **JSP Path**: `/views/home/home.jsp` - ĐÚNG (file tồn tại)
- **Service**: `CatalogService`, `CatalogServiceImpl` - ĐÚNG
- **Linter Errors**: 0 lỗi

### Nguyên nhân có thể

Lỗi 404 `/AloTra/home` thường do:

1. **Servlet chưa được compile/deploy**
   - Class file chưa được tạo trong `WEB-INF/classes/`
   - Servlet container chưa load được servlet

2. **Server chưa được restart sau khi refactor**
   - Servlet container cache cũ
   - Annotation scanning chưa chạy lại

3. **Build/Deployment issue**
   - Maven build chưa thành công
   - WAR file chưa được update
   - Classpath issue

---

## ✅ GIẢI PHÁP

### Bước 1: Clean & Rebuild Project

```bash
# Maven clean
mvn clean

# Maven compile
mvn compile

# Maven package (tạo WAR)
mvn package
```

### Bước 2: Restart Application Server

- **Tomcat**: Restart server hoàn toàn
- **IDE Embedded Server**: Stop và Start lại
- **Docker**: Restart container

### Bước 3: Kiểm tra Deployment

1. Kiểm tra file `HomeController.class` có trong `WEB-INF/classes/stnw/controller/home/`
2. Kiểm tra server logs có lỗi khi load servlet không
3. Kiểm tra `@WebServlet` annotation có được scan không

### Bước 4: Kiểm tra URL Pattern

Thử các URL sau:
- `http://localhost:8080/AloTra/` (empty pattern)
- `http://localhost:8080/AloTra/home`
- `http://localhost:8080/AloTra/trang-chu`

### Bước 5: Kiểm tra Server Logs

Xem server logs để tìm:
- Servlet initialization errors
- ClassNotFoundException
- NoClassDefFoundError
- Annotation scanning errors

---

## 🔧 KIỂM TRA NHANH

### 1. Kiểm tra file .class có tồn tại không

```powershell
# Windows PowerShell
Get-ChildItem -Path "target\AloTra\WEB-INF\classes\stnw\controller\home" -Filter "*.class" -Recurse
```

### 2. Kiểm tra WAR file

```powershell
# Xem nội dung WAR
Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::OpenRead("target\AloTra.war").Entries | Where-Object { $_.FullName -like "*HomeController*" }
```

### 3. Test trực tiếp JSP

Truy cập: `http://localhost:8080/AloTra/views/home/home.jsp`
- Nếu JSP load được → Vấn đề ở servlet mapping
- Nếu JSP không load → Vấn đề ở deployment path

---

## 📋 CHECKLIST DEBUG

- [ ] Đã clean project (`mvn clean`)
- [ ] Đã rebuild project (`mvn compile`)
- [ ] Đã package WAR (`mvn package`)
- [ ] Đã restart server
- [ ] Đã kiểm tra `HomeController.class` tồn tại
- [ ] Đã kiểm tra server logs
- [ ] Đã kiểm tra context path (`/AloTra`)
- [ ] Đã thử URL `/` (empty pattern)
- [ ] Đã thử URL `/home`
- [ ] Đã thử URL `/trang-chu`

---

## ⚠️ LƯU Ý

Nếu vẫn lỗi sau khi rebuild và restart:

1. **Kiểm tra Servlet Container Version**
   - Jakarta EE 9+ yêu cầu `jakarta.servlet.*` (không phải `javax.servlet.*`)
   - Code đã dùng `jakarta.servlet.*` → ĐÚNG

2. **Kiểm tra web.xml**
   - `metadata-complete="false"` để cho phép annotation scanning
   - Hoặc thêm servlet mapping thủ công trong web.xml

3. **Kiểm tra ClassLoader**
   - Servlet có thể không được load do classloader issue
   - Kiểm tra dependencies trong `pom.xml`

---

## 🎯 KẾT LUẬN

**Code hoàn toàn đúng**, vấn đề là **deployment/runtime issue**.

**Giải pháp:** Clean → Rebuild → Restart Server

---

**Tài liệu này được tạo bởi AI Assistant**  
**Ngày:** 2025-01-27

