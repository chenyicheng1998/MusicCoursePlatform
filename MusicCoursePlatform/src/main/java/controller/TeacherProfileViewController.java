package controller;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import dao.BookingDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Booking;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;
import util.LocalizationManager;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TeacherProfileViewController {

    @FXML private BorderPane rootPane;
    @FXML private Label appNameLabel;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Button setAvailabilityButton;
    @FXML private Button logoutButton;
    @FXML private Label teacherNameLabel;
    @FXML private Label myScheduleLabel;
    @FXML private FlowPane scheduleContainer;

    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private BookingDAO bookingDAO;
    private LocalizationManager localizationManager;

    private TeacherProfile teacherProfile;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d");

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        bookingDAO = new BookingDAO();
        localizationManager = LocalizationManager.getInstance();

        setupLanguageSelector();
        loadTeacherInfo();
        updateTexts();
        loadSchedule();

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
        appNameLabel.setText(localizationManager.getString("app.name"));
        setAvailabilityButton.setText(localizationManager.getString("nav.set.availability"));
        logoutButton.setText(localizationManager.getString("nav.logout"));
        myScheduleLabel.setText(localizationManager.getString("schedule.my.schedule"));
    }

    private void applyDirection() {
        localizationManager.applyDirection(rootPane);
    }

    private void loadTeacherInfo() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            teacherNameLabel.setText(currentUser.getUsername());
            teacherProfile = teacherProfileDAO.findByUserId(currentUser.getUserId());

            if (teacherProfile == null) {
                teacherProfile = new TeacherProfile(currentUser.getUserId(), "Piano");
                teacherProfileDAO.create(teacherProfile);
            }
        }
    }

    private void loadSchedule() {
        scheduleContainer.getChildren().clear();

        if (teacherProfile == null) {
            Label noSchedule = new Label("No schedule found");
            noSchedule.setStyle("-fx-text-fill: #718096;");
            scheduleContainer.getChildren().add(noSchedule);
            return;
        }

        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileId(teacherProfile.getTeacherProfileId());

        if (slots.isEmpty()) {
            Label noSchedule = new Label("No time slots scheduled");
            noSchedule.setStyle("-fx-text-fill: #718096;");
            scheduleContainer.getChildren().add(noSchedule);
            return;
        }

        for (TimeSlot slot : slots) {
            VBox scheduleCard = createScheduleCard(slot);
            scheduleContainer.getChildren().add(scheduleCard);
        }
    }

    private VBox createScheduleCard(TimeSlot slot) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16;");
        card.setPrefWidth(200);
        card.setAlignment(Pos.TOP_LEFT);

        Label dateLabel = new Label(slot.getLessonDate().format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");

        String timeText = slot.getStartTime() + " - " + slot.getEndTime();
        String statusText = slot.isAvailable() ? "Available" : "Booked";

        HBox timeBox = new HBox(8);
        timeBox.setAlignment(Pos.CENTER_LEFT);

        Button timeBtn = new Button(timeText);
        if (slot.isAvailable()) {
            timeBtn.setStyle("-fx-background-color: #E8F5E9; -fx-text-fill: #2D4A47; -fx-background-radius: 8; -fx-padding: 8 16;");
        } else {
            // Booked slots display in red
            timeBtn.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #C62828; -fx-background-radius: 8; -fx-padding: 8 16;");
        }
        timeBtn.setPrefWidth(100);

        Button deleteBtn = new Button("Del");
        deleteBtn.setPrefWidth(40);
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-cursor: hand;");

        // Disable delete button if slot is booked
        if (!slot.isAvailable()) {
            deleteBtn.setDisable(true);
            deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CCCCCC; -fx-opacity: 0.5;");
        } else {
            deleteBtn.setOnAction(e -> handleDeleteSlot(slot));
        }

        timeBox.getChildren().addAll(timeBtn, deleteBtn);

        Label statusLabel = new Label(statusText);
        if (slot.isAvailable()) {
            statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #2D4A47;");
        } else {
            statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #C62828;");
        }

        card.getChildren().addAll(dateLabel, timeBox, statusLabel);

        return card;
    }

    private void handleDeleteSlot(TimeSlot slot) {
        // Double check if slot is booked
        if (!slot.isAvailable()) {
            showError("Cannot delete a booked slot!");
            return;
        }

        boolean deleted = timeSlotDAO.delete(slot.getSlotId());
        if (deleted) {
            loadSchedule();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/teacher_set_availability.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(localizationManager.getString("app.title.teacher.dashboard"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().logout();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(localizationManager.getString("app.title.login"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        // headless-safe, no Alert popup
        System.err.println("Error: " + message);
    }
}

