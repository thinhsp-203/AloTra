package stnw.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "PointTransaction")
@Getter @Setter
public class PointTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transaction_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer points; // Số điểm (dương = cộng, âm = trừ)

    @Column(nullable = false, columnDefinition = "NVARCHAR(50)")
    private String type; // "EARN" (tích điểm), "REDEEM" (đổi quà), "EXPIRED" (hết hạn)

    @Column(columnDefinition = "NVARCHAR(500)")
    private String description; // Mô tả chi tiết

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order; // Liên kết với đơn hàng (nếu là tích điểm từ đơn hàng)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id")
    private Reward reward; // Liên kết với quà tặng (nếu là đổi quà)

    @Column(nullable = false)
    private Integer balance_after; // Số điểm còn lại sau giao dịch

    private LocalDateTime createdDate;

    @Transient
    public Date getCreatedDateAsDate() {
        if (this.createdDate == null) return null;
        return Date.from(this.createdDate.atZone(ZoneId.systemDefault()).toInstant());
    }
}

