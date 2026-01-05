package stnw.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Store")
@Getter
@Setter
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer store_id;

    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String store_name;

    @Column(columnDefinition = "NVARCHAR(500)")
    private String address;

    @Column(columnDefinition = "NVARCHAR(20)")
    private String phone;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String email;

    @Column(columnDefinition = "NVARCHAR(200)")
    private String ward; // Xã/Phường

    @Column(columnDefinition = "NVARCHAR(200)")
    private String province; // Tỉnh/Thành phố

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String mapIframe; // Iframe embed code từ Google Maps

    @Column(columnDefinition = "NVARCHAR(50)")
    private String opening_hours;

    private Boolean isActive;
}

