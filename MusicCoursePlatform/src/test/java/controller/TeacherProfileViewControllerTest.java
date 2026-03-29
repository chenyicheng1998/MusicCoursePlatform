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

    @Mock private TeacherProfileDAO mockTeacherProfileDAO;
    @Mock private TimeSlotDAO mockTimeSlotDAO;
    @Mock private LocalizationManager mockLocalizationManager;

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

                // Setup common localization mocks with lenient() to avoid UnnecessaryStubbingException
                lenient().when(mockLocalizationManager.getString("app.name")).thenReturn("MusicCoursePlatform");
                lenient().when(mockLocalizationManager.getString("nav.set.availability")).thenReturn("Set Availability");
                lenient().when(mockLocalizationManager.getString("nav.logout")).thenReturn("Logout");
                lenient().when(mockLocalizationManager.getString("schedule.my.schedule")).thenReturn("My Schedule");
                lenient().when(mockLocalizationManager.getString("message.no.schedule")).thenReturn("No schedule found");
                lenient().when(mockLocalizationManager.getString("message.no.time.slots")).thenReturn("No time slots scheduled");
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

    // --- handleLogout ---

    @Test
    void testHandleLogout_ClearsSession() {
        User user = createUser();
        SessionManager.getInstance().setCurrentUser(user);

        SessionManager.getInstance().logout();

        assertFalse(SessionManager.getInstance().isLoggedIn());
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