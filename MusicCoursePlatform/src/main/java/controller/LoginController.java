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

public class LoginController implements LanguageManager.LanguageChangeListener {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Label titleLabel;
    @FXML private Label noAccountLabel;
    @FXML private Hyperlink createAccountLink;
    @FXML private Button loginButton;
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
        if (titleLabel != null) titleLabel.setText(langManager.getString("login.title"));
        if (emailField != null) emailField.setPromptText(langManager.getString("login.email"));
        if (passwordField != null) passwordField.setPromptText(langManager.getString("login.password"));
        if (loginButton != null) loginButton.setText(langManager.getString("login.button"));
        if (noAccountLabel != null) noAccountLabel.setText(langManager.getString("login.noAccount"));
        if (createAccountLink != null) createAccountLink.setText(langManager.getString("login.createAccount"));
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError(langManager.getString("login.error.empty"));
            return;
        }

        try {
            User user = userService.authenticateByEmail(email, password);
            if (user != null) {
                SessionManager.getInstance().setCurrentUser(user);
                navigateToDashboard(event, user);
            } else {
                showError(langManager.getString("login.error.invalid"));
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError(langManager.getString("login.error.failed") + ": " + e.getMessage());
        }
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
    private void handleSignup(ActionEvent event) {
        try {
            Parent signupRoot = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            Scene signupScene = new Scene(signupRoot);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(signupScene);
            stage.setTitle("Music Course Platform - Sign Up");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not load signup screen!");
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
