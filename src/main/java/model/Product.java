package model;

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

  @Column(nullable=false, length=200)
  private String product_name;

  @Column(name="description", columnDefinition="NVARCHAR(MAX)")
  private String description;

  @Column(nullable=false, precision=18, scale=2)
  private BigDecimal price;

  @Column(precision=5, scale=2)
  private BigDecimal discount;

  private Integer stock;
  private String thumbnail;

  @Column(columnDefinition="NVARCHAR(MAX)")
  private String images;

  @ManyToOne 
  @JoinColumn(name="cate_id", referencedColumnName="id")
  private Category category;

  @ManyToOne @JoinColumn(name="supplier_id")
  private Supplier supplier;

  private Integer views;

  @Column(precision=3, scale=2)
  private BigDecimal rating;

  private Integer sold;
  private Boolean isActive;
  private Boolean isFeatured;

  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;
}
