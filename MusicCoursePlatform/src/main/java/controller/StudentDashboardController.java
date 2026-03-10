package controller;

import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import dao.BookingDAO;
import dao.LearnerProfileDAO;
import dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import model.LearnerProfile;
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

public class StudentDashboardController implements LanguageManager.LanguageChangeListener {

    @FXML private Label teacherNameLabel;
    @FXML private Label teacherInstrumentLabel;
    @FXML private Label teacherExperienceLabel;
    @FXML private Label teacherRateLabel;
    @FXML private Label teacherBioLabel;
    @FXML private ComboBox<String> instrumentCombo;
    @FXML private ComboBox<String> teacherCombo;
    @FXML private Label monthLabel;
    @FXML private FlowPane calendarGrid;
    @FXML private Label selectedDateLabel;
    @FXML private Label selectedTimeLabel;
    @FXML private VBox timeSlotsContainer;
    @FXML private ComboBox<String> languageCombo;
    @FXML private Button bookButton;
    @FXML private Label errorLabel;
    @FXML private Label filterLabel;
    @FXML private Label selectTeacherLabel;
    @FXML private Label experienceLabel;
    @FXML private Label rateLabel;
    @FXML private Label aboutLabel;
    @FXML private Label availableTimesLabel;
    @FXML private Label selectedTimeTitleLabel;
    @FXML private Button viewScheduleButton;
    @FXML private Button logoutButton;
    @FXML private Label frameLabel;
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
    private LearnerProfileDAO learnerProfileDAO;
    private UserDAO userDAO;
    private LanguageManager langManager;
    private boolean updatingLanguageCombo = false;

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
        langManager = LanguageManager.getInstance();
        langManager.addLanguageChangeListener(this);

        currentMonth = YearMonth.now();

        loadLearnerProfile();
        setupInstrumentCombo();
        setupLanguageCombo();
        updateCalendar();
        loadTeachers();
        updateTexts();
    }

    @Override
    public void onLanguageChanged(Locale newLocale) {
        updateTexts();
        updateCalendar();
        updateTimeSlots();
        updateLanguageComboDisplay();
        updateInstrumentCombo();
    }

    private void updateTexts() {
        if (filterLabel != null) filterLabel.setText(langManager.getString("student.filterInstrument"));
        if (selectTeacherLabel != null) selectTeacherLabel.setText(langManager.getString("student.selectTeacher"));
        if (experienceLabel != null) experienceLabel.setText(langManager.getString("student.experience"));
        if (rateLabel != null) rateLabel.setText(langManager.getString("student.rate"));
        if (aboutLabel != null) aboutLabel.setText(langManager.getString("student.about"));
        if (availableTimesLabel != null) availableTimesLabel.setText(langManager.getString("timeslots.available"));
        if (selectedTimeTitleLabel != null) selectedTimeTitleLabel.setText(langManager.getString("timeslots.selectedTime"));
        if (bookButton != null) bookButton.setText(langManager.getString("timeslots.bookLesson"));
        if (viewScheduleButton != null) viewScheduleButton.setText(langManager.getString("nav.viewSchedule"));
        if (logoutButton != null) logoutButton.setText(langManager.getString("nav.logout"));
        if (frameLabel != null) frameLabel.setText(langManager.getString("calendar.frame"));
        if (selectedDateLabel != null && selectedDate == null) {
            selectedDateLabel.setText(langManager.getString("timeslots.selectDate"));
        }
        if (selectedTimeLabel != null && selectedSlot == null) {
            selectedTimeLabel.setText(langManager.getString("timeslots.noneSelected"));
        }
        updateDayHeaders();
        updateLanguageComboDisplay();
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

    private void loadLearnerProfile() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            learnerProfile = learnerProfileDAO.findByUserId(currentUser.getUserId());
            if (learnerProfile == null) {
                learnerProfile = new LearnerProfile(currentUser.getUserId(), "Piano");
                learnerProfileDAO.create(learnerProfile);
            }
        }
    }

    private void setupInstrumentCombo() {
        updateInstrumentCombo();
    }

    private void updateInstrumentCombo() {
        if (instrumentCombo != null) {
            instrumentCombo.getItems().clear();
            instrumentCombo.getItems().addAll(
                langManager.getString("instrument.piano"),
                langManager.getString("instrument.guitar"),
                langManager.getString("instrument.violin"),
                langManager.getString("instrument.drums"),
                langManager.getString("instrument.flute"),
                langManager.getString("instrument.saxophone"),
                langManager.getString("instrument.cello"),
                langManager.getString("instrument.voice")
            );
            instrumentCombo.setValue(langManager.getString("instrument.piano"));
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

    private void loadTeachers() {
        String instrument = instrumentCombo.getValue();
        if (instrument != null) {
            teacherProfiles = teacherProfileDAO.findByInstrument(instrument);
            teacherCombo.getItems().clear();
            for (TeacherProfile profile : teacherProfiles) {
                User user = userDAO.findById(profile.getUserId());
                String name = (user != null) ? user.getUsername() : "Teacher " + profile.getTeacherProfileId();
                teacherCombo.getItems().add(name);
            }
            if (!teacherProfiles.isEmpty()) {
                teacherCombo.setValue(teacherCombo.getItems().get(0));
                selectedTeacher = teacherProfiles.get(0);
                updateTeacherDisplay();
                updateCalendar();
            } else {
                teacherNameLabel.setText("No teachers available");
                if (teacherInstrumentLabel != null) teacherInstrumentLabel.setText("");
                if (teacherExperienceLabel != null) teacherExperienceLabel.setText("");
                if (teacherRateLabel != null) teacherRateLabel.setText("");
            }
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
        if (selectedTeacher != null) {
            User user = userDAO.findById(selectedTeacher.getUserId());
            String name = (user != null) ? user.getUsername() : "Teacher " + selectedTeacher.getTeacherProfileId();
            teacherNameLabel.setText(name);

            if (teacherInstrumentLabel != null) {
                teacherInstrumentLabel.setText(selectedTeacher.getInstrumentsTaught());
            }
            if (teacherExperienceLabel != null) {
                teacherExperienceLabel.setText(selectedTeacher.getYearsExperience() + " years exp.");
            }
            if (teacherRateLabel != null) {
                teacherRateLabel.setText("$" + selectedTeacher.getHourlyRate() + "/hr");
            }
            if (teacherBioLabel != null) {
                String bio = selectedTeacher.getBiography();
                teacherBioLabel.setText((bio != null && !bio.isEmpty()) ? bio : "No biography available.");
            }
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

            if (hasAvailableSlots(date)) {
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

    private boolean hasAvailableSlots(LocalDate date) {
        if (selectedTeacher == null) return false;
        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(selectedTeacher.getTeacherProfileId(), date);
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

        if (selectedDate == null || selectedTeacher == null) {
            return;
        }

        List<TimeSlot> slots = timeSlotDAO.findByTeacherProfileIdAndDate(
                selectedTeacher.getTeacherProfileId(), selectedDate);

        for (TimeSlot slot : slots) {
            if (slot.isAvailable()) {
                HBox slotBox = createTimeSlotBox(slot);
                timeSlotsContainer.getChildren().add(slotBox);
            }
        }

        if (timeSlotsContainer.getChildren().isEmpty()) {
            Label noSlotsLabel = new Label(langManager.getString("timeslots.noSlots"));
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
        if (learnerProfile == null) {
            showError(langManager.getString("booking.profileNotFound"));
            return;
        }
        if (selectedSlot == null) {
            showError(langManager.getString("booking.selectSlot"));
            return;
        }

        Booking booking = new Booking(learnerProfile.getLearnerProfileId(), selectedSlot.getSlotId());

        boolean created = bookingDAO.create(booking);
        if (created) {
            timeSlotDAO.updateStatus(selectedSlot.getSlotId(), TimeSlot.STATUS_BOOKED);
            selectedSlot = null;
            if (selectedTimeLabel != null) {
                selectedTimeLabel.setText(langManager.getString("timeslots.noneSelected"));
            }
            if (bookButton != null) {
                bookButton.setDisable(true);
            }
            updateTimeSlots();
            showSuccessMessage(langManager.getString("booking.success"));
        } else {
            showError(langManager.getString("booking.failed"));
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
        if (selectedTeacher == null) {
            showError(langManager.getString("booking.selectTeacher"));
            return;
        }
        if (selectedSlot == null) {
            showError(langManager.getString("booking.selectSlot"));
            return;
        }
        confirmBooking();
    }

    @FXML
    private void handleViewSchedule(ActionEvent event) {
        try {
            Parent scheduleRoot = FXMLLoader.load(getClass().getResource("/fxml/student_schedule_view.fxml"));
            Scene scheduleScene = new Scene(scheduleRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scheduleScene);
            stage.setTitle("Music Course Platform - My Schedule");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load schedule view");
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