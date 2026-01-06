package stnw.dao;

import stnw.model.Voucher;
import java.util.List;
import java.util.Optional;

public interface VoucherDao {
    Optional<Voucher> findActiveByCode(String code);
    List<Voucher> findAvailableVouchers();
    Voucher findById(int id);
    void save(Voucher voucher);
    void update(Voucher voucher);
    void delete(int id);
    List<Voucher> findAll();
}
