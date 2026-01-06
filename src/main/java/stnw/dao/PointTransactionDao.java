package stnw.dao;

import stnw.model.PointTransaction;
import java.util.List;

public interface PointTransactionDao {
    void save(PointTransaction transaction);
    PointTransaction findById(Integer id);
    List<PointTransaction> findByUserId(Integer userId);
    List<PointTransaction> findByUserIdOrderByDateDesc(Integer userId);
    void deleteByUserId(Integer userId);
}

