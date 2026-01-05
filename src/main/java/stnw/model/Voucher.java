package stnw.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId; // THÊM IMPORT NÀY
import java.util.Date;   // THÊM IMPORT NÀY

@Entity
@Table(name="Voucher")
@Getter @Setter
public class Voucher {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer voucher_id; // Tên cũ là "id", model của bạn là "voucher_id"

  @Column(nullable=false, length=50, unique=true, columnDefinition="NVARCHAR(50)")
  private String code;

  @Column(name="description", length=500, columnDefinition="NVARCHAR(500)")
  private String description;

  @Column(columnDefinition="NVARCHAR(20)")
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
  
  // --- CÁC PHƯƠNG THỨC TRANSIENT ĐƯỢC THÊM VÀO ---
  
  @Transient
  public Date getStart_dateAsDate() {
    if (this.start_date == null) return null;
    return Date.from(this.start_date.atZone(ZoneId.systemDefault()).toInstant());
  }
  
  @Transient
  public Date getEnd_dateAsDate() {
    if (this.end_date == null) return null;
    return Date.from(this.end_date.atZone(ZoneId.systemDefault()).toInstant());
  }
  
  @Transient
  public String getStart_dateAsLocalDateTimeString() {
    if (this.start_date == null) return "";
    return this.start_date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
  }
  
  @Transient
  public String getEnd_dateAsLocalDateTimeString() {
    if (this.end_date == null) return "";
    return this.end_date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
  }
}