package stnw.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "Reward")
@Getter @Setter
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reward_id;

    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String name;

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String description;

    @Column(nullable = false)
    private Integer points_required; // Số điểm cần để đổi

    @Column(columnDefinition = "NVARCHAR(500)")
    private String image_url;

    private Integer stock; // Số lượng tồn kho (null = không giới hạn)

    private Boolean isActive; // Trạng thái hoạt động

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Transient
    public Date getCreatedDateAsDate() {
        if (this.createdDate == null) return null;
        return Date.from(this.createdDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Transient
    public Date getUpdatedDateAsDate() {
        if (this.updatedDate == null) return null;
        return Date.from(this.updatedDate.atZone(ZoneId.systemDefault()).toInstant());
    }
}

