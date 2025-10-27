package dao.jpa;
import jakarta.persistence.EntityManager;
import model.Voucher;
import java.time.LocalDateTime;
import java.util.Optional;

public class VoucherRepository {
  private final EntityManager em;
  public VoucherRepository(EntityManager em){ this.em = em; }

  public Optional<Voucher> findActiveByCode(String code){
    if(code==null || code.isBlank()) return Optional.empty();
    var now = LocalDateTime.now(); // DB đang lưu SYSUTCDATETIME(), lệch TZ không ảnh hưởng logic lớn
    var list = em.createQuery(
      "select v from Voucher v where v.isActive = true and v.code = :c " +
      "and (v.start_date is null or v.start_date <= :now) " +
      "and (v.end_date   is null or v.end_date   >= :now) " +
      "and (v.usage_limit is null or v.used_count < v.usage_limit)", Voucher.class)
      .setParameter("c", code).setParameter("now", now).setMaxResults(1).getResultList();
    return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
  }
}
