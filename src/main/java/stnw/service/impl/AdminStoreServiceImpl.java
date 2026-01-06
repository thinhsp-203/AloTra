package stnw.service.impl;

import stnw.dao.StoreDao;
import stnw.dao.impl.StoreDaoImpl;
import stnw.model.Store;
import stnw.service.AdminStoreService;

import java.util.List;

public class AdminStoreServiceImpl implements AdminStoreService {
    
    private final StoreDao storeDao = new StoreDaoImpl();
    
    @Override
    public List<Store> getAllStores() {
        return storeDao.findAll();
    }
    
    @Override
    public Store getStoreById(int id) {
        return storeDao.findById(id);
    }
    
    @Override
    public void saveStore(Store store) {
        if (store.getStore_id() == null) {
            storeDao.save(store);
        } else {
            storeDao.update(store);
        }
    }
    
    @Override
    public void saveStoreFromParams(Integer storeId, String storeName, String address, String phone, 
                                    String email, String ward, String province, String mapIframe, 
                                    String openingHours, Boolean isActive) {
        // Tạo Entity từ parameters
        Store store;
        if (storeId != null) {
            store = getStoreById(storeId);
            if (store == null) {
                throw new IllegalArgumentException("Cửa hàng không tồn tại!");
            }
        } else {
            store = new Store();
        }
        
        // Set fields
        store.setStore_name(storeName);
        store.setAddress(address);
        store.setPhone(phone);
        store.setEmail(email);
        store.setWard(ward);
        store.setProvince(province);
        store.setMapIframe(mapIframe);
        store.setOpening_hours(openingHours);
        store.setIsActive(isActive);
        
        // Gọi method saveStore hiện có
        saveStore(store);
    }
    
    @Override
    public void deleteStore(int id) {
        storeDao.delete(id);
    }
}

