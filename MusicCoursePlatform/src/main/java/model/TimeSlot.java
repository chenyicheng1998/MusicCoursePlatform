package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * TimeSlot entity class representing an available time slot for lessons.
 * Teachers create time slots to indicate their availability.
 * 
 * @author Lu Liu
 * @version 1.0 (Sprint 3)
 */
public class TimeSlot {

    // Status constants
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_BOOKED = "BOOKED";

    private int slotId;
    private int teacherId;
    private LocalDate lessonDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;  // "AVAILABLE" or "BOOKED"
    private LocalDateTime createdAt;

    // Optional: Associated User (teacher) object for convenience
    private User teacher;

    /**
     * Default constructor
     */
    public TimeSlot() {
        this.status = STATUS_AVAILABLE;
    }

    /**
     * Constructor with essential fields
     * 
     * @param teacherId The ID of the teacher
     * @param lessonDate Date of the lesson
     * @param startTime Start time of the slot
     * @param endTime End time of the slot
     */
    public TimeSlot(int teacherId, LocalDate lessonDate, LocalTime startTime, LocalTime endTime) {
        this.teacherId = teacherId;
        this.lessonDate = lessonDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = STATUS_AVAILABLE;
    }

    /**
     * Constructor with status
     */
    public TimeSlot(int teacherId, LocalDate lessonDate, LocalTime startTime, 
                    LocalTime endTime, String status) {
        this.teacherId = teacherId;
        this.lessonDate = lessonDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    /**
     * Full constructor with all fields
     */
    public TimeSlot(int slotId, int teacherId, LocalDate lessonDate, LocalTime startTime,
                    LocalTime endTime, String status, LocalDateTime createdAt) {
        this.slotId = slotId;
        this.teacherId = teacherId;
        this.lessonDate = lessonDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ==================== Getters and Setters ====================

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(LocalDate lessonDate) {
        this.lessonDate = lessonDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    // ==================== Utility Methods ====================

    /**
     * Check if the slot is available for booking
     * @return true if status is AVAILABLE
     */
    public boolean isAvailable() {
        return STATUS_AVAILABLE.equals(status);
    }

    /**
     * Check if the slot is booked
     * @return true if status is BOOKED
     */
    public boolean isBooked() {
        return STATUS_BOOKED.equals(status);
    }

    /**
     * Mark the slot as booked
     */
    public void markAsBooked() {
        this.status = STATUS_BOOKED;
    }

    /**
     * Mark the slot as available
     */
    public void markAsAvailable() {
        this.status = STATUS_AVAILABLE;
    }

    /**
     * Get duration of the slot in minutes
     * @return Duration in minutes
     */
    public long getDurationMinutes() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * Get formatted time range string
     * @return String like "09:00 - 10:00"
     */
    public String getTimeRangeString() {
        if (startTime == null || endTime == null) {
            return "";
        }
        return String.format("%s - %s", 
                startTime.toString(), 
                endTime.toString());
    }

    /**
     * Check if this time slot overlaps with another
     * @param other Another time slot to check
     * @return true if they overlap
     */
    public boolean overlapsWith(TimeSlot other) {
        if (other == null || !this.lessonDate.equals(other.lessonDate)) {
            return false;
        }
        return this.startTime.isBefore(other.endTime) && 
               this.endTime.isAfter(other.startTime);
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "slotId=" + slotId +
                ", teacherId=" + teacherId +
                ", lessonDate=" + lessonDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                '}';
    }
}
