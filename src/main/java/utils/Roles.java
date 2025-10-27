package utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Roles {
    private static final Map<Integer, String> ROLE_NAMES;

    static {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "Quản trị viên");
        map.put(2, "Nhân viên");
        map.put(3, "Khách hàng");
        ROLE_NAMES = Collections.unmodifiableMap(map);
    }

    private Roles() {
    }

    public static Map<Integer, String> names() {
        return ROLE_NAMES;
    }

    public static String resolve(int roleId) {
        return ROLE_NAMES.getOrDefault(roleId, "Không xác định");
    }
}
