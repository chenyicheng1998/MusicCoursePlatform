package dao;

import model.TeacherProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for TeacherProfile.
 *
 * Database localization note (Sprint 6):
 * The {@code instrument_key} column stores a canonical lowercase English key
 * (e.g. "piano") regardless of the locale that was active when the teacher
 * saved their profile. Localised display names are resolved at the UI layer
 * via {@link util.LocalizationManager#getLocalizedInstrumentName}.
 */
public class TeacherProfileDAO {

    private static final Logger logger = LoggerFactory.getLogger(TeacherProfileDAO.class);

    private static final String BASE_QUERY =
            "SELECT teacher_profile_id, biography, instrument_key, years_experience, "
            + "hourly_rate, location, created_at, updated_at, user_id FROM TEACHERPROFILE";

    public boolean create(TeacherProfile profile) {
        String sql = "INSERT INTO TEACHERPROFILE "
                + "(biography, instrument_key, years_experience, hourly_rate, location, user_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, profile.getBiography());
            stmt.setString(2, normalizeKey(profile.getInstrumentsTaught()));
            stmt.setInt(3, profile.getYearsExperience());
            stmt.setInt(4, profile.getHourlyRate());
            stmt.setString(5, profile.getLocation());
            stmt.setInt(6, profile.getUserId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        profile.setTeacherProfileId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.error("Error creating teacher profile: {}", e.getMessage(), e);
            return false;
        }
    }

    public TeacherProfile findById(int teacherProfileId) {
        String sql = BASE_QUERY + " WHERE teacher_profile_id = ?";

        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherProfileId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeacherProfile(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding teacher profile by ID: {}", e.getMessage(), e);
        }
        return null;
    }

    public TeacherProfile findByUserId(int userId) {
        String sql = BASE_QUERY + " WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTeacherProfile(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding teacher profile by user ID: {}", e.getMessage(), e);
        }
        return null;
    }

    public List<TeacherProfile> findAll() {
        List<TeacherProfile> profiles = new ArrayList<>();
        String sql = BASE_QUERY + " ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getNewConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                profiles.add(mapResultSetToTeacherProfile(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all teacher profiles: {}", e.getMessage(), e);
        }
        return profiles;
    }

    /**
     * Find teacher profiles by canonical instrument key (exact match,
     * case-insensitive).
     * The key should be a lowercase string such as "piano", "guitar", etc.
     */
    public List<TeacherProfile> findByInstrument(String instrumentKey) {
        List<TeacherProfile> profiles = new ArrayList<>();
        String sql = BASE_QUERY + " WHERE LOWER(instrument_key) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, instrumentKey);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profiles.add(mapResultSetToTeacherProfile(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding teacher profiles by instrument: {}", e.getMessage(), e);
        }
        return profiles;
    }

    public List<TeacherProfile> findByLocation(String location) {
        List<TeacherProfile> profiles = new ArrayList<>();
        String sql = BASE_QUERY + " WHERE location LIKE ?";

        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + location + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profiles.add(mapResultSetToTeacherProfile(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding teacher profiles by location: {}", e.getMessage(), e);
        }
        return profiles;
    }

    public boolean update(TeacherProfile profile) {
        String sql = "UPDATE TEACHERPROFILE "
                + "SET biography = ?, instrument_key = ?, years_experience = ?, "
                + "hourly_rate = ?, location = ?, updated_at = CURRENT_DATE "
                + "WHERE teacher_profile_id = ?";

        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, profile.getBiography());
            stmt.setString(2, normalizeKey(profile.getInstrumentsTaught()));
            stmt.setInt(3, profile.getYearsExperience());
            stmt.setInt(4, profile.getHourlyRate());
            stmt.setString(5, profile.getLocation());
            stmt.setInt(6, profile.getTeacherProfileId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error updating teacher profile: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean delete(int teacherProfileId) {
        String sql = "DELETE FROM TEACHERPROFILE WHERE teacher_profile_id = ?";

        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherProfileId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.error("Error deleting teacher profile: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Normalize an instrument value to a lowercase canonical key safe for the DB
     * FK.
     */
    private static String normalizeKey(String raw) {
        if (raw == null || raw.isBlank())
            return "piano";
        // Strip any comma-separated extras and lowercase
        return raw.split(",")[0].trim().toLowerCase();
    }

    private TeacherProfile mapResultSetToTeacherProfile(ResultSet rs) throws SQLException {
        TeacherProfile profile = new TeacherProfile();
        profile.setTeacherProfileId(rs.getInt("teacher_profile_id"));
        profile.setBiography(rs.getString("biography"));
        // instrument_key holds the canonical lowercase key (e.g. "piano")
        profile.setInstrumentsTaught(rs.getString("instrument_key"));
        profile.setYearsExperience(rs.getInt("years_experience"));
        profile.setHourlyRate(rs.getInt("hourly_rate"));
        profile.setLocation(rs.getString("location"));
        profile.setUserId(rs.getInt("user_id"));

        Date createdAt = rs.getDate("created_at");
        if (createdAt != null) {
            profile.setCreatedAt(createdAt.toLocalDate());
        }

        Date updatedAt = rs.getDate("updated_at");
        if (updatedAt != null) {
            profile.setUpdatedAt(updatedAt.toLocalDate());
        }

        return profile;
    }
}
