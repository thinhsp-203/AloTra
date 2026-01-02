package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name="PaymentConfig")
@Getter @Setter
public class PaymentConfig {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer config_id;

  @Column(nullable=false, unique=true, columnDefinition="NVARCHAR(50)")
  private String payment_method; // VNPAY, MOMO, COD, etc.

  @Column(nullable=false)
  private Boolean isActive;

  @Column(columnDefinition="NVARCHAR(200)")
  private String display_name;

  @Column(columnDefinition="NVARCHAR(MAX)")
  private String api_endpoint;

  @Column(columnDefinition="NVARCHAR(500)")
  private String merchant_id;

  @Column(columnDefinition="NVARCHAR(500)")
  private String secret_key;

  @Column(columnDefinition="NVARCHAR(500)")
  private String access_key;

  @Column(columnDefinition="NVARCHAR(MAX)")
  private String config_json; // JSON for additional configs

  private Integer display_order;

  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;
}