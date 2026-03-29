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

public class LoginController {

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
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label noAccountLabel;

    @FXML
    private Hyperlink createAccountLink;

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
        languageCombo.setValue(localizationManager.getCurrentLanguageDisplayName());
    }

    @FXML
    private void handleLanguageChange(ActionEvent event) {
        String selected = languageCombo.getValue();
        Locale newLocale = LocalizationManager.getLocaleFromDisplayName(selected);

        localizationManager.setLocale(newLocale);
    }

    private void updateTexts() {
        languageLabel.setText(localizationManager.getString("language.selector"));
        titleLabel.setText(localizationManager.getString("login.title"));
        emailField.setPromptText(localizationManager.getString("login.email"));
        passwordField.setPromptText(localizationManager.getString("login.password"));
        loginButton.setText(localizationManager.getString("login.button"));
        noAccountLabel.setText(localizationManager.getString("login.no.account"));
        createAccountLink.setText(localizationManager.getString("login.create.account"));
    }

    private void applyDirection() {
        localizationManager.applyDirection(rootPane);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError(localizationManager.getString("error.fill.fields"));
            return;
        }

        try {
            User user = userService.authenticateByEmail(email, password);
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                navigateToDashboard(event, user);
            } else {
                showError(localizationManager.getString("error.invalid.credentials"));
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError(localizationManager.getString("error.login.failed").replace("{0}", e.getMessage()));
        }
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
    private void handleSignup(ActionEvent event) {
        try {
            Parent signupRoot = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            Scene signupScene = new Scene(signupRoot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(signupScene);
            stage.setTitle(localizationManager.getString("app.title.signup"));
        } catch (IOException e) {
            e.printStackTrace();
            showError(localizationManager.getString("error.load.signup"));
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        emailField.clear();
        passwordField.clear();
        hideError();
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    private void hideError() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }
}
