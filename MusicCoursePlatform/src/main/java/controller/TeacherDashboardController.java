package controller;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.TeacherProfile;
import model.TimeSlot;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CalendarBuilder;
import util.LocalizationManager;
import util.NavigationHelper;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TeacherDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(TeacherDashboardController.class);
    private static final String ERROR_FILL_FIELDS = "error.fill.fields";

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label appNameLabel;
    @FXML
    private ComboBox<String> languageCombo;
    @FXML
    private Button viewScheduleButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label nameLabel;
    @FXML
    private ComboBox<String> instrumentsCombo;
    @FXML
    private TextField experienceField;
    @FXML
    private TextField pricingField;
    @FXML
    private TextArea bioField;
    @FXML
    private Button saveProfileButton;
    @FXML
    private Label calendarFrameLabel;
    @FXML
    private Label monthLabel;
    @FXML
    private FlowPane calendarGrid;
    @FXML
    private Label sunLabel;
    @FXML
    private Label monLabel;
    @FXML
    private Label tueLabel;
    @FXML
    private Label wedLabel;
    @FXML
    private Label thuLabel;
    @FXML
    private Label friLabel;
    @FXML
    private Label satLabel;
    @FXML
    private Label setAvailabilityLabel;
    @FXML
    private Label selectedDateLabel;
    @FXML
    private Label startTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private ComboBox<String> startTimeCombo;
    @FXML
    private ComboBox<String> endTimeCombo;
    @FXML
    private Button addTimeSlotButton;
    @FXML
    private VBox timeSlotsContainer;
    @FXML
    private Label errorLabel;

    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private LocalizationManager localizationManager;

    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private TeacherProfile teacherProfile;
    private User currentUser;

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        localizationManager = LocalizationManager.getInstance();

        currentMonth = YearMonth.now();
        currentUser = SessionManager.getInstance().getCurrentUser();

        setupInstrumentsCombo();
        setupTimeComboBoxes();
        setupLanguageSelector();
        loadTeacherProfile();
        updateTexts();
        updateCalendar();

        // Refresh UI labels and combo whenever the locale changes
        localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            updateTexts();
            applyDirection();
        });

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

        updateInstrumentsCombo();

        sunLabel.setText(localizationManager.getString("calendar.day.sun"));
        monLabel.setText(localizationManager.getString("calendar.day.mon"));
        tueLabel.setText(localizationManager.getString("calendar.day.tue"));
        wedLabel.setText(localizationManager.getString("calendar.day.wed"));
        thuLabel.setText(localizationManager.getString("calendar.day.thu"));
        friLabel.setText(localizationManager.getString("calendar.day.fri"));
        satLabel.setText(localizationManager.getString("calendar.day.sat"));

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
        updateInstrumentsCombo();
    }

    /**
     * Repopulate the instruments combo with names for the current locale,
     * preserving the current selection by tracking its canonical key.
     */
    private void updateInstrumentsCombo() {
        // Remember which instrument was selected (by canonical key)
        String currentKey = localizationManager.getInstrumentKey(instrumentsCombo.getValue());

        instrumentsCombo.getItems().clear();
        instrumentsCombo.getItems().addAll(
                localizationManager.getString("instrument.piano"),
                localizationManager.getString("instrument.guitar"),
                localizationManager.getString("instrument.violin"),
                localizationManager.getString("instrument.drums"),
                localizationManager.getString("instrument.flute"),
                localizationManager.getString("instrument.saxophone"),
                localizationManager.getString("instrument.cello"),
                localizationManager.getString("instrument.voice"));

        // Restore selection in the new locale
        if (currentKey != null) {
            instrumentsCombo.setValue(localizationManager.getString("instrument." + currentKey));
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

    private void loadTeacherProfile() {
        if (currentUser == null)
            return;

        teacherProfile = teacherProfileDAO.findByUserId(currentUser.getUserId());

        if (teacherProfile != null) {
            nameLabel.setText(currentUser.getUsername());

            if (teacherProfile.getInstrumentsTaught() != null) {
                // instrument_key is stored as a canonical lowercase key (e.g. "piano").
                // Translate to the current locale's display name for the combo.
                String localizedName = localizationManager.getLocalizedInstrumentName(
                        teacherProfile.getInstrumentsTaught().trim());
                instrumentsCombo.setValue(localizedName);
            }

            experienceField.setText(String.valueOf(teacherProfile.getYearsExperience()));
            pricingField.setText(String.valueOf(teacherProfile.getHourlyRate()));
            if (teacherProfile.getBiography() != null) {
                bioField.setText(teacherProfile.getBiography());
            }
        } else {
            nameLabel.setText(currentUser.getUsername());
            // Create a default profile with canonical key "piano"
            teacherProfile = new TeacherProfile(currentUser.getUserId(), "piano");
            teacherProfileDAO.create(teacherProfile);
        }
    }

    /**
     * Save profile: convert the localised combo selection to a canonical
     * lowercase key before persisting to the database. This means a teacher
     * who saves in Chinese stores "piano", not "钢琴", so students browsing
     * in any language can still find them.
     */
    @FXML
    private void handleSaveProfile(ActionEvent event) {
        if (currentUser == null || teacherProfile == null)
            return;

        // Convert the displayed (localised) name to the canonical DB key
        String selectedDisplay = instrumentsCombo.getValue();
        String instrumentKey = localizationManager.getInstrumentKey(selectedDisplay);
        String canonicalInstrument = (instrumentKey != null) ? instrumentKey : "piano";

        String experience = experienceField.getText();
        String pricing = pricingField.getText();
        String bio = bioField.getText();

        teacherProfile.setInstrumentsTaught(canonicalInstrument);

        if (experience != null && !experience.isEmpty()) {
            try {
                teacherProfile.setYearsExperience(Integer.parseInt(experience));
            } catch (NumberFormatException e) {
                showError(localizationManager.getString(ERROR_FILL_FIELDS));
                return;
            }
        }

        if (pricing != null && !pricing.isEmpty()) {
            try {
                teacherProfile.setHourlyRate(Integer.parseInt(pricing));
            } catch (NumberFormatException e) {
                showError(localizationManager.getString(ERROR_FILL_FIELDS));
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
        CalendarBuilder.buildCalendar(calendarGrid, monthLabel, currentMonth, selectedDate,
                this::hasTimeSlots, this::handleDateClick);
    }

    private boolean hasTimeSlots(LocalDate date) {
        if (teacherProfile == null)
            return false;
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(
                teacherProfile.getTeacherProfileId(), date);
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

        if (selectedDate == null || teacherProfile == null)
            return;

        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(
                teacherProfile.getTeacherProfileId(), selectedDate);

        for (TimeSlot slot : slots) {
            timeSlotsContainer.getChildren().add(createTimeSlotBox(slot));
        }

        if (timeSlotsContainer.getChildren().isEmpty()) {
            Label noSlotsLabel = new Label(localizationManager.getString("message.no.slots.set"));
            noSlotsLabel.setStyle("-fx-text-fill: #718096;");
            timeSlotsContainer.getChildren().add(noSlotsLabel);
        }
    }

    private HBox createTimeSlotBox(TimeSlot slot) {
        String timeText = slot.getStartTime() + " - " + slot.getEndTime();

        Label timeLabel = new Label(timeText);
        timeLabel.getStyleClass().add("time-slot");
        timeLabel.setPrefWidth(150);

        Button deleteBtn = new Button(localizationManager.getString("action.delete"));
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
        if (selectedDate == null || teacherProfile == null) {
            showError(localizationManager.getString(ERROR_FILL_FIELDS));
            return;
        }

        String startStr = startTimeCombo.getValue();
        String endStr = endTimeCombo.getValue();

        if (startStr == null || endStr == null) {
            showError(localizationManager.getString(ERROR_FILL_FIELDS));
            return;
        }

        if (startStr.compareTo(endStr) >= 0) {
            showError(localizationManager.getString("error.time.order"));
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
            showError(localizationManager.getString("message.cannot.delete.booked"));
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
        NavigationHelper.navigateTo(event, getClass(),
                "/fxml/teacher_schedule_view.fxml",
                localizationManager.getString("app.title.teacher.dashboard"));
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
        NavigationHelper.logout(event, getClass(), localizationManager.getString("app.title.login"));
    }
}