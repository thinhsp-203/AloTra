package stnw.service.impl;

import stnw.dao.StoreDao;
import stnw.dao.impl.StoreDaoImpl;
import stnw.model.Store;
import stnw.service.StoreService;

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
        storeDao.save(store);
    }

    @Override
    public void deleteStore(Integer id) {
        storeDao.delete(id);
    }
}

