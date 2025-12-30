package dao;

import model.Reward;
import java.util.List;

public interface RewardDao {
    List<Reward> findAll();
    List<Reward> findAllActive();
    Reward findById(Integer id);
    void save(Reward reward);
    void update(Reward reward);
    void delete(Integer id);
    long count();
}

