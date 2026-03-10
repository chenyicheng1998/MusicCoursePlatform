package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import service.UserService;
import util.LanguageManager;

import java.io.IOException;
import java.util.Locale;

public class SignupController implements LanguageManager.LanguageChangeListener {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Label titleLabel;
    @FXML private Label hasAccountLabel;
    @FXML private Hyperlink loginLink;
    @FXML private Button studentSignupButton;
    @FXML private Button teacherSignupButton;
    @FXML private ComboBox<String> languageCombo;

    private UserService userService;
    private LanguageManager langManager;
    private boolean updatingLanguageCombo = false;

    @FXML
    public void initialize() {
        userService = new UserService();
        langManager = LanguageManager.getInstance();
        langManager.addLanguageChangeListener(this);
        setupLanguageCombo();
        updateTexts();
    }

    private void setupLanguageCombo() {
        if (languageCombo != null) {
            updatingLanguageCombo = true;
            languageCombo.getItems().clear();
            languageCombo.getItems().addAll(
                langManager.getString("language.english"),
                langManager.getString("language.finnish")
            );
            languageCombo.setValue(langManager.isEnglish() 
                ? langManager.getString("language.english") 
                : langManager.getString("language.finnish"));
            updatingLanguageCombo = false;
            
            languageCombo.setOnAction(e -> {
                if (updatingLanguageCombo) return;
                String selected = languageCombo.getValue();
                if (selected != null) {
                    if (selected.equals("English") || selected.equals("Englanti")) {
                        langManager.setLanguage("EN");
                    } else {
                        langManager.setLanguage("FI");
                    }
                }
            });
        }
    }

    private void updateLanguageComboDisplay() {
        if (languageCombo != null && !updatingLanguageCombo) {
            updatingLanguageCombo = true;
            languageCombo.getItems().clear();
            languageCombo.getItems().addAll(
                langManager.getString("language.english"),
                langManager.getString("language.finnish")
            );
            languageCombo.setValue(langManager.isEnglish() 
                ? langManager.getString("language.english") 
                : langManager.getString("language.finnish"));
            updatingLanguageCombo = false;
        }
    }

    @Override
    public void onLanguageChanged(Locale newLocale) {
        updateTexts();
        updateLanguageComboDisplay();
    }

    private void updateTexts() {
        if (titleLabel != null) titleLabel.setText(langManager.getString("signup.title"));
        if (usernameField != null) usernameField.setPromptText(langManager.getString("signup.username"));
        if (emailField != null) emailField.setPromptText(langManager.getString("signup.email"));
        if (passwordField != null) passwordField.setPromptText(langManager.getString("signup.password"));
        if (studentSignupButton != null) studentSignupButton.setText(langManager.getString("signup.student"));
        if (teacherSignupButton != null) teacherSignupButton.setText(langManager.getString("signup.teacher"));
        if (hasAccountLabel != null) hasAccountLabel.setText(langManager.getString("signup.hasAccount"));
        if (loginLink != null) loginLink.setText(langManager.getString("signup.login"));
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
            showError(langManager.getString("signup.error.empty"));
            return;
        }

        if (!isValidEmail(email)) {
            showError(langManager.getString("login.error.invalid"));
            return;
        }

        if (password.length() < 6) {
            showError(langManager.getString("signup.error.empty"));
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
            showError("Registration failed: " + e.getMessage());
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
                title = "Music Course Platform - Teacher Dashboard";
            } else {
                fxmlPath = "/fxml/student_course_booking.fxml";
                title = "Music Course Platform - Student Dashboard";
            }
            
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load dashboard!");
        }
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene loginScene = new Scene(loginRoot);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Music Course Platform - Login");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load login screen!");
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
