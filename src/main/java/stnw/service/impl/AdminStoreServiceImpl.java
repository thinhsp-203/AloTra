package stnw.service.impl;

import stnw.config.JpaUtil;
import jakarta.persistence.EntityManager;
import stnw.model.Store;
import stnw.service.AdminStoreService;

import java.util.List;

public class AdminStoreServiceImpl implements AdminStoreService {
    
    @Override
    public List<Store> getAllStores() {
        EntityManager em = JpaUtil.em();
        try {
            return em.createQuery("SELECT s FROM Store s ORDER BY s.store_name", Store.class).getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public Store getStoreById(int id) {
        EntityManager em = JpaUtil.em();
        try {
            return em.find(Store.class, id);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void saveStore(Store store) {
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            if (store.getStore_id() == null) {
                em.persist(store);
            } else {
                em.merge(store);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi lưu cửa hàng: " + e.getMessage(), e);
        } finally {
            em.close();
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
        EntityManager em = JpaUtil.em();
        try {
            em.getTransaction().begin();
            
            Store store = em.find(Store.class, id);
            if (store != null) {
                em.remove(store);
            }
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Lỗi khi xóa cửa hàng: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}

