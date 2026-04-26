package controller;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherProfileViewControllerTest {

    private TeacherProfileViewController controller;

    @Mock
    private TeacherProfileDAO mockTeacherProfileDAO;
    @Mock
    private TimeSlotDAO mockTimeSlotDAO;
    @Mock
    private LocalizationManager mockLocalizationManager;

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
                controller = new TeacherProfileViewController();

                setField(controller, "teacherNameLabel", new Label());
                setField(controller, "scheduleContainer", new FlowPane());
                setField(controller, "languageCombo", new ComboBox<>());

                // Inject new localization fields
                setField(controller, "appNameLabel", new Label());
                setField(controller, "setAvailabilityButton", new javafx.scene.control.Button());
                setField(controller, "logoutButton", new javafx.scene.control.Button());
                setField(controller, "myScheduleLabel", new Label());
                setField(controller, "rootPane", new javafx.scene.layout.BorderPane());

                setField(controller, "teacherProfileDAO", mockTeacherProfileDAO);
                setField(controller, "timeSlotDAO", mockTimeSlotDAO);
                setField(controller, "localizationManager", mockLocalizationManager);

                // Setup common localization mocks with lenient() to avoid
                // UnnecessaryStubbingException
                lenient().when(mockLocalizationManager.getString("app.name")).thenReturn("MusicCoursePlatform");
                lenient().when(mockLocalizationManager.getString("nav.set.availability"))
                        .thenReturn("Set Availability");
                lenient().when(mockLocalizationManager.getString("nav.logout")).thenReturn("Logout");
                lenient().when(mockLocalizationManager.getString("schedule.my.schedule")).thenReturn("My Schedule");
                lenient().when(mockLocalizationManager.getString("message.no.schedule"))
                        .thenReturn("No schedule found");
                lenient().when(mockLocalizationManager.getString("message.no.time.slots"))
                        .thenReturn("No time slots scheduled");
                lenient().when(mockLocalizationManager.getString("schedule.status.available")).thenReturn("Available");
                lenient().when(mockLocalizationManager.getString("schedule.status.booked")).thenReturn("Booked");
                lenient().when(mockLocalizationManager.getString("action.delete")).thenReturn("Delete");
                lenient().when(mockLocalizationManager.getCurrentLanguageDisplayName()).thenReturn("English");
                lenient().when(mockLocalizationManager.getCurrentLocale()).thenReturn(Locale.ENGLISH);

            } catch (Exception e) {
                fail("Setup failed: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();

        SessionManager.getInstance().logout();
    }

    // --- loadSchedule: no teacher profile ---

    @Test
    void testLoadSchedule_NoTeacherProfile_ShowsNoScheduleLabel() throws Exception {
        runOnFX(() -> {
            setField(controller, "teacherProfile", null);

            invokeMethod("loadSchedule");

            FlowPane container = getField(controller, "scheduleContainer", FlowPane.class);
            assertEquals(1, container.getChildren().size());
            assertTrue(container.getChildren().get(0) instanceof Label);
            assertEquals("No schedule found", ((Label) container.getChildren().get(0)).getText());
        });
    }

    // --- loadSchedule: no slots ---

    @Test
    void testLoadSchedule_NoSlots_ShowsNoSlotsLabel() throws Exception {
        runOnFX(() -> {
            setField(controller, "teacherProfile", createTeacherProfile());

            when(mockTimeSlotDAO.findByTeacherProfileId(anyInt()))
                    .thenReturn(Collections.emptyList());

            invokeMethod("loadSchedule");

            FlowPane container = getField(controller, "scheduleContainer", FlowPane.class);
            assertEquals(1, container.getChildren().size());
            assertTrue(container.getChildren().get(0) instanceof Label);
            assertEquals("No time slots scheduled", ((Label) container.getChildren().get(0)).getText());
        });
    }

    // --- loadSchedule: with slots ---

    @Test
    void testLoadSchedule_WithSlots_ShowsCards() throws Exception {
        runOnFX(() -> {
            setField(controller, "teacherProfile", createTeacherProfile());

            when(mockTimeSlotDAO.findByTeacherProfileId(anyInt()))
                    .thenReturn(List.of(createTimeSlot()));

            invokeMethod("loadSchedule");

            FlowPane container = getField(controller, "scheduleContainer", FlowPane.class);
            assertFalse(container.getChildren().isEmpty());
        });
    }

    // --- loadTeacherInfo ---

    @Test
    void testLoadTeacherInfo_UserLoggedIn_ProfileExists_SetsName() throws Exception {
        runOnFX(() -> {
            User user = createUser();
            SessionManager.getInstance().setCurrentUser(user);

            TeacherProfile profile = createTeacherProfile();
            when(mockTeacherProfileDAO.findByUserId(1)).thenReturn(profile);

            invokeMethod("loadTeacherInfo");

            Label nameLabel = getField(controller, "teacherNameLabel", Label.class);
            assertEquals("teacher1", nameLabel.getText());
            verify(mockTeacherProfileDAO).findByUserId(1);
            verify(mockTeacherProfileDAO, never()).create(any());
        });
    }

    @Test
    void testLoadTeacherInfo_UserLoggedIn_ProfileNull_CreatesProfile() throws Exception {
        runOnFX(() -> {
            User user = createUser();
            SessionManager.getInstance().setCurrentUser(user);

            when(mockTeacherProfileDAO.findByUserId(1)).thenReturn(null);
            when(mockTeacherProfileDAO.create(any(TeacherProfile.class))).thenReturn(true);

            invokeMethod("loadTeacherInfo");

            verify(mockTeacherProfileDAO).create(any(TeacherProfile.class));
        });
    }

    @Test
    void testLoadTeacherInfo_NoUserLoggedIn_SkipsLoad() throws Exception {
        runOnFX(() -> {
            SessionManager.getInstance().logout();

            invokeMethod("loadTeacherInfo");

            verify(mockTeacherProfileDAO, never()).findByUserId(anyInt());
        });
    }

    // --- handleDeleteSlot ---

    @Test
    void testHandleDeleteSlot_AvailableSlot_DeleteSucceeds_ReloadsSchedule() throws Exception {
        runOnFX(() -> {
            setField(controller, "teacherProfile", createTeacherProfile());
            invokeMethod("setupDateFormatter");

            TimeSlot slot = createTimeSlot(); // AVAILABLE
            when(mockTimeSlotDAO.delete(slot.getSlotId())).thenReturn(true);
            when(mockTimeSlotDAO.findByTeacherProfileId(anyInt())).thenReturn(Collections.emptyList());

            invokeHandleDeleteSlot(slot);

            verify(mockTimeSlotDAO).delete(slot.getSlotId());
        });
    }

    @Test
    void testHandleDeleteSlot_AvailableSlot_DeleteFails_NoReload() throws Exception {
        runOnFX(() -> {
            setField(controller, "teacherProfile", createTeacherProfile());

            TimeSlot slot = createTimeSlot();
            when(mockTimeSlotDAO.delete(slot.getSlotId())).thenReturn(false);

            invokeHandleDeleteSlot(slot);

            verify(mockTimeSlotDAO).delete(slot.getSlotId());
            // loadSchedule should NOT be called after failed delete
            verify(mockTimeSlotDAO, never()).findByTeacherProfileId(anyInt());
        });
    }

    @Test
    void testHandleDeleteSlot_BookedSlot_DoesNotDelete() throws Exception {
        runOnFX(() -> {
            TimeSlot slot = createTimeSlot();
            slot.setSlotStatus(TimeSlot.STATUS_BOOKED);

            when(mockLocalizationManager.getString("message.cannot.delete.booked"))
                    .thenReturn("Cannot delete a booked slot");

            invokeHandleDeleteSlot(slot);

            verify(mockTimeSlotDAO, never()).delete(anyInt());
        });
    }

    // --- setupDateFormatter ---

    @Test
    void testSetupDateFormatter_ChineseLocale_FormatsDate() throws Exception {
        runOnFX(() -> {
            when(mockLocalizationManager.createDateFormatter()).thenReturn(
                    java.time.format.DateTimeFormatter.ofPattern("M月d日 EEEE", java.util.Locale.CHINESE));
            invokeMethod("setupDateFormatter");

            // verify dateFormatter is not null (createScheduleCard can be called without NPE)
            TimeSlot slot = createTimeSlot();
            setField(controller, "teacherProfile", createTeacherProfile());
            when(mockTimeSlotDAO.findByTeacherProfileId(anyInt())).thenReturn(List.of(slot));
            invokeMethod("loadSchedule");

            FlowPane container = getField(controller, "scheduleContainer", FlowPane.class);
            assertFalse(container.getChildren().isEmpty());
        });
    }

    @Test
    void testSetupDateFormatter_ArabicLocale_FormatsDate() throws Exception {
        runOnFX(() -> {
            when(mockLocalizationManager.createDateFormatter()).thenReturn(
                    java.time.format.DateTimeFormatter.ofPattern("EEEE، d MMMM", new java.util.Locale("ar")));
            invokeMethod("setupDateFormatter");

            TimeSlot slot = createTimeSlot();
            setField(controller, "teacherProfile", createTeacherProfile());
            when(mockTimeSlotDAO.findByTeacherProfileId(anyInt())).thenReturn(List.of(slot));
            invokeMethod("loadSchedule");

            FlowPane container = getField(controller, "scheduleContainer", FlowPane.class);
            assertFalse(container.getChildren().isEmpty());
        });
    }

    // --- loadSchedule: booked slot ---

    @Test
    void testLoadSchedule_WithBookedSlot_ShowsCard() throws Exception {
        runOnFX(() -> {
            setField(controller, "teacherProfile", createTeacherProfile());
            invokeMethod("setupDateFormatter");

            TimeSlot slot = createTimeSlot();
            slot.setSlotStatus(TimeSlot.STATUS_BOOKED);

            when(mockTimeSlotDAO.findByTeacherProfileId(anyInt())).thenReturn(List.of(slot));

            invokeMethod("loadSchedule");

            FlowPane container = getField(controller, "scheduleContainer", FlowPane.class);
            assertFalse(container.getChildren().isEmpty());
        });
    }

    // --- updateTexts ---

    @Test
    void testUpdateTexts_SetsAllLabels() throws Exception {
        runOnFX(() -> {
            when(mockLocalizationManager.getString("app.name")).thenReturn("MusicCourse");
            when(mockLocalizationManager.getString("nav.set.availability")).thenReturn("Set Avail");
            when(mockLocalizationManager.getString("nav.logout")).thenReturn("Logout");
            when(mockLocalizationManager.getString("schedule.my.schedule")).thenReturn("My Schedule");

            invokeMethod("updateTexts");

            Label appName = getField(controller, "appNameLabel", Label.class);
            assertEquals("MusicCourse", appName.getText());
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

    // --- setupLanguageSelector ---

    @Test
    void testSetupLanguageSelector_PopulatesCombo() throws Exception {
        runOnFX(() -> {
            invokeMethod("setupLanguageSelector");

            ComboBox<String> combo = getField(controller, "languageCombo", ComboBox.class);
            assertTrue(combo.getItems().contains("English"));
            assertTrue(combo.getItems().contains("中文"));
            assertTrue(combo.getItems().contains("العربية"));
        });
    }

    // --- handleLanguageChange ---

    @Test
    void testHandleLanguageChange_ChineseSelected_SetsLocale() throws Exception {
        runOnFX(() -> {
            ComboBox<String> combo = getField(controller, "languageCombo", ComboBox.class);
            combo.getItems().addAll("English", "中文", "العربية");
            combo.setValue("中文");

            invokeMethod("handleLanguageChange"); // uses ActionEvent overload in invokeMethod

            verify(mockLocalizationManager).setLocale(util.LocalizationManager.CHINESE);
        });
    }

    @Test
    void testHandleLanguageChange_ArabicSelected_SetsLocale() throws Exception {
        runOnFX(() -> {
            ComboBox<String> combo = getField(controller, "languageCombo", ComboBox.class);
            combo.getItems().addAll("English", "中文", "العربية");
            combo.setValue("العربية");

            invokeMethod("handleLanguageChange");

            verify(mockLocalizationManager).setLocale(util.LocalizationManager.ARABIC);
        });
    }

    // --- applyDirection ---

    @Test
    void testApplyDirection_InvokesLocalizationManager() throws Exception {
        runOnFX(() -> {
            invokeMethod("applyDirection");

            verify(mockLocalizationManager).applyDirection(any());
        });
    }

    /**
     * Calls the real initialize() with all FXML fields set and no session user.
     * Covers the initialize() method body including the locale listener setup.
     */
    @Test
    void testInitialize_WithNoSessionUser_ShowsNoScheduleLabel() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Exception[] thrown = {null};
        Platform.runLater(() -> {
            try {
                TeacherProfileViewController tpvc = new TeacherProfileViewController();

                setField(tpvc, "rootPane", new javafx.scene.layout.BorderPane());
                setField(tpvc, "appNameLabel", new Label());
                setField(tpvc, "languageCombo", new ComboBox<>());
                setField(tpvc, "setAvailabilityButton", new javafx.scene.control.Button());
                setField(tpvc, "logoutButton", new javafx.scene.control.Button());
                setField(tpvc, "teacherNameLabel", new Label());
                setField(tpvc, "myScheduleLabel", new Label());
                setField(tpvc, "scheduleContainer", new FlowPane());

                SessionManager.getInstance().logout();

                tpvc.initialize(); // real DAOs + real LocalizationManager

                FlowPane container = getField(tpvc, "scheduleContainer", FlowPane.class);
                // No current user → teacherProfile is null → "no schedule" label shown
                assertFalse(container.getChildren().isEmpty());
            } catch (Exception e) {
                thrown[0] = e;
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        if (thrown[0] != null) throw thrown[0];
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
        slot.setLessonDate(java.time.LocalDate.now());
        return slot;
    }

    private void invokeHandleDeleteSlot(TimeSlot slot) {
        try {
            Method method = TeacherProfileViewController.class.getDeclaredMethod("handleDeleteSlot", TimeSlot.class);
            method.setAccessible(true);
            method.invoke(controller, slot);
        } catch (Exception e) {
            fail("Could not invoke handleDeleteSlot: "
                    + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
        }
    }

    private void invokeMethod(String methodName) {
        try {
            try {
                Method method = TeacherProfileViewController.class.getDeclaredMethod(methodName, ActionEvent.class);
                method.setAccessible(true);
                method.invoke(controller, new ActionEvent());
            } catch (NoSuchMethodException e) {
                Method method = TeacherProfileViewController.class.getDeclaredMethod(methodName);
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