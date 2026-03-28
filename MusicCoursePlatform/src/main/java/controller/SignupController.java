package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.User;
import service.UserService;
import util.LocalizationManager;

import java.io.IOException;
import java.util.Locale;

public class SignupController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ComboBox<String> languageCombo;

    @FXML
    private Label languageLabel;

    @FXML
    private Button backButton;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button studentButton;

    @FXML
    private Button teacherButton;

    @FXML
    private Label haveAccountLabel;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private Label errorLabel;

    private UserService userService;
    private LocalizationManager localizationManager;

    @FXML
    public void initialize() {
        userService = new UserService();
        localizationManager = LocalizationManager.getInstance();

        // Setup language combo box
        setupLanguageSelector();

        // Initialize texts
        updateTexts();

        // Listen for locale changes
        localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            updateTexts();
            applyDirection();
        });

        // Apply initial direction
        applyDirection();
    }

    private void setupLanguageSelector() {
        languageCombo.getItems().addAll("English", "中文", "العربية");
        languageCombo.setValue("English");
    }

    @FXML
    private void handleLanguageChange(ActionEvent event) {
        String selected = languageCombo.getValue();
        Locale newLocale;

        switch (selected) {
            case "中文":
                newLocale = LocalizationManager.CHINESE;
                break;
            case "العربية":
                newLocale = LocalizationManager.ARABIC;
                break;
            default:
                newLocale = LocalizationManager.ENGLISH;
                break;
        }

        localizationManager.setLocale(newLocale);
    }

    private void updateTexts() {
        languageLabel.setText(localizationManager.getString("language.selector"));
        titleLabel.setText(localizationManager.getString("signup.title"));
        usernameField.setPromptText(localizationManager.getString("signup.username"));
        emailField.setPromptText(localizationManager.getString("signup.email"));
        passwordField.setPromptText(localizationManager.getString("signup.password"));
        studentButton.setText(localizationManager.getString("signup.as.student"));
        teacherButton.setText(localizationManager.getString("signup.as.teacher"));
        haveAccountLabel.setText(localizationManager.getString("signup.have.account"));
        loginLink.setText(localizationManager.getString("signup.login"));
    }

    private void applyDirection() {
        localizationManager.applyDirection(rootPane);
    }

    @FXML
    private void handleStudentSignup(ActionEvent event) {
        registerUser("LEARNER", event);
    }

    @FXML
    private void handleTeacherSignup(ActionEvent event) {
        registerUser("TEACHER", event);
    }

    private void registerUser(String userType, ActionEvent event) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError(localizationManager.getString("error.fill.fields"));
            return;
        }

        if (!isValidEmail(email)) {
            showError(localizationManager.getString("error.invalid.email"));
            return;
        }

        if (password.length() < 6) {
            showError(localizationManager.getString("error.weak.password"));
            return;
        }

        try {
            User user = userService.registerUser(username, password, email, userType);
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                navigateToDashboard(event, user);
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError(localizationManager.getString("error.signup.failed").replace("{0}", e.getMessage()));
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void navigateToDashboard(ActionEvent event, User user) {
        try {
            String fxmlPath;
            String title;

            if (user.isTeacher()) {
                fxmlPath = "/fxml/teacher_set_availability.fxml";
                title = localizationManager.getString("app.title.teacher.dashboard");
            } else {
                fxmlPath = "/fxml/student_course_booking.fxml";
                title = localizationManager.getString("app.title.student.dashboard");
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            showError(localizationManager.getString("error.load.dashboard"));
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene loginScene = new Scene(loginRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle(localizationManager.getString("app.title.login"));
        } catch (IOException e) {
            e.printStackTrace();
            showError(localizationManager.getString("error.load.dashboard"));
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }
}
