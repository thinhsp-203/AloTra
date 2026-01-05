package stnw.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "Notification")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 500, columnDefinition = "NVARCHAR(500)")
    private String message;
    
    @Column(length = 500, columnDefinition = "NVARCHAR(500)")
    private String link;
    
    @Column(nullable = false)
    private Boolean isRead = false;
    
    @Column(nullable = true)
    private Boolean isDeleted = false;
    
    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted != null && isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted != null ? isDeleted : false;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    // Method to convert LocalDateTime to Date for JSP compatibility
    @Transient
    public Date getCreatedDateAsDate() {
        if (this.createdDate == null) return null;
        return Date.from(this.createdDate.atZone(ZoneId.systemDefault()).toInstant());
    }
}

