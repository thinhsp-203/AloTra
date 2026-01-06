package stnw.dao;

import stnw.model.Banner;
import java.util.List;

public interface BannerDao {
    List<Banner> findAllActive();
    List<Banner> findAll();
    Banner findById(int id);
    void save(Banner banner);
    void update(Banner banner);
    void delete(int id);
    int getMaxSortOrder();
}
