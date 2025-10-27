package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "[User]")
@Getter @Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable=false, length=100, unique=true)
  private String email;

  @Column(nullable=false, length=50, unique=true)
  private String username;

  @Column(nullable=false, length=100, name="password")
  private String password;

  private String fullname;
  private String avatar;
  private Integer roleid;

  @Column(length=20, unique=true)
  private String phone;

  private LocalDateTime createdDate;
  private Boolean isActive;

  @Column(name="address", length=500)
  private String address;

  private String resetToken;
  private LocalDateTime tokenExpiry;
}
