package dao.impl;

import dao.CategoryDao;
import model.Category;
import dao.DBConnection;
import java.sql.*;
import java.util.*;

public class CategoryDaoImpl extends DBConnection implements CategoryDao {
  @Override
  public void insert(Category category) {
    String sql = "INSERT INTO Category(cate_name, icons) VALUES (?, ?)";
    try (Connection con = super.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, category.getName());
      ps.setString(2, category.getIcon());
      ps.executeUpdate();
    } catch (Exception e) { e.printStackTrace(); }
  }

  @Override
  public void edit(Category category) {
    String sql = "UPDATE Category SET cate_name=?, icons=? WHERE cate_id=?";
    try (Connection con = super.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, category.getName());
      ps.setString(2, category.getIcon());
      ps.setInt(3, category.getId());
      ps.executeUpdate();
    } catch (Exception e) { e.printStackTrace(); }
  }

  @Override
  public void delete(int id) {
    String sql = "DELETE FROM Category WHERE cate_id=?";
    try (Connection con = super.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, id);
      ps.executeUpdate();
    } catch (Exception e) { e.printStackTrace(); }
  }

  @Override
  public Category get(int id) {
    String sql = "SELECT * FROM Category WHERE cate_id=?";
    try (Connection con = super.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Category c = new Category();
          c.setId(rs.getInt("cate_id"));
          c.setName(rs.getString("cate_name"));
          c.setIcon(rs.getString("icons"));
          return c;
        }
      }
    } catch (Exception e) { e.printStackTrace(); }
    return null;
  }

  @Override
  public Category get(String name) {
    String sql = "SELECT * FROM Category WHERE cate_name=?";
    try (Connection con = super.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, name);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Category c = new Category();
          c.setId(rs.getInt("cate_id"));
          c.setName(rs.getString("cate_name"));
          c.setIcon(rs.getString("icons"));
          return c;
        }
      }
    } catch (Exception e) { e.printStackTrace(); }
    return null;
  }

  @Override
  public List<Category> getAll() {
    List<Category> list = new ArrayList<>();
    String sql = "SELECT * FROM Category ORDER BY cate_id DESC";
    try (Connection con = super.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        Category c = new Category();
        c.setId(rs.getInt("cate_id"));
        c.setName(rs.getString("cate_name"));
        c.setIcon(rs.getString("icons"));
        list.add(c);
      }
    } catch (Exception e) { e.printStackTrace(); }
    return list;
  }

  @Override
  public List<Category> search(String keyword) {
    List<Category> list = new ArrayList<>();
    String sql = "SELECT * FROM Category WHERE cate_name LIKE ?";
    try (Connection con = super.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setString(1, "%" + keyword + "%");
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          Category c = new Category();
          c.setId(rs.getInt("cate_id"));
          c.setName(rs.getString("cate_name"));
          c.setIcon(rs.getString("icons"));
          list.add(c);
        }
      }
    } catch (Exception e) { e.printStackTrace(); }
    return list;
  }
}
