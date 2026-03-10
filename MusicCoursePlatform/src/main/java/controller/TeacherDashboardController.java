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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;
import util.LanguageManager;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TeacherDashboardController implements LanguageManager.LanguageChangeListener {

    @FXML private Label nameLabel;
    @FXML private ComboBox<String> instrumentsCombo;
    @FXML private TextField experienceField;
    @FXML private TextField pricingField;
    @FXML private TextArea bioField;
    @FXML private Label monthLabel;
    @FXML private FlowPane calendarGrid;
    @FXML private Label selectedDateLabel;
    @FXML private ComboBox<String> startTimeCombo;
    @FXML private ComboBox<String> endTimeCombo;
    @FXML private VBox timeSlotsContainer;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Label errorLabel;
    @FXML private Button viewScheduleButton;
    @FXML private Button logoutButton;
    @FXML private Button addSlotButton;
    @FXML private Button saveProfileButton;
    @FXML private Label frameLabel;
    @FXML private Label setAvailabilityLabel;
    @FXML private Label startTimeLabel;
    @FXML private Label endTimeLabel;
    @FXML private Label daySun;
    @FXML private Label dayMon;
    @FXML private Label dayTue;
    @FXML private Label dayWed;
    @FXML private Label dayThu;
    @FXML private Label dayFri;
    @FXML private Label daySat;

    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private BookingDAO bookingDAO;
    private LanguageManager langManager;
    private boolean updatingLanguageCombo = false;
    
    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private TeacherProfile teacherProfile;
    private User currentUser;

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        bookingDAO = new BookingDAO();
        langManager = LanguageManager.getInstance();
        langManager.addLanguageChangeListener(this);
        
        currentMonth = YearMonth.now();
        currentUser = SessionManager.getInstance().getCurrentUser();
        
        setupInstrumentsCombo();
        setupTimeComboBoxes();
        setupLanguageCombo();
        loadTeacherProfile();
        updateCalendar();
        updateTexts();
    }

    @Override
    public void onLanguageChanged(Locale newLocale) {
        updateTexts();
        updateCalendar();
        updateTimeSlots();
        updateLanguageComboDisplay();
        updateInstrumentsCombo();
    }

    private void updateTexts() {
        if (viewScheduleButton != null) viewScheduleButton.setText(langManager.getString("nav.viewSchedule"));
        if (logoutButton != null) logoutButton.setText(langManager.getString("nav.logout"));
        if (addSlotButton != null) addSlotButton.setText(langManager.getString("teacher.addSlot"));
        if (saveProfileButton != null) saveProfileButton.setText(langManager.getString("message.save"));
        if (frameLabel != null) frameLabel.setText(langManager.getString("calendar.frame"));
        if (setAvailabilityLabel != null) setAvailabilityLabel.setText(langManager.getString("teacher.setAvailability"));
        if (startTimeLabel != null) startTimeLabel.setText(langManager.getString("teacher.startTime"));
        if (endTimeLabel != null) endTimeLabel.setText(langManager.getString("teacher.endTime"));
        if (selectedDateLabel != null && selectedDate == null) {
            selectedDateLabel.setText(langManager.getString("timeslots.selectDate"));
        }
        updateDayHeaders();
    }

    private void updateDayHeaders() {
        if (daySun != null) daySun.setText(langManager.getString("calendar.sun"));
        if (dayMon != null) dayMon.setText(langManager.getString("calendar.mon"));
        if (dayTue != null) dayTue.setText(langManager.getString("calendar.tue"));
        if (dayWed != null) dayWed.setText(langManager.getString("calendar.wed"));
        if (dayThu != null) dayThu.setText(langManager.getString("calendar.thu"));
        if (dayFri != null) dayFri.setText(langManager.getString("calendar.fri"));
        if (daySat != null) daySat.setText(langManager.getString("calendar.sat"));
    }

    private void setupInstrumentsCombo() {
        updateInstrumentsCombo();
    }

    private void updateInstrumentsCombo() {
        if (instrumentsCombo != null) {
            String currentValue = instrumentsCombo.getValue();
            instrumentsCombo.getItems().clear();
            instrumentsCombo.getItems().addAll(
                langManager.getString("instrument.piano"),
                langManager.getString("instrument.guitar"),
                langManager.getString("instrument.violin"),
                langManager.getString("instrument.drums"),
                langManager.getString("instrument.flute"),
                langManager.getString("instrument.saxophone"),
                langManager.getString("instrument.cello"),
                langManager.getString("instrument.voice")
            );
            if (currentValue != null) {
                instrumentsCombo.setValue(langManager.getString("instrument.piano"));
            }
        }
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
                showError("Invalid experience format. Please enter a number.");
                return;
            }
        }

        if (pricing != null && !pricing.isEmpty()) {
            try {
                teacherProfile.setHourlyRate(Integer.parseInt(pricing));
            } catch (NumberFormatException e) {
                showError("Invalid pricing format");
                return;
            }
        }
        teacherProfile.setBiography(bio);
        
        boolean updated = teacherProfileDAO.update(teacherProfile);
        if (updated) {
            showSuccess("Profile saved successfully!");
        } else {
            showError("Failed to save profile");
        }
    }

    private void updateCalendar() {
        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", langManager.getCurrentLocale())));
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
            Label noSlotsLabel = new Label(langManager.getString("timeslots.noSlots"));
            noSlotsLabel.setStyle("-fx-text-fill: #718096;");
            timeSlotsContainer.getChildren().add(noSlotsLabel);
        }
    }

    private HBox createTimeSlotBox(TimeSlot slot) {
        String timeText = slot.getStartTime() + " - " + slot.getEndTime();
        
        Label timeLabel = new Label(timeText);
        timeLabel.getStyleClass().add("time-slot");
        timeLabel.setPrefWidth(150);

        Button deleteBtn = new Button(langManager.getString("message.delete"));
        deleteBtn.setPrefWidth(60);
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
            showError("Please select a date first!");
            return;
        }
        
        if (teacherProfile == null) {
            showError("Teacher profile not found!");
            return;
        }
        
        String startStr = startTimeCombo.getValue();
        String endStr = endTimeCombo.getValue();
        
        if (startStr == null || endStr == null) {
            showError("Please select start and end time!");
            return;
        }
        
        // Validate time range: start time must be before end time
        if (startStr.compareTo(endStr) >= 0) {
            showError("End time must be after start time!");
            return;
        }

        TimeSlot slot = new TimeSlot(teacherProfile.getTeacherProfileId(), selectedDate, startStr, endStr);
        
        boolean created = timeSlotDAO.create(slot);
        if (created) {
            updateTimeSlots();
            updateCalendar();
            showSuccess("Time slot added!");
        } else {
            showError("Failed to add time slot");
        }
    }

    private void handleDeleteSlot(TimeSlot slot) {
        if (slot.isBooked()) {
            showError("Cannot delete a booked slot!");
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
            stage.setTitle("Music Course Platform - My Schedule");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load schedule view");
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
            stage.setTitle("Music Course Platform - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
