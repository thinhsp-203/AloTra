package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="Orders")
@Getter @Setter
public class Orders {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer order_id;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="user_id", nullable=false)
  private User user;

  @Column(nullable=false, columnDefinition="NVARCHAR(100)")
  private String fullname;

  @Column(nullable=false, columnDefinition="NVARCHAR(20)")
  private String phone;

  @Column(name="address", nullable=false, columnDefinition="NVARCHAR(500)")
  private String address;

  @Column(columnDefinition="NVARCHAR(500)")
  private String note;

  @Column(nullable=false, precision=18, scale=2)
  private BigDecimal total_amount;

  @Column(columnDefinition="NVARCHAR(50)")
  private String payment_method;
  
  @Column(columnDefinition="NVARCHAR(50)")
  private String payment_status;
  
  @Column(columnDefinition="NVARCHAR(50)")
  private String order_status;
  
  @Column(name="cancellation_reason", columnDefinition="NVARCHAR(1000)")
  private String cancellation_reason;

  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;
  
  @OneToMany(mappedBy="order", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
  private List<OrderDetail> orderDetails = new ArrayList<>();
  
  public void addOrderDetail(OrderDetail detail) {
    orderDetails.add(detail);
    detail.setOrder(this);
  }

  // Methods to convert LocalDateTime to Date for JSP compatibility
  @Transient
  public Date getCreatedDateAsDate() {
    if (this.createdDate == null) return null;
    return Date.from(this.createdDate.atZone(ZoneId.systemDefault()).toInstant());
  }
  
  @Transient
  public Date getUpdatedDateAsDate() {
    if (this.updatedDate == null) return null;
    return Date.from(this.updatedDate.atZone(ZoneId.systemDefault()).toInstant());
  }
}