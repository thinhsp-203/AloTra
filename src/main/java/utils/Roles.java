package utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Roles {
    
    // --- 1. CÁC HẰNG SỐ BẮT BUỘC (Để các class khác gọi) ---
    public static final int ADMIN = 1;
    public static final int STAFF = 2;
    public static final int CUSTOMER = 3;

    // --- 2. MAPPING TÊN (Để hiển thị ra giao diện nếu cần) ---
    private static final Map<Integer, String> ROLE_NAMES;
    static {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(ADMIN, "Quản trị viên");
        map.put(STAFF, "Nhân viên");
        map.put(CUSTOMER, "Khách hàng");
        ROLE_NAMES = Collections.unmodifiableMap(map);
    }

    private Roles() {} // Chặn khởi tạo class

    public static Map<Integer, String> names() {
        return ROLE_NAMES;
    }

    public static String resolve(int roleId) {
        return ROLE_NAMES.getOrDefault(roleId, "Không xác định");
    }
}