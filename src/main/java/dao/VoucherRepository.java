package dao;

import jakarta.persistence.EntityManager;
import model.Voucher;

import java.util.Optional;

public interface VoucherRepository {
    Optional<Voucher> findActiveByCode(String code, EntityManager em);
}