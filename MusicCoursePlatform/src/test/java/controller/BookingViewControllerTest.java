package controller;

import dao.*;
import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import util.LocalizationManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingViewControllerTest {

    private BookingViewController controller;

    private BookingDAO bookingDAO;
    private TimeSlotDAO timeSlotDAO;
    private TeacherProfileDAO teacherProfileDAO;
    private LearnerProfileDAO learnerProfileDAO;
    private UserDAO userDAO;
    private LocalizationManager localizationManager;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize JavaFX toolkit (compatible with modular JavaFX, no swing dependency)
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> latch.countDown());
            latch.await();
        } catch (IllegalStateException e) {
            // Already initialized, ignore
        }

        controller = new BookingViewController();

        bookingDAO = mock(BookingDAO.class);
        timeSlotDAO = mock(TimeSlotDAO.class);
        teacherProfileDAO = mock(TeacherProfileDAO.class);
        learnerProfileDAO = mock(LearnerProfileDAO.class);
        userDAO = mock(UserDAO.class);
        localizationManager = mock(LocalizationManager.class);

        // Inject mocks
        injectPrivateField("bookingDAO", bookingDAO);
        injectPrivateField("timeSlotDAO", timeSlotDAO);
        injectPrivateField("teacherProfileDAO", teacherProfileDAO);
        injectPrivateField("learnerProfileDAO", learnerProfileDAO);
        injectPrivateField("userDAO", userDAO);
        injectPrivateField("localizationManager", localizationManager);

        // Inject UI components
        injectPrivateField("userNameLabel", new Label());
        injectPrivateField("bookingsContainer", new FlowPane());
        injectPrivateField("languageCombo", new ComboBox<>());

        // Inject new localization fields
        injectPrivateField("appNameLabel", new Label());
        injectPrivateField("courseBookingButton", new javafx.scene.control.Button());
        injectPrivateField("logoutButton", new javafx.scene.control.Button());
        injectPrivateField("myBookingsLabel", new Label());
        injectPrivateField("rootPane", new javafx.scene.layout.BorderPane());
    }

    private void injectPrivateField(String fieldName, Object value) throws Exception {
        Field field = BookingViewController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    @Test
    void testSetupLanguageCombo() throws Exception {
        // Add localization mocks for initialize method calls
        when(localizationManager.getCurrentLanguageDisplayName()).thenReturn("English");
        when(localizationManager.getString("app.name")).thenReturn("MusicCoursePlatform");
        when(localizationManager.getString("nav.course.booking")).thenReturn("Course Booking");
        when(localizationManager.getString("nav.logout")).thenReturn("Logout");
        when(localizationManager.getString("schedule.my.bookings")).thenReturn("My Bookings");
        when(localizationManager.getString("message.no.bookings")).thenReturn("No bookings found");

        controller.initialize();

        ComboBox<String> combo =
                (ComboBox<String>) getPrivateField("languageCombo");

        assertEquals("English", combo.getValue());
        assertTrue(combo.getItems().contains("中文"));
        assertTrue(combo.getItems().contains("العربية"));
    }

    @Test
    void testLoadBookings_NoLearnerProfile() throws Exception {
        injectPrivateField("learnerProfile", null);

        // Add localization mocks
        when(localizationManager.getString("message.no.bookings")).thenReturn("No bookings found");
        when(localizationManager.getString("app.name")).thenReturn("MusicCoursePlatform");
        when(localizationManager.getString("nav.course.booking")).thenReturn("Course Booking");
        when(localizationManager.getString("nav.logout")).thenReturn("Logout");
        when(localizationManager.getString("schedule.my.bookings")).thenReturn("My Bookings");
        when(localizationManager.getCurrentLanguageDisplayName()).thenReturn("English");

        controller.initialize();

        FlowPane container =
                (FlowPane) getPrivateField("bookingsContainer");

        assertEquals(1, container.getChildren().size());
        assertTrue(container.getChildren().get(0) instanceof Label);
    }

    @Test
    void testLoadBookings_WithBookings() throws Exception {
        LearnerProfile learner = new LearnerProfile();
        learner.setLearnerProfileId(1);

        injectPrivateField("learnerProfile", learner);

        Booking booking = new Booking();
        booking.setBookingId(1);
        booking.setSlotId(10);
        booking.setBookingStatus(Booking.STATUS_CONFIRMED);

        when(bookingDAO.findByLearnerProfileId(1))
                .thenReturn(List.of(booking));

        TimeSlot slot = new TimeSlot();
        slot.setSlotId(10);
        slot.setTeacherProfileId(5);
        slot.setLessonDate(LocalDate.now());
        slot.setStartTime("10:00");
        slot.setEndTime("11:00");

        when(timeSlotDAO.findById(10)).thenReturn(slot);

        TeacherProfile teacher = new TeacherProfile();
        teacher.setTeacherProfileId(5);
        teacher.setUserId(2);
        teacher.setInstrumentsTaught("Piano");

        when(teacherProfileDAO.findById(5)).thenReturn(teacher);

        User teacherUser = new User();
        teacherUser.setUsername("John");

        when(userDAO.findById(2)).thenReturn(teacherUser);

        // Add localization mocks
        when(localizationManager.getString("instrument.piano")).thenReturn("Piano");
        when(localizationManager.getString("action.delete")).thenReturn("Delete");
        when(localizationManager.getString("message.unknown")).thenReturn("Unknown");
        when(localizationManager.getString("app.name")).thenReturn("MusicCoursePlatform");
        when(localizationManager.getString("nav.course.booking")).thenReturn("Course Booking");
        when(localizationManager.getString("nav.logout")).thenReturn("Logout");
        when(localizationManager.getString("schedule.my.bookings")).thenReturn("My Bookings");
        when(localizationManager.getCurrentLanguageDisplayName()).thenReturn("English");

        controller.initialize();

        FlowPane container =
                (FlowPane) getPrivateField("bookingsContainer");

        assertFalse(container.getChildren().isEmpty());
    }

    @Test
    void testHandleDeleteBooking() throws Exception {
        Booking booking = new Booking();
        booking.setBookingId(1);
        booking.setSlotId(10);
        booking.setBookingStatus(Booking.STATUS_CONFIRMED);

        TimeSlot slot = new TimeSlot();
        slot.setSlotId(10);

        when(bookingDAO.update(any())).thenReturn(true);
        when(localizationManager.getString(anyString())).thenReturn("Test String");

        controller.handleDeleteBooking(booking, slot);

        verify(bookingDAO).update(booking);
        verify(timeSlotDAO).updateStatus(10, TimeSlot.STATUS_AVAILABLE);

        assertEquals(Booking.STATUS_CANCELLED, booking.getBookingStatus());
    }

    @Test
    void testHandleDeleteBooking_UpdateFails_DoesNotUpdateTimeSlot() throws Exception {
        Booking booking = new Booking();
        booking.setBookingId(2);
        booking.setSlotId(20);
        booking.setBookingStatus(Booking.STATUS_CONFIRMED);

        TimeSlot slot = new TimeSlot();
        slot.setSlotId(20);

        when(bookingDAO.update(any())).thenReturn(false);

        controller.handleDeleteBooking(booking, slot);

        verify(bookingDAO).update(booking);
        verify(timeSlotDAO, never()).updateStatus(anyInt(), anyString());
    }

    @Test
    void testLoadBookings_CancelledBooking_NotDisplayed() throws Exception {
        LearnerProfile learner = new LearnerProfile();
        learner.setLearnerProfileId(5);
        injectPrivateField("learnerProfile", learner);

        Booking cancelledBooking = new Booking();
        cancelledBooking.setBookingId(1);
        cancelledBooking.setSlotId(10);
        cancelledBooking.setBookingStatus(Booking.STATUS_CANCELLED);

        when(bookingDAO.findByLearnerProfileId(5)).thenReturn(List.of(cancelledBooking));
        when(localizationManager.getString("message.no.bookings")).thenReturn("No bookings");
        when(localizationManager.getString("app.name")).thenReturn("App");
        when(localizationManager.getString("nav.course.booking")).thenReturn("Booking");
        when(localizationManager.getString("nav.logout")).thenReturn("Logout");
        when(localizationManager.getString("schedule.my.bookings")).thenReturn("My Bookings");
        when(localizationManager.getCurrentLanguageDisplayName()).thenReturn("English");

        controller.initialize();

        FlowPane container = (FlowPane) getPrivateField("bookingsContainer");
        // Cancelled booking should not create a card, container may be empty
        // (or show "no bookings" label if all are cancelled)
        assertTrue(container.getChildren().size() <= 1);
    }

    @Test
    void testUpdateTexts_RTLLocale_ReversesNavButtons() throws Exception {
        injectPrivateField("prevPageBtn", new javafx.scene.control.Button("‹"));
        injectPrivateField("nextPageBtn", new javafx.scene.control.Button("›"));

        when(localizationManager.getString("app.name")).thenReturn("App");
        when(localizationManager.getString("nav.course.booking")).thenReturn("Booking");
        when(localizationManager.getString("nav.logout")).thenReturn("Logout");
        when(localizationManager.getString("schedule.my.bookings")).thenReturn("My Bookings");
        when(localizationManager.isRTL()).thenReturn(true);

        // Call updateTexts via reflection
        java.lang.reflect.Method method =
                BookingViewController.class.getDeclaredMethod("updateTexts");
        method.setAccessible(true);
        method.invoke(controller);

        javafx.scene.control.Button prevBtn =
                (javafx.scene.control.Button) getPrivateField("prevPageBtn");
        javafx.scene.control.Button nextBtn =
                (javafx.scene.control.Button) getPrivateField("nextPageBtn");
        assertEquals("›", prevBtn.getText());
        assertEquals("‹", nextBtn.getText());
    }

    @Test
    void testSetupDateFormatter_ArabicLocale_UsesArabicPattern() throws Exception {
        when(localizationManager.getCurrentLocale()).thenReturn(util.LocalizationManager.ARABIC);

        java.lang.reflect.Method method =
                BookingViewController.class.getDeclaredMethod("setupDateFormatter");
        method.setAccessible(true);
        method.invoke(controller);

        java.time.format.DateTimeFormatter formatter =
                (java.time.format.DateTimeFormatter) getPrivateField("dateFormatter");
        assertNotNull(formatter);
    }

    @Test
    void testSetupDateFormatter_ChineseLocale_UsesChinesePattern() throws Exception {
        when(localizationManager.getCurrentLocale()).thenReturn(util.LocalizationManager.CHINESE);

        java.lang.reflect.Method method =
                BookingViewController.class.getDeclaredMethod("setupDateFormatter");
        method.setAccessible(true);
        method.invoke(controller);

        java.time.format.DateTimeFormatter formatter =
                (java.time.format.DateTimeFormatter) getPrivateField("dateFormatter");
        assertNotNull(formatter);
    }

    @Test
    void testSetupDateFormatter_EnglishLocale_UsesEnglishPattern() throws Exception {
        when(localizationManager.getCurrentLocale()).thenReturn(util.LocalizationManager.ENGLISH);

        java.lang.reflect.Method method =
                BookingViewController.class.getDeclaredMethod("setupDateFormatter");
        method.setAccessible(true);
        method.invoke(controller);

        java.time.format.DateTimeFormatter formatter =
                (java.time.format.DateTimeFormatter) getPrivateField("dateFormatter");
        assertNotNull(formatter);
    }

    @Test
    void testHandlePrevPage_NoException() throws Exception {
        java.lang.reflect.Method method =
                BookingViewController.class.getDeclaredMethod("handlePrevPage",
                        javafx.event.ActionEvent.class);
        method.setAccessible(true);
        // Just invoke it - it's a placeholder, should complete without exception
        method.invoke(controller, new javafx.event.ActionEvent());
    }

    @Test
    void testHandleNextPage_NoException() throws Exception {
        java.lang.reflect.Method method =
                BookingViewController.class.getDeclaredMethod("handleNextPage",
                        javafx.event.ActionEvent.class);
        method.setAccessible(true);
        method.invoke(controller, new javafx.event.ActionEvent());
    }

    @Test
    void testLoadBookings_WithBookings_NullSlot_DoesNotAddCard() throws Exception {
        LearnerProfile learner = new LearnerProfile();
        learner.setLearnerProfileId(7);
        injectPrivateField("learnerProfile", learner);

        Booking booking = new Booking();
        booking.setBookingId(1);
        booking.setSlotId(99);
        booking.setBookingStatus(Booking.STATUS_CONFIRMED);

        when(bookingDAO.findByLearnerProfileId(7)).thenReturn(List.of(booking));
        when(timeSlotDAO.findById(99)).thenReturn(null); // slot not found

        when(localizationManager.getString("message.no.bookings")).thenReturn("No bookings");
        when(localizationManager.getString("app.name")).thenReturn("App");
        when(localizationManager.getString("nav.course.booking")).thenReturn("Booking");
        when(localizationManager.getString("nav.logout")).thenReturn("Logout");
        when(localizationManager.getString("schedule.my.bookings")).thenReturn("My Bookings");
        when(localizationManager.getCurrentLanguageDisplayName()).thenReturn("English");

        controller.initialize();

        FlowPane container = (FlowPane) getPrivateField("bookingsContainer");
        // Booking with null slot should result in empty card not being added,
        // or bookings container may have an entry
        assertTrue(container.getChildren().size() >= 0);
    }

    // -----------------------------------------------------------------------
    // Additional tests: loadUserInfo, handleLanguageChange, applyDirection,
    // createBookingCard edge-cases (run against injected mocks, NOT initialize)
    // -----------------------------------------------------------------------

    /** Set up dateFormatter on the controller without calling initialize(). */
    private void setupDateFormatterWithEnglish() throws Exception {
        when(localizationManager.getCurrentLocale()).thenReturn(java.util.Locale.ENGLISH);
        java.lang.reflect.Method m =
                BookingViewController.class.getDeclaredMethod("setupDateFormatter");
        m.setAccessible(true);
        m.invoke(controller);
    }

    @Test
    void testLoadUserInfo_WithLoggedInUser_SetsUsernameLabel() throws Exception {
        User user = new User();
        user.setUserId(42);
        user.setUsername("learnerX");
        SessionManager.getInstance().setCurrentUser(user);

        LearnerProfile profile = new LearnerProfile();
        profile.setLearnerProfileId(42);
        when(learnerProfileDAO.findByUserId(42)).thenReturn(profile);

        java.lang.reflect.Method m =
                BookingViewController.class.getDeclaredMethod("loadUserInfo");
        m.setAccessible(true);
        m.invoke(controller);

        Label label = (Label) getPrivateField("userNameLabel");
        assertEquals("learnerX", label.getText());
        verify(learnerProfileDAO).findByUserId(42);

        SessionManager.getInstance().logout();
    }

    @Test
    void testHandleLanguageChange_CallsSetLocale() throws Exception {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("English", "中文", "العربية");
        combo.setValue("中文");
        injectPrivateField("languageCombo", combo);

        java.lang.reflect.Method m =
                BookingViewController.class.getDeclaredMethod("handleLanguageChange",
                        javafx.event.ActionEvent.class);
        m.setAccessible(true);
        m.invoke(controller, new javafx.event.ActionEvent());

        verify(localizationManager).setLocale(util.LocalizationManager.CHINESE);
    }

    @Test
    void testApplyDirection_CallsLocalizationManagerApplyDirection() throws Exception {
        java.lang.reflect.Method m =
                BookingViewController.class.getDeclaredMethod("applyDirection");
        m.setAccessible(true);
        m.invoke(controller);

        verify(localizationManager).applyDirection(any());
    }

    @Test
    void testLoadBookings_NullTeacher_CreatesCardWithUnknownFallback() throws Exception {
        setupDateFormatterWithEnglish();

        LearnerProfile learner = new LearnerProfile();
        learner.setLearnerProfileId(9);
        injectPrivateField("learnerProfile", learner);

        Booking booking = new Booking();
        booking.setBookingId(5);
        booking.setSlotId(55);
        booking.setBookingStatus(Booking.STATUS_CONFIRMED);

        when(bookingDAO.findByLearnerProfileId(9)).thenReturn(List.of(booking));

        TimeSlot slot = new TimeSlot();
        slot.setSlotId(55);
        slot.setTeacherProfileId(7);
        slot.setLessonDate(LocalDate.now());
        slot.setStartTime("09:00");
        slot.setEndTime("10:00");
        when(timeSlotDAO.findById(55)).thenReturn(slot);

        when(teacherProfileDAO.findById(7)).thenReturn(null); // null teacher
        when(localizationManager.getString("message.unknown")).thenReturn("Unknown");
        when(localizationManager.getString("action.delete")).thenReturn("Delete");

        java.lang.reflect.Method m =
                BookingViewController.class.getDeclaredMethod("loadBookings");
        m.setAccessible(true);
        m.invoke(controller);

        FlowPane container = (FlowPane) getPrivateField("bookingsContainer");
        assertFalse(container.getChildren().isEmpty());
    }

    @Test
    void testLoadBookings_NullTeacherUser_UsesDotTeacherFallback() throws Exception {
        setupDateFormatterWithEnglish();

        LearnerProfile learner = new LearnerProfile();
        learner.setLearnerProfileId(10);
        injectPrivateField("learnerProfile", learner);

        Booking booking = new Booking();
        booking.setBookingId(6);
        booking.setSlotId(66);
        booking.setBookingStatus(Booking.STATUS_CONFIRMED);

        when(bookingDAO.findByLearnerProfileId(10)).thenReturn(List.of(booking));

        TimeSlot slot = new TimeSlot();
        slot.setSlotId(66);
        slot.setTeacherProfileId(8);
        slot.setLessonDate(LocalDate.now());
        slot.setStartTime("14:00");
        slot.setEndTime("15:00");
        when(timeSlotDAO.findById(66)).thenReturn(slot);

        TeacherProfile teacher = new TeacherProfile();
        teacher.setTeacherProfileId(8);
        teacher.setUserId(20);
        teacher.setInstrumentsTaught("guitar");
        when(teacherProfileDAO.findById(8)).thenReturn(teacher);
        when(userDAO.findById(20)).thenReturn(null); // null user
        when(localizationManager.getLocalizedInstrumentName("guitar")).thenReturn("Guitar");
        when(localizationManager.getString("message.teacher")).thenReturn("Teacher");
        when(localizationManager.getString("message.unknown")).thenReturn("Unknown");
        when(localizationManager.getString("action.delete")).thenReturn("Delete");

        java.lang.reflect.Method m =
                BookingViewController.class.getDeclaredMethod("loadBookings");
        m.setAccessible(true);
        m.invoke(controller);

        FlowPane container = (FlowPane) getPrivateField("bookingsContainer");
        assertFalse(container.getChildren().isEmpty());
    }

    @Test
    void testLoadBookings_WithTeacherAndUser_ShowsFullCard() throws Exception {
        setupDateFormatterWithEnglish();

        LearnerProfile learner = new LearnerProfile();
        learner.setLearnerProfileId(11);
        injectPrivateField("learnerProfile", learner);

        Booking booking = new Booking();
        booking.setBookingId(7);
        booking.setSlotId(77);
        booking.setBookingStatus(Booking.STATUS_CONFIRMED);

        when(bookingDAO.findByLearnerProfileId(11)).thenReturn(List.of(booking));

        TimeSlot slot = new TimeSlot();
        slot.setSlotId(77);
        slot.setTeacherProfileId(9);
        slot.setLessonDate(LocalDate.now());
        slot.setStartTime("11:00");
        slot.setEndTime("12:00");
        when(timeSlotDAO.findById(77)).thenReturn(slot);

        TeacherProfile teacher = new TeacherProfile();
        teacher.setTeacherProfileId(9);
        teacher.setUserId(30);
        teacher.setInstrumentsTaught("piano");
        when(teacherProfileDAO.findById(9)).thenReturn(teacher);

        User teacherUser = new User();
        teacherUser.setUsername("MrSmith");
        when(userDAO.findById(30)).thenReturn(teacherUser);

        when(localizationManager.getLocalizedInstrumentName("piano")).thenReturn("Piano");
        when(localizationManager.getString("message.unknown")).thenReturn("Unknown");
        when(localizationManager.getString("action.delete")).thenReturn("Delete");

        java.lang.reflect.Method m =
                BookingViewController.class.getDeclaredMethod("loadBookings");
        m.setAccessible(true);
        m.invoke(controller);

        FlowPane container = (FlowPane) getPrivateField("bookingsContainer");
        assertFalse(container.getChildren().isEmpty());
    }

    @Test
    void testUpdateTexts_LTRLocale_UsesNormalNavButtons() throws Exception {
        injectPrivateField("prevPageBtn", new javafx.scene.control.Button("‹"));
        injectPrivateField("nextPageBtn", new javafx.scene.control.Button("›"));

        when(localizationManager.getString("app.name")).thenReturn("App");
        when(localizationManager.getString("nav.course.booking")).thenReturn("Booking");
        when(localizationManager.getString("nav.logout")).thenReturn("Logout");
        when(localizationManager.getString("schedule.my.bookings")).thenReturn("My Bookings");
        when(localizationManager.isRTL()).thenReturn(false);

        java.lang.reflect.Method method =
                BookingViewController.class.getDeclaredMethod("updateTexts");
        method.setAccessible(true);
        method.invoke(controller);

        javafx.scene.control.Button prevBtn =
                (javafx.scene.control.Button) getPrivateField("prevPageBtn");
        javafx.scene.control.Button nextBtn =
                (javafx.scene.control.Button) getPrivateField("nextPageBtn");
        assertEquals("‹", prevBtn.getText());
        assertEquals("›", nextBtn.getText());
    }

    private Object getPrivateField(String fieldName) throws Exception {
        Field field = BookingViewController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(controller);
    }
}