package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Settings")
@Getter @Setter
public class Settings {
    @Id
    @Column(name = "setting_key", columnDefinition = "NVARCHAR(50)")
    private String key; // Ví dụ: "LOGO_URL", "BANNER_URL"

    @Column(name = "setting_value", columnDefinition = "NVARCHAR(MAX)")
    private String value;
}