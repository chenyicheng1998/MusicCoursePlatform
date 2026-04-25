package dao;

import model.User;
import util.DaoHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Data Access Object for User entity.
 * Provides CRUD operations for the USERS table.
 *
 * @author Lu Liu
 * @version 2.0 (Sprint 7 — refactored to use DaoHelper)
 */
public class UserDAO {

    private static final String COLUMNS = "user_id, username, password_hash, email, user_type, created_at";
    private static final String BASE_QUERY = "SELECT " + COLUMNS + " FROM USERS";

    public boolean create(User user) {
        String sql = "INSERT INTO USERS (username, password_hash, email, user_type) VALUES (?, ?, ?, ?)";
        return DaoHelper.executeInsert(sql,
                stmt -> {
                    stmt.setString(1, user.getUsername());
                    stmt.setString(2, user.getPasswordHash());
                    stmt.setString(3, user.getEmail());
                    stmt.setString(4, user.getUserType());
                },
                user::setUserId);
    }

    public User findById(int userId) {
        return DaoHelper.querySingle(
                BASE_QUERY + " WHERE user_id = ?",
                stmt -> stmt.setInt(1, userId),
                this::mapResultSetToUser);
    }

    public User findByUsername(String username) {
        return DaoHelper.querySingle(
                BASE_QUERY + " WHERE username = ?",
                stmt -> stmt.setString(1, username),
                this::mapResultSetToUser);
    }

    public User findByEmail(String email) {
        return DaoHelper.querySingle(
                BASE_QUERY + " WHERE email = ?",
                stmt -> stmt.setString(1, email),
                this::mapResultSetToUser);
    }

    public List<User> findAll() {
        return DaoHelper.queryList(
                BASE_QUERY + " ORDER BY created_at DESC",
                stmt -> {
                },
                this::mapResultSetToUser);
    }

    public List<User> findByUserType(String userType) {
        return DaoHelper.queryList(
                BASE_QUERY + " WHERE user_type = ? ORDER BY created_at DESC",
                stmt -> stmt.setString(1, userType),
                this::mapResultSetToUser);
    }

    public boolean update(User user) {
        return DaoHelper.executeUpdate(
                "UPDATE USERS SET username = ?, password_hash = ?, email = ?, user_type = ? WHERE user_id = ?",
                stmt -> {
                    stmt.setString(1, user.getUsername());
                    stmt.setString(2, user.getPasswordHash());
                    stmt.setString(3, user.getEmail());
                    stmt.setString(4, user.getUserType());
                    stmt.setInt(5, user.getUserId());
                });
    }

    public boolean delete(int userId) {
        return DaoHelper.executeUpdate(
                "DELETE FROM USERS WHERE user_id = ?",
                stmt -> stmt.setInt(1, userId));
    }

    public boolean usernameExists(String username) {
        Integer count = DaoHelper.querySingle(
                "SELECT COUNT(*) FROM USERS WHERE username = ?",
                stmt -> stmt.setString(1, username),
                rs -> rs.getInt(1));
        return count != null && count > 0;
    }

    public boolean emailExists(String email) {
        Integer count = DaoHelper.querySingle(
                "SELECT COUNT(*) FROM USERS WHERE email = ?",
                stmt -> stmt.setString(1, email),
                rs -> rs.getInt(1));
        return count != null && count > 0;
    }

    public int countAll() {
        Integer count = DaoHelper.querySingle(
                "SELECT COUNT(*) FROM USERS",
                stmt -> {
                },
                rs -> rs.getInt(1));
        return count != null ? count : 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setUserType(rs.getString("user_type"));
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            user.setCreatedAt(timestamp.toLocalDateTime());
        }
        return user;
    }
}
