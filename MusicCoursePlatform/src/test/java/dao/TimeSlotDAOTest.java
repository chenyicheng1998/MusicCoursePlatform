package dao;

import model.TimeSlot;
import model.User;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TimeSlotDAOTest {

    private static TimeSlotDAO timeSlotDAO;
    private static UserDAO userDAO;
    private static User testTeacher;
    private static TimeSlot testSlot;
    private static final String TEST_PREFIX = "test_ts_" + System.currentTimeMillis() + "_";

    @BeforeAll
    static void setUpClass() {
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

        testSlot = new TimeSlot(
            testTeacher.getUserId(),
            LocalDate.now().plusDays(7),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0)
        );
    }

    @AfterEach
    void tearDown() {
        if (testSlot != null && testSlot.getSlotId() > 0) {
            timeSlotDAO.delete(testSlot.getSlotId());
        }
        if (testTeacher != null && testTeacher.getUserId() > 0) {
            userDAO.delete(testTeacher.getUserId());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Create time slot successfully")
    void testCreate_Success() {
        boolean result = timeSlotDAO.create(testSlot);

        assertTrue(result);
        assertTrue(testSlot.getSlotId() > 0);
        assertEquals(TimeSlot.STATUS_AVAILABLE, testSlot.getStatus());
    }

    @Test
    @Order(2)
    @DisplayName("Find time slot by ID")
    void testFindById() {
        timeSlotDAO.create(testSlot);

        TimeSlot found = timeSlotDAO.findById(testSlot.getSlotId());

        assertNotNull(found);
        assertEquals(testTeacher.getUserId(), found.getTeacherId());
        assertEquals(LocalTime.of(10, 0), found.getStartTime());
    }

    @Test
    @Order(3)
    @DisplayName("Find time slots by teacher ID")
    void testFindByTeacherId() {
        timeSlotDAO.create(testSlot);

        List<TimeSlot> slots = timeSlotDAO.findByTeacherId(testTeacher.getUserId());

        assertNotNull(slots);
        assertTrue(slots.stream().anyMatch(s -> s.getSlotId() == testSlot.getSlotId()));
    }

    @Test
    @Order(4)
    @DisplayName("Find time slots by teacher and date")
    void testFindByTeacherIdAndDate() {
        timeSlotDAO.create(testSlot);

        List<TimeSlot> slots = timeSlotDAO.findByTeacherIdAndDate(
            testTeacher.getUserId(), 
            testSlot.getLessonDate()
        );

        assertNotNull(slots);
        assertFalse(slots.isEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("Find available time slots by teacher")
    void testFindAvailableByTeacherId() {
        timeSlotDAO.create(testSlot);

        List<TimeSlot> slots = timeSlotDAO.findAvailableByTeacherId(testTeacher.getUserId());

        assertNotNull(slots);
        assertTrue(slots.stream().anyMatch(s -> s.getSlotId() == testSlot.getSlotId()));
    }

    @Test
    @Order(6)
    @DisplayName("Find available time slots by date")
    void testFindAvailableByDate() {
        timeSlotDAO.create(testSlot);

        List<TimeSlot> slots = timeSlotDAO.findAvailableByDate(testSlot.getLessonDate());

        assertNotNull(slots);
        assertTrue(slots.stream().anyMatch(s -> s.getSlotId() == testSlot.getSlotId()));
    }

    @Test
    @Order(7)
    @DisplayName("Update time slot successfully")
    void testUpdate() {
        timeSlotDAO.create(testSlot);

        testSlot.setStartTime(LocalTime.of(14, 0));
        testSlot.setEndTime(LocalTime.of(15, 0));

        boolean result = timeSlotDAO.update(testSlot);

        assertTrue(result);

        TimeSlot updated = timeSlotDAO.findById(testSlot.getSlotId());
        assertEquals(LocalTime.of(14, 0), updated.getStartTime());
        assertEquals(LocalTime.of(15, 0), updated.getEndTime());
    }

    @Test
    @Order(8)
    @DisplayName("Update time slot status")
    void testUpdateStatus() {
        timeSlotDAO.create(testSlot);

        boolean result = timeSlotDAO.updateStatus(testSlot.getSlotId(), TimeSlot.STATUS_BOOKED);

        assertTrue(result);

        TimeSlot updated = timeSlotDAO.findById(testSlot.getSlotId());
        assertEquals(TimeSlot.STATUS_BOOKED, updated.getStatus());
    }

    @Test
    @Order(9)
    @DisplayName("Delete time slot successfully")
    void testDelete() {
        timeSlotDAO.create(testSlot);
        int slotId = testSlot.getSlotId();

        boolean result = timeSlotDAO.delete(slotId);

        assertTrue(result);
        assertNull(timeSlotDAO.findById(slotId));

        testSlot.setSlotId(0);
    }

    @Test
    @Order(10)
    @DisplayName("Find non-existent time slot returns null")
    void testFindById_NotFound() {
        TimeSlot found = timeSlotDAO.findById(999999);
        assertNull(found);
    }
}
