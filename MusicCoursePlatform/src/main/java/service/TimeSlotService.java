package service;

import dao.TimeSlotDAO;
import dao.UserDAO;
import model.TimeSlot;
import model.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TimeSlotService {

    private final TimeSlotDAO timeSlotDAO;
    private final UserDAO userDAO;

    public TimeSlotService() {
        this.timeSlotDAO = new TimeSlotDAO();
        this.userDAO = new UserDAO();
    }

    public TimeSlotService(TimeSlotDAO timeSlotDAO, UserDAO userDAO) {
        this.timeSlotDAO = timeSlotDAO;
        this.userDAO = userDAO;
    }

    public TimeSlot createTimeSlot(int teacherId, LocalDate lessonDate, 
                                    LocalTime startTime, LocalTime endTime) {
        User teacher = userDAO.findById(teacherId);
        if (teacher == null) {
            throw new IllegalArgumentException("Teacher not found");
        }
        if (!teacher.isTeacher()) {
            throw new IllegalArgumentException("User is not a teacher");
        }

        validateTimeSlot(lessonDate, startTime, endTime);
        checkForOverlap(teacherId, lessonDate, startTime, endTime, -1);

        TimeSlot slot = new TimeSlot(teacherId, lessonDate, startTime, endTime);
        
        boolean success = timeSlotDAO.create(slot);
        if (!success) {
            throw new RuntimeException("Failed to create time slot");
        }

        return slot;
    }

    public TimeSlot updateTimeSlot(int slotId, LocalDate lessonDate,
                                    LocalTime startTime, LocalTime endTime) {
        TimeSlot slot = timeSlotDAO.findById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Time slot not found");
        }
        if (slot.isBooked()) {
            throw new IllegalArgumentException("Cannot modify a booked time slot");
        }

        validateTimeSlot(lessonDate, startTime, endTime);
        checkForOverlap(slot.getTeacherId(), lessonDate, startTime, endTime, slotId);

        slot.setLessonDate(lessonDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);

        boolean success = timeSlotDAO.update(slot);
        if (!success) {
            throw new RuntimeException("Failed to update time slot");
        }

        return slot;
    }

    public TimeSlot getTimeSlotById(int slotId) {
        return timeSlotDAO.findById(slotId);
    }

    public List<TimeSlot> getTimeSlotsByTeacher(int teacherId) {
        return timeSlotDAO.findByTeacherId(teacherId);
    }

    public List<TimeSlot> getTimeSlotsByTeacherAndDate(int teacherId, LocalDate date) {
        return timeSlotDAO.findByTeacherIdAndDate(teacherId, date);
    }

    public List<TimeSlot> getAvailableSlotsByTeacher(int teacherId) {
        return timeSlotDAO.findAvailableByTeacherId(teacherId);
    }

    public List<TimeSlot> getAvailableSlotsByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return timeSlotDAO.findAvailableByDate(date);
    }

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotDAO.findAll();
    }

    public boolean markAsBooked(int slotId) {
        TimeSlot slot = timeSlotDAO.findById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Time slot not found");
        }
        if (slot.isBooked()) {
            throw new IllegalArgumentException("Time slot is already booked");
        }
        return timeSlotDAO.updateStatus(slotId, TimeSlot.STATUS_BOOKED);
    }

    public boolean markAsAvailable(int slotId) {
        TimeSlot slot = timeSlotDAO.findById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Time slot not found");
        }
        return timeSlotDAO.updateStatus(slotId, TimeSlot.STATUS_AVAILABLE);
    }

    public boolean deleteTimeSlot(int slotId) {
        TimeSlot slot = timeSlotDAO.findById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Time slot not found");
        }
        if (slot.isBooked()) {
            throw new IllegalArgumentException("Cannot delete a booked time slot");
        }
        return timeSlotDAO.delete(slotId);
    }

    private void validateTimeSlot(LocalDate lessonDate, LocalTime startTime, LocalTime endTime) {
        if (lessonDate == null) {
            throw new IllegalArgumentException("Lesson date cannot be null");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        if (lessonDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Lesson date cannot be in the past");
        }
    }

    private void checkForOverlap(int teacherId, LocalDate date, LocalTime startTime, 
                                  LocalTime endTime, int excludeSlotId) {
        List<TimeSlot> existingSlots = timeSlotDAO.findByTeacherIdAndDate(teacherId, date);
        
        for (TimeSlot existing : existingSlots) {
            if (existing.getSlotId() == excludeSlotId) {
                continue;
            }
            if (startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime())) {
                throw new IllegalArgumentException("Time slot overlaps with existing slot");
            }
        }
    }
}
