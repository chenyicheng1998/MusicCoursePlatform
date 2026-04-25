package dao;

import model.TeacherProfile;
import util.DaoHelper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static final String BASE_QUERY = "SELECT teacher_profile_id, biography, instrument_key, years_experience, "
            + "hourly_rate, location, created_at, updated_at, user_id FROM TEACHERPROFILE";

    public boolean create(TeacherProfile profile) {
        String sql = "INSERT INTO TEACHERPROFILE "
                + "(biography, instrument_key, years_experience, hourly_rate, location, user_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        return DaoHelper.executeInsert(sql,
                stmt -> {
                    stmt.setString(1, profile.getBiography());
                    stmt.setString(2, DaoHelper.normalizeInstrumentKey(profile.getInstrumentsTaught()));
                    stmt.setInt(3, profile.getYearsExperience());
                    stmt.setInt(4, profile.getHourlyRate());
                    stmt.setString(5, profile.getLocation());
                    stmt.setInt(6, profile.getUserId());
                },
                profile::setTeacherProfileId);
    }

    public TeacherProfile findById(int teacherProfileId) {
        return DaoHelper.querySingle(
                BASE_QUERY + " WHERE teacher_profile_id = ?",
                stmt -> stmt.setInt(1, teacherProfileId),
                this::mapResultSetToTeacherProfile);
    }

    public TeacherProfile findByUserId(int userId) {
        return DaoHelper.querySingle(
                BASE_QUERY + " WHERE user_id = ?",
                stmt -> stmt.setInt(1, userId),
                this::mapResultSetToTeacherProfile);
    }

    public List<TeacherProfile> findAll() {
        return DaoHelper.queryList(
                BASE_QUERY + " ORDER BY created_at DESC",
                stmt -> {
                },
                this::mapResultSetToTeacherProfile);
    }

    public List<TeacherProfile> findByInstrument(String instrumentKey) {
        return DaoHelper.queryList(
                BASE_QUERY + " WHERE LOWER(instrument_key) = LOWER(?)",
                stmt -> stmt.setString(1, instrumentKey),
                this::mapResultSetToTeacherProfile);
    }

    public List<TeacherProfile> findByLocation(String location) {
        return DaoHelper.queryList(
                BASE_QUERY + " WHERE location LIKE ?",
                stmt -> stmt.setString(1, "%" + location + "%"),
                this::mapResultSetToTeacherProfile);
    }

    public boolean update(TeacherProfile profile) {
        return DaoHelper.executeUpdate(
                "UPDATE TEACHERPROFILE "
                        + "SET biography = ?, instrument_key = ?, years_experience = ?, "
                        + "hourly_rate = ?, location = ?, updated_at = CURRENT_DATE "
                        + "WHERE teacher_profile_id = ?",
                stmt -> {
                    stmt.setString(1, profile.getBiography());
                    stmt.setString(2, DaoHelper.normalizeInstrumentKey(profile.getInstrumentsTaught()));
                    stmt.setInt(3, profile.getYearsExperience());
                    stmt.setInt(4, profile.getHourlyRate());
                    stmt.setString(5, profile.getLocation());
                    stmt.setInt(6, profile.getTeacherProfileId());
                });
    }

    public boolean delete(int teacherProfileId) {
        return DaoHelper.executeUpdate(
                "DELETE FROM TEACHERPROFILE WHERE teacher_profile_id = ?",
                stmt -> stmt.setInt(1, teacherProfileId));
    }

    private TeacherProfile mapResultSetToTeacherProfile(ResultSet rs) throws SQLException {
        TeacherProfile profile = new TeacherProfile();
        profile.setTeacherProfileId(rs.getInt("teacher_profile_id"));
        profile.setBiography(rs.getString("biography"));
        profile.setInstrumentsTaught(rs.getString("instrument_key"));
        profile.setYearsExperience(rs.getInt("years_experience"));
        profile.setHourlyRate(rs.getInt("hourly_rate"));
        profile.setLocation(rs.getString("location"));
        profile.setUserId(rs.getInt("user_id"));
        profile.setCreatedAt(DaoHelper.readLocalDate(rs, "created_at"));
        profile.setUpdatedAt(DaoHelper.readLocalDate(rs, "updated_at"));
        return profile;
    }
}
