package dao;

import model.AboutUs;
import java.util.List;

public interface AboutUsDao {
    List<AboutUs> findAll();
    AboutUs findById(Integer id);
    void save(AboutUs aboutUs);
    void update(AboutUs aboutUs);
    void delete(Integer id);
    List<AboutUs> findAllActiveOrderBySortOrder();
}

