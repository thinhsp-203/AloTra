package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.ZoneId;

@Entity
@Table(name = "Review")
@Getter @Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer rating; // Rating from 1 to 5

    @Column(columnDefinition = "NVARCHAR(1000)")
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdDate;
    
    // Admin có thể duyệt review (chúng ta sẽ tự động duyệt = true)
    private Boolean isApproved = true; 
    
    /**
     * Phương thức Transient để JSTL (JSP) có thể hiển thị ngày tháng
     */
    @Transient
    public Date getCreatedDateAsDate() { 
        if (this.createdDate == null) return null;
        return Date.from(this.createdDate
            .atZone(ZoneId.systemDefault())
            .toInstant());
    }
}