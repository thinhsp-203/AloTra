package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "AboutUs")
@Getter
@Setter
public class AboutUs {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "title", nullable = false, length = 500)
    private String title;
    
    @Column(name = "content", columnDefinition = "NVARCHAR(MAX)")
    private String content;
    
    @Column(name = "image", length = 500)
    private String image;
    
    @Column(name = "isActive")
    private Boolean isActive;
    
    @Column(name = "sortOrder")
    private Integer sortOrder;
    
    @Column(name = "createdDate")
    private LocalDateTime createdDate;
    
    @Column(name = "updatedDate")
    private LocalDateTime updatedDate;
}

