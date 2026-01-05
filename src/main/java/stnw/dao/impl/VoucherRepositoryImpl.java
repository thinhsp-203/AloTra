package stnw.dao.impl;

import stnw.dao.VoucherRepository;
import jakarta.persistence.EntityManager;
import stnw.model.Voucher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class VoucherRepositoryImpl implements VoucherRepository {
    @Override
    public Optional<Voucher> findActiveByCode(String code, EntityManager em) {
        if (code == null || code.isBlank()) return Optional.empty();
        var now = LocalDateTime.now();
        var list = em.createQuery(
                        "select v from Voucher v where v.isActive = true and v.code = :c " +
                                "and (v.start_date is null or v.start_date <= :now) " +
                                "and (v.end_date   is null or v.end_date   >= :now) " +
                                "and (v.usage_limit is null or v.used_count < v.usage_limit)", Voucher.class)
                .setParameter("c", code)
                .setParameter("now", now)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
    
    @Override
    public List<Voucher> findAvailableVouchers(EntityManager em) {
        var now = LocalDateTime.now();
        return em.createQuery(
                "select v from Voucher v where v.isActive = true " +
                        "and (v.start_date is null or v.start_date <= :now) " +
                        "and (v.end_date is null or v.end_date >= :now) " +
                        "and (v.usage_limit is null or v.used_count < v.usage_limit) " +
                        "order by v.discount_value desc", Voucher.class)
                .setParameter("now", now)
                .getResultList();
    }
}

