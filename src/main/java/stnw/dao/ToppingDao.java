package stnw.dao;

import stnw.model.Topping;
import java.util.List;

public interface ToppingDao {
    List<Topping> findAll();
    List<Topping> findByIds(List<Integer> ids);
    Topping findById(int id);
    void save(Topping topping);
    void update(Topping topping);
    void delete(int id);
}

