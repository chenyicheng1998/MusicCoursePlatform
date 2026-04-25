package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.User;
import service.UserService;
import util.LocalizationManager;

import java.io.IOException;
import java.util.Locale;
import java.util.logging.Logger;

public abstract class BaseController {

    protected static final Logger LOGGER = Logger.getLogger(BaseController.class.getName());

    @FXML
    protected StackPane rootPane;
    @FXML
    protected ComboBox<String> languageCombo;
    @FXML
    protected Label languageLabel;
    @FXML
    protected Button backButton;
    @FXML
    protected Label titleLabel;
    @FXML
    protected Label errorLabel;

    protected UserService userService;
    protected LocalizationManager localizationManager;

    protected void initializeBase() {
        userService = new UserService();
        localizationManager = LocalizationManager.getInstance();

        setupLanguageSelector();
        updateTexts();

        localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            updateTexts();
            applyDirection();
        });

        applyDirection();
    }

    protected void setupLanguageSelector() {
        languageCombo.getItems().addAll("English", "中文", "العربية");
        languageCombo.setValue(localizationManager.getCurrentLanguageDisplayName());
    }

    @FXML
    protected void handleLanguageChange(ActionEvent event) {
        String selected = languageCombo.getValue();
        Locale newLocale = LocalizationManager.getLocaleFromDisplayName(selected);
        localizationManager.setLocale(newLocale);
    }

    protected void applyDirection() {
        localizationManager.applyDirection(rootPane);
    }

    protected abstract void updateTexts();

    protected void navigateToDashboard(ActionEvent event, User user) {
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
            LOGGER.warning("Failed to load dashboard: " + e.getMessage());
            showError(localizationManager.getString("error.load.dashboard"));
        }
    }

    protected void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
        }
    }

    protected void hideError() {
        if (errorLabel != null) {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }
}
