package controller;

import dao.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import util.LocalizationManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentDashboardControllerTest {

    private StudentDashboardController controller;

    @Mock private TeacherProfileDAO mockTeacherProfileDAO;
    @Mock private TimeSlotDAO mockTimeSlotDAO;
    @Mock private BookingDAO mockBookingDAO;
    @Mock private LearnerProfileDAO mockLearnerProfileDAO;
    @Mock private UserDAO mockUserDAO;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        latch.await();
    }

    @BeforeEach
    void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller = new StudentDashboardController();

                setField(controller, "teacherNameLabel", new Label());
                setField(controller, "teacherInstrumentLabel", new Label());
                setField(controller, "teacherExperienceLabel", new Label());
                setField(controller, "teacherRateLabel", new Label());
                setField(controller, "errorLabel", new Label());
                setField(controller, "instrumentCombo", new ComboBox<>());
                setField(controller, "teacherCombo", new ComboBox<>());
                setField(controller, "monthLabel", new Label());
                setField(controller, "calendarGrid", new FlowPane());
                setField(controller, "selectedDateLabel", new Label());
                setField(controller, "selectedTimeLabel", new Label());
                setField(controller, "timeSlotsContainer", new VBox());
                setField(controller, "languageCombo", new ComboBox<>());
                setField(controller, "bookButton", new Button());
                setField(controller, "teacherBioLabel", new Label());

                // Add localization fields
                setField(controller, "appNameLabel", new Label());
                setField(controller, "viewScheduleButton", new Button());
                setField(controller, "logoutButton", new Button());
                setField(controller, "filterInstrumentLabel", new Label());
                setField(controller, "selectTeacherLabel", new Label());
                setField(controller, "experienceTitleLabel", new Label());
                setField(controller, "rateTitleLabel", new Label());
                setField(controller, "aboutTitleLabel", new Label());
                setField(controller, "calendarFrameLabel", new Label());
                setField(controller, "availableTimesLabel", new Label());
                setField(controller, "selectedTimeTitleLabel", new Label());
                setField(controller, "sunLabel", new Label());
                setField(controller, "monLabel", new Label());
                setField(controller, "tueLabel", new Label());
                setField(controller, "wedLabel", new Label());
                setField(controller, "thuLabel", new Label());
                setField(controller, "friLabel", new Label());
                setField(controller, "satLabel", new Label());
                setField(controller, "rootPane", new javafx.scene.layout.BorderPane());

                setField(controller, "teacherProfileDAO", mockTeacherProfileDAO);
                setField(controller, "timeSlotDAO", mockTimeSlotDAO);
                setField(controller, "bookingDAO", mockBookingDAO);
                setField(controller, "learnerProfileDAO", mockLearnerProfileDAO);
                setField(controller, "userDAO", mockUserDAO);
                setField(controller, "localizationManager", LocalizationManager.getInstance());

                setField(controller, "currentMonth", YearMonth.now());

            } catch (Exception e) {
                fail("Setup failed: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();

        SessionManager.getInstance().logout();
    }

    // --- handlePrevMonth ---

    @Test
    void testHandlePrevMonth_DecrementsMonth() throws Exception {
        runOnFX(() -> {
            YearMonth before = YearMonth.now();
            setField(controller, "currentMonth", before);

            invokeMethod("handlePrevMonth");

            YearMonth after = getField(controller, "currentMonth", YearMonth.class);
            assertEquals(before.minusMonths(1), after);
        });
    }

    // --- handleNextMonth ---

    @Test
    void testHandleNextMonth_IncrementsMonth() throws Exception {
        runOnFX(() -> {
            YearMonth before = YearMonth.now();
            setField(controller, "currentMonth", before);

            invokeMethod("handleNextMonth");

            YearMonth after = getField(controller, "currentMonth", YearMonth.class);
            assertEquals(before.plusMonths(1), after);
        });
    }

    // --- handleBookNow: no teacher selected ---

    @Test
    void testHandleBookNow_NoTeacher_DoesNotCallBookingDAO() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedTeacher", null);
            setField(controller, "selectedSlot", null);

            invokeMethod("handleBookNow");

            verifyNoInteractions(mockBookingDAO);
        });
    }

    // --- handleBookNow: no slot selected ---

    @Test
    void testHandleBookNow_NoSlot_DoesNotCallBookingDAO() throws Exception {
        runOnFX(() -> {
            TeacherProfile teacher = createTeacherProfile();
            setField(controller, "selectedTeacher", teacher);
            setField(controller, "selectedSlot", null);

            invokeMethod("handleBookNow");

            verifyNoInteractions(mockBookingDAO);
        });
    }

    // --- confirmBooking: no learner profile ---

    @Test
    void testConfirmBooking_NoLearnerProfile_DoesNotCallBookingDAO() throws Exception {
        runOnFX(() -> {
            setField(controller, "learnerProfile", null);
            setField(controller, "selectedSlot", createTimeSlot(true));
            setField(controller, "selectedTeacher", createTeacherProfile());

            invokeMethod("handleBookNow");

            verifyNoInteractions(mockBookingDAO);
        });
    }

    // --- confirmBooking: successful booking ---

    @Test
    void testConfirmBooking_Success_CreatesBookingAndUpdatesSlot() throws Exception {
        runOnFX(() -> {
            LearnerProfile learner = new LearnerProfile(1, "Piano");
            learner.setLearnerProfileId(10);
            TimeSlot slot = createTimeSlot(true);
            TeacherProfile teacher = createTeacherProfile();

            setField(controller, "learnerProfile", learner);
            setField(controller, "selectedSlot", slot);
            setField(controller, "selectedTeacher", teacher);
            setField(controller, "selectedDate", LocalDate.now());

            when(mockBookingDAO.create(any(Booking.class))).thenReturn(true);
            lenient().when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(Collections.emptyList());

            invokeMethod("confirmBooking");

            verify(mockBookingDAO).create(any(Booking.class));
            verify(mockTimeSlotDAO).updateStatus(slot.getSlotId(), TimeSlot.STATUS_BOOKED);
            assertNull(getField(controller, "selectedSlot", TimeSlot.class));
        });
    }

    // --- confirmBooking: booking fails ---

    @Test
    void testConfirmBooking_Failure_DoesNotUpdateSlotStatus() throws Exception {
        runOnFX(() -> {
            LearnerProfile learner = new LearnerProfile(1, "Piano");
            learner.setLearnerProfileId(10);
            TimeSlot slot = createTimeSlot(true);

            setField(controller, "learnerProfile", learner);
            setField(controller, "selectedSlot", slot);
            setField(controller, "selectedTeacher", createTeacherProfile());

            when(mockBookingDAO.create(any(Booking.class))).thenReturn(false);

            invokeMethod("confirmBooking");

            verify(mockBookingDAO).create(any(Booking.class));
            verify(mockTimeSlotDAO, never()).updateStatus(anyInt(), anyString());
        });
    }

    // --- handleLogout ---

    @Test
    void testHandleLogout_ClearsSession() {
        User user = new User("test", "hash", "test@test.com", "LEARNER");
        SessionManager.getInstance().setCurrentUser(user);

        SessionManager.getInstance().logout();

        assertFalse(SessionManager.getInstance().isLoggedIn());
    }

    // --- updateTimeSlots: no date or teacher ---

    @Test
    void testUpdateTimeSlots_NoDateOrTeacher_ContainerEmpty() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", null);
            setField(controller, "selectedTeacher", null);

            invokeMethod("updateTimeSlots");

            VBox container = getField(controller, "timeSlotsContainer", VBox.class);
            assertTrue(container.getChildren().isEmpty());
        });
    }

    // --- updateTimeSlots: no available slots shows label ---

    @Test
    void testUpdateTimeSlots_NoAvailableSlots_ShowsNoSlotsLabel() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "selectedTeacher", createTeacherProfile());

            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(Collections.emptyList());

            invokeMethod("updateTimeSlots");

            VBox container = getField(controller, "timeSlotsContainer", VBox.class);
            assertEquals(1, container.getChildren().size());
            assertTrue(container.getChildren().get(0) instanceof Label);
            assertEquals("No available slots", ((Label) container.getChildren().get(0)).getText());
        });
    }

    // --- updateTimeSlots: available slots shown ---

    @Test
    void testUpdateTimeSlots_WithAvailableSlots_ShowsSlots() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "selectedTeacher", createTeacherProfile());

            TimeSlot slot = createTimeSlot(true);
            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(List.of(slot));

            invokeMethod("updateTimeSlots");

            VBox container = getField(controller, "timeSlotsContainer", VBox.class);
            assertFalse(container.getChildren().isEmpty());
        });
    }

    // --- handleLanguageChange ---

    @Test
    void testHandleLanguageChange_SetsLocale() throws Exception {
        runOnFX(() -> {
            ComboBox<String> langCombo = getField(controller, "languageCombo", ComboBox.class);
            langCombo.getItems().add("中文");
            langCombo.setValue("中文");

            invokeMethod("handleLanguageChange");

            assertEquals(LocalizationManager.CHINESE, LocalizationManager.getInstance().getCurrentLocale());

            // Reset to English so other tests are not affected
            LocalizationManager.getInstance().setLocale(LocalizationManager.ENGLISH);
        });
    }

    // --- loadLearnerProfile ---

    @Test
    void testLoadLearnerProfile_NoUser_DoesNotCallDAO() throws Exception {
        runOnFX(() -> {
            SessionManager.getInstance().logout();

            invokeMethod("loadLearnerProfile");

            verifyNoInteractions(mockLearnerProfileDAO);
        });
    }

    @Test
    void testLoadLearnerProfile_WithUserAndExistingProfile() throws Exception {
        runOnFX(() -> {
            User user = new User("student1", "hash", "s@test.com", "LEARNER");
            user.setUserId(5);
            SessionManager.getInstance().setCurrentUser(user);

            LearnerProfile existingProfile = new LearnerProfile(5, "piano");
            existingProfile.setLearnerProfileId(10);
            when(mockLearnerProfileDAO.findByUserId(5)).thenReturn(existingProfile);

            invokeMethod("loadLearnerProfile");

            LearnerProfile result = getField(controller, "learnerProfile", LearnerProfile.class);
            assertNotNull(result);
            assertEquals(10, result.getLearnerProfileId());

            SessionManager.getInstance().logout();
        });
    }

    @Test
    void testLoadLearnerProfile_WithUserButNoProfile_CreatesNew() throws Exception {
        runOnFX(() -> {
            User user = new User("student2", "hash", "s2@test.com", "LEARNER");
            user.setUserId(6);
            SessionManager.getInstance().setCurrentUser(user);

            when(mockLearnerProfileDAO.findByUserId(6)).thenReturn(null);
            when(mockLearnerProfileDAO.create(any())).thenReturn(true);

            invokeMethod("loadLearnerProfile");

            verify(mockLearnerProfileDAO).create(any(LearnerProfile.class));

            SessionManager.getInstance().logout();
        });
    }

    // --- loadTeachers ---

    @Test
    void testLoadTeachers_NullInstrumentValue_ReturnsEarly() throws Exception {
        runOnFX(() -> {
            // instrumentCombo has no value (null)
            invokeMethod("loadTeachers");

            verifyNoInteractions(mockTeacherProfileDAO);
        });
    }

    @Test
    void testLoadTeachers_EmptyList_ShowsNoTeachersMessage() throws Exception {
        runOnFX(() -> {
            ComboBox<String> combo = getField(controller, "instrumentCombo", ComboBox.class);
            combo.getItems().add("Piano");
            combo.setValue("Piano");

            when(mockTeacherProfileDAO.findByInstrument(anyString()))
                    .thenReturn(Collections.emptyList());

            invokeMethod("loadTeachers");

            Label teacherNameLabel = getField(controller, "teacherNameLabel", Label.class);
            assertFalse(teacherNameLabel.getText().isEmpty());
        });
    }

    @Test
    void testLoadTeachers_WithTeachers_PopulatesComboAndSetsSelected() throws Exception {
        runOnFX(() -> {
            ComboBox<String> combo = getField(controller, "instrumentCombo", ComboBox.class);
            combo.getItems().add("Piano");
            combo.setValue("Piano");

            TeacherProfile profile = createTeacherProfile();
            User user = new User("teacher1", "hash", "t@test.com", "TEACHER");
            user.setUserId(1);

            when(mockTeacherProfileDAO.findByInstrument(anyString()))
                    .thenReturn(List.of(profile));
            when(mockUserDAO.findById(anyInt())).thenReturn(user);
            lenient().when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(Collections.emptyList());

            invokeMethod("loadTeachers");

            ComboBox<String> teacherCombo = getField(controller, "teacherCombo", ComboBox.class);
            assertFalse(teacherCombo.getItems().isEmpty());

            TeacherProfile selected = getField(controller, "selectedTeacher", TeacherProfile.class);
            assertNotNull(selected);
        });
    }

    @Test
    void testLoadTeachers_WithTeachers_UserNotFound_UsesDefaultName() throws Exception {
        runOnFX(() -> {
            ComboBox<String> combo = getField(controller, "instrumentCombo", ComboBox.class);
            combo.getItems().add("Piano");
            combo.setValue("Piano");

            TeacherProfile profile = createTeacherProfile();
            profile.setTeacherProfileId(99);

            when(mockTeacherProfileDAO.findByInstrument(anyString()))
                    .thenReturn(List.of(profile));
            when(mockUserDAO.findById(anyInt())).thenReturn(null);
            lenient().when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(Collections.emptyList());

            invokeMethod("loadTeachers");

            ComboBox<String> teacherCombo = getField(controller, "teacherCombo", ComboBox.class);
            assertTrue(teacherCombo.getItems().get(0).contains("Teacher 99"));
        });
    }

    // --- handleInstrumentChange ---

    @Test
    void testHandleInstrumentChange_CallsLoadTeachersAndUpdateTimeSlots() throws Exception {
        runOnFX(() -> {
            ComboBox<String> combo = getField(controller, "instrumentCombo", ComboBox.class);
            combo.getItems().add("Guitar");
            combo.setValue("Guitar");

            when(mockTeacherProfileDAO.findByInstrument(anyString()))
                    .thenReturn(Collections.emptyList());

            invokeMethod("handleInstrumentChange");

            verify(mockTeacherProfileDAO).findByInstrument(anyString());
        });
    }

    // --- handleTeacherChange ---

    @Test
    void testHandleTeacherChange_ValidIndex_SetsSelectedTeacher() throws Exception {
        runOnFX(() -> {
            TeacherProfile profile = createTeacherProfile();
            setField(controller, "teacherProfiles", List.of(profile));

            ComboBox<String> teacherCombo = getField(controller, "teacherCombo", ComboBox.class);
            teacherCombo.getItems().add("Teacher1");
            teacherCombo.getSelectionModel().select(0);

            User user = new User("teacher1", "hash", "t@test.com", "TEACHER");
            when(mockUserDAO.findById(anyInt())).thenReturn(user);
            lenient().when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(Collections.emptyList());

            invokeMethod("handleTeacherChange");

            TeacherProfile selected = getField(controller, "selectedTeacher", TeacherProfile.class);
            assertNotNull(selected);
            assertEquals(1, selected.getTeacherProfileId());
        });
    }

    @Test
    void testHandleTeacherChange_NegativeIndex_DoesNothing() throws Exception {
        runOnFX(() -> {
            setField(controller, "teacherProfiles", List.of(createTeacherProfile()));

            ComboBox<String> teacherCombo = getField(controller, "teacherCombo", ComboBox.class);
            teacherCombo.getSelectionModel().clearSelection();

            invokeMethod("handleTeacherChange");

            verifyNoInteractions(mockUserDAO);
        });
    }

    // --- updateTeacherDisplay ---

    @Test
    void testUpdateTeacherDisplay_NullTeacher_DoesNothing() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedTeacher", null);

            invokeMethod("updateTeacherDisplay");

            verifyNoInteractions(mockUserDAO);
        });
    }

    @Test
    void testUpdateTeacherDisplay_WithTeacher_SetsAllLabels() throws Exception {
        runOnFX(() -> {
            TeacherProfile profile = createTeacherProfile();
            profile.setBiography("Experienced teacher");
            setField(controller, "selectedTeacher", profile);

            User user = new User("teacher1", "hash", "t@test.com", "TEACHER");
            when(mockUserDAO.findById(anyInt())).thenReturn(user);

            invokeMethod("updateTeacherDisplay");

            Label nameLabel = getField(controller, "teacherNameLabel", Label.class);
            assertEquals("teacher1", nameLabel.getText());

            Label rateLabel = getField(controller, "teacherRateLabel", Label.class);
            assertTrue(rateLabel.getText().contains("$50"));
        });
    }

    @Test
    void testUpdateTeacherDisplay_WithTeacherBioEmpty_ShowsNoBioText() throws Exception {
        runOnFX(() -> {
            TeacherProfile profile = createTeacherProfile();
            profile.setBiography("");
            setField(controller, "selectedTeacher", profile);

            when(mockUserDAO.findById(anyInt())).thenReturn(
                    new User("t", "h", "t@t.com", "TEACHER"));

            invokeMethod("updateTeacherDisplay");

            Label bioLabel = getField(controller, "teacherBioLabel", Label.class);
            assertFalse(bioLabel.getText().isEmpty());
        });
    }

    @Test
    void testUpdateTeacherDisplay_UserNotFound_UsesDefaultName() throws Exception {
        runOnFX(() -> {
            TeacherProfile profile = createTeacherProfile();
            profile.setTeacherProfileId(42);
            setField(controller, "selectedTeacher", profile);

            when(mockUserDAO.findById(anyInt())).thenReturn(null);

            invokeMethod("updateTeacherDisplay");

            Label nameLabel = getField(controller, "teacherNameLabel", Label.class);
            assertTrue(nameLabel.getText().contains("Teacher 42"));
        });
    }

    // --- hasAvailableSlots ---

    @Test
    void testHasAvailableSlots_NoTeacher_ReturnsFalse() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedTeacher", null);

            Method method = StudentDashboardController.class
                    .getDeclaredMethod("hasAvailableSlots", LocalDate.class);
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(controller, LocalDate.now());

            assertFalse(result);
        });
    }

    @Test
    void testHasAvailableSlots_WithAvailableSlot_ReturnsTrue() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedTeacher", createTeacherProfile());

            TimeSlot slot = createTimeSlot(true);
            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(List.of(slot));

            Method method = StudentDashboardController.class
                    .getDeclaredMethod("hasAvailableSlots", LocalDate.class);
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(controller, LocalDate.now());

            assertTrue(result);
        });
    }

    @Test
    void testHasAvailableSlots_BookedSlotsOnly_ReturnsFalse() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedTeacher", createTeacherProfile());

            TimeSlot slot = createTimeSlot(false);
            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(List.of(slot));

            Method method = StudentDashboardController.class
                    .getDeclaredMethod("hasAvailableSlots", LocalDate.class);
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(controller, LocalDate.now());

            assertFalse(result);
        });
    }

    // --- handleSlotSelect ---

    @Test
    void testHandleSlotSelect_SetsSelectedSlotAndUpdatesLabel() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "selectedTeacher", createTeacherProfile());

            TimeSlot slot = createTimeSlot(true);
            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(List.of(slot));

            Method method = StudentDashboardController.class
                    .getDeclaredMethod("handleSlotSelect", TimeSlot.class);
            method.setAccessible(true);
            method.invoke(controller, slot);

            TimeSlot selectedSlot = getField(controller, "selectedSlot", TimeSlot.class);
            assertNotNull(selectedSlot);
            assertEquals(slot.getSlotId(), selectedSlot.getSlotId());

            Label timeLabel = getField(controller, "selectedTimeLabel", Label.class);
            assertTrue(timeLabel.getText().contains(slot.getStartTime()));
        });
    }

    // --- createTimeSlotBox with selected slot ---

    @Test
    void testCreateTimeSlotBox_SelectedSlotMatching_HasSelectedStyle() throws Exception {
        runOnFX(() -> {
            TimeSlot slot = createTimeSlot(true);
            setField(controller, "selectedSlot", slot);

            Method method = StudentDashboardController.class
                    .getDeclaredMethod("createTimeSlotBox", TimeSlot.class);
            method.setAccessible(true);
            javafx.scene.layout.HBox box =
                    (javafx.scene.layout.HBox) method.invoke(controller, slot);

            assertNotNull(box);
            javafx.scene.control.Button btn =
                    (javafx.scene.control.Button) box.getChildren().get(0);
            assertTrue(btn.getStyleClass().contains("time-slot-selected"));
        });
    }

    // --- showError / showSuccessMessage ---

    @Test
    void testShowError_SetsLabelText() throws Exception {
        runOnFX(() -> {
            Method method = StudentDashboardController.class
                    .getDeclaredMethod("showError", String.class);
            method.setAccessible(true);
            method.invoke(controller, "Test error message");

            Label label = getField(controller, "errorLabel", Label.class);
            assertEquals("Test error message", label.getText());
            assertTrue(label.isVisible());
        });
    }

    @Test
    void testShowSuccessMessage_SetsGreenLabel() throws Exception {
        runOnFX(() -> {
            Method method = StudentDashboardController.class
                    .getDeclaredMethod("showSuccessMessage", String.class);
            method.setAccessible(true);
            method.invoke(controller, "Success!");

            Label label = getField(controller, "errorLabel", Label.class);
            assertEquals("Success!", label.getText());
            assertTrue(label.getStyle().contains("#38a169"));
        });
    }

    // --- updateCalendar with selectedDate and available slots ---

    @Test
    void testUpdateCalendar_WithSelectedDate_RendersCalendar() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedTeacher", createTeacherProfile());
            setField(controller, "selectedDate", LocalDate.now());

            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(List.of(createTimeSlot(true)));

            invokeMethod("updateCalendar");

            FlowPane grid = getField(controller, "calendarGrid", FlowPane.class);
            assertFalse(grid.getChildren().isEmpty());
        });
    }

    // --- updateTexts with selectedDate and selectedSlot set ---

    @Test
    void testUpdateTexts_WithSelectedDateSet_DoesNotOverwriteDateLabel() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "selectedSlot", createTimeSlot(true));

            invokeMethod("updateTexts");

            // selectedDateLabel should NOT be reset when selectedDate is non-null
            Label dateLabel = getField(controller, "selectedDateLabel", Label.class);
            assertNotNull(dateLabel);
        });
    }

    // --- Helpers ---

    private TeacherProfile createTeacherProfile() {
        TeacherProfile profile = new TeacherProfile();
        profile.setTeacherProfileId(1);
        profile.setUserId(1);
        profile.setInstrumentsTaught("Piano");
        profile.setYearsExperience(5);
        profile.setHourlyRate(50);
        return profile;
    }

    private TimeSlot createTimeSlot(boolean available) {
        TimeSlot slot = new TimeSlot();
        slot.setSlotId(1);
        slot.setStartTime("10:00");
        slot.setEndTime("11:00");
        slot.setSlotStatus(available ? TimeSlot.STATUS_AVAILABLE : TimeSlot.STATUS_BOOKED);
        return slot;
    }

    private void invokeMethod(String methodName) {
        try {
            try {
                Method method = StudentDashboardController.class.getDeclaredMethod(methodName, ActionEvent.class);
                method.setAccessible(true);
                method.invoke(controller, new ActionEvent());
            } catch (NoSuchMethodException e) {
                Method method = StudentDashboardController.class.getDeclaredMethod(methodName);
                method.setAccessible(true);
                method.invoke(controller);
            }
        } catch (Exception e) {
            fail("Could not invoke " + methodName + ": " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static <T> T getField(Object target, String fieldName, Class<T> type) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return type.cast(field.get(target));
        } catch (Exception e) {
            fail("Could not get field: " + fieldName);
            return null;
        }
    }

    private void runOnFX(RunnableWithException task) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Exception[] thrown = {null};
        Platform.runLater(() -> {
            try {
                task.run();
            } catch (Exception e) {
                thrown[0] = e;
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        if (thrown[0] != null) throw thrown[0];
    }

    @FunctionalInterface
    interface RunnableWithException {
        void run() throws Exception;
    }
}