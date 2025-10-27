package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="OrderDetail")
@Getter @Setter
public class OrderDetail {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer detail_id;

  @ManyToOne
  @JoinColumn(name="order_id", nullable=false)
  private Orders order;

  @ManyToOne
  @JoinColumn(name="product_id", nullable=false)
  private Product product;

  @Column(nullable=false, length=200)
  private String product_name;

  @Column(length=20)
  private String size_name;

  @Column(nullable=false)
  private Integer quantity;

  @Column(nullable=false, precision=18, scale=2)
  private BigDecimal price; // giá đơn vị (1 sp), không nhân quantity

  @Column(length=500)
  private String toppings; // JSON/txt
}
