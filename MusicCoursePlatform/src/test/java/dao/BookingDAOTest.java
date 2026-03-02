package dao;

import model.Booking;
import model.TimeSlot;
import model.User;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingDAOTest {

    private static BookingDAO bookingDAO;
    private static TimeSlotDAO timeSlotDAO;
    private static UserDAO userDAO;
    private static User testTeacher;
    private static User testLearner;
    private static TimeSlot testSlot;
    private static Booking testBooking;
    private static final String TEST_PREFIX = "test_bk_" + System.currentTimeMillis() + "_";

    @BeforeAll
    static void setUpClass() {
        bookingDAO = new BookingDAO();
        timeSlotDAO = new TimeSlotDAO();
        userDAO = new UserDAO();
    }

    @BeforeEach
    void setUp() {
        testTeacher = new User(
            TEST_PREFIX + "teacher",
            "hashed_password",
            TEST_PREFIX + "teacher@test.com",
            "TEACHER"
        );
        userDAO.create(testTeacher);

        testLearner = new User(
            TEST_PREFIX + "learner",
            "hashed_password",
            TEST_PREFIX + "learner@test.com",
            "LEARNER"
        );
        userDAO.create(testLearner);

        testSlot = new TimeSlot(
            testTeacher.getUserId(),
            LocalDate.now().plusDays(7),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0)
        );
        timeSlotDAO.create(testSlot);

        testBooking = new Booking(testSlot.getSlotId(), testLearner.getUserId(), "Test booking");
    }

    @AfterEach
    void tearDown() {
        if (testBooking != null && testBooking.getBookingId() > 0) {
            bookingDAO.delete(testBooking.getBookingId());
        }
        if (testSlot != null && testSlot.getSlotId() > 0) {
            timeSlotDAO.delete(testSlot.getSlotId());
        }
        if (testTeacher != null && testTeacher.getUserId() > 0) {
            userDAO.delete(testTeacher.getUserId());
        }
        if (testLearner != null && testLearner.getUserId() > 0) {
            userDAO.delete(testLearner.getUserId());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Create booking successfully")
    void testCreate_Success() {
        boolean result = bookingDAO.create(testBooking);

        assertTrue(result);
        assertTrue(testBooking.getBookingId() > 0);
        assertEquals(Booking.STATUS_PENDING, testBooking.getStatus());
    }

    @Test
    @Order(2)
    @DisplayName("Find booking by ID")
    void testFindById() {
        bookingDAO.create(testBooking);

        Booking found = bookingDAO.findById(testBooking.getBookingId());

        assertNotNull(found);
        assertEquals(testSlot.getSlotId(), found.getSlotId());
        assertEquals(testLearner.getUserId(), found.getLearnerId());
    }

    @Test
    @Order(3)
    @DisplayName("Find booking by slot ID")
    void testFindBySlotId() {
        bookingDAO.create(testBooking);

        Booking found = bookingDAO.findBySlotId(testSlot.getSlotId());

        assertNotNull(found);
        assertEquals(testBooking.getBookingId(), found.getBookingId());
    }

    @Test
    @Order(4)
    @DisplayName("Find bookings by learner ID")
    void testFindByLearnerId() {
        bookingDAO.create(testBooking);

        List<Booking> bookings = bookingDAO.findByLearnerId(testLearner.getUserId());

        assertNotNull(bookings);
        assertTrue(bookings.stream().anyMatch(b -> b.getBookingId() == testBooking.getBookingId()));
    }

    @Test
    @Order(5)
    @DisplayName("Find active bookings by learner ID")
    void testFindActiveByLearnerId() {
        bookingDAO.create(testBooking);

        List<Booking> bookings = bookingDAO.findActiveByLearnerId(testLearner.getUserId());

        assertNotNull(bookings);
        assertTrue(bookings.stream().anyMatch(b -> b.getBookingId() == testBooking.getBookingId()));
    }

    @Test
    @Order(6)
    @DisplayName("Find bookings by status")
    void testFindByStatus() {
        bookingDAO.create(testBooking);

        List<Booking> bookings = bookingDAO.findByStatus(Booking.STATUS_PENDING);

        assertNotNull(bookings);
        assertTrue(bookings.stream().anyMatch(b -> b.getBookingId() == testBooking.getBookingId()));
    }

    @Test
    @Order(7)
    @DisplayName("Update booking successfully")
    void testUpdate() {
        bookingDAO.create(testBooking);

        testBooking.setNotes("Updated notes");
        testBooking.setStatus(Booking.STATUS_CONFIRMED);

        boolean result = bookingDAO.update(testBooking);

        assertTrue(result);

        Booking updated = bookingDAO.findById(testBooking.getBookingId());
        assertEquals("Updated notes", updated.getNotes());
        assertEquals(Booking.STATUS_CONFIRMED, updated.getStatus());
    }

    @Test
    @Order(8)
    @DisplayName("Update booking status")
    void testUpdateStatus() {
        bookingDAO.create(testBooking);

        boolean result = bookingDAO.updateStatus(testBooking.getBookingId(), Booking.STATUS_CONFIRMED);

        assertTrue(result);

        Booking updated = bookingDAO.findById(testBooking.getBookingId());
        assertEquals(Booking.STATUS_CONFIRMED, updated.getStatus());
    }

    @Test
    @Order(9)
    @DisplayName("Delete booking successfully")
    void testDelete() {
        bookingDAO.create(testBooking);
        int bookingId = testBooking.getBookingId();

        boolean result = bookingDAO.delete(bookingId);

        assertTrue(result);
        assertNull(bookingDAO.findById(bookingId));

        testBooking.setBookingId(0);
    }

    @Test
    @Order(10)
    @DisplayName("Find non-existent booking returns null")
    void testFindById_NotFound() {
        Booking found = bookingDAO.findById(999999);
        assertNull(found);
    }

    @Test
    @Order(11)
    @DisplayName("Cancelled booking not returned by findBySlotId")
    void testFindBySlotId_ExcludesCancelled() {
        bookingDAO.create(testBooking);
        bookingDAO.updateStatus(testBooking.getBookingId(), Booking.STATUS_CANCELLED);

        Booking found = bookingDAO.findBySlotId(testSlot.getSlotId());

        assertNull(found);
    }
}
