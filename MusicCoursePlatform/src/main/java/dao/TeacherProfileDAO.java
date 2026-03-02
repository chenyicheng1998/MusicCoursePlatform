package dao;

import model.TeacherProfile;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeacherProfileDAO {

    public boolean create(TeacherProfile profile) {
        String sql = "INSERT INTO teacher_profiles (user_id, biography, instruments_taught, years_experience, hourly_rate, location) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getBiography());
            stmt.setString(3, profile.getInstrumentsTaught());
            stmt.setInt(4, profile.getYearsExperience());
            stmt.setBigDecimal(5, profile.getHourlyRate());
            stmt.setString(6, profile.getLocation());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        profile.setProfileId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error creating teacher profile: " + e.getMessage());
            return false;
        }
    }

    public TeacherProfile findById(int profileId) {
        String sql = "SELECT * FROM teacher_profiles WHERE profile_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, profileId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeacherProfile(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher profile by ID: " + e.getMessage());
        }
        return null;
    }

    public TeacherProfile findByUserId(int userId) {
        String sql = "SELECT * FROM teacher_profiles WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeacherProfile(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher profile by user ID: " + e.getMessage());
        }
        return null;
    }

    public List<TeacherProfile> findAll() {
        List<TeacherProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM teacher_profiles ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                profiles.add(mapResultSetToTeacherProfile(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all teacher profiles: " + e.getMessage());
        }
        return profiles;
    }

    public List<TeacherProfile> findByInstrument(String instrument) {
        List<TeacherProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM teacher_profiles WHERE instruments_taught LIKE ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + instrument + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profiles.add(mapResultSetToTeacherProfile(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher profiles by instrument: " + e.getMessage());
        }
        return profiles;
    }

    public List<TeacherProfile> findByLocation(String location) {
        List<TeacherProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM teacher_profiles WHERE location LIKE ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + location + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profiles.add(mapResultSetToTeacherProfile(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher profiles by location: " + e.getMessage());
        }
        return profiles;
    }

    public boolean update(TeacherProfile profile) {
        String sql = "UPDATE teacher_profiles SET biography = ?, instruments_taught = ?, years_experience = ?, hourly_rate = ?, location = ? WHERE profile_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, profile.getBiography());
            stmt.setString(2, profile.getInstrumentsTaught());
            stmt.setInt(3, profile.getYearsExperience());
            stmt.setBigDecimal(4, profile.getHourlyRate());
            stmt.setString(5, profile.getLocation());
            stmt.setInt(6, profile.getProfileId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating teacher profile: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int profileId) {
        String sql = "DELETE FROM teacher_profiles WHERE profile_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, profileId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting teacher profile: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteByUserId(int userId) {
        String sql = "DELETE FROM teacher_profiles WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting teacher profile by user ID: " + e.getMessage());
            return false;
        }
    }

    private TeacherProfile mapResultSetToTeacherProfile(ResultSet rs) throws SQLException {
        TeacherProfile profile = new TeacherProfile();
        profile.setProfileId(rs.getInt("profile_id"));
        profile.setUserId(rs.getInt("user_id"));
        profile.setBiography(rs.getString("biography"));
        profile.setInstrumentsTaught(rs.getString("instruments_taught"));
        profile.setYearsExperience(rs.getInt("years_experience"));
        profile.setHourlyRate(rs.getBigDecimal("hourly_rate"));
        profile.setLocation(rs.getString("location"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            profile.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            profile.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return profile;
    }
}
