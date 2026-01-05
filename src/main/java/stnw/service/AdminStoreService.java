package stnw.service;

import java.util.List;
import stnw.model.Store;

public interface AdminStoreService {
    List<Store> getAllStores();
    Store getStoreById(int id);
    void saveStore(Store store);
    
    /**
     * Lưu cửa hàng từ parameters (tạo mới hoặc cập nhật)
     * Controller chỉ truyền parameters, Service tự tạo Entity
     */
    void saveStoreFromParams(Integer storeId, String storeName, String address, String phone, 
                            String email, String ward, String province, String mapIframe, 
                            String openingHours, Boolean isActive);
    
    void deleteStore(int id);
}

