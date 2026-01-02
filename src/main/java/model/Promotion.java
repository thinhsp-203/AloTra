package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
@Table(name = "Promotions")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "NVARCHAR(200)")
    private String title; // Tiêu đề khuyến mãi

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description; // Mô tả ngắn

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content; // Nội dung chi tiết

    @Column(nullable = false, columnDefinition = "NVARCHAR(500)")
    private String imageUrl; // Đường dẫn tới ảnh khuyến mãi

    private boolean isActive = true;

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updatedDate = LocalDateTime.now();

    // Getters and Setters
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    // --- CÁC PHƯƠNG THỨC TRANSIENT ĐỂ CONVERT LocalDateTime SANG Date CHO JSP ---
    
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

