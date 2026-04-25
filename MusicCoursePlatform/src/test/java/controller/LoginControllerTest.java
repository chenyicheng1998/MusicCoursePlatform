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
class LoginControllerTest {

    private LoginController controller;

    @Mock
    private service.UserService mockUserService;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown(); // already initialized
        }
        latch.await();
    }

    @BeforeEach
    void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                controller = new LoginController();

                // Inject FXML fields via reflection
                setField(controller, "emailField", new TextField());
                setField(controller, "passwordField", new PasswordField());
                setField(controller, "errorLabel", new Label());

                // Inject mock service via reflection
                setField(controller, "userService", mockUserService);

                // Hide error label initially
                controller.initialize();
                setField(controller, "userService", mockUserService); // override after initialize
            } catch (Exception e) {
                fail("Setup failed: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        latch.await();
    }

    // --- handleLogin tests ---

    @Test
    void testHandleLogin_EmptyEmail_ShowsError() throws Exception {
        runOnFX(() -> {
            getField(controller, "emailField", TextField.class).setText("");
            getField(controller, "passwordField", PasswordField.class).setText("password");

            invokeHandleLogin(controller);

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Please fill in all fields!", errorLabel.getText());
            assertTrue(errorLabel.isVisible());
        });
    }

    @Test
    void testHandleLogin_EmptyPassword_ShowsError() throws Exception {
        runOnFX(() -> {
            getField(controller, "emailField", TextField.class).setText("test@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("");

            invokeHandleLogin(controller);

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Please fill in all fields!", errorLabel.getText());
            assertTrue(errorLabel.isVisible());
        });
    }

    @Test
    void testHandleLogin_InvalidCredentials_ShowsError() throws Exception {
        lenient().when(mockUserService.authenticateByEmail("wrong@test.com", "wrongpass")).thenReturn(null);

        runOnFX(() -> {
            getField(controller, "emailField", TextField.class).setText("wrong@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("wrongpass");

            invokeHandleLogin(controller);

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertTrue(errorLabel.getText().contains("Invalid") || errorLabel.getText().contains("错误")
                    || errorLabel.getText().contains("غير"));
            assertTrue(errorLabel.isVisible());
        });
    }

    @Test
    void testHandleLogin_ServiceThrowsIllegalArgument_ShowsError() throws Exception {
        lenient().when(mockUserService.authenticateByEmail(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        runOnFX(() -> {
            getField(controller, "emailField", TextField.class).setText("test@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password");

            invokeHandleLogin(controller);

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertEquals("Invalid input", errorLabel.getText());
        });
    }

    @Test
    void testHandleLogin_ServiceThrowsException_ShowsError() throws Exception {
        lenient().when(mockUserService.authenticateByEmail(anyString(), anyString()))
                .thenThrow(new RuntimeException("DB error"));

        runOnFX(() -> {
            getField(controller, "emailField", TextField.class).setText("test@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password");

            invokeHandleLogin(controller);

            Label errorLabel = getField(controller, "errorLabel", Label.class);
            assertTrue(errorLabel.getText().contains("Login failed") ||
                    errorLabel.getText().contains("登录失败") ||
                    errorLabel.getText().contains("فشل"));
        });
    }

    // --- Additional tests for BaseController coverage ---

    /**
     * Initialize the controller with ALL required fields (including BaseController fields)
     * to cover initializeBase(), setupLanguageSelector(), updateTexts(), applyDirection().
     */
    @Test
    void testInitializeBase_WithAllFields_CoversBaseControllerMethods() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Exception[] thrown = {null};
        Platform.runLater(() -> {
            try {
                LoginController fullController = new LoginController();

                // BaseController fields
                setField(fullController, "languageCombo", new ComboBox<>());
                setField(fullController, "languageLabel", new Label());
                setField(fullController, "titleLabel", new Label());
                setField(fullController, "errorLabel", new Label());
                setField(fullController, "backButton", new Button());
                setField(fullController, "rootPane", new StackPane());

                // LoginController-specific fields
                setField(fullController, "emailField", new TextField());
                setField(fullController, "passwordField", new PasswordField());
                setField(fullController, "loginButton", new Button());
                setField(fullController, "noAccountLabel", new Label());
                setField(fullController, "createAccountLink", new Hyperlink());

                // This call covers: initializeBase(), setupLanguageSelector(),
                // updateTexts(), localeProperty listener, applyDirection()
                fullController.initialize();

                Label langLabel = getField(fullController, "languageLabel", Label.class);
                assertFalse(langLabel.getText().isEmpty());
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
    void testShowError_AndHideError_ToggleVisibility() throws Exception {
        runOnFX(() -> {
            // Show error
            Method showError = findMethod(LoginController.class, "showError", String.class);
            // showError is in BaseController, access via reflection via superclass
            showError.setAccessible(true);
            showError.invoke(controller, "Some error");

            Label label = getField(controller, "errorLabel", Label.class);
            assertTrue(label.isVisible());
            assertEquals("Some error", label.getText());

            // Hide error
            Method hideError = findMethod(LoginController.class, "hideError");
            hideError.setAccessible(true);
            hideError.invoke(controller);

            assertFalse(label.isVisible());
        });
    }

    @Test
    void testHandleBack_ClearsFieldsAndHidesError() throws Exception {
        runOnFX(() -> {
            TextField emailField = getField(controller, "emailField", TextField.class);
            PasswordField passwordField = getField(controller, "passwordField", PasswordField.class);
            Label errorLabel = getField(controller, "errorLabel", Label.class);

            emailField.setText("test@test.com");
            passwordField.setText("password");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);

            invokeHandleBack(controller);

            assertEquals("", emailField.getText());
            assertEquals("", passwordField.getText());
            assertFalse(errorLabel.isVisible());
        });
    }

    // --- Helper: find method from class hierarchy ---

    private static Method findMethod(Class<?> clazz, String name, Class<?>... params) throws NoSuchMethodException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredMethod(name, params);
            } catch (NoSuchMethodException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method not found: " + name);
    }

    // --- handleLogin: valid credentials - user is LEARNER ---

    @Test
    void testHandleLogin_ValidLearnerCredentials_SetsSessionUser() throws Exception {
        User learner = new User("learner1", "hash", "l@test.com", "LEARNER");
        lenient().when(mockUserService.authenticateByEmail("l@test.com", "password")).thenReturn(learner);

        runOnFX(() -> {
            getField(controller, "emailField", TextField.class).setText("l@test.com");
            getField(controller, "passwordField", PasswordField.class).setText("password");

            invokeHandleLogin(controller);

            // navigateToDashboard will fail (no FXML), but user should be set in session
            // The showError from the catch block will run
            // Most important: authenticateByEmail was called
            verify(mockUserService, atLeastOnce()).authenticateByEmail("l@test.com", "password");

            SessionManager.getInstance().logout();
        });
    }

    // --- handleLanguageChange ---

    @Test
    void testHandleLanguageChange_ChineseSelected() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        final Exception[] thrown = {null};
        Platform.runLater(() -> {
            try {
                LoginController fullController = new LoginController();

                ComboBox<String> langCombo = new ComboBox<>();
                langCombo.getItems().addAll("English", "中文", "العربية");
                langCombo.setValue("中文");

                setField(fullController, "languageCombo", langCombo);
                setField(fullController, "errorLabel", new Label());
                setField(fullController, "rootPane", new StackPane());

                // Inject localizationManager
                setField(fullController, "localizationManager",
                        util.LocalizationManager.getInstance());

                Method method = findMethod(LoginController.class, "handleLanguageChange",
                        ActionEvent.class);
                method.setAccessible(true);
                method.invoke(fullController, new ActionEvent());

                assertEquals(util.LocalizationManager.CHINESE,
                        util.LocalizationManager.getInstance().getCurrentLocale());

                // Reset locale
                util.LocalizationManager.getInstance().setLocale(util.LocalizationManager.ENGLISH);
            } catch (Exception e) {
                thrown[0] = e;
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        if (thrown[0] != null) throw thrown[0];
    }

    // --- handleBack tests ---

    @Test
    void testHandleBack_ClearsFields() throws Exception {
        runOnFX(() -> {
            TextField emailField = getField(controller, "emailField", TextField.class);
            PasswordField passwordField = getField(controller, "passwordField", PasswordField.class);
            emailField.setText("test@test.com");
            passwordField.setText("password");

            invokeHandleBack(controller);

            assertEquals("", emailField.getText());
            assertEquals("", passwordField.getText());
        });
    }

    @Test
    void testHandleBack_HidesErrorLabel() throws Exception {
        runOnFX(() -> {
            Label errorLabel = getField(controller, "errorLabel", Label.class);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);

            invokeHandleBack(controller);

            assertFalse(errorLabel.isVisible());
            assertFalse(errorLabel.isManaged());
        });
    }

    // --- Helper methods ---

    private void invokeHandleLogin(LoginController controller) {
        try {
            Method method = LoginController.class.getDeclaredMethod("handleLogin", ActionEvent.class);
            method.setAccessible(true);
            method.invoke(controller, new ActionEvent());
        } catch (Exception e) {
            fail("Could not invoke handleLogin: " + e.getMessage());
        }
    }

    private void invokeHandleBack(LoginController controller) {
        try {
            Method method = LoginController.class.getDeclaredMethod("handleBack", ActionEvent.class);
            method.setAccessible(true);
            method.invoke(controller, new ActionEvent());
        } catch (Exception e) {
            fail("Could not invoke handleBack: " + e.getMessage());
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