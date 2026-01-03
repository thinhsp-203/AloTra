package dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Integer id;
    private String message;
    private String link;
    private Boolean isRead;
    private LocalDateTime createdDate;
    
    public NotificationDTO() {
    }
    
    public NotificationDTO(Integer id, String message, String link, Boolean isRead, LocalDateTime createdDate) {
        this.id = id;
        this.message = message;
        this.link = link;
        this.isRead = isRead;
        this.createdDate = createdDate;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}


