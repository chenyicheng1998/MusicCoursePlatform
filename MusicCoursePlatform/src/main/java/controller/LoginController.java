package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the login screen.
 * Handles user login functionality.
 *
 * @author CHEN Yicheng
 * @version 1.0
 */
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    /**
     * Initialize method called after FXML is loaded.
     */
    @FXML
    public void initialize() {
        // Initialization logic can be added here
        System.out.println("LoginController initialized");
    }

    /**
     * Handle login button click.
     * Currently just shows a message - business logic will be implemented later.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields!");
            return;
        }

        // TODO: Implement actual login logic using UserService
        System.out.println("Login attempt - Email: " + email);

        // For now, just show a success message
        showAlert(Alert.AlertType.INFORMATION, "Login", "Login functionality will be implemented soon!");
    }

    /**
     * Handle "Create account" link click.
     * Navigates to signup screen.
     */
    @FXML
    private void handleSignup(ActionEvent event) {
        try {
            // Load signup screen
            Parent signupRoot = FXMLLoader.load(getClass().getResource("/fxml/signup.fxml"));
            Scene signupScene = new Scene(signupRoot);

            // Get current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(signupScene);
            stage.setTitle("Music Course Platform - Sign Up");

            System.out.println("Navigated to signup screen");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load signup screen!");
        }
    }

    /**
     * Show alert dialog.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
