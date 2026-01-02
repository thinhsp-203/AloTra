package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="Topping")
@Getter @Setter
public class Topping {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer topping_id;

  @Column(nullable=false, columnDefinition="NVARCHAR(100)")
  private String topping_name;

  @Column(nullable=false, precision=18, scale=2)
  private BigDecimal price;

  private Boolean isAvailable;
}
