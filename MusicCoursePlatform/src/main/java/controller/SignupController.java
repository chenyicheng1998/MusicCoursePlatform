package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;

public class SignupController extends BaseController {

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
    public void initialize() {
        initializeBase();
    }

    @Override
    protected void updateTexts() {
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

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle(localizationManager.getString("app.title.login"));
        } catch (IOException e) {
            LOGGER.warning("Failed to load login screen: " + e.getMessage());
            showError(localizationManager.getString("error.load.dashboard"));
        }
    }
}
