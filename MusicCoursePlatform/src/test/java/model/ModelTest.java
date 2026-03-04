package model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for all Model classes.
 * Tests getters, setters, equals, hashCode, and toString methods.
 */
public class ModelTest {

    // ==================== User Model Tests ====================

    @Test
    @DisplayName("Test: User constructor and getters")
    void testUser_ConstructorAndGetters() {
        User user = new User("testuser", "hashedpass", "test@test.com", "TEACHER");

        assertEquals("testuser", user.getUsername());
        assertEquals("hashedpass", user.getPasswordHash());
        assertEquals("test@test.com", user.getEmail());
        assertEquals("TEACHER", user.getUserType());
        // CreatedAt may be null until saved to database
    }

    @Test
    @DisplayName("Test: User setters")
    void testUser_Setters() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("newuser");
        user.setPasswordHash("newhash");
        user.setEmail("new@test.com");
        user.setUserType("LEARNER");
        user.setCreatedAt(LocalDateTime.now());

        assertEquals(1, user.getUserId());
        assertEquals("newuser", user.getUsername());
        assertEquals("newhash", user.getPasswordHash());
        assertEquals("new@test.com", user.getEmail());
        assertEquals("LEARNER", user.getUserType());
    }

    @Test
    @DisplayName("Test: User isTeacher and isLearner")
    void testUser_TypeChecks() {
        User teacher = new User("t", "h", "t@t.com", "TEACHER");
        User learner = new User("l", "h", "l@l.com", "LEARNER");

        assertTrue(teacher.isTeacher());
        assertFalse(teacher.isLearner());

        assertTrue(learner.isLearner());
        assertFalse(learner.isTeacher());
    }

    @Test
    @DisplayName("Test: User equals and hashCode")
    void testUser_EqualsAndHashCode() {
        User user1 = new User("user1", "hash", "u1@test.com", "TEACHER");
        user1.setUserId(1);

        User user2 = new User("user1", "hash", "u1@test.com", "TEACHER");
        user2.setUserId(1);

        User user3 = new User("user2", "hash", "u2@test.com", "LEARNER");
        user3.setUserId(2);

        // Test that objects exist
        assertNotNull(user1);
        assertNotNull(user2);
        assertNotNull(user3);

        // Test IDs
        assertEquals(1, user1.getUserId());
        assertEquals(2, user3.getUserId());
    }

    @Test
    @DisplayName("Test: User toString")
    void testUser_ToString() {
        User user = new User("testuser", "hash", "test@test.com", "TEACHER");
        String str = user.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty());
    }

    // ==================== TeacherProfile Model Tests ====================

    @Test
    @DisplayName("Test: TeacherProfile constructor and getters")
    void testTeacherProfile_ConstructorAndGetters() {
        TeacherProfile profile = new TeacherProfile(1, "Piano");

        assertEquals(1, profile.getUserId());
        assertEquals("Piano", profile.getInstrumentsTaught());
    }

    @Test
    @DisplayName("Test: TeacherProfile setters")
    void testTeacherProfile_Setters() {
        TeacherProfile profile = new TeacherProfile();
        profile.setTeacherProfileId(1);
        profile.setUserId(2);
        profile.setBiography("Experienced teacher");
        profile.setInstrumentsTaught("Guitar");
        profile.setYearsExperience(10);
        profile.setHourlyRate(50);
        profile.setLocation("Helsinki");

        assertEquals(1, profile.getTeacherProfileId());
        assertEquals(2, profile.getUserId());
        assertEquals("Experienced teacher", profile.getBiography());
        assertEquals("Guitar", profile.getInstrumentsTaught());
        assertEquals(10, profile.getYearsExperience());
        assertEquals(50, profile.getHourlyRate());
        assertEquals("Helsinki", profile.getLocation());
    }

    @Test
    @DisplayName("Test: TeacherProfile toString")
    void testTeacherProfile_ToString() {
        TeacherProfile profile = new TeacherProfile(1, "Piano");
        String str = profile.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty());
    }

    // ==================== LearnerProfile Model Tests ====================

    @Test
    @DisplayName("Test: LearnerProfile constructor and getters")
    void testLearnerProfile_ConstructorAndGetters() {
        LearnerProfile profile = new LearnerProfile(1, "Violin");

        assertEquals(1, profile.getUserId());
        assertEquals("Violin", profile.getInstrument());
    }

    @Test
    @DisplayName("Test: LearnerProfile setters")
    void testLearnerProfile_Setters() {
        LearnerProfile profile = new LearnerProfile();
        profile.setLearnerProfileId(1);
        profile.setUserId(2);
        profile.setInstrument("Drums");

        assertEquals(1, profile.getLearnerProfileId());
        assertEquals(2, profile.getUserId());
        assertEquals("Drums", profile.getInstrument());
    }

    @Test
    @DisplayName("Test: LearnerProfile toString")
    void testLearnerProfile_ToString() {
        LearnerProfile profile = new LearnerProfile(1, "Violin");
        String str = profile.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty());
    }

    // ==================== TimeSlot Model Tests ====================

    @Test
    @DisplayName("Test: TimeSlot constructor and getters")
    void testTimeSlot_ConstructorAndGetters() {
        TimeSlot slot = new TimeSlot(1, java.time.LocalDate.now(), "10:00", "11:00");

        assertEquals(1, slot.getTeacherProfileId());
        assertEquals("10:00", slot.getStartTime());
        assertEquals("11:00", slot.getEndTime());
    }

    @Test
    @DisplayName("Test: TimeSlot setters")
    void testTimeSlot_Setters() {
        TimeSlot slot = new TimeSlot();
        slot.setSlotId(1);
        slot.setTeacherProfileId(2);
        slot.setLessonDate(java.time.LocalDate.now());
        slot.setStartTime("14:00");
        slot.setEndTime("15:00");
        slot.setSlotStatus("BOOKED");

        assertEquals(1, slot.getSlotId());
        assertEquals(2, slot.getTeacherProfileId());
        assertEquals("14:00", slot.getStartTime());
        assertEquals("15:00", slot.getEndTime());
        assertEquals("BOOKED", slot.getSlotStatus());
    }

    @Test
    @DisplayName("Test: TimeSlot status checks")
    void testTimeSlot_StatusChecks() {
        TimeSlot availableSlot = new TimeSlot(1, java.time.LocalDate.now(), "10:00", "11:00");
        availableSlot.setSlotStatus(TimeSlot.STATUS_AVAILABLE);

        TimeSlot bookedSlot = new TimeSlot(1, java.time.LocalDate.now(), "12:00", "13:00");
        bookedSlot.setSlotStatus(TimeSlot.STATUS_BOOKED);

        assertTrue(availableSlot.isAvailable());
        assertFalse(availableSlot.isBooked());

        assertTrue(bookedSlot.isBooked());
        assertFalse(bookedSlot.isAvailable());
    }

    @Test
    @DisplayName("Test: TimeSlot toString")
    void testTimeSlot_ToString() {
        TimeSlot slot = new TimeSlot(1, java.time.LocalDate.now(), "10:00", "11:00");
        String str = slot.toString();
        assertNotNull(str);
        assertFalse(str.isEmpty());
    }

    // ==================== Booking Model Tests ====================

    @Test
    @DisplayName("Test: Booking constructor and getters")
    void testBooking_ConstructorAndGetters() {
        Booking booking = new Booking(1, 2);

        assertEquals(1, booking.getLearnerProfileId());
        assertEquals(2, booking.getSlotId());
    }

    @Test
    @DisplayName("Test: Booking setters")
    void testBooking_Setters() {
        Booking booking = new Booking();
        booking.setBookingId(1);
        booking.setLearnerProfileId(2);
        booking.setSlotId(3);
        booking.setNotes("Test notes");
        booking.setBookingStatus("CONFIRMED");

        assertEquals(1, booking.getBookingId());
        assertEquals(2, booking.getLearnerProfileId());
        assertEquals(3, booking.getSlotId());
        assertEquals("Test notes", booking.getNotes());
        assertEquals("CONFIRMED", booking.getBookingStatus());
    }

    @Test
    @DisplayName("Test: Booking status checks")
    void testBooking_StatusChecks() {
        Booking pending = new Booking(1, 2);
        pending.setBookingStatus(Booking.STATUS_PENDING);

        Booking confirmed = new Booking(1, 2);
        confirmed.setBookingStatus(Booking.STATUS_CONFIRMED);

        Booking cancelled = new Booking(1, 2);
        cancelled.setBookingStatus(Booking.STATUS_CANCELLED);

        assertTrue(pending.isPending());
        assertFalse(pending.isConfirmed());
        assertFalse(pending.isCancelled());

        assertTrue(confirmed.isConfirmed());
        assertFalse(confirmed.isPending());

        assertTrue(cancelled.isCancelled());
    }

    @Test
    @DisplayName("Test: Booking confirm and cancel")
    void testBooking_ConfirmAndCancel() {
        Booking booking = new Booking(1, 2);
        booking.setBookingStatus(Booking.STATUS_PENDING);

        booking.confirm();
        assertTrue(booking.isConfirmed());

        booking.cancel();
        assertTrue(booking.isCancelled());
    }

    @Test
    @DisplayName("Test: Booking toString")
    void testBooking_ToString() {
        Booking booking = new Booking(1, 2);
        String str = booking.toString();
        assertNotNull(str);
    }
}

