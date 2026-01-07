package stnw.model;

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

  @Column(nullable=false, length=100, columnDefinition="NVARCHAR(100)")
  private String fullname;

  @Column(nullable=false, length=20, columnDefinition="NVARCHAR(20)")
  private String phone;

  @Column(name="address", nullable=false, length=500, columnDefinition="NVARCHAR(500)")
  private String address;

  @Column(columnDefinition="NVARCHAR(1000)")
  private String note;

  @Column(nullable=false, precision=18, scale=2)
  private BigDecimal total_amount;

  @Column(columnDefinition="NVARCHAR(50)")
  private String payment_method;
  
  @Column(columnDefinition="NVARCHAR(50)")
  private String payment_status;
  
  @Column(columnDefinition="NVARCHAR(50)")
  private String order_status;

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
  
  /**
   * Tính tổng tiền sản phẩm (subtotal) từ order details
   */
  @Transient
  public BigDecimal getSubtotal() {
    if (orderDetails == null || orderDetails.isEmpty()) {
      return BigDecimal.ZERO;
    }
    return orderDetails.stream()
        .map(OrderDetail::getLineTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
  
  /**
   * Ước lượng phí vận chuyển (15,000 hoặc 30,000)
   * Dựa trên diff = total_amount - subtotal
   */
  @Transient
  public BigDecimal getEstimatedShippingFee() {
    BigDecimal subtotal = getSubtotal();
    BigDecimal diff = total_amount.subtract(subtotal);
    
    // Nếu diff >= 30000, có thể là shipping 30000
    if (diff.compareTo(new BigDecimal("30000")) >= 0) {
      return new BigDecimal("30000");
    }
    // Mặc định là shipping 15000
    return new BigDecimal("15000");
  }
  
  /**
   * Tính số tiền giảm giá (nếu có)
   * discount = subtotal + shipping - total_amount
   */
  @Transient
  public BigDecimal getEstimatedDiscount() {
    BigDecimal subtotal = getSubtotal();
    BigDecimal shipping = getEstimatedShippingFee();
    BigDecimal expectedTotal = subtotal.add(shipping);
    BigDecimal discount = expectedTotal.subtract(total_amount);
    
    // Đảm bảo discount không âm
    if (discount.compareTo(BigDecimal.ZERO) < 0) {
      return BigDecimal.ZERO;
    }
    return discount;
  }
}