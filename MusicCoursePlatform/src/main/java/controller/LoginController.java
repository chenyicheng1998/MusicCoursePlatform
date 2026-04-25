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

public class LoginController extends BaseController {

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
    public void initialize() {
        initializeBase();
    }

    @Override
    protected void updateTexts() {
        languageLabel.setText(localizationManager.getString("language.selector"));
        titleLabel.setText(localizationManager.getString("login.title"));
        emailField.setPromptText(localizationManager.getString("login.email"));
        passwordField.setPromptText(localizationManager.getString("login.password"));
        loginButton.setText(localizationManager.getString("login.button"));
        noAccountLabel.setText(localizationManager.getString("login.no.account"));
        createAccountLink.setText(localizationManager.getString("login.create.account"));
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

    @FXML
    private void handleSignup(ActionEvent event) {
        try {
            Parent signupRoot = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            Scene signupScene = new Scene(signupRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(signupScene);
            stage.setTitle(localizationManager.getString("app.title.signup"));
        } catch (IOException e) {
            LOGGER.warning("Failed to load signup screen: " + e.getMessage());
            showError(localizationManager.getString("error.load.signup"));
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        emailField.clear();
        passwordField.clear();
        hideError();
    }
}
