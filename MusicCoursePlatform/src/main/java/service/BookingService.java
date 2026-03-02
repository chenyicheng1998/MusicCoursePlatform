package service;

import dao.BookingDAO;
import dao.TimeSlotDAO;
import dao.UserDAO;
import model.Booking;
import model.TimeSlot;
import model.User;

import java.util.List;

public class BookingService {

    private final BookingDAO bookingDAO;
    private final TimeSlotDAO timeSlotDAO;
    private final UserDAO userDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.timeSlotDAO = new TimeSlotDAO();
        this.userDAO = new UserDAO();
    }

    public BookingService(BookingDAO bookingDAO, TimeSlotDAO timeSlotDAO, UserDAO userDAO) {
        this.bookingDAO = bookingDAO;
        this.timeSlotDAO = timeSlotDAO;
        this.userDAO = userDAO;
    }

    public Booking createBooking(int slotId, int learnerId, String notes) {
        User learner = userDAO.findById(learnerId);
        if (learner == null) {
            throw new IllegalArgumentException("Learner not found");
        }
        if (!learner.isLearner()) {
            throw new IllegalArgumentException("User is not a learner");
        }

        TimeSlot slot = timeSlotDAO.findById(slotId);
        if (slot == null) {
            throw new IllegalArgumentException("Time slot not found");
        }
        if (!slot.isAvailable()) {
            throw new IllegalArgumentException("Time slot is not available");
        }

        Booking existingBooking = bookingDAO.findBySlotId(slotId);
        if (existingBooking != null) {
            throw new IllegalArgumentException("Time slot is already booked");
        }

        Booking booking = new Booking(slotId, learnerId, notes);
        
        boolean success = bookingDAO.create(booking);
        if (!success) {
            throw new RuntimeException("Failed to create booking");
        }

        timeSlotDAO.updateStatus(slotId, TimeSlot.STATUS_BOOKED);

        return booking;
    }

    public Booking confirmBooking(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }
        if (!booking.isPending()) {
            throw new IllegalArgumentException("Booking is not in pending status");
        }

        booking.confirm();
        boolean success = bookingDAO.updateStatus(bookingId, Booking.STATUS_CONFIRMED);
        if (!success) {
            throw new RuntimeException("Failed to confirm booking");
        }

        return booking;
    }

    public Booking cancelBooking(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }
        if (booking.isCancelled()) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        booking.cancel();
        boolean success = bookingDAO.updateStatus(bookingId, Booking.STATUS_CANCELLED);
        if (!success) {
            throw new RuntimeException("Failed to cancel booking");
        }

        timeSlotDAO.updateStatus(booking.getSlotId(), TimeSlot.STATUS_AVAILABLE);

        return booking;
    }

    public Booking getBookingById(int bookingId) {
        return bookingDAO.findById(bookingId);
    }

    public Booking getBookingBySlotId(int slotId) {
        return bookingDAO.findBySlotId(slotId);
    }

    public List<Booking> getBookingsByLearner(int learnerId) {
        return bookingDAO.findByLearnerId(learnerId);
    }

    public List<Booking> getActiveBookingsByLearner(int learnerId) {
        return bookingDAO.findActiveByLearnerId(learnerId);
    }

    public List<Booking> getBookingsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        return bookingDAO.findByStatus(status);
    }

    public List<Booking> getPendingBookings() {
        return bookingDAO.findByStatus(Booking.STATUS_PENDING);
    }

    public List<Booking> getAllBookings() {
        return bookingDAO.findAll();
    }

    public boolean deleteBooking(int bookingId) {
        Booking booking = bookingDAO.findById(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        if (booking.isActive()) {
            timeSlotDAO.updateStatus(booking.getSlotId(), TimeSlot.STATUS_AVAILABLE);
        }

        return bookingDAO.delete(bookingId);
    }
}
