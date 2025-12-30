package model;

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

    @Column(nullable = false, length = 200)
    private String store_name;

    @Column(length = 500)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String district;

    @Column(length = 200)
    private String city;

    @Column(length = 200)
    private String province;

    private Double latitude;
    private Double longitude;

    @Column(length = 50)
    private String opening_hours;

    private Boolean isActive;
}

