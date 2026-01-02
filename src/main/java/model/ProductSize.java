package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="ProductSize")
@Getter @Setter
public class ProductSize {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer size_id;

  @ManyToOne
  @JoinColumn(name="product_id", nullable=false)
  private Product product;

  @Column(nullable=false, columnDefinition="NVARCHAR(20)")
  private String size_name; // S/M/L

  @Column(precision=18, scale=2)
  private BigDecimal price_adjustment;
}
