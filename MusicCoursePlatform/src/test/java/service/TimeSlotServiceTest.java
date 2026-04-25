package service;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import dao.UserDAO;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TimeSlotService class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TimeSlotServiceTest {

    private static TimeSlotService timeSlotService;
    private static TimeSlotDAO timeSlotDAO;
    private static TeacherProfileDAO teacherProfileDAO;
    private static UserDAO userDAO;
    private static User testTeacher;
    private static TeacherProfile testProfile;

    @BeforeAll
    static void setUpClass() {
        userDAO = new UserDAO();
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        timeSlotService = new TimeSlotService(timeSlotDAO, teacherProfileDAO);

        // Create test teacher
        testTeacher = new User("tsstest" + System.currentTimeMillis(), "hash",
                "tss@test.com", "TEACHER");
        userDAO.create(testTeacher);

        testProfile = new TeacherProfile(testTeacher.getUserId(), "Piano");
        teacherProfileDAO.create(testProfile);
    }

    @AfterAll
    static void tearDownClass() {
        // Cleanup
        if (testProfile != null) {
            List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileId(testProfile.getTeacherProfileId());
            for (TimeSlot slot : slots) {
                if (!slot.isBooked()) {
                    timeSlotDAO.delete(slot.getSlotId());
                }
            }
            teacherProfileDAO.delete(testProfile.getTeacherProfileId());
        }
        if (testTeacher != null) {
            userDAO.delete(testTeacher.getUserId());
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test: Create time slot successfully")
    void testCreateTimeSlot_Success() {
        TimeSlot slot = timeSlotService.createTimeSlot(
                testProfile.getTeacherProfileId(),
                LocalDate.now().plusDays(1),
                "10:00",
                "11:00");

        assertNotNull(slot);
        assertTrue(slot.getSlotId() > 0);

        // Cleanup
        timeSlotDAO.delete(slot.getSlotId());
    }

    @Test
    @Order(2)
    @DisplayName("Test: Create time slot for non-existent teacher")
    void testCreateTimeSlot_TeacherNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            timeSlotService.createTimeSlot(99999, LocalDate.now().plusDays(1),
                    "10:00", "11:00");
        });
    }

    @Test
    @Order(3)
    @DisplayName("Test: Create time slot with null date")
    void testCreateTimeSlot_NullDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            timeSlotService.createTimeSlot(testProfile.getTeacherProfileId(),
                    null, "10:00", "11:00");
        });
    }

    @Test
    @Order(4)
    @DisplayName("Test: Create time slot with empty time")
    void testCreateTimeSlot_EmptyTime() {
        assertThrows(IllegalArgumentException.class, () -> {
            timeSlotService.createTimeSlot(testProfile.getTeacherProfileId(),
                    LocalDate.now().plusDays(1), "", "11:00");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            timeSlotService.createTimeSlot(testProfile.getTeacherProfileId(),
                    LocalDate.now().plusDays(1), "10:00", null);
        });
    }

    @Test
    @Order(5)
    @DisplayName("Test: Update time slot successfully")
    void testUpdateTimeSlot_Success() {
        TimeSlot slot = timeSlotService.createTimeSlot(
                testProfile.getTeacherProfileId(),
                LocalDate.now().plusDays(2),
                "14:00",
                "15:00");

        TimeSlot updated = timeSlotService.updateTimeSlot(
                slot.getSlotId(),
                LocalDate.now().plusDays(2),
                "15:00",
                "16:00");

        assertNotNull(updated);
        assertEquals("15:00", updated.getStartTime());

        timeSlotDAO.delete(slot.getSlotId());
    }

    @Test
    @Order(6)
    @DisplayName("Test: Update non-existent time slot")
    void testUpdateTimeSlot_NotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            timeSlotService.updateTimeSlot(99999, LocalDate.now().plusDays(1),
                    "10:00", "11:00");
        });
    }

    @Test
    @Order(7)
    @DisplayName("Test: Get time slot by ID")
    void testGetTimeSlotById() {
        TimeSlot slot = timeSlotService.createTimeSlot(
                testProfile.getTeacherProfileId(),
                LocalDate.now().plusDays(3),
                "09:00",
                "10:00");

        TimeSlot found = timeSlotService.getTimeSlotById(slot.getSlotId());
        assertNotNull(found);

        timeSlotDAO.delete(slot.getSlotId());
    }

    @Test
    @Order(8)
    @DisplayName("Test: Get time slots by teacher profile")
    void testGetTimeSlotsByTeacherProfile() {
        List<TimeSlot> slots = timeSlotService.getTimeSlotsByTeacherProfile(
                testProfile.getTeacherProfileId());
        assertNotNull(slots);
    }

    @Test
    @Order(9)
    @DisplayName("Test: Get time slots by teacher and date")
    void testGetTimeSlotsByTeacherProfileAndDate() {
        LocalDate date = LocalDate.now().plusDays(5);
        timeSlotService.createTimeSlot(testProfile.getTeacherProfileId(),
                date, "11:00", "12:00");

        List<TimeSlot> slots = timeSlotService.getTimeSlotsByTeacherProfileAndDate(
                testProfile.getTeacherProfileId(), date);

        assertNotNull(slots);
        assertFalse(slots.isEmpty());

        // Cleanup
        for (TimeSlot slot : slots) {
            if (!slot.isBooked()) {
                timeSlotDAO.delete(slot.getSlotId());
            }
        }
    }

    @Test
    @Order(10)
    @DisplayName("Test: Get available slots by teacher profile")
    void testGetAvailableSlotsByTeacherProfile() {
        List<TimeSlot> slots = timeSlotService.getAvailableSlotsByTeacherProfile(
                testProfile.getTeacherProfileId());
        assertNotNull(slots);
    }

    @Test
    @Order(11)
    @DisplayName("Test: Get available slots by date")
    void testGetAvailableSlotsByDate() {
        List<TimeSlot> slots = timeSlotService.getAvailableSlotsByDate(
                LocalDate.now().plusDays(1));
        assertNotNull(slots);
    }

    @Test
    @Order(12)
    @DisplayName("Test: Get available slots with null date")
    void testGetAvailableSlotsByDate_NullDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            timeSlotService.getAvailableSlotsByDate(null);
        });
    }

    @Test
    @Order(13)
    @DisplayName("Test: Get all time slots")
    void testGetAllTimeSlots() {
        List<TimeSlot> slots = timeSlotService.getAllTimeSlots();
        assertNotNull(slots);
    }

    @Test
    @Order(14)
    @DisplayName("Test: Mark as booked")
    void testMarkAsBooked() {
        TimeSlot slot = timeSlotService.createTimeSlot(
                testProfile.getTeacherProfileId(),
                LocalDate.now().plusDays(7),
                "13:00",
                "14:00");

        boolean marked = timeSlotService.markAsBooked(slot.getSlotId());
        assertTrue(marked);

        // Cleanup
        timeSlotDAO.updateStatus(slot.getSlotId(), TimeSlot.STATUS_AVAILABLE);
        timeSlotDAO.delete(slot.getSlotId());
    }

    @Test
    @Order(15)
    @DisplayName("Test: Mark as available")
    void testMarkAsAvailable() {
        TimeSlot slot = timeSlotService.createTimeSlot(
                testProfile.getTeacherProfileId(),
                LocalDate.now().plusDays(8),
                "16:00",
                "17:00");

        timeSlotDAO.updateStatus(slot.getSlotId(), TimeSlot.STATUS_BOOKED);
        boolean marked = timeSlotService.markAsAvailable(slot.getSlotId());
        assertTrue(marked);

        timeSlotDAO.delete(slot.getSlotId());
    }

    @Test
    @Order(16)
    @DisplayName("Test: Delete time slot")
    void testDeleteTimeSlot() {
        TimeSlot slot = timeSlotService.createTimeSlot(
                testProfile.getTeacherProfileId(),
                LocalDate.now().plusDays(10),
                "18:00",
                "19:00");

        boolean deleted = timeSlotService.deleteTimeSlot(slot.getSlotId());
        assertTrue(deleted);
    }

    @Test
    @Order(17)
    @DisplayName("Test: Delete non-existent time slot")
    void testDeleteTimeSlot_NotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            timeSlotService.deleteTimeSlot(99999);
        });
    }

    @Test
    @Order(18)
    @DisplayName("Test: Create time slot with past date")
    void testCreateTimeSlot_PastDate() {
        assertThrows(IllegalArgumentException.class, () -> {
            timeSlotService.createTimeSlot(
                    testProfile.getTeacherProfileId(),
                    LocalDate.now().minusDays(1),
                    "10:00",
                    "11:00");
        });
    }

    @Test
    @Order(19)
    @DisplayName("Test: Create overlapping time slot throws exception")
    void testCreateTimeSlot_OverlapThrows() {
        LocalDate date = LocalDate.now().plusDays(20);
        TimeSlot existing = timeSlotService.createTimeSlot(
                testProfile.getTeacherProfileId(), date, "10:00", "11:00");

        try {
            assertThrows(IllegalArgumentException.class, () -> {
                timeSlotService.createTimeSlot(
                        testProfile.getTeacherProfileId(), date, "10:30", "11:30");
            });
        } finally {
            timeSlotDAO.delete(existing.getSlotId());
        }
    }

    @Test
    @Order(20)
    @DisplayName("Test: Update booked time slot throws exception")
    void testUpdateTimeSlot_BookedSlot() {
        LocalDate date = LocalDate.now().plusDays(21);
        TimeSlot slot = timeSlotService.createTimeSlot(
                testProfile.getTeacherProfileId(), date, "10:00", "11:00");
        timeSlotDAO.updateStatus(slot.getSlotId(), TimeSlot.STATUS_BOOKED);

        try {
            assertThrows(IllegalArgumentException.class, () -> {
                timeSlotService.updateTimeSlot(slot.getSlotId(), date, "11:00", "12:00");
            });
        } finally {
            timeSlotDAO.updateStatus(slot.getSlotId(), TimeSlot.STATUS_AVAILABLE);
            timeSlotDAO.delete(slot.getSlotId());
        }
    }

    @Test
    @Order(21)
    @DisplayName("Test: Mark as booked - slot not found")
    void testMarkAsBooked_SlotNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            timeSlotService.markAsBooked(99999);
        });
    }

    @Test
    @Order(22)
    @DisplayName("Test: Mark as booked - already booked")
    void testMarkAsBooked_AlreadyBooked() {
        LocalDate date = LocalDate.now().plusDays(22);
        TimeSlot slot = timeSlotService.createTimeSlot(
                testProfile.getTeacherProfileId(), date, "07:00", "08:00");
        timeSlotDAO.updateStatus(slot.getSlotId(), TimeSlot.STATUS_BOOKED);

        try {
            assertThrows(IllegalArgumentException.class, () -> {
                timeSlotService.markAsBooked(slot.getSlotId());
            });
        } finally {
            timeSlotDAO.updateStatus(slot.getSlotId(), TimeSlot.STATUS_AVAILABLE);
            timeSlotDAO.delete(slot.getSlotId());
        }
    }

    @Test
    @Order(23)
    @DisplayName("Test: Mark as available - slot not found")
    void testMarkAsAvailable_SlotNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            timeSlotService.markAsAvailable(99999);
        });
    }

    @Test
    @Order(24)
    @DisplayName("Test: Delete booked time slot throws exception")
    void testDeleteTimeSlot_BookedSlot() {
        LocalDate date = LocalDate.now().plusDays(23);
        TimeSlot slot = timeSlotService.createTimeSlot(
                testProfile.getTeacherProfileId(), date, "06:00", "07:00");
        timeSlotDAO.updateStatus(slot.getSlotId(), TimeSlot.STATUS_BOOKED);

        try {
            assertThrows(IllegalArgumentException.class, () -> {
                timeSlotService.deleteTimeSlot(slot.getSlotId());
            });
        } finally {
            timeSlotDAO.updateStatus(slot.getSlotId(), TimeSlot.STATUS_AVAILABLE);
            timeSlotDAO.delete(slot.getSlotId());
        }
    }

    @Test
    @Order(25)
    @DisplayName("Test: Default constructor creates service successfully")
    void testDefaultConstructor() {
        TimeSlotService service = new TimeSlotService();
        assertNotNull(service);
        // Verify it works by calling a simple method
        assertNotNull(service.getAllTimeSlots());
    }
}
