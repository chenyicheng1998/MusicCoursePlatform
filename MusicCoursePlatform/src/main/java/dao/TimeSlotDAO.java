package dao;

import model.TimeSlot;
import util.DaoHelper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TimeSlotDAO {

    private static final String BASE_QUERY = "SELECT slot_id, lesson_date, start_time, end_time, "
            + "slot_status, teacher_profile_id, created_at FROM TIMESLOT";

    public boolean create(TimeSlot slot) {
        String sql = "INSERT INTO TIMESLOT (lesson_date, start_time, end_time, slot_status, teacher_profile_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        return DaoHelper.executeInsert(sql,
                stmt -> {
                    stmt.setDate(1, Date.valueOf(slot.getLessonDate()));
                    stmt.setString(2, slot.getStartTime());
                    stmt.setString(3, slot.getEndTime());
                    stmt.setString(4, slot.getSlotStatus());
                    stmt.setInt(5, slot.getTeacherProfileId());
                },
                slot::setSlotId);
    }

    public TimeSlot findById(int slotId) {
        return DaoHelper.querySingle(
                BASE_QUERY + " WHERE slot_id = ?",
                stmt -> stmt.setInt(1, slotId),
                this::mapResultSetToTimeSlot);
    }

    public List<TimeSlot> findByTeacherProfileId(int teacherProfileId) {
        return DaoHelper.queryList(
                BASE_QUERY + " WHERE teacher_profile_id = ? ORDER BY lesson_date, start_time",
                stmt -> stmt.setInt(1, teacherProfileId),
                this::mapResultSetToTimeSlot);
    }

    public List<TimeSlot> findByTeacherProfileIdAndDate(int teacherProfileId, LocalDate date) {
        return DaoHelper.queryList(
                BASE_QUERY + " WHERE teacher_profile_id = ? AND lesson_date = ? ORDER BY start_time",
                stmt -> {
                    stmt.setInt(1, teacherProfileId);
                    stmt.setDate(2, Date.valueOf(date));
                },
                this::mapResultSetToTimeSlot);
    }

    public List<TimeSlot> findAvailableByTeacherProfileId(int teacherProfileId) {
        return DaoHelper.queryList(
                BASE_QUERY + " WHERE teacher_profile_id = ? AND slot_status = 'AVAILABLE' "
                        + "ORDER BY lesson_date, start_time",
                stmt -> stmt.setInt(1, teacherProfileId),
                this::mapResultSetToTimeSlot);
    }

    public List<TimeSlot> findAvailableByDate(LocalDate date) {
        return DaoHelper.queryList(
                BASE_QUERY + " WHERE lesson_date = ? AND slot_status = 'AVAILABLE' ORDER BY start_time",
                stmt -> stmt.setDate(1, Date.valueOf(date)),
                this::mapResultSetToTimeSlot);
    }

    public List<TimeSlot> findAll() {
        return DaoHelper.queryList(
                BASE_QUERY + " ORDER BY lesson_date, start_time",
                stmt -> {
                },
                this::mapResultSetToTimeSlot);
    }

    public boolean update(TimeSlot slot) {
        return DaoHelper.executeUpdate(
                "UPDATE TIMESLOT SET lesson_date = ?, start_time = ?, end_time = ?, slot_status = ? WHERE slot_id = ?",
                stmt -> {
                    stmt.setDate(1, Date.valueOf(slot.getLessonDate()));
                    stmt.setString(2, slot.getStartTime());
                    stmt.setString(3, slot.getEndTime());
                    stmt.setString(4, slot.getSlotStatus());
                    stmt.setInt(5, slot.getSlotId());
                });
    }

    public boolean updateStatus(int slotId, String status) {
        return DaoHelper.executeUpdate(
                "UPDATE TIMESLOT SET slot_status = ? WHERE slot_id = ?",
                stmt -> {
                    stmt.setString(1, status);
                    stmt.setInt(2, slotId);
                });
    }

    public boolean delete(int slotId) {
        return DaoHelper.executeUpdate(
                "DELETE FROM TIMESLOT WHERE slot_id = ?",
                stmt -> stmt.setInt(1, slotId));
    }

    private TimeSlot mapResultSetToTimeSlot(ResultSet rs) throws SQLException {
        TimeSlot slot = new TimeSlot();
        slot.setSlotId(rs.getInt("slot_id"));
        slot.setLessonDate(rs.getDate("lesson_date").toLocalDate());
        slot.setStartTime(rs.getString("start_time"));
        slot.setEndTime(rs.getString("end_time"));
        slot.setSlotStatus(rs.getString("slot_status"));
        slot.setTeacherProfileId(rs.getInt("teacher_profile_id"));
        Date createdAt = rs.getDate("created_at");
        if (createdAt != null) {
            slot.setCreatedAt(createdAt.toLocalDate());
        }
        return slot;
    }
}
