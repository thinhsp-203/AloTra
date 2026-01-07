package stnw.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name="Product")
@Getter @Setter
public class Product {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer product_id;

  @Column(nullable=false, length=200, columnDefinition="NVARCHAR(200)")
  private String product_name;

  @Column(name="description", columnDefinition="NVARCHAR(MAX)")
  private String description;

  @Column(nullable=false, precision=18, scale=2)
  private BigDecimal price;

  @Column(precision=5, scale=2)
  private BigDecimal discount;

  @Column(columnDefinition="NVARCHAR(500)")
  private String thumbnail;

  @Column(columnDefinition="NVARCHAR(MAX)")
  private String images;

  @ManyToOne 
  @JoinColumn(name="cate_id", referencedColumnName="cate_id") // Sửa tại đây
  private Category category;

  private Integer views;

  @Column(precision=3, scale=2)
  private BigDecimal rating;

  private Integer sold;
  private Boolean isActive;
  private Boolean isFeatured;

  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;
  
  /**
   * Tính giá sau khi giảm giá (nếu có)
   * @return Giá sau giảm, hoặc giá gốc nếu không có giảm giá
   */
  @Transient
  public BigDecimal getFinalPrice() {
    if (price == null) {
      return BigDecimal.ZERO;
    }
    if (discount == null || discount.compareTo(BigDecimal.ZERO) <= 0) {
      return price;
    }
    // Giá sau giảm = giá gốc * (1 - discount/100)
    BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
      discount.divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP)
    );
    return price.multiply(discountMultiplier).setScale(0, java.math.RoundingMode.HALF_UP);
  }
  
  /**
   * Kiểm tra xem sản phẩm có đang giảm giá không
   * @return true nếu có discount > 0
   */
  @Transient
  public boolean hasDiscount() {
    return discount != null && discount.compareTo(BigDecimal.ZERO) > 0;
  }
}
