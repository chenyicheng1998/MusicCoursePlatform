package model;

import java.time.LocalDateTime;

/**
 * Booking entity class representing a lesson booking in the Music Course Platform.
 * Bookings link learners to time slots.
 * 
 * @author Lu Liu
 * @version 1.0 (Sprint 3)
 */
public class Booking {

    // Status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private int bookingId;
    private int slotId;
    private int learnerId;
    private LocalDateTime bookingDate;
    private String status;  // "PENDING", "CONFIRMED", or "CANCELLED"
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Optional: Associated objects for convenience
    private TimeSlot timeSlot;
    private User learner;

    /**
     * Default constructor
     */
    public Booking() {
        this.status = STATUS_PENDING;
        this.bookingDate = LocalDateTime.now();
    }

    /**
     * Constructor with essential fields
     * 
     * @param slotId The ID of the time slot being booked
     * @param learnerId The ID of the learner making the booking
     */
    public Booking(int slotId, int learnerId) {
        this.slotId = slotId;
        this.learnerId = learnerId;
        this.status = STATUS_PENDING;
        this.bookingDate = LocalDateTime.now();
    }

    /**
     * Constructor with notes
     */
    public Booking(int slotId, int learnerId, String notes) {
        this.slotId = slotId;
        this.learnerId = learnerId;
        this.notes = notes;
        this.status = STATUS_PENDING;
        this.bookingDate = LocalDateTime.now();
    }

    /**
     * Full constructor with all fields
     */
    public Booking(int bookingId, int slotId, int learnerId, LocalDateTime bookingDate,
                   String status, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.bookingId = bookingId;
        this.slotId = slotId;
        this.learnerId = learnerId;
        this.bookingDate = bookingDate;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== Getters and Setters ====================

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getLearnerId() {
        return learnerId;
    }

    public void setLearnerId(int learnerId) {
        this.learnerId = learnerId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public User getLearner() {
        return learner;
    }

    public void setLearner(User learner) {
        this.learner = learner;
    }

    // ==================== Utility Methods ====================

    /**
     * Check if booking is pending
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    /**
     * Check if booking is confirmed
     * @return true if status is CONFIRMED
     */
    public boolean isConfirmed() {
        return STATUS_CONFIRMED.equals(status);
    }

    /**
     * Check if booking is cancelled
     * @return true if status is CANCELLED
     */
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }

    /**
     * Confirm the booking
     */
    public void confirm() {
        this.status = STATUS_CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancel the booking
     */
    public void cancel() {
        this.status = STATUS_CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if booking is active (not cancelled)
     * @return true if booking is pending or confirmed
     */
    public boolean isActive() {
        return isPending() || isConfirmed();
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", slotId=" + slotId +
                ", learnerId=" + learnerId +
                ", bookingDate=" + bookingDate +
                ", status='" + status + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
