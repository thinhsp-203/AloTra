package service;

import java.util.List;
import model.Store;

public interface AdminStoreService {
    List<Store> getAllStores();
    Store getStoreById(int id);
    void saveStore(Store store);
    void deleteStore(int id);
}

