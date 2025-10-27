package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="Voucher")
@Getter @Setter
public class Voucher {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer voucher_id;

  @Column(nullable=false, length=50, unique=true)
  private String code;

  @Column(name="description", length=500)
  private String description;

  private String discount_type; // Percent | Fixed

  @Column(nullable=false, precision=18, scale=2)
  private BigDecimal discount_value;

  @Column(precision=18, scale=2)
  private BigDecimal min_order_value;

  @Column(precision=18, scale=2)
  private BigDecimal max_discount;

  private Integer usage_limit;
  private Integer used_count;

  private LocalDateTime start_date;
  private LocalDateTime end_date;

  private Boolean isActive;
}
