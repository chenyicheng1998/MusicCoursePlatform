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
 * Controller for the signup screen.
 * Handles user registration functionality.
 *
 * @author CHEN Yicheng
 * @version 1.0
 */
public class SignupController {

    @FXML
    private TextField usernameField;

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
        System.out.println("SignupController initialized");
    }

    /**
     * Handle "Sign up as Student" button click.
     * Currently just shows a message - business logic will be implemented later.
     */
    @FXML
    private void handleStudentSignup(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields!");
            return;
        }

        // TODO: Implement actual signup logic using UserService with userType="LEARNER"
        System.out.println("Student signup attempt - Username: " + username + ", Email: " + email);

        // For now, just show a success message
        showAlert(Alert.AlertType.INFORMATION, "Sign Up",
            "Student registration functionality will be implemented soon!");
    }

    /**
     * Handle "Sign up as Teacher" button click.
     * Currently just shows a message - business logic will be implemented later.
     */
    @FXML
    private void handleTeacherSignup(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields!");
            return;
        }

        // TODO: Implement actual signup logic using UserService with userType="TEACHER"
        System.out.println("Teacher signup attempt - Username: " + username + ", Email: " + email);

        // For now, just show a success message
        showAlert(Alert.AlertType.INFORMATION, "Sign Up",
            "Teacher registration functionality will be implemented soon!");
    }

    /**
     * Handle "Already have an account? Log in" link click.
     * Navigates back to login screen.
     */
    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            // Load login screen
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene loginScene = new Scene(loginRoot);

            // Get current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Music Course Platform - Login");

            System.out.println("Navigated back to login screen");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load login screen!");
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
