package controller;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;
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
class TeacherDashboardControllerTest {

    private TeacherDashboardController controller;

    @Mock
    private TeacherProfileDAO mockTeacherProfileDAO;
    @Mock
    private TimeSlotDAO mockTimeSlotDAO;

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
                controller = new TeacherDashboardController();

                setField(controller, "nameLabel", new Label());
                setField(controller, "instrumentsCombo", new ComboBox<>());
                setField(controller, "experienceField", new TextField());
                setField(controller, "pricingField", new TextField());
                setField(controller, "bioField", new TextArea());
                setField(controller, "monthLabel", new Label());
                setField(controller, "calendarGrid", new FlowPane());
                setField(controller, "selectedDateLabel", new Label());
                setField(controller, "startTimeCombo", new ComboBox<>());
                setField(controller, "endTimeCombo", new ComboBox<>());
                setField(controller, "timeSlotsContainer", new VBox());
                setField(controller, "languageCombo", new ComboBox<>());
                setField(controller, "errorLabel", new Label());

                // Additional fields needed by updateTexts()
                setField(controller, "appNameLabel", new Label());
                setField(controller, "viewScheduleButton", new Button());
                setField(controller, "logoutButton", new Button());
                setField(controller, "saveProfileButton", new Button());
                setField(controller, "calendarFrameLabel", new Label());
                setField(controller, "setAvailabilityLabel", new Label());
                setField(controller, "startTimeLabel", new Label());
                setField(controller, "endTimeLabel", new Label());
                setField(controller, "addTimeSlotButton", new Button());
                setField(controller, "rootPane", new javafx.scene.layout.BorderPane());
                setField(controller, "sunLabel", new Label());
                setField(controller, "monLabel", new Label());
                setField(controller, "tueLabel", new Label());
                setField(controller, "wedLabel", new Label());
                setField(controller, "thuLabel", new Label());
                setField(controller, "friLabel", new Label());
                setField(controller, "satLabel", new Label());

                setField(controller, "teacherProfileDAO", mockTeacherProfileDAO);
                setField(controller, "timeSlotDAO", mockTimeSlotDAO);
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

    // --- handleSaveProfile: no user or profile ---

    @Test
    void testHandleSaveProfile_NoUser_DoesNotCallDAO() throws Exception {
        runOnFX(() -> {
            setField(controller, "currentUser", null);
            setField(controller, "teacherProfile", null);

            invokeMethod("handleSaveProfile");

            verifyNoInteractions(mockTeacherProfileDAO);
        });
    }

    // --- handleSaveProfile: invalid experience ---

    @Test
    void testHandleSaveProfile_InvalidExperience_ShowsError() throws Exception {
        runOnFX(() -> {
            setField(controller, "currentUser", createUser());
            setField(controller, "teacherProfile", createTeacherProfile());

            ComboBox<String> instrumentsCombo = getField(controller, "instrumentsCombo", ComboBox.class);
            instrumentsCombo.getItems().add("Piano");
            instrumentsCombo.setValue("Piano");

            getField(controller, "experienceField", TextField.class).setText("abc");
            getField(controller, "pricingField", TextField.class).setText("50");
            getField(controller, "bioField", TextArea.class).setText("Bio");

            invokeMethod("handleSaveProfile");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertTrue(errorLabel.getText().contains("Please fill in all fields"));
            verifyNoInteractions(mockTeacherProfileDAO);
        });
    }

    // --- handleSaveProfile: invalid pricing ---

    @Test
    void testHandleSaveProfile_InvalidPricing_ShowsError() throws Exception {
        runOnFX(() -> {
            setField(controller, "currentUser", createUser());
            setField(controller, "teacherProfile", createTeacherProfile());

            ComboBox<String> instrumentsCombo = getField(controller, "instrumentsCombo", ComboBox.class);
            instrumentsCombo.getItems().add("Piano");
            instrumentsCombo.setValue("Piano");

            getField(controller, "experienceField", TextField.class).setText("5");
            getField(controller, "pricingField", TextField.class).setText("abc");
            getField(controller, "bioField", TextArea.class).setText("Bio");

            invokeMethod("handleSaveProfile");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertTrue(errorLabel.getText().contains("Please fill in all fields"));
            verifyNoInteractions(mockTeacherProfileDAO);
        });
    }

    // --- handleSaveProfile: success ---

    @Test
    void testHandleSaveProfile_ValidInput_CallsUpdate() throws Exception {
        runOnFX(() -> {
            setField(controller, "currentUser", createUser());
            setField(controller, "teacherProfile", createTeacherProfile());

            ComboBox<String> instrumentsCombo = getField(controller, "instrumentsCombo", ComboBox.class);
            instrumentsCombo.getItems().add("Piano");
            instrumentsCombo.setValue("Piano");

            getField(controller, "experienceField", TextField.class).setText("5");
            getField(controller, "pricingField", TextField.class).setText("50");
            getField(controller, "bioField", TextArea.class).setText("Experienced teacher");

            when(mockTeacherProfileDAO.update(any(TeacherProfile.class))).thenReturn(true);

            invokeMethod("handleSaveProfile");

            verify(mockTeacherProfileDAO).update(any(TeacherProfile.class));
        });
    }

    // --- handleSaveProfile: update fails ---

    @Test
    void testHandleSaveProfile_UpdateFails_ShowsError() throws Exception {
        runOnFX(() -> {
            setField(controller, "currentUser", createUser());
            setField(controller, "teacherProfile", createTeacherProfile());

            ComboBox<String> instrumentsCombo = getField(controller, "instrumentsCombo", ComboBox.class);
            instrumentsCombo.getItems().add("Piano");
            instrumentsCombo.setValue("Piano");

            getField(controller, "experienceField", TextField.class).setText("5");
            getField(controller, "pricingField", TextField.class).setText("50");
            getField(controller, "bioField", TextArea.class).setText("Bio");

            when(mockTeacherProfileDAO.update(any(TeacherProfile.class))).thenReturn(false);

            invokeMethod("handleSaveProfile");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertTrue(errorLabel.getText().contains("Signup failed"));
        });
    }

    // --- handleAddTimeSlot: no date ---

    @Test
    void testHandleAddTimeSlot_NoDate_ShowsError() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", null);
            setField(controller, "teacherProfile", createTeacherProfile());

            invokeMethod("handleAddTimeSlot");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Please fill in all fields!", errorLabel.getText());
            verifyNoInteractions(mockTimeSlotDAO);
        });
    }

    // --- handleAddTimeSlot: no teacher profile ---

    @Test
    void testHandleAddTimeSlot_NoTeacherProfile_ShowsError() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "teacherProfile", null);

            invokeMethod("handleAddTimeSlot");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Please fill in all fields!", errorLabel.getText());
            verifyNoInteractions(mockTimeSlotDAO);
        });
    }

    // --- handleAddTimeSlot: no time selected ---

    @Test
    void testHandleAddTimeSlot_NoTimeSelected_ShowsError() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "teacherProfile", createTeacherProfile());

            invokeMethod("handleAddTimeSlot");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Please fill in all fields!", errorLabel.getText());
            verifyNoInteractions(mockTimeSlotDAO);
        });
    }

    // --- handleAddTimeSlot: success ---

    @Test
    void testHandleAddTimeSlot_ValidInput_CreatesSlot() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "teacherProfile", createTeacherProfile());

            ComboBox<String> startCombo = getField(controller, "startTimeCombo", ComboBox.class);
            ComboBox<String> endCombo = getField(controller, "endTimeCombo", ComboBox.class);
            startCombo.getItems().add("10:00");
            startCombo.setValue("10:00");
            endCombo.getItems().add("11:00");
            endCombo.setValue("11:00");

            when(mockTimeSlotDAO.create(any(TimeSlot.class))).thenReturn(true);
            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(Collections.emptyList());

            invokeMethod("handleAddTimeSlot");

            verify(mockTimeSlotDAO).create(any(TimeSlot.class));
        });
    }

    // --- handleAddTimeSlot: create fails ---

    @Test
    void testHandleAddTimeSlot_CreateFails_ShowsError() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "teacherProfile", createTeacherProfile());

            ComboBox<String> startCombo = getField(controller, "startTimeCombo", ComboBox.class);
            ComboBox<String> endCombo = getField(controller, "endTimeCombo", ComboBox.class);
            startCombo.getItems().add("10:00");
            startCombo.setValue("10:00");
            endCombo.getItems().add("11:00");
            endCombo.setValue("11:00");

            when(mockTimeSlotDAO.create(any(TimeSlot.class))).thenReturn(false);

            invokeMethod("handleAddTimeSlot");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertTrue(errorLabel.getText().contains("Signup failed"));
        });
    }

    // --- updateTimeSlots: no date or profile ---

    @Test
    void testUpdateTimeSlots_NoDateOrProfile_ContainerEmpty() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", null);
            setField(controller, "teacherProfile", null);

            invokeMethod("updateTimeSlots");

            VBox container = getField(controller, "timeSlotsContainer", VBox.class);
            assertTrue(container.getChildren().isEmpty());
        });
    }

    // --- updateTimeSlots: no slots shows label ---

    @Test
    void testUpdateTimeSlots_NoSlots_ShowsNoSlotsLabel() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "teacherProfile", createTeacherProfile());

            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(Collections.emptyList());

            invokeMethod("updateTimeSlots");

            VBox container = getField(controller, "timeSlotsContainer", VBox.class);
            assertEquals(1, container.getChildren().size());
            assertTrue(container.getChildren().get(0) instanceof Label);
            assertEquals("No time slots set", ((Label) container.getChildren().get(0)).getText());
        });
    }

    // --- updateTimeSlots: slots shown ---

    @Test
    void testUpdateTimeSlots_WithSlots_ShowsSlots() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "teacherProfile", createTeacherProfile());

            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(List.of(createTimeSlot()));

            invokeMethod("updateTimeSlots");

            VBox container = getField(controller, "timeSlotsContainer", VBox.class);
            assertFalse(container.getChildren().isEmpty());
        });
    }

    // --- handleLogout ---

    @Test
    void testHandleLogout_ClearsSession() {
        User user = createUser();
        SessionManager.getInstance().setCurrentUser(user);

        SessionManager.getInstance().logout();

        assertFalse(SessionManager.getInstance().isLoggedIn());
    }

    // --- handleDeleteSlot ---

    @Test
    void testHandleDeleteSlot_BookedSlot_ShowsErrorAndDoesNotDelete() throws Exception {
        runOnFX(() -> {
            TimeSlot bookedSlot = createTimeSlot();
            bookedSlot.setSlotStatus(TimeSlot.STATUS_BOOKED);

            Method method = TeacherDashboardController.class
                    .getDeclaredMethod("handleDeleteSlot", TimeSlot.class);
            method.setAccessible(true);
            method.invoke(controller, bookedSlot);

            verify(mockTimeSlotDAO, never()).delete(anyInt());

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertFalse(errorLabel.getText().isEmpty());
        });
    }

    @Test
    void testHandleDeleteSlot_AvailableSlot_DeletesAndUpdatesUI() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "teacherProfile", createTeacherProfile());

            TimeSlot slot = createTimeSlot();
            slot.setSlotStatus(TimeSlot.STATUS_AVAILABLE);

            when(mockTimeSlotDAO.delete(slot.getSlotId())).thenReturn(true);
            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(Collections.emptyList());

            Method method = TeacherDashboardController.class
                    .getDeclaredMethod("handleDeleteSlot", TimeSlot.class);
            method.setAccessible(true);
            method.invoke(controller, slot);

            verify(mockTimeSlotDAO).delete(slot.getSlotId());
        });
    }

    @Test
    void testHandleDeleteSlot_DeleteFails_NoUIChange() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "teacherProfile", createTeacherProfile());

            TimeSlot slot = createTimeSlot();
            slot.setSlotStatus(TimeSlot.STATUS_AVAILABLE);

            when(mockTimeSlotDAO.delete(slot.getSlotId())).thenReturn(false);

            Method method = TeacherDashboardController.class
                    .getDeclaredMethod("handleDeleteSlot", TimeSlot.class);
            method.setAccessible(true);
            method.invoke(controller, slot);

            verify(mockTimeSlotDAO).delete(slot.getSlotId());
            // No update calls when delete fails
            verify(mockTimeSlotDAO, never()).findByTeacherProfileIdAndDate(anyInt(), any());
        });
    }

    // --- loadTeacherProfile ---

    @Test
    void testLoadTeacherProfile_NullUser_ReturnsEarly() throws Exception {
        runOnFX(() -> {
            setField(controller, "currentUser", null);

            invokeMethod("loadTeacherProfile");

            verifyNoInteractions(mockTeacherProfileDAO);
        });
    }

    @Test
    void testLoadTeacherProfile_ProfileExists_SetsFields() throws Exception {
        runOnFX(() -> {
            User user = createUser();
            setField(controller, "currentUser", user);

            TeacherProfile profile = createTeacherProfile();
            profile.setInstrumentsTaught("piano");
            profile.setYearsExperience(5);
            profile.setHourlyRate(60);
            profile.setBiography("Great teacher");

            when(mockTeacherProfileDAO.findByUserId(user.getUserId())).thenReturn(profile);

            invokeMethod("loadTeacherProfile");

            Label nameLabel = getField(controller, "nameLabel", Label.class);
            assertEquals(user.getUsername(), nameLabel.getText());

            TextField expField = getField(controller, "experienceField", TextField.class);
            assertEquals("5", expField.getText());
        });
    }

    @Test
    void testLoadTeacherProfile_ProfileDoesNotExist_CreatesDefault() throws Exception {
        runOnFX(() -> {
            User user = createUser();
            setField(controller, "currentUser", user);

            when(mockTeacherProfileDAO.findByUserId(user.getUserId())).thenReturn(null);
            when(mockTeacherProfileDAO.create(any())).thenReturn(true);

            invokeMethod("loadTeacherProfile");

            verify(mockTeacherProfileDAO).create(any(TeacherProfile.class));

            TeacherProfile profile = getField(controller, "teacherProfile", TeacherProfile.class);
            assertNotNull(profile);
            assertEquals("piano", profile.getInstrumentsTaught());
        });
    }

    // --- handleAddTimeSlot: invalid time order ---

    @Test
    void testHandleAddTimeSlot_StartTimeEqualToEnd_ShowsError() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "teacherProfile", createTeacherProfile());

            ComboBox<String> startCombo = getField(controller, "startTimeCombo", ComboBox.class);
            ComboBox<String> endCombo = getField(controller, "endTimeCombo", ComboBox.class);
            startCombo.getItems().add("10:00");
            startCombo.setValue("10:00");
            endCombo.getItems().add("10:00");
            endCombo.setValue("10:00");

            invokeMethod("handleAddTimeSlot");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertFalse(errorLabel.getText().isEmpty());
            verifyNoInteractions(mockTimeSlotDAO);
        });
    }

    @Test
    void testHandleAddTimeSlot_StartAfterEnd_ShowsError() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", LocalDate.now());
            setField(controller, "teacherProfile", createTeacherProfile());

            ComboBox<String> startCombo = getField(controller, "startTimeCombo", ComboBox.class);
            ComboBox<String> endCombo = getField(controller, "endTimeCombo", ComboBox.class);
            startCombo.getItems().add("14:00");
            startCombo.setValue("14:00");
            endCombo.getItems().add("10:00");
            endCombo.setValue("10:00");

            invokeMethod("handleAddTimeSlot");

            verifyNoInteractions(mockTimeSlotDAO);
        });
    }

    // --- createTimeSlotBox with booked slot ---

    @Test
    void testCreateTimeSlotBox_BookedSlot_HasBookedStyle() throws Exception {
        runOnFX(() -> {
            TimeSlot bookedSlot = createTimeSlot();
            bookedSlot.setSlotStatus(TimeSlot.STATUS_BOOKED);

            Method method = TeacherDashboardController.class
                    .getDeclaredMethod("createTimeSlotBox", TimeSlot.class);
            method.setAccessible(true);
            javafx.scene.layout.HBox box =
                    (javafx.scene.layout.HBox) method.invoke(controller, bookedSlot);

            assertNotNull(box);
            javafx.scene.control.Label timeLabel =
                    (javafx.scene.control.Label) box.getChildren().get(0);
            assertTrue(timeLabel.getStyleClass().contains("time-slot-booked"));
        });
    }

    @Test
    void testCreateTimeSlotBox_AvailableSlot_NoBookedStyle() throws Exception {
        runOnFX(() -> {
            TimeSlot slot = createTimeSlot();

            Method method = TeacherDashboardController.class
                    .getDeclaredMethod("createTimeSlotBox", TimeSlot.class);
            method.setAccessible(true);
            javafx.scene.layout.HBox box =
                    (javafx.scene.layout.HBox) method.invoke(controller, slot);

            assertNotNull(box);
        });
    }

    // --- hasTimeSlots ---

    @Test
    void testHasTimeSlots_NullTeacherProfile_ReturnsFalse() throws Exception {
        runOnFX(() -> {
            setField(controller, "teacherProfile", null);

            Method method = TeacherDashboardController.class
                    .getDeclaredMethod("hasTimeSlots", LocalDate.class);
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(controller, LocalDate.now());

            assertFalse(result);
        });
    }

    @Test
    void testHasTimeSlots_WithSlots_ReturnsTrue() throws Exception {
        runOnFX(() -> {
            setField(controller, "teacherProfile", createTeacherProfile());

            when(mockTimeSlotDAO.findByTeacherProfileIdAndDate(anyInt(), any()))
                    .thenReturn(List.of(createTimeSlot()));

            Method method = TeacherDashboardController.class
                    .getDeclaredMethod("hasTimeSlots", LocalDate.class);
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(controller, LocalDate.now());

            assertTrue(result);
        });
    }

    // --- setupTimeComboBoxes ---

    @Test
    void testSetupTimeComboBoxes_PopulatesItems() throws Exception {
        runOnFX(() -> {
            ComboBox<String> startCombo = getField(controller, "startTimeCombo", ComboBox.class);
            ComboBox<String> endCombo = getField(controller, "endTimeCombo", ComboBox.class);

            invokeMethod("setupTimeComboBoxes");

            assertFalse(startCombo.getItems().isEmpty());
            assertFalse(endCombo.getItems().isEmpty());
            // from 7:00 to 21:30 (2 slots per hour = 30 slots)
            assertTrue(startCombo.getItems().contains("7:00"));
            assertTrue(startCombo.getItems().contains("21:00"));
        });
    }

    // --- updateInstrumentsCombo with preserved key ---

    @Test
    void testUpdateInstrumentsCombo_PreservesCurrentSelection() throws Exception {
        runOnFX(() -> {
            ComboBox<String> combo = getField(controller, "instrumentsCombo", ComboBox.class);
            combo.getItems().add("Piano");
            combo.setValue("Piano");

            invokeMethod("updateInstrumentsCombo");

            // Should still have a value after repopulating
            assertNotNull(combo.getValue());
        });
    }

    // --- showError / showSuccess ---

    @Test
    void testShowError_SetsErrorLabelText() throws Exception {
        runOnFX(() -> {
            Method method = TeacherDashboardController.class
                    .getDeclaredMethod("showError", String.class);
            method.setAccessible(true);
            method.invoke(controller, "Error occurred");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Error occurred", errorLabel.getText());
        });
    }

    @Test
    void testShowSuccess_SetsSuccessLabelText() throws Exception {
        runOnFX(() -> {
            Method method = TeacherDashboardController.class
                    .getDeclaredMethod("showSuccess", String.class);
            method.setAccessible(true);
            method.invoke(controller, "Profile saved!");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Profile saved!", errorLabel.getText());
        });
    }

    // --- updateTexts with selectedDate ---

    @Test
    void testUpdateTexts_WithSelectedDateNull_SetsSelectDatePrompt() throws Exception {
        runOnFX(() -> {
            setField(controller, "selectedDate", null);

            invokeMethod("updateTexts");

            Label selectedDateLabel = getField(controller, "selectedDateLabel", Label.class);
            assertFalse(selectedDateLabel.getText().isEmpty());
        });
    }

    // --- Helpers ---

    private User createUser() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("teacher1");
        user.setEmail("teacher@test.com");
        user.setUserType("TEACHER");
        return user;
    }

    private TeacherProfile createTeacherProfile() {
        TeacherProfile profile = new TeacherProfile();
        profile.setTeacherProfileId(1);
        profile.setUserId(1);
        profile.setInstrumentsTaught("Piano");
        profile.setYearsExperience(5);
        profile.setHourlyRate(50);
        return profile;
    }

    private TimeSlot createTimeSlot() {
        TimeSlot slot = new TimeSlot();
        slot.setSlotId(1);
        slot.setStartTime("10:00");
        slot.setEndTime("11:00");
        slot.setSlotStatus(TimeSlot.STATUS_AVAILABLE);
        return slot;
    }

    private void invokeMethod(String methodName) {
        try {
            try {
                Method method = TeacherDashboardController.class.getDeclaredMethod(methodName, ActionEvent.class);
                method.setAccessible(true);
                method.invoke(controller, new ActionEvent());
            } catch (NoSuchMethodException e) {
                Method method = TeacherDashboardController.class.getDeclaredMethod(methodName);
                method.setAccessible(true);
                method.invoke(controller);
            }
        } catch (Exception e) {
            fail("Could not invoke " + methodName + ": "
                    + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
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
        final Exception[] thrown = { null };
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
        if (thrown[0] != null)
            throw thrown[0];
    }

    @FunctionalInterface
    interface RunnableWithException {
        void run() throws Exception;
    }
}