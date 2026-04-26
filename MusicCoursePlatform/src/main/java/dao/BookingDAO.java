package dao;

import model.Booking;
import util.DaoHelper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BookingDAO {

    private static final String BASE_QUERY = "SELECT booking_id, booking_date, booking_status, notes, "
            + "learner_profile_id, slot_id, created_at, updated_at FROM BOOKING";

    public boolean create(Booking booking) {
        String sql = "INSERT INTO BOOKING (booking_date, booking_status, notes, learner_profile_id, slot_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        return DaoHelper.executeInsert(sql,
                stmt -> {
                    stmt.setDate(1, Date.valueOf(booking.getBookingDate()));
                    stmt.setString(2, booking.getBookingStatus());
                    stmt.setString(3, booking.getNotes());
                    stmt.setInt(4, booking.getLearnerProfileId());
                    stmt.setInt(5, booking.getSlotId());
                },
                booking::setBookingId);
    }

    public Booking findById(int bookingId) {
        return DaoHelper.querySingle(
                BASE_QUERY + " WHERE booking_id = ?",
                stmt -> stmt.setInt(1, bookingId),
                this::mapResultSetToBooking);
    }

    public List<Booking> findByLearnerProfileId(int learnerProfileId) {
        return DaoHelper.queryList(
                BASE_QUERY + " WHERE learner_profile_id = ? ORDER BY booking_date DESC",
                stmt -> stmt.setInt(1, learnerProfileId),
                this::mapResultSetToBooking);
    }

    public Booking findBySlotId(int slotId) {
        return DaoHelper.querySingle(
                BASE_QUERY + " WHERE slot_id = ?",
                stmt -> stmt.setInt(1, slotId),
                this::mapResultSetToBooking);
    }

    public List<Booking> findByStatus(String status) {
        return DaoHelper.queryList(
                BASE_QUERY + " WHERE booking_status = ? ORDER BY booking_date DESC",
                stmt -> stmt.setString(1, status),
                this::mapResultSetToBooking);
    }

    public List<Booking> findAll() {
        return DaoHelper.queryList(
                BASE_QUERY + " ORDER BY booking_date DESC",
                stmt -> {
                },
                this::mapResultSetToBooking);
    }

    public boolean update(Booking booking) {
        return DaoHelper.executeUpdate(
                "UPDATE BOOKING SET booking_status = ?, notes = ?, updated_at = CURRENT_DATE WHERE booking_id = ?",
                stmt -> {
                    stmt.setString(1, booking.getBookingStatus());
                    stmt.setString(2, booking.getNotes());
                    stmt.setInt(3, booking.getBookingId());
                });
    }

    public boolean updateStatus(int bookingId, String status) {
        return DaoHelper.executeUpdate(
                "UPDATE BOOKING SET booking_status = ?, updated_at = CURRENT_DATE WHERE booking_id = ?",
                stmt -> {
                    stmt.setString(1, status);
                    stmt.setInt(2, bookingId);
                });
    }

    public boolean delete(int bookingId) {
        return DaoHelper.executeUpdate(
                "DELETE FROM BOOKING WHERE booking_id = ?",
                stmt -> stmt.setInt(1, bookingId));
    }

    private Booking mapResultSetToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setBookingDate(rs.getDate("booking_date").toLocalDate());
        booking.setBookingStatus(rs.getString("booking_status"));
        booking.setNotes(rs.getString("notes"));
        booking.setLearnerProfileId(rs.getInt("learner_profile_id"));
        booking.setSlotId(rs.getInt("slot_id"));
        booking.setCreatedAt(DaoHelper.readLocalDate(rs, "created_at"));
        booking.setUpdatedAt(DaoHelper.readLocalDate(rs, "updated_at"));
        return booking;
    }
}
