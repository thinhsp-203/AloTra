package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.User;

public class UserDaoImpl implements UserDao {
     @Override
     public User get(String username) {
         String sql = "SELECT * FROM [User] WHERE username = ?";
         try (Connection conn = new DBConnection().getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setString(1, username);
             try (ResultSet rs = ps.executeQuery()) {
                 if (rs.next()) {
                    return mapRow(rs);
                 }
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
         return null;
     }
 
     @Override
     public void insert(User user) {
        String sql = "INSERT INTO [User](email, username, fullname, password, avatar, roleid, phone, createdDate) "
                   + "VALUES (?,?,?,?,?,?,?,?)";
         try (Connection conn = new DBConnection().getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setString(1, user.getEmail());
             ps.setString(2, user.getUserName());
             ps.setString(3, user.getFullName());
             ps.setString(4, user.getPassWord());
             ps.setString(5, user.getAvatar());
             ps.setInt(6, user.getRoleId());
             ps.setString(7, user.getPhone());
             Date createdDate = user.getCreatedDate() != null ? user.getCreatedDate() : new Date();
             ps.setDate(8, new java.sql.Date(createdDate.getTime()));
             ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
     }
 
     @Override
     public boolean checkExistUsername(String username) {
        return exists("SELECT 1 FROM [User] WHERE username = ?", username);
     }
 
     @Override
     public boolean checkExistEmail(String email) {
        return exists("SELECT 1 FROM [User] WHERE email = ?", email);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return exists("SELECT 1 FROM [User] WHERE phone = ?", phone);
    }

    @Override
    public int count(String keyword, Integer roleId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM [User] WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, roleId);
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = prepare(conn, sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public List<User> search(String keyword, Integer roleId, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM [User] WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, keyword, roleId);
        sql.append(" ORDER BY createdDate DESC, id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(limit);

        List<User> users = new ArrayList<>();
        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = prepare(conn, sql.toString(), params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private boolean exists(String sql, String value) {
         try (Connection conn = new DBConnection().getConnection();
              PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
             try (ResultSet rs = ps.executeQuery()) {
                 return rs.next();
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
         return false;
     }

    private void appendFilters(StringBuilder sql, List<Object> params, String keyword, Integer roleId) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            // SỬA LỖI NỐI CHUỖI
            String pattern = "%" + keyword.trim() + "%";
            sql.append(" AND (username LIKE ? OR fullname LIKE ? OR email LIKE ? OR phone LIKE ?)");
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }
        if (roleId != null) {
            sql.append(" AND roleid = ?");
            params.add(roleId);
        }
    }

    private PreparedStatement prepare(Connection conn, String sql, List<Object> params) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        // SỬA LỖI VÒNG LẶP FOR VÀ THAM SỐ INDEX
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            if (value instanceof Integer) {
                ps.setInt(i + 1, (Integer) value);
            } else if (value instanceof String) {
                ps.setString(i + 1, (String) value);
            } else {
                ps.setObject(i + 1, value);
            }
        }
        return ps;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setUserName(rs.getString("username"));
        user.setFullName(rs.getString("fullname"));
        user.setPassWord(rs.getString("password"));
        user.setAvatar(rs.getString("avatar"));
        user.setRoleid(rs.getInt("roleid"));
        user.setPhone(rs.getString("phone"));
        Timestamp created = rs.getTimestamp("createdDate");
        if (created != null) {
            user.setCreatedDate(new Date(created.getTime()));
        }
        return user;
    }
 }