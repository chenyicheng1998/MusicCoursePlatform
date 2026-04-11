package service;

import dao.BookingDAO;
import dao.LearnerProfileDAO;
import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import dao.UserDAO;
import model.Booking;
import model.LearnerProfile;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookingService class.
 * Tests booking creation, cancellation, and management logic.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingServiceTest {

    private static BookingService bookingService;
    private static BookingDAO bookingDAO;
    private static TimeSlotDAO timeSlotDAO;
    private static LearnerProfileDAO learnerProfileDAO;
    private static TeacherProfileDAO teacherProfileDAO;
    private static UserDAO userDAO;

    private static User testLearner;
    private static User testTeacher;
    private static LearnerProfile testLearnerProfile;
    private static TeacherProfile testTeacherProfile;
    private static TimeSlot testTimeSlot;

    @BeforeAll
    static void setUpClass() {
        userDAO = new UserDAO();
        bookingDAO = new BookingDAO();
        timeSlotDAO = new TimeSlotDAO();
        learnerProfileDAO = new LearnerProfileDAO();
        teacherProfileDAO = new TeacherProfileDAO();
        bookingService = new BookingService(bookingDAO, timeSlotDAO, learnerProfileDAO);

        // Create test learner
        testLearner = new User("booktest" + System.currentTimeMillis(), "hash",
                "test" + System.currentTimeMillis() + "@test.com", "LEARNER");
        userDAO.create(testLearner);

        // Create test teacher
        testTeacher = new User("teachtest" + System.currentTimeMillis(), "hash",
                "teach" + System.currentTimeMillis() + "@test.com", "TEACHER");
        userDAO.create(testTeacher);

        // Create learner profile
        testLearnerProfile = new LearnerProfile(testLearner.getUserId(), "Piano");
        learnerProfileDAO.create(testLearnerProfile);

        // Create teacher profile
        testTeacherProfile = new TeacherProfile(testTeacher.getUserId(), "Piano");
        teacherProfileDAO.create(testTeacherProfile);
    }

    @AfterAll
    static void tearDownClass() {
        // Cleanup
        if (testLearnerProfile != null) {
            learnerProfileDAO.delete(testLearnerProfile.getLearnerProfileId());
        }
        if (testTeacherProfile != null) {
            teacherProfileDAO.delete(testTeacherProfile.getTeacherProfileId());
        }
        if (testLearner != null) {
            userDAO.delete(testLearner.getUserId());
        }
        if (testTeacher != null) {
            userDAO.delete(testTeacher.getUserId());
        }
    }

    @BeforeEach
    void setUp() {
        // Create a fresh time slot for each test (using TEACHER profile ID)
        testTimeSlot = new TimeSlot(testTeacherProfile.getTeacherProfileId(),
                LocalDate.now().plusDays(1),
                "10:00", "11:00");
        timeSlotDAO.create(testTimeSlot);
    }

    @AfterEach
    void tearDown() {
        // Clean up bookings and time slots after each test
        List<Booking> bookings = bookingDAO.findAll();
        for (Booking b : bookings) {
            if (b.getLearnerProfileId() == testLearnerProfile.getLearnerProfileId()) {
                bookingDAO.delete(b.getBookingId());
            }
        }

        if (testTimeSlot != null && testTimeSlot.getSlotId() > 0) {
            timeSlotDAO.delete(testTimeSlot.getSlotId());
        }
    }

    // ==================== Create Booking Tests ====================

    @Test
    @Order(1)
    @DisplayName("Test: Create booking successfully")
    void testCreateBooking_Success() {
        Booking booking = bookingService.createBooking(
                testTimeSlot.getSlotId(),
                testLearnerProfile.getLearnerProfileId(),
                "Test booking");

        assertNotNull(booking);
        assertTrue(booking.getBookingId() > 0);
        assertEquals(testTimeSlot.getSlotId(), booking.getSlotId());
        assertEquals(testLearnerProfile.getLearnerProfileId(), booking.getLearnerProfileId());
    }

    @Test
    @Order(2)
    @DisplayName("Test: Create booking with invalid learner profile")
    void testCreateBooking_InvalidLearnerProfile() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(testTimeSlot.getSlotId(), 99999, "Test");
        });
    }

    @Test
    @Order(3)
    @DisplayName("Test: Create booking with invalid time slot")
    void testCreateBooking_InvalidTimeSlot() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(99999, testLearnerProfile.getLearnerProfileId(), "Test");
        });
    }

    @Test
    @Order(4)
    @DisplayName("Test: Create booking for already booked slot")
    void testCreateBooking_AlreadyBooked() {
        // First booking succeeds
        bookingService.createBooking(
                testTimeSlot.getSlotId(),
                testLearnerProfile.getLearnerProfileId(),
                "First booking");

        // Second booking should fail
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(
                    testTimeSlot.getSlotId(),
                    testLearnerProfile.getLearnerProfileId(),
                    "Second booking");
        });
    }

    // ==================== Confirm Booking Tests ====================

    @Test
    @Order(5)
    @DisplayName("Test: Confirm booking successfully")
    void testConfirmBooking_Success() {
        Booking booking = bookingService.createBooking(
                testTimeSlot.getSlotId(),
                testLearnerProfile.getLearnerProfileId(),
                "Test");

        Booking confirmed = bookingService.confirmBooking(booking.getBookingId());

        assertNotNull(confirmed);
        assertTrue(confirmed.isConfirmed());
    }

    @Test
    @Order(6)
    @DisplayName("Test: Confirm non-existent booking")
    void testConfirmBooking_NotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.confirmBooking(99999);
        });
    }

    // ==================== Cancel Booking Tests ====================

    @Test
    @Order(7)
    @DisplayName("Test: Cancel booking successfully")
    void testCancelBooking_Success() {
        Booking booking = bookingService.createBooking(
                testTimeSlot.getSlotId(),
                testLearnerProfile.getLearnerProfileId(),
                "Test");

        Booking cancelled = bookingService.cancelBooking(booking.getBookingId());

        assertNotNull(cancelled);
        assertTrue(cancelled.isCancelled());
    }

    @Test
    @Order(8)
    @DisplayName("Test: Cancel non-existent booking")
    void testCancelBooking_NotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.cancelBooking(99999);
        });
    }

    @Test
    @Order(9)
    @DisplayName("Test: Cancel already cancelled booking")
    void testCancelBooking_AlreadyCancelled() {
        Booking booking = bookingService.createBooking(
                testTimeSlot.getSlotId(),
                testLearnerProfile.getLearnerProfileId(),
                "Test");

        bookingService.cancelBooking(booking.getBookingId());

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.cancelBooking(booking.getBookingId());
        });
    }

    // ==================== Get Booking Tests ====================

    @Test
    @Order(10)
    @DisplayName("Test: Get booking by ID")
    void testGetBookingById() {
        Booking booking = bookingService.createBooking(
                testTimeSlot.getSlotId(),
                testLearnerProfile.getLearnerProfileId(),
                "Test");

        Booking found = bookingService.getBookingById(booking.getBookingId());

        assertNotNull(found);
        assertEquals(booking.getBookingId(), found.getBookingId());
    }

    @Test
    @Order(11)
    @DisplayName("Test: Get booking by slot ID")
    void testGetBookingBySlotId() {
        Booking booking = bookingService.createBooking(
                testTimeSlot.getSlotId(),
                testLearnerProfile.getLearnerProfileId(),
                "Test");

        Booking found = bookingService.getBookingBySlotId(testTimeSlot.getSlotId());

        assertNotNull(found);
        assertEquals(booking.getSlotId(), found.getSlotId());
    }

    @Test
    @Order(12)
    @DisplayName("Test: Get bookings by learner profile")
    void testGetBookingsByLearnerProfile() {
        bookingService.createBooking(
                testTimeSlot.getSlotId(),
                testLearnerProfile.getLearnerProfileId(),
                "Test");

        List<Booking> bookings = bookingService.getBookingsByLearnerProfile(
                testLearnerProfile.getLearnerProfileId());

        assertNotNull(bookings);
        assertFalse(bookings.isEmpty());
    }

    @Test
    @Order(13)
    @DisplayName("Test: Get bookings by status")
    void testGetBookingsByStatus() {
        bookingService.createBooking(
                testTimeSlot.getSlotId(),
                testLearnerProfile.getLearnerProfileId(),
                "Test");

        List<Booking> bookings = bookingService.getBookingsByStatus(Booking.STATUS_CONFIRMED);

        assertNotNull(bookings);
    }

    @Test
    @Order(14)
    @DisplayName("Test: Get bookings by status with empty status")
    void testGetBookingsByStatus_EmptyStatus() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getBookingsByStatus("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getBookingsByStatus(null);
        });
    }

    @Test
    @Order(15)
    @DisplayName("Test: Get pending bookings")
    void testGetPendingBookings() {
        List<Booking> bookings = bookingService.getPendingBookings();
        assertNotNull(bookings);
    }

    @Test
    @Order(16)
    @DisplayName("Test: Get all bookings")
    void testGetAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        assertNotNull(bookings);
    }

    // ==================== Delete Booking Tests ====================

    @Test
    @Order(17)
    @DisplayName("Test: Delete booking successfully")
    void testDeleteBooking_Success() {
        Booking booking = bookingService.createBooking(
                testTimeSlot.getSlotId(),
                testLearnerProfile.getLearnerProfileId(),
                "Test");

        boolean deleted = bookingService.deleteBooking(booking.getBookingId());

        assertTrue(deleted);
        assertNull(bookingService.getBookingById(booking.getBookingId()));
    }

    @Test
    @Order(18)
    @DisplayName("Test: Delete non-existent booking")
    void testDeleteBooking_NotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.deleteBooking(99999);
        });
    }
}
