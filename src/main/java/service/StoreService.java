package service;

import model.Store;
import java.util.List;

public interface StoreService {
    List<Store> getAllStores();
    List<Store> searchStores(String keyword);
    Store getStoreById(Integer id);
    void saveStore(Store store);
    void deleteStore(Integer id);
}

