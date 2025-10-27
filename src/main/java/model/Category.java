package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Category")
@Getter @Setter
public class Category {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer cate_id;

  @Column(nullable=false, length=100)
  private String cate_name;

  private String icons;

  @Column(name="description", length=500)
  private String description;

  private Boolean isActive;
  private Integer displayOrder;
}
