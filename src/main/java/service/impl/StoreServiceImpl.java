package service.impl;

import dao.StoreDao;
import dao.impl.StoreDaoImpl;
import model.Store;
import service.StoreService;

import java.util.List;

public class StoreServiceImpl implements StoreService {
    private final StoreDao storeDao = new StoreDaoImpl();

    @Override
    public List<Store> getAllStores() {
        return storeDao.findAll();
    }

    @Override
    public List<Store> searchStores(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return storeDao.findAll();
        }
        return storeDao.searchByAddress(keyword.trim());
    }

    @Override
    public Store getStoreById(Integer id) {
        return storeDao.findById(id);
    }

    @Override
    public void saveStore(Store store) {
        jakarta.persistence.EntityManager em = config.JpaUtil.em();
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
    public void deleteStore(Integer id) {
        jakarta.persistence.EntityManager em = config.JpaUtil.em();
        try {
            em.getTransaction().begin();
            Store store = em.find(Store.class, id);
            if (store != null) {
                store.setIsActive(false); // Soft delete
                em.merge(store);
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

