package model;

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

  @Column(nullable=false, columnDefinition="NVARCHAR(200)")
  private String supplier_name;

  @Column(columnDefinition="NVARCHAR(100)")
  private String contact_name;
  
  @Column(columnDefinition="NVARCHAR(20)")
  private String phone;
  
  @Column(columnDefinition="NVARCHAR(100)")
  private String email;

  @Column(name="address", columnDefinition="NVARCHAR(500)")
  private String address;

  private Boolean isActive;
}
