package dao;

import model.TimeSlot;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAO {

    public boolean create(TimeSlot slot) {
        String sql = "INSERT INTO time_slots (teacher_id, lesson_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, slot.getTeacherId());
            stmt.setDate(2, Date.valueOf(slot.getLessonDate()));
            stmt.setTime(3, Time.valueOf(slot.getStartTime()));
            stmt.setTime(4, Time.valueOf(slot.getEndTime()));
            stmt.setString(5, slot.getStatus());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        slot.setSlotId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error creating time slot: " + e.getMessage());
            return false;
        }
    }

    public TimeSlot findById(int slotId) {
        String sql = "SELECT * FROM time_slots WHERE slot_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, slotId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTimeSlot(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding time slot by ID: " + e.getMessage());
        }
        return null;
    }

    public List<TimeSlot> findByTeacherId(int teacherId) {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM time_slots WHERE teacher_id = ? ORDER BY lesson_date, start_time";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapResultSetToTimeSlot(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding time slots by teacher ID: " + e.getMessage());
        }
        return slots;
    }

    public List<TimeSlot> findByTeacherIdAndDate(int teacherId, LocalDate date) {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM time_slots WHERE teacher_id = ? AND lesson_date = ? ORDER BY start_time";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapResultSetToTimeSlot(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding time slots by teacher and date: " + e.getMessage());
        }
        return slots;
    }

    public List<TimeSlot> findAvailableByTeacherId(int teacherId) {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM time_slots WHERE teacher_id = ? AND status = 'AVAILABLE' ORDER BY lesson_date, start_time";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapResultSetToTimeSlot(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding available time slots: " + e.getMessage());
        }
        return slots;
    }

    public List<TimeSlot> findAvailableByDate(LocalDate date) {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM time_slots WHERE lesson_date = ? AND status = 'AVAILABLE' ORDER BY start_time";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapResultSetToTimeSlot(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding available time slots by date: " + e.getMessage());
        }
        return slots;
    }

    public List<TimeSlot> findAll() {
        List<TimeSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM time_slots ORDER BY lesson_date, start_time";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                slots.add(mapResultSetToTimeSlot(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all time slots: " + e.getMessage());
        }
        return slots;
    }

    public boolean update(TimeSlot slot) {
        String sql = "UPDATE time_slots SET teacher_id = ?, lesson_date = ?, start_time = ?, end_time = ?, status = ? WHERE slot_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, slot.getTeacherId());
            stmt.setDate(2, Date.valueOf(slot.getLessonDate()));
            stmt.setTime(3, Time.valueOf(slot.getStartTime()));
            stmt.setTime(4, Time.valueOf(slot.getEndTime()));
            stmt.setString(5, slot.getStatus());
            stmt.setInt(6, slot.getSlotId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating time slot: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(int slotId, String status) {
        String sql = "UPDATE time_slots SET status = ? WHERE slot_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, slotId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating time slot status: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int slotId) {
        String sql = "DELETE FROM time_slots WHERE slot_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, slotId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting time slot: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteByTeacherId(int teacherId) {
        String sql = "DELETE FROM time_slots WHERE teacher_id = ?";
        
        try (Connection conn = DatabaseConnection.getNewConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, teacherId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting time slots by teacher ID: " + e.getMessage());
            return false;
        }
    }

    private TimeSlot mapResultSetToTimeSlot(ResultSet rs) throws SQLException {
        TimeSlot slot = new TimeSlot();
        slot.setSlotId(rs.getInt("slot_id"));
        slot.setTeacherId(rs.getInt("teacher_id"));
        slot.setLessonDate(rs.getDate("lesson_date").toLocalDate());
        slot.setStartTime(rs.getTime("start_time").toLocalTime());
        slot.setEndTime(rs.getTime("end_time").toLocalTime());
        slot.setStatus(rs.getString("status"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            slot.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return slot;
    }
}
