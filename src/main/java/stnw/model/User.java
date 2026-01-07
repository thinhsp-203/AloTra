package stnw.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import stnw.enums.Roles;
import java.time.LocalDateTime;
import java.time.ZoneId; // Import ZoneId
import java.util.Date;

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

  @Column(columnDefinition="NVARCHAR(100)")
  private String fullname;
  
  @Column(columnDefinition="NVARCHAR(500)")
  private String avatar;
  
  private Integer roleid;

  @Column(length=20, unique=true, columnDefinition="NVARCHAR(20)")
  private String phone;

  private LocalDateTime createdDate;

  @Column(name="address", length=500, columnDefinition="NVARCHAR(500)")
  private String address;

  private String resetToken;
  private LocalDateTime tokenExpiry;
  
  @Column(name = "code")
  private String code;

  @Column(name = "is_active")
  private Boolean isActive;
  
  @Column(name = "loyalty_points")
  private Integer loyalty_points; // Điểm tích lũy của user

  public String getCode() {
      return code;
  }

  public void setCode(String code) {
      this.code = code;
  }

  public Boolean getIsActive() {
      return isActive;
  }

  public void setIsActive(Boolean isActive) {
      this.isActive = isActive;
  }
  
  // ⬇️ TRANSIENT METHODS (không lưu DB, chỉ dùng trong JSP)
  @Transient
  public String getRoleName() {
    return Roles.resolve(this.roleid != null ? this.roleid : 0);
  }
  
  /**
   * CHUẨN HÓA TÊN PHƯƠNG THỨC:
   * Đổi tên từ getCreatedDate() -> getCreatedDateAsDate()
   * để nhất quán với model Orders.java và JSTL
   */
  @Transient
  public Date getCreatedDateAsDate() { 
    if (this.createdDate == null) return null;
    return Date.from(this.createdDate
        .atZone(ZoneId.systemDefault())
        .toInstant());
  }

}