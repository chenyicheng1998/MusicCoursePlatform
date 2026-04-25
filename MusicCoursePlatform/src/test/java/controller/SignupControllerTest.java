package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignupControllerTest {

    private SignupController controller;

    @Mock
    private service.UserService mockUserService;

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
                controller = new SignupController();
                setField(controller, "usernameField", new TextField());
                setField(controller, "emailField", new TextField());
                setField(controller, "passwordField", new PasswordField());
                setField(controller, "errorLabel", new Label());
                setField(controller, "userService", mockUserService);
                setField(controller, "localizationManager", util.LocalizationManager.getInstance());
            } catch (Exception e) {
                fail("Setup failed: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    // --- Empty field validation ---

    @Test
    void testStudentSignup_EmptyUsername_ShowsError() throws Exception {
        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("");
            getField(controller, "emailField", TextField.class).setText("test@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            invokeMethod("handleStudentSignup");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Please fill in all fields!", errorLabel.getText());
            assertTrue(errorLabel.isVisible());
        });
    }

    @Test
    void testStudentSignup_EmptyEmail_ShowsError() throws Exception {
        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("testuser");
            getField(controller, "emailField", TextField.class).setText("");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            invokeMethod("handleStudentSignup");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Please fill in all fields!", errorLabel.getText());
        });
    }

    @Test
    void testStudentSignup_EmptyPassword_ShowsError() throws Exception {
        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("testuser");
            getField(controller, "emailField", TextField.class).setText("test@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("");

            invokeMethod("handleStudentSignup");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Please fill in all fields!", errorLabel.getText());
        });
    }

    // --- Email validation ---

    @Test
    void testStudentSignup_InvalidEmail_ShowsError() throws Exception {
        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("testuser");
            getField(controller, "emailField", TextField.class).setText("notanemail");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            invokeMethod("handleStudentSignup");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Please enter a valid email address!", errorLabel.getText());
        });
    }

    // --- Password length validation ---

    @Test
    void testStudentSignup_ShortPassword_ShowsError() throws Exception {
        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("testuser");
            getField(controller, "emailField", TextField.class).setText("test@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("123");

            invokeMethod("handleStudentSignup");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Password must be at least 6 characters!", errorLabel.getText());
        });
    }

    // --- Successful registration ---

    @Test
    void testStudentSignup_ValidInput_CallsRegisterWithLearner() throws Exception {
        User mockUser = new User("testuser", "hash", "test@test.com", "LEARNER");
        when(mockUserService.registerUser("testuser", "password123", "test@test.com", "LEARNER"))
                .thenReturn(mockUser);

        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("testuser");
            getField(controller, "emailField", TextField.class).setText("test@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            invokeMethod("handleStudentSignup");

            verify(mockUserService).registerUser("testuser", "password123", "test@test.com", "LEARNER");
        });
    }

    @Test
    void testTeacherSignup_ValidInput_CallsRegisterWithTeacher() throws Exception {
        User mockUser = new User("teacheruser", "hash", "teacher@test.com", "TEACHER");
        when(mockUserService.registerUser("teacheruser", "password123", "teacher@test.com", "TEACHER"))
                .thenReturn(mockUser);

        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("teacheruser");
            getField(controller, "emailField", TextField.class).setText("teacher@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            invokeMethod("handleTeacherSignup");

            verify(mockUserService).registerUser("teacheruser", "password123", "teacher@test.com", "TEACHER");
        });
    }

    // --- Exception handling ---

    @Test
    void testStudentSignup_IllegalArgumentException_ShowsError() throws Exception {
        when(mockUserService.registerUser(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Username already taken"));

        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("testuser");
            getField(controller, "emailField", TextField.class).setText("test@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            invokeMethod("handleStudentSignup");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Username already taken", errorLabel.getText());
        });
    }

    @Test
    void testStudentSignup_GenericException_ShowsError() throws Exception {
        when(mockUserService.registerUser(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("DB error"));

        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("testuser");
            getField(controller, "emailField", TextField.class).setText("test@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            invokeMethod("handleStudentSignup");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertTrue(errorLabel.getText().contains("Registration failed"));
        });
    }

    // --- Additional tests for SignupController coverage ---

    /**
     * Test initialize with all required fields to cover initializeBase(),
     * updateTexts(), setupLanguageSelector(), applyDirection() in BaseController.
     */
    @Test
    void testInitializeWithAllFields_CoversUpdateTexts() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Exception[] thrown = {null};
        Platform.runLater(() -> {
            try {
                SignupController fullController = new SignupController();

                // BaseController fields
                setField(fullController, "languageCombo", new ComboBox<>());
                setField(fullController, "languageLabel", new Label());
                setField(fullController, "titleLabel", new Label());
                setField(fullController, "errorLabel", new Label());
                setField(fullController, "backButton", new Button());
                setField(fullController, "rootPane", new StackPane());

                // SignupController-specific fields
                setField(fullController, "usernameField", new TextField());
                setField(fullController, "emailField", new TextField());
                setField(fullController, "passwordField", new PasswordField());
                setField(fullController, "studentButton", new Button());
                setField(fullController, "teacherButton", new Button());
                setField(fullController, "haveAccountLabel", new Label());
                setField(fullController, "loginLink", new Hyperlink());

                fullController.initialize();

                Label titleLabel = getField(fullController, "titleLabel", Label.class);
                assertFalse(titleLabel.getText().isEmpty());
            } catch (Exception e) {
                thrown[0] = e;
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        if (thrown[0] != null) throw thrown[0];
    }

    @Test
    void testTeacherSignup_NullReturn_DoesNotNavigate() throws Exception {
        when(mockUserService.registerUser(anyString(), anyString(), anyString(), eq("TEACHER")))
                .thenReturn(null);

        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("teacher1");
            getField(controller, "emailField", TextField.class).setText("t@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            invokeMethod("handleTeacherSignup");

            // When null is returned, no navigation happens (no exception)
            verify(mockUserService).registerUser("teacher1", "password123", "t@test.com", "TEACHER");
        });
    }

    @Test
    void testTeacherSignup_EmptyUsername_ShowsError() throws Exception {
        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("");
            getField(controller, "emailField", TextField.class).setText("t@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            invokeMethod("handleTeacherSignup");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Please fill in all fields!", errorLabel.getText());
        });
    }

    @Test
    void testTeacherSignup_GenericException_ShowsError() throws Exception {
        when(mockUserService.registerUser(anyString(), anyString(), anyString(), eq("TEACHER")))
                .thenThrow(new RuntimeException("DB error"));

        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("user");
            getField(controller, "emailField", TextField.class).setText("u@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("pass123");

            invokeMethod("handleTeacherSignup");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertTrue(errorLabel.getText().contains("Registration failed"));
        });
    }

    @Test
    void testStudentSignup_NullReturn_NoNavigationNoException() throws Exception {
        when(mockUserService.registerUser(anyString(), anyString(), anyString(), eq("LEARNER")))
                .thenReturn(null);

        runOnFX(() -> {
            getField(controller, "usernameField", TextField.class).setText("learner1");
            getField(controller, "emailField", TextField.class).setText("l@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            invokeMethod("handleStudentSignup");

            verify(mockUserService).registerUser("learner1", "password123", "l@test.com", "LEARNER");
        });
    }

    // --- isValidEmail ---

    @Test
    void testIsValidEmail_ValidEmails_ReturnsTrue() throws Exception {
        runOnFX(() -> {
            // Valid email - should not show invalid email error
            getField(controller, "usernameField", TextField.class).setText("user");
            getField(controller, "emailField", TextField.class).setText("valid.email+tag@domain.co.uk");
            getField(controller, "passwordField", PasswordField.class).setText("password123");

            when(mockUserService.registerUser(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(null);

            invokeMethod("handleStudentSignup");

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertNotEquals("Please enter a valid email address!", errorLabel.getText());
        });
    }

    // --- Helpers ---

    private void invokeMethod(String methodName) {
        try {
            Method method = SignupController.class.getDeclaredMethod(methodName, ActionEvent.class);
            method.setAccessible(true);
            method.invoke(controller, new ActionEvent());
        } catch (Exception e) {
            fail("Could not invoke " + methodName + ": " + e.getMessage());
        }
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = findField(target.getClass(), fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static <T> T getField(Object target, String fieldName, Class<T> type) {
        try {
            Field field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            return type.cast(field.get(target));
        } catch (Exception e) {
            fail("Could not get field: " + fieldName);
            return null;
        }
    }

    private static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field not found in hierarchy: " + fieldName);
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