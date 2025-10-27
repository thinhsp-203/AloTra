package model;

import java.io.Serializable;

/**
 * Category Model - KHÔNG DÙNG LOMBOK để tránh conflict
 * Giữ tên getter/setter đồng nhất với legacy code
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;        // legacy code dùng .getId()
    private String name;   // legacy code dùng .getName()
    private String icon;   // legacy code dùng .getIcon()
    
    // Constructors
    public Category() {}
    
    // Getters and Setters - QUAN TRỌNG: giữ đồng nhất
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}