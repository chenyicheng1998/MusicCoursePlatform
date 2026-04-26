package controller;

import dao.BookingDAO;
import dao.LearnerProfileDAO;
import dao.TeacherProfileDAO;
import dao.TimeSlotDAO;
import dao.UserDAO;
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
import model.Booking;
import model.LearnerProfile;
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

public class BookingViewController {

    private static final Logger logger = LoggerFactory.getLogger(BookingViewController.class);

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label appNameLabel;
    @FXML
    private ComboBox<String> languageCombo;
    @FXML
    private Button courseBookingButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label myBookingsLabel;
    @FXML
    private FlowPane bookingsContainer;
    @FXML
    private Button prevPageBtn;
    @FXML
    private Button nextPageBtn;

    private BookingDAO bookingDAO;
    private TimeSlotDAO timeSlotDAO;
    private TeacherProfileDAO teacherProfileDAO;
    private LearnerProfileDAO learnerProfileDAO;
    private UserDAO userDAO;
    private LocalizationManager localizationManager;

    private LearnerProfile learnerProfile;
    private DateTimeFormatter dateFormatter;

    @FXML
    public void initialize() {
        bookingDAO = new BookingDAO();
        timeSlotDAO = new TimeSlotDAO();
        teacherProfileDAO = new TeacherProfileDAO();
        learnerProfileDAO = new LearnerProfileDAO();
        userDAO = new UserDAO();
        localizationManager = LocalizationManager.getInstance();

        setupDateFormatter();
        setupLanguageSelector();
        loadUserInfo();
        updateTexts();
        loadBookings();

        // Listen for locale changes
        localizationManager.localeProperty().addListener((obs, oldLocale, newLocale) -> {
            setupDateFormatter();
            updateTexts();
            applyDirection();
            loadBookings(); // Reload bookings with new locale
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
        courseBookingButton.setText(localizationManager.getString("nav.course.booking"));
        logoutButton.setText(localizationManager.getString("nav.logout"));
        myBookingsLabel.setText(localizationManager.getString("schedule.my.bookings"));

        // Update navigation buttons for RTL languages
        if (prevPageBtn != null && nextPageBtn != null) {
            if (localizationManager.isRTL()) {
                // In RTL languages, reverse the direction
                prevPageBtn.setText("›");
                nextPageBtn.setText("‹");
            } else {
                // LTR languages use normal direction
                prevPageBtn.setText("‹");
                nextPageBtn.setText("›");
            }
        }
    }

    private void applyDirection() {
        localizationManager.applyDirection(rootPane);
    }

    private void loadUserInfo() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            userNameLabel.setText(currentUser.getUsername());
            learnerProfile = learnerProfileDAO.findByUserId(currentUser.getUserId());
        }
    }

    private void loadBookings() {
        bookingsContainer.getChildren().clear();

        if (learnerProfile == null) {
            Label noBookings = new Label(localizationManager.getString("message.no.bookings"));
            noBookings.setStyle("-fx-text-fill: #718096;");
            bookingsContainer.getChildren().add(noBookings);
            return;
        }

        List<Booking> bookings = bookingDAO.findByLearnerProfileId(learnerProfile.getLearnerProfileId());

        if (bookings.isEmpty()) {
            Label noBookings = new Label(localizationManager.getString("message.no.bookings"));
            noBookings.setStyle("-fx-text-fill: #718096;");
            bookingsContainer.getChildren().add(noBookings);
            return;
        }

        for (Booking booking : bookings) {
            if (!booking.isCancelled()) {
                VBox bookingCard = createBookingCard(booking);
                bookingsContainer.getChildren().add(bookingCard);
            }
        }
    }

    private VBox createBookingCard(Booking booking) {
        VBox card = new VBox(8);
        card.setStyle(
                "-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16;");
        card.setPrefWidth(200);
        card.setAlignment(Pos.TOP_LEFT);

        TimeSlot slot = timeSlotDAO.findById(booking.getSlotId());
        if (slot == null)
            return card;

        TeacherProfile teacher = teacherProfileDAO.findById(slot.getTeacherProfileId());
        String teacherName = localizationManager.getString("message.unknown");
        String instrument = localizationManager.getString("message.unknown");

        if (teacher != null) {
            User teacherUser = userDAO.findById(teacher.getUserId());
            teacherName = (teacherUser != null) ? teacherUser.getUsername()
                    : localizationManager.getString("message.teacher");

            // Localize instrument name
            String rawInstrument = teacher.getInstrumentsTaught();
            instrument = localizeInstrumentName(rawInstrument);
        }

        Label instrumentLabel = new Label(instrument + ", " + teacherName);
        instrumentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");

        Label dateLabel = new Label(slot.getLessonDate().format(dateFormatter));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #718096;");

        String timeText = slot.getStartTime() + " - " + slot.getEndTime();

        HBox timeBox = new HBox(8);
        timeBox.setAlignment(Pos.CENTER_LEFT);

        Button timeBtn = new Button(timeText);
        timeBtn.setStyle(
                "-fx-background-color: #2D4A47; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 8 16;");
        timeBtn.setPrefWidth(100);

        Button deleteBtn = new Button(localizationManager.getString("action.delete"));
        deleteBtn.setPrefWidth(60);
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #718096; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> handleDeleteBooking(booking, slot));

        timeBox.getChildren().addAll(timeBtn, deleteBtn);

        card.getChildren().addAll(instrumentLabel, dateLabel, timeBox);

        return card;
    }

    private String localizeInstrumentName(String instrumentName) {
        return localizationManager.getLocalizedInstrumentName(instrumentName);
    }

    void handleDeleteBooking(Booking booking, TimeSlot slot) {
        booking.setBookingStatus(Booking.STATUS_CANCELLED);
        boolean updated = bookingDAO.update(booking);

        if (updated) {
            timeSlotDAO.updateStatus(slot.getSlotId(), TimeSlot.STATUS_AVAILABLE);
            loadBookings();
        }
    }

    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        NavigationHelper.navigateTo(event, getClass(),
                "/fxml/student_course_booking.fxml",
                localizationManager.getString("app.title.student.dashboard"));
    }

    @FXML
    private void handlePrevPage(ActionEvent event) {
        // Placeholder for pagination
    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        // Placeholder for pagination
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        NavigationHelper.logout(event, getClass(), localizationManager.getString("app.title.login"));
    }
}
