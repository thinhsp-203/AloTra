package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="Orders")
@Getter @Setter
public class Orders {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer order_id;

  @ManyToOne
  @JoinColumn(name="user_id", nullable=false)
  private User user;

  @Column(nullable=false, length=100)
  private String fullname;

  @Column(nullable=false, length=20)
  private String phone;

  @Column(name="address", nullable=false, length=500)
  private String address;

  private String note;

  @Column(nullable=false, precision=18, scale=2)
  private BigDecimal total_amount;

  private String payment_method;
  private String payment_status;
  private String order_status;

  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;
}
