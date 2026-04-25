package dao;

import model.LearnerProfile;
import util.DaoHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LearnerProfileDAO {

    private static final String BASE_QUERY = "SELECT learner_profile_id, instrument_key, created_at, updated_at, user_id FROM LEARNERPROFILE";

    public boolean create(LearnerProfile profile) {
        String sql = "INSERT INTO LEARNERPROFILE (instrument_key, user_id) VALUES (?, ?)";
        return DaoHelper.executeInsert(sql,
                stmt -> {
                    stmt.setString(1, DaoHelper.normalizeInstrumentKey(profile.getInstrument()));
                    stmt.setInt(2, profile.getUserId());
                },
                profile::setLearnerProfileId);
    }

    public LearnerProfile findById(int learnerProfileId) {
        return DaoHelper.querySingle(
                BASE_QUERY + " WHERE learner_profile_id = ?",
                stmt -> stmt.setInt(1, learnerProfileId),
                this::mapResultSetToLearnerProfile);
    }

    public LearnerProfile findByUserId(int userId) {
        return DaoHelper.querySingle(
                BASE_QUERY + " WHERE user_id = ?",
                stmt -> stmt.setInt(1, userId),
                this::mapResultSetToLearnerProfile);
    }

    public List<LearnerProfile> findAll() {
        return DaoHelper.queryList(
                BASE_QUERY + " ORDER BY created_at DESC",
                stmt -> {
                },
                this::mapResultSetToLearnerProfile);
    }

    public boolean update(LearnerProfile profile) {
        return DaoHelper.executeUpdate(
                "UPDATE LEARNERPROFILE SET instrument_key = ?, updated_at = CURRENT_DATE WHERE learner_profile_id = ?",
                stmt -> {
                    stmt.setString(1, DaoHelper.normalizeInstrumentKey(profile.getInstrument()));
                    stmt.setInt(2, profile.getLearnerProfileId());
                });
    }

    public boolean delete(int learnerProfileId) {
        return DaoHelper.executeUpdate(
                "DELETE FROM LEARNERPROFILE WHERE learner_profile_id = ?",
                stmt -> stmt.setInt(1, learnerProfileId));
    }

    private LearnerProfile mapResultSetToLearnerProfile(ResultSet rs) throws SQLException {
        LearnerProfile profile = new LearnerProfile();
        profile.setLearnerProfileId(rs.getInt("learner_profile_id"));
        profile.setInstrument(rs.getString("instrument_key"));
        profile.setUserId(rs.getInt("user_id"));
        profile.setCreatedAt(DaoHelper.readLocalDate(rs, "created_at"));
        profile.setUpdatedAt(DaoHelper.readLocalDate(rs, "updated_at"));
        return profile;
    }
}
