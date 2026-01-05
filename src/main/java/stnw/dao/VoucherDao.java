package stnw.dao;

import jakarta.persistence.EntityManager;
import stnw.model.Voucher;

import java.util.List;
import java.util.Optional;

public interface VoucherDao {
    Optional<Voucher> findActiveByCode(String code, EntityManager em);
    
    /**
     * Lấy danh sách voucher khả dụng (active, trong thời gian hiệu lực, còn lượt sử dụng)
     */
    List<Voucher> findAvailableVouchers(EntityManager em);
}

