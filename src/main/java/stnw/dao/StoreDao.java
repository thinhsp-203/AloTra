package stnw.dao;

import stnw.model.Store;
import java.util.List;

public interface StoreDao {
    List<Store> findAll();
    List<Store> searchByAddress(String keyword);
    Store findById(Integer id);
    List<Store> findByCity(String city);
    List<Store> findByDistrict(String district);
    void save(Store store);
    void update(Store store);
    void delete(Integer id);
}

