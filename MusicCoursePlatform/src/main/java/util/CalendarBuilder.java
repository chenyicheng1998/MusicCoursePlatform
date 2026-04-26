package util;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Utility for building month calendar grids used across multiple controllers.
 *
 * <p>Both {@code StudentDashboardController} and {@code TeacherDashboardController}
 * render an identical calendar widget; this class eliminates that duplication.</p>
 */
public final class CalendarBuilder {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final double DAY_SIZE = 40.0;

    private CalendarBuilder() {
    }

    /**
     * Rebuild the calendar grid for the given month.
     *
     * @param calendarGrid the FlowPane to populate
     * @param monthLabel   the Label showing "Month YYYY"
     * @param currentMonth the month to render
     * @param selectedDate the currently selected date (may be {@code null})
     * @param hasHighlight predicate — {@code true} if a date should receive
     *                     the {@code calendar-day-available} style class
     * @param onDateClick  callback invoked when a day button is clicked
     */
    public static void buildCalendar(FlowPane calendarGrid,
                                     Label monthLabel,
                                     YearMonth currentMonth,
                                     LocalDate selectedDate,
                                     Predicate<LocalDate> hasHighlight,
                                     Consumer<LocalDate> onDateClick) {

        monthLabel.setText(currentMonth.format(MONTH_FORMAT));
        calendarGrid.getChildren().clear();

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;

        for (int i = 0; i < dayOfWeek; i++) {
            Label emptyLabel = new Label("");
            emptyLabel.setPrefWidth(DAY_SIZE);
            emptyLabel.setPrefHeight(DAY_SIZE);
            calendarGrid.getChildren().add(emptyLabel);
        }

        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            LocalDate date = currentMonth.atDay(day);
            Button dayBtn = new Button(String.valueOf(day));
            dayBtn.setPrefWidth(DAY_SIZE);
            dayBtn.setPrefHeight(DAY_SIZE);
            dayBtn.getStyleClass().add("calendar-day");

            if (hasHighlight.test(date)) {
                dayBtn.getStyleClass().add("calendar-day-available");
            }

            if (date.equals(selectedDate)) {
                dayBtn.getStyleClass().add("calendar-day-selected");
            }

            dayBtn.setOnAction(e -> onDateClick.accept(date));
            calendarGrid.getChildren().add(dayBtn);
        }
    }
}

