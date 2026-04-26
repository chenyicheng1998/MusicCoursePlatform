package controller;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.LocalizationManager;
import util.NavigationHelper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TeacherProfileViewController {

    private static final Logger logger = LoggerFactory.getLogger(TeacherProfileViewController.class);

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label appNameLabel;
    @FXML
    private ComboBox<String> languageCombo;
    @FXML
    private Button setAvailabilityButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label teacherNameLabel;
    @FXML
    private Label myScheduleLabel;
    @FXML
    private FlowPane scheduleContainer;

    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private LocalizationManager localizationManager;

    private TeacherProfile teacherProfile;
    private DateTimeFormatter dateFormatter;

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        localizationManager = LocalizationManager.getInstance();

        setupDateFormatter();
        setupLanguageSelector();
        loadTeacherInfo();
        updateTexts();
        loadSchedule();

        // Listen for locale changes
        localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            setupDateFormatter();
            updateTexts();
            applyDirection();
            loadSchedule(); // Reload schedule with new locale
        });

        // Apply initial direction
        applyDirection();
    }

    private void setupDateFormatter() {
        dateFormatter = localizationManager.createDateFormatter();
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
                teacherProfile = new TeacherProfile(currentUser.getUserId(), "piano");
                teacherProfileDAO.create(teacherProfile);
            }
        }
    }

    private void loadSchedule() {
        scheduleContainer.getChildren().clear();

        if (teacherProfile == null) {
            Label noSchedule = new Label(localizationManager.getString("message.no.schedule"));
            noSchedule.setStyle("-fx-text-fill: #718096;");
            scheduleContainer.getChildren().add(noSchedule);
            return;
        }

        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileId(teacherProfile.getTeacherProfileId());

        if (slots.isEmpty()) {
            Label noSchedule = new Label(localizationManager.getString("message.no.time.slots"));
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
        card.setStyle(
                "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16;");
        card.setPrefWidth(200);
        card.setAlignment(Pos.TOP_LEFT);

        Label dateLabel = new Label(slot.getLessonDate().format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");

        String timeText = slot.getStartTime() + " - " + slot.getEndTime();
        String statusText = slot.isAvailable() ? localizationManager.getString("schedule.status.available")
                : localizationManager.getString("schedule.status.booked");

        HBox timeBox = new HBox(8);
        timeBox.setAlignment(Pos.CENTER_LEFT);

        Button timeBtn = new Button(timeText);
        if (slot.isAvailable()) {
            timeBtn.setStyle(
                    "-fx-background-color: #E8F5E9; -fx-text-fill: #2D4A47; -fx-background-radius: 8; -fx-padding: 8 16;");
        } else {
            // Booked slots display in red
            timeBtn.setStyle(
                    "-fx-background-color: #FFEBEE; -fx-text-fill: #C62828; -fx-background-radius: 8; -fx-padding: 8 16;");
        }
        timeBtn.setPrefWidth(100);

        Button deleteBtn = new Button(localizationManager.getString("action.delete"));
        deleteBtn.setPrefWidth(60);
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
            showError(localizationManager.getString("message.cannot.delete.booked"));
            return;
        }

        boolean deleted = timeSlotDAO.delete(slot.getSlotId());
        if (deleted) {
            loadSchedule();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        NavigationHelper.navigateTo(event, getClass(),
                "/fxml/teacher_set_availability.fxml",
                localizationManager.getString("app.title.teacher.dashboard"));
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationHelper.logout(event, getClass(), localizationManager.getString("app.title.login"));
    }

    private void showError(String message) {
        logger.warn("Schedule view error: {}", message);
    }
}
