package dao;

import model.PointTransaction;
import java.util.List;

public interface PointTransactionDao {
    void save(PointTransaction transaction);
    List<PointTransaction> findByUserId(Integer userId);
    List<PointTransaction> findByUserIdOrderByDateDesc(Integer userId);
}

