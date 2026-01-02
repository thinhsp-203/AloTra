package model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cate_id")
    private int id;
    
    @Column(name = "cate_name", columnDefinition="NVARCHAR(200)")
    private String name;
    
    @Column(name = "icons", columnDefinition="NVARCHAR(500)")
    private String icon;
    
    // Constructors
    public Category() {}
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}