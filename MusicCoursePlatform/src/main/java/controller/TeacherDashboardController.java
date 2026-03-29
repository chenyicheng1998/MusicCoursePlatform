package controller;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import dao.BookingDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;
import util.LocalizationManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TeacherDashboardController {

    @FXML private BorderPane rootPane;
    @FXML private Label appNameLabel;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Button viewScheduleButton;
    @FXML private Button logoutButton;
    @FXML private Label nameLabel;
    @FXML private ComboBox<String> instrumentsCombo;
    @FXML private TextField experienceField;
    @FXML private TextField pricingField;
    @FXML private TextArea bioField;
    @FXML private Button saveProfileButton;
    @FXML private Label calendarFrameLabel;
    @FXML private Label monthLabel;
    @FXML private FlowPane calendarGrid;
    @FXML private Label sunLabel;
    @FXML private Label monLabel;
    @FXML private Label tueLabel;
    @FXML private Label wedLabel;
    @FXML private Label thuLabel;
    @FXML private Label friLabel;
    @FXML private Label satLabel;
    @FXML private Label setAvailabilityLabel;
    @FXML private Label selectedDateLabel;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;
    @FXML private Button addTimeSlotButton;
    @FXML private VBox timeSlotsContainer;
    @FXML private Label errorLabel;

    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private BookingDAO bookingDAO;
    private LocalizationManager localizationManager;

    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private TeacherProfile teacherProfile;
    private User currentUser;

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        bookingDAO = new BookingDAO();
        localizationManager = LocalizationManager.getInstance();

        currentMonth = YearMonth.now();
        currentUser = SessionManager.getInstance().getCurrentUser();

        setupInstrumentsCombo();
        setupTimeComboBoxes();
        setupLanguageSelector();
        loadTeacherProfile();
        updateTexts();
        updateCalendar();

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
        appNameLabel.setText(localizationManager.getString("app.name"));
        viewScheduleButton.setText(localizationManager.getString("nav.view.schedule"));
        logoutButton.setText(localizationManager.getString("nav.logout"));
        saveProfileButton.setText(localizationManager.getString("teacher.save.profile"));
        calendarFrameLabel.setText(localizationManager.getString("calendar.frame"));
        setAvailabilityLabel.setText(localizationManager.getString("teacher.set.availability"));
        startTimeLabel.setText(localizationManager.getString("teacher.start.time"));
        endTimeLabel.setText(localizationManager.getString("teacher.end.time"));
        addTimeSlotButton.setText(localizationManager.getString("teacher.add.slot"));

        // Update day of week labels
        sunLabel.setText(localizationManager.getString("calendar.day.sun"));
        monLabel.setText(localizationManager.getString("calendar.day.mon"));
        tueLabel.setText(localizationManager.getString("calendar.day.tue"));
        wedLabel.setText(localizationManager.getString("calendar.day.wed"));
        thuLabel.setText(localizationManager.getString("calendar.day.thu"));
        friLabel.setText(localizationManager.getString("calendar.day.fri"));
        satLabel.setText(localizationManager.getString("calendar.day.sat"));

        // Update combo box prompts
        if (instrumentsCombo != null) {
            instrumentsCombo.setPromptText(localizationManager.getString("teacher.instruments"));
        }
        if (experienceField != null) {
            experienceField.setPromptText(localizationManager.getString("teacher.experience.years"));
        }
        if (pricingField != null) {
            pricingField.setPromptText(localizationManager.getString("teacher.pricing"));
        }
        if (bioField != null) {
            bioField.setPromptText(localizationManager.getString("teacher.bio.edit"));
        }
        if (startTimeCombo != null) {
            startTimeCombo.setPromptText(localizationManager.getString("teacher.select.start"));
        }
        if (endTimeCombo != null) {
            endTimeCombo.setPromptText(localizationManager.getString("teacher.select.end"));
        }
        if (selectedDateLabel != null && selectedDate == null) {
            selectedDateLabel.setText(localizationManager.getString("student.select.date"));
        }
    }

    private void applyDirection() {
        localizationManager.applyDirection(rootPane);
    }

    private void setupInstrumentsCombo() {
        instrumentsCombo.getItems().addAll(
            "Piano", "Guitar", "Violin", "Drums", "Flute", "Saxophone", "Cello", "Voice"
        );
    }

    private void setupTimeComboBoxes() {
        for (int hour = 7; hour <= 21; hour++) {
            for (int min = 0; min < 60; min += 30) {
                String time = String.format("%d:%02d", hour, min);
                startTimeCombo.getItems().add(time);
                endTimeCombo.getItems().add(time);
            }
        }
    }

    private void loadTeacherProfile() {
        if (currentUser == null) return;
        
        teacherProfile = teacherProfileDAO.findByUserId(currentUser.getUserId());
        
        if (teacherProfile != null) {
            nameLabel.setText(currentUser.getUsername());
            if (teacherProfile.getInstrumentsTaught() != null) {
                instrumentsCombo.setValue(teacherProfile.getInstrumentsTaught().split(",")[0].trim());
            }
            experienceField.setText(String.valueOf(teacherProfile.getYearsExperience()));
            pricingField.setText(String.valueOf(teacherProfile.getHourlyRate()));
            if (teacherProfile.getBiography() != null) {
                bioField.setText(teacherProfile.getBiography());
            }
        } else {
            nameLabel.setText(currentUser != null ? currentUser.getUsername() : "Name");
            teacherProfile = new TeacherProfile(currentUser.getUserId(), "Piano");
            teacherProfileDAO.create(teacherProfile);
        }
    }

    @FXML
    private void handleSaveProfile(ActionEvent event) {
        if (currentUser == null || teacherProfile == null) return;

        String instrument = instrumentsCombo.getValue();
        String experience = experienceField.getText();
        String pricing = pricingField.getText();
        String bio = bioField.getText();

        teacherProfile.setInstrumentsTaught(instrument);

        if (experience != null && !experience.isEmpty()) {
            try {
                teacherProfile.setYearsExperience(Integer.parseInt(experience));
            } catch (NumberFormatException e) {
                showError(localizationManager.getString("error.fill.fields"));
                return;
            }
        }

        if (pricing != null && !pricing.isEmpty()) {
            try {
                teacherProfile.setHourlyRate(Integer.parseInt(pricing));
            } catch (NumberFormatException e) {
                showError(localizationManager.getString("error.fill.fields"));
                return;
            }
        }
        teacherProfile.setBiography(bio);

        boolean updated = teacherProfileDAO.update(teacherProfile);
        if (updated) {
            showSuccess(localizationManager.getString("success.profile.saved"));
        } else {
            showError(localizationManager.getString("error.signup.failed").replace("{0}", ""));
        }
    }

    private void updateCalendar() {
        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        calendarGrid.getChildren().clear();
        
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        
        for (int i = 0; i < dayOfWeek; i++) {
            Label emptyLabel = new Label("");
            emptyLabel.setPrefWidth(40);
            emptyLabel.setPrefHeight(40);
            calendarGrid.getChildren().add(emptyLabel);
        }
        
        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            LocalDate date = currentMonth.atDay(day);
            Button dayBtn = new Button(String.valueOf(day));
            dayBtn.setPrefWidth(40);
            dayBtn.setPrefHeight(40);
            dayBtn.getStyleClass().add("calendar-day");
            
            if (hasTimeSlots(date)) {
                dayBtn.getStyleClass().add("calendar-day-available");
            }
            
            if (date.equals(selectedDate)) {
                dayBtn.getStyleClass().add("calendar-day-selected");
            }
            
            final LocalDate clickedDate = date;
            dayBtn.setOnAction(e -> handleDateClick(clickedDate));
            
            calendarGrid.getChildren().add(dayBtn);
        }
    }

    private boolean hasTimeSlots(LocalDate date) {
        if (teacherProfile == null) return false;
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(teacherProfile.getTeacherProfileId(), date);
        return !slots.isEmpty();
    }

    private void handleDateClick(LocalDate date) {
        selectedDate = date;
        selectedDateLabel.setText(date.format(DateTimeFormatter.ofPattern("EEEE, d. MMMM")));
        updateCalendar();
        updateTimeSlots();
    }

    private void updateTimeSlots() {
        timeSlotsContainer.getChildren().clear();
        
        if (selectedDate == null || teacherProfile == null) return;
        
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(teacherProfile.getTeacherProfileId(), selectedDate);
        
        for (TimeSlot slot : slots) {
            HBox slotBox = createTimeSlotBox(slot);
            timeSlotsContainer.getChildren().add(slotBox);
        }
        
        if (timeSlotsContainer.getChildren().isEmpty()) {
            Label noSlotsLabel = new Label("No time slots set");
            noSlotsLabel.setStyle("-fx-text-fill: #718096;");
            timeSlotsContainer.getChildren().add(noSlotsLabel);
        }
    }

    private HBox createTimeSlotBox(TimeSlot slot) {
        String timeText = slot.getStartTime() + " - " + slot.getEndTime();
        
        Label timeLabel = new Label(timeText);
        timeLabel.getStyleClass().add("time-slot");
        timeLabel.setPrefWidth(150);  // Increased from 120 to 150 to show full time

        Button deleteBtn = new Button("Del");
        deleteBtn.setPrefWidth(40);
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteSlot(slot));
        
        HBox box = new HBox(10, timeLabel, deleteBtn);
        box.setStyle("-fx-alignment: CENTER_LEFT;");
        
        if (slot.isBooked()) {
            timeLabel.getStyleClass().add("time-slot-booked");
        }
        
        return box;
    }

    @FXML
    private void handleAddTimeSlot(ActionEvent event) {
        if (selectedDate == null) {
            showError(localizationManager.getString("error.fill.fields"));
            return;
        }

        if (teacherProfile == null) {
            showError(localizationManager.getString("error.fill.fields"));
            return;
        }

        String startStr = startTimeCombo.getValue();
        String endStr = endTimeCombo.getValue();

        if (startStr == null || endStr == null) {
            showError(localizationManager.getString("error.fill.fields"));
            return;
        }

        // Validate time range: start time must be before end time
        if (startStr.compareTo(endStr) >= 0) {
            showError(localizationManager.getString("error.fill.fields"));
            return;
        }

        TimeSlot slot = new TimeSlot(teacherProfile.getTeacherProfileId(), selectedDate, startStr, endStr);

        boolean created = timeSlotDAO.create(slot);
        if (created) {
            updateTimeSlots();
            updateCalendar();
            showSuccess(localizationManager.getString("success.slot.added"));
        } else {
            showError(localizationManager.getString("error.signup.failed").replace("{0}", ""));
        }
    }

    private void handleDeleteSlot(TimeSlot slot) {
        if (slot.isBooked()) {
            showError(localizationManager.getString("error.fill.fields"));
            return;
        }

        boolean deleted = timeSlotDAO.delete(slot.getSlotId());
        if (deleted) {
            updateTimeSlots();
            updateCalendar();
        }
    }

    @FXML
    private void handlePrevMonth(ActionEvent event) {
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleNextMonth(ActionEvent event) {
        currentMonth = currentMonth.plusMonths(1);
        updateCalendar();
    }

    @FXML
    private void handleViewSchedule(ActionEvent event) {
        try {
            Parent scheduleRoot = FXMLLoader.load(getClass().getResource("/fxml/teacher_schedule_view.fxml"));
            Scene scheduleScene = new Scene(scheduleRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scheduleScene);
            stage.setTitle(localizationManager.getString("app.title.teacher.dashboard"));
        } catch (IOException e) {
            e.printStackTrace();
            showError(localizationManager.getString("error.load.dashboard"));
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
        }
    }

    private void showSuccess(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.getInstance().logout();
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Scene loginScene = new Scene(loginRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle(localizationManager.getString("app.title.login"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
