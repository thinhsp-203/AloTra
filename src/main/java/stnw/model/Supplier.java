package stnw.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="Supplier")
@Getter @Setter
public class Supplier {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer supplier_id;

  @Column(nullable=false, length=200)
  private String supplier_name;

  private String contact_name;
  private String phone;
  private String email;

  @Column(name="address", length=500)
  private String address;

  private Boolean isActive;
}
