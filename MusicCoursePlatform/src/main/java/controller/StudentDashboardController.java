package controller;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import dao.BookingDAO;
import dao.LearnerProfileDAO;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Booking;
import model.LearnerProfile;
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

public class StudentDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(StudentDashboardController.class);

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
    private Label filterInstrumentLabel;
    @FXML
    private Label selectTeacherLabel;
    @FXML
    private Label teacherNameLabel;
    @FXML
    private Label teacherInstrumentLabel;
    @FXML
    private Label teacherExperienceLabel;
    @FXML
    private Label teacherRateLabel;
    @FXML
    private Label teacherBioLabel;
    @FXML
    private Label experienceTitleLabel;
    @FXML
    private Label rateTitleLabel;
    @FXML
    private Label aboutTitleLabel;
    @FXML
    private ComboBox<String> instrumentCombo;
    @FXML
    private ComboBox<String> teacherCombo;
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
    private Label availableTimesLabel;
    @FXML
    private Label selectedDateLabel;
    @FXML
    private Label selectedTimeLabel;
    @FXML
    private Label selectedTimeTitleLabel;
    @FXML
    private VBox timeSlotsContainer;
    @FXML
    private Button bookButton;
    @FXML
    private Label errorLabel;

    private TeacherProfileDAO teacherProfileDAO;
    private TimeSlotDAO timeSlotDAO;
    private BookingDAO bookingDAO;
    private LearnerProfileDAO learnerProfileDAO;
    private UserDAO userDAO;
    private LocalizationManager localizationManager;

    private YearMonth currentMonth;
    private LocalDate selectedDate;
    private TeacherProfile selectedTeacher;
    private LearnerProfile learnerProfile;
    private List<TeacherProfile> teacherProfiles;
    private TimeSlot selectedSlot;

    @FXML
    public void initialize() {
        teacherProfileDAO = new TeacherProfileDAO();
        timeSlotDAO = new TimeSlotDAO();
        bookingDAO = new BookingDAO();
        learnerProfileDAO = new LearnerProfileDAO();
        userDAO = new UserDAO();
        localizationManager = LocalizationManager.getInstance();

        currentMonth = YearMonth.now();

        loadLearnerProfile();
        setupInstrumentCombo();
        setupLanguageSelector();
        updateTexts();
        updateCalendar();
        loadTeachers();

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
        filterInstrumentLabel.setText(localizationManager.getString("student.filter.instrument"));
        selectTeacherLabel.setText(localizationManager.getString("student.select.teacher"));
        experienceTitleLabel.setText(localizationManager.getString("student.experience"));
        rateTitleLabel.setText(localizationManager.getString("student.rate"));
        aboutTitleLabel.setText(localizationManager.getString("student.about"));
        calendarFrameLabel.setText(localizationManager.getString("calendar.frame"));
        availableTimesLabel.setText(localizationManager.getString("student.available.times"));
        selectedTimeTitleLabel.setText(localizationManager.getString("student.selected.time"));
        bookButton.setText(localizationManager.getString("student.book.lesson"));

        sunLabel.setText(localizationManager.getString("calendar.day.sun"));
        monLabel.setText(localizationManager.getString("calendar.day.mon"));
        tueLabel.setText(localizationManager.getString("calendar.day.tue"));
        wedLabel.setText(localizationManager.getString("calendar.day.wed"));
        thuLabel.setText(localizationManager.getString("calendar.day.thu"));
        friLabel.setText(localizationManager.getString("calendar.day.fri"));
        satLabel.setText(localizationManager.getString("calendar.day.sat"));

        updateInstrumentCombo();

        if (teacherCombo != null) {
            teacherCombo.setPromptText(localizationManager.getString("student.teacher.prompt"));
        }
        if (selectedDateLabel != null && selectedDate == null) {
            selectedDateLabel.setText(localizationManager.getString("student.select.date"));
        }
        if (selectedTimeLabel != null && selectedSlot == null) {
            selectedTimeLabel.setText(localizationManager.getString("student.none.selected"));
        }
    }

    private void applyDirection() {
        localizationManager.applyDirection(rootPane);
    }

    private void loadLearnerProfile() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            learnerProfile = learnerProfileDAO.findByUserId(currentUser.getUserId());
            if (learnerProfile == null) {
                // Use canonical key "piano" — not a localised display name
                learnerProfile = new LearnerProfile(currentUser.getUserId(), "piano");
                learnerProfileDAO.create(learnerProfile);
            }
        }
    }

    private void setupInstrumentCombo() {
        updateInstrumentCombo();
    }

    /**
     * Repopulate the instrument filter combo for the current locale,
     * restoring the previous selection via its canonical key.
     */
    private void updateInstrumentCombo() {
        String currentKey = localizationManager.getInstrumentKey(instrumentCombo.getValue());

        instrumentCombo.getItems().clear();
        instrumentCombo.getItems().addAll(
                localizationManager.getString("instrument.piano"),
                localizationManager.getString("instrument.guitar"),
                localizationManager.getString("instrument.violin"),
                localizationManager.getString("instrument.drums"),
                localizationManager.getString("instrument.flute"),
                localizationManager.getString("instrument.saxophone"),
                localizationManager.getString("instrument.cello"),
                localizationManager.getString("instrument.voice"));

        if (currentKey != null) {
            instrumentCombo.setValue(localizationManager.getString("instrument." + currentKey));
        } else {
            instrumentCombo.setValue(localizationManager.getString("instrument.piano"));
        }
    }

    /**
     * Load teachers whose {@code instrument_key} matches the currently
     * selected instrument. Because the DB now stores canonical lowercase keys
     * ("piano", "guitar", …) the query is a simple exact match — no LIKE
     * pattern and no locale-dependent string needed.
     */
    private void loadTeachers() {
        String selectedLocalizedInstrument = instrumentCombo.getValue();
        if (selectedLocalizedInstrument == null)
            return;

        // Resolve canonical key from whatever locale the student sees
        String instrumentKey = localizationManager.getInstrumentKey(selectedLocalizedInstrument);
        if (instrumentKey == null)
            instrumentKey = "piano";

        teacherProfiles = teacherProfileDAO.findByInstrument(instrumentKey);
        teacherCombo.getItems().clear();

        for (TeacherProfile profile : teacherProfiles) {
            User user = userDAO.findById(profile.getUserId());
            String name = (user != null)
                    ? user.getUsername()
                    : "Teacher " + profile.getTeacherProfileId();
            teacherCombo.getItems().add(name);
        }

        if (!teacherProfiles.isEmpty()) {
            teacherCombo.setValue(teacherCombo.getItems().get(0));
            selectedTeacher = teacherProfiles.get(0);
            updateTeacherDisplay();
            updateCalendar();
        } else {
            teacherNameLabel.setText(localizationManager.getString("message.no.teachers.available"));
            if (teacherInstrumentLabel != null)
                teacherInstrumentLabel.setText("");
            if (teacherExperienceLabel != null)
                teacherExperienceLabel.setText("");
            if (teacherRateLabel != null)
                teacherRateLabel.setText("");
        }
    }

    @FXML
    private void handleInstrumentChange(ActionEvent event) {
        loadTeachers();
        updateTimeSlots();
    }

    @FXML
    private void handleTeacherChange(ActionEvent event) {
        int index = teacherCombo.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < teacherProfiles.size()) {
            selectedTeacher = teacherProfiles.get(index);
            updateTeacherDisplay();
            updateCalendar();
            updateTimeSlots();
        }
    }

    private void updateTeacherDisplay() {
        if (selectedTeacher == null)
            return;

        User user = userDAO.findById(selectedTeacher.getUserId());
        String name = (user != null)
                ? user.getUsername()
                : "Teacher " + selectedTeacher.getTeacherProfileId();
        teacherNameLabel.setText(name);

        if (teacherInstrumentLabel != null) {
            // instrument_key is a canonical key — translate it for display
            String localizedInstrument = localizationManager.getLocalizedInstrumentName(
                    selectedTeacher.getInstrumentsTaught());
            teacherInstrumentLabel.setText(localizedInstrument);
        }
        if (teacherExperienceLabel != null) {
            teacherExperienceLabel.setText(selectedTeacher.getYearsExperience()
                    + " " + localizationManager.getString("student.years.experience"));
        }
        if (teacherRateLabel != null) {
            teacherRateLabel.setText("$" + selectedTeacher.getHourlyRate()
                    + "/" + localizationManager.getString("student.hour"));
        }
        if (teacherBioLabel != null) {
            String bio = selectedTeacher.getBiography();
            String noBioText = localizationManager.getString("student.no.biography");
            teacherBioLabel.setText((bio != null && !bio.isEmpty()) ? bio : noBioText);
        }
    }

    private void updateCalendar() {
        CalendarBuilder.buildCalendar(calendarGrid, monthLabel, currentMonth, selectedDate,
                this::hasAvailableSlots, this::handleDateClick);
    }

    private boolean hasAvailableSlots(LocalDate date) {
        if (selectedTeacher == null)
            return false;
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(
                selectedTeacher.getTeacherProfileId(), date);
        return slots.stream().anyMatch(TimeSlot::isAvailable);
    }

    private void handleDateClick(LocalDate date) {
        selectedDate = date;
        selectedDateLabel.setText(date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")));
        updateCalendar();
        updateTimeSlots();
    }

    private void updateTimeSlots() {
        timeSlotsContainer.getChildren().clear();

        if (selectedDate == null || selectedTeacher == null)
            return;

        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(
                selectedTeacher.getTeacherProfileId(), selectedDate);

        for (TimeSlot slot : slots) {
            if (slot.isAvailable()) {
                timeSlotsContainer.getChildren().add(createTimeSlotBox(slot));
            }
        }

        if (timeSlotsContainer.getChildren().isEmpty()) {
            Label noSlotsLabel = new Label(localizationManager.getString("message.no.time.slots"));
            noSlotsLabel.setStyle("-fx-text-fill: #718096;");
            timeSlotsContainer.getChildren().add(noSlotsLabel);
        }
    }

    private HBox createTimeSlotBox(TimeSlot slot) {
        String timeText = slot.getStartTime() + " - " + slot.getEndTime();

        Button slotBtn = new Button(timeText);
        slotBtn.getStyleClass().add("time-slot");
        slotBtn.setPrefWidth(160);

        if (selectedSlot != null && selectedSlot.getSlotId() == slot.getSlotId()) {
            slotBtn.getStyleClass().add("time-slot-selected");
        }

        slotBtn.setOnAction(e -> handleSlotSelect(slot));

        HBox box = new HBox(slotBtn);
        box.setStyle("-fx-alignment: CENTER;");
        return box;
    }

    private void handleSlotSelect(TimeSlot slot) {
        selectedSlot = slot;
        if (selectedTimeLabel != null) {
            selectedTimeLabel.setText(slot.getStartTime() + " - " + slot.getEndTime());
        }
        if (bookButton != null) {
            bookButton.setDisable(false);
        }
        updateTimeSlots();
    }

    private void confirmBooking() {
        if (learnerProfile == null || selectedSlot == null) {
            showError(localizationManager.getString("error.fill.fields"));
            return;
        }

        Booking booking = new Booking(learnerProfile.getLearnerProfileId(), selectedSlot.getSlotId());

        boolean created = bookingDAO.create(booking);
        if (created) {
            timeSlotDAO.updateStatus(selectedSlot.getSlotId(), TimeSlot.STATUS_BOOKED);
            selectedSlot = null;
            if (selectedTimeLabel != null) {
                selectedTimeLabel.setText(localizationManager.getString("student.none.selected"));
            }
            if (bookButton != null) {
                bookButton.setDisable(true);
            }
            updateTimeSlots();
            showSuccessMessage(localizationManager.getString("success.booking.created"));
        } else {
            showError(localizationManager.getString("error.signup.failed").replace("{0}", ""));
        }
    }

    private void showSuccessMessage(String message) {
        if (errorLabel != null) {
            errorLabel.setStyle("-fx-text-fill: #38a169;");
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setStyle("-fx-text-fill: #e53e3e;");
            errorLabel.setText(message);
            errorLabel.setVisible(true);
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
    private void handleBookNow(ActionEvent event) {
        if (selectedTeacher == null || selectedSlot == null) {
            showError(localizationManager.getString("error.fill.fields"));
            return;
        }
        confirmBooking();
    }

    @FXML
    private void handleViewSchedule(ActionEvent event) {
        NavigationHelper.navigateTo(event, getClass(),
                "/fxml/student_schedule_view.fxml",
                localizationManager.getString("app.title.student.dashboard"));
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationHelper.logout(event, getClass(), localizationManager.getString("app.title.login"));
    }
}
