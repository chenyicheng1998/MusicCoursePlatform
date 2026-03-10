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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Booking;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TeacherProfileViewController {

    @FXML private Label teacherNameLabel;
    @FXML private FlowPane scheduleContainer;
    @FXML private ComboBox<String> languageCombo;

    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private BookingDAO bookingDAO;

    private TeacherProfile teacherProfile;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d");

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        bookingDAO = new BookingDAO();

        setupLanguageCombo();
        loadTeacherInfo();
        loadSchedule();
    }

    private void setupLanguageCombo() {
        if (languageCombo != null) {
            languageCombo.getItems().addAll("EN", "DE", "ZH");
            languageCombo.setValue("EN");
        }
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
        timeBtn.setPrefWidth(120);

        Button deleteBtn = new Button("🗑");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        // headless-safe, no Alert popup
        System.err.println("Error: " + message);
    }
}

