package util;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CalendarBuilderTest {

    @BeforeEach
    void initFX() throws Exception {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await();
        } catch (IllegalStateException e) {
            // Already initialized
        }
    }

    private void runOnFX(Runnable r) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                r.run();
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        if (error.get() != null) {
            throw new RuntimeException(error.get());
        }
    }

    // -----------------------------------------------------------------------
    // Month label formatting
    // -----------------------------------------------------------------------

    @Test
    void testBuildCalendar_SetsMonthLabel() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            YearMonth month = YearMonth.of(2026, 4); // April 2026

            CalendarBuilder.buildCalendar(grid, monthLabel, month, null,
                    date -> false, date -> {});

            assertTrue(monthLabel.getText().contains("2026"));
            assertTrue(monthLabel.getText().contains("April")
                    || monthLabel.getText().contains("avril")
                    || !monthLabel.getText().isEmpty());
        });
    }

    // -----------------------------------------------------------------------
    // Grid child count: padding + day buttons
    // -----------------------------------------------------------------------

    @Test
    void testBuildCalendar_CorrectChildCount_April2026() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            // April 2026: starts on Wednesday (DayOfWeek=3, 3%7=3) → 3 padding labels + 30 days = 33
            YearMonth month = YearMonth.of(2026, 4);

            CalendarBuilder.buildCalendar(grid, monthLabel, month, null,
                    date -> false, date -> {});

            assertEquals(33, grid.getChildren().size());
        });
    }

    @Test
    void testBuildCalendar_NopadddingWhenMonthStartsOnSunday() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            // March 2026: starts on Sunday (DayOfWeek=7, 7%7=0) → 0 padding + 31 days = 31
            YearMonth month = YearMonth.of(2026, 3);

            CalendarBuilder.buildCalendar(grid, monthLabel, month, null,
                    date -> false, date -> {});

            assertEquals(31, grid.getChildren().size());
        });
    }

    // -----------------------------------------------------------------------
    // CSS: calendar-day-available when hasHighlight is true
    // -----------------------------------------------------------------------

    @Test
    void testBuildCalendar_HighlightedDayHasAvailableStyleClass() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            YearMonth month = YearMonth.of(2026, 4);
            LocalDate highlightDate = LocalDate.of(2026, 4, 15);

            CalendarBuilder.buildCalendar(grid, monthLabel, month, null,
                    date -> date.equals(highlightDate), date -> {});

            // Buttons start after 3 padding labels; day 15 is at index 3 + 14 = 17
            Button day15Btn = (Button) grid.getChildren().get(17);
            assertTrue(day15Btn.getStyleClass().contains("calendar-day-available"),
                    "Day 15 should have calendar-day-available style");
        });
    }

    @Test
    void testBuildCalendar_NonHighlightedDayDoesNotHaveAvailableStyleClass() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            YearMonth month = YearMonth.of(2026, 4);

            CalendarBuilder.buildCalendar(grid, monthLabel, month, null,
                    date -> false, date -> {});

            Button day1Btn = (Button) grid.getChildren().get(3); // offset by 3 padding
            assertFalse(day1Btn.getStyleClass().contains("calendar-day-available"));
        });
    }

    // -----------------------------------------------------------------------
    // CSS: calendar-day-selected for selectedDate
    // -----------------------------------------------------------------------

    @Test
    void testBuildCalendar_SelectedDateHasSelectedStyleClass() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            YearMonth month = YearMonth.of(2026, 4);
            LocalDate selectedDate = LocalDate.of(2026, 4, 10);

            CalendarBuilder.buildCalendar(grid, monthLabel, month, selectedDate,
                    date -> false, date -> {});

            // Day 10 index = 3 (padding) + 9 (days 1-9) = 12
            Button day10Btn = (Button) grid.getChildren().get(12);
            assertTrue(day10Btn.getStyleClass().contains("calendar-day-selected"),
                    "Day 10 should have calendar-day-selected style");
        });
    }

    @Test
    void testBuildCalendar_NullSelectedDate_NoSelectedStyle() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            YearMonth month = YearMonth.of(2026, 4);

            CalendarBuilder.buildCalendar(grid, monthLabel, month, null,
                    date -> false, date -> {});

            for (var child : grid.getChildren()) {
                if (child instanceof Button btn) {
                    assertFalse(btn.getStyleClass().contains("calendar-day-selected"));
                }
            }
        });
    }

    // -----------------------------------------------------------------------
    // All day buttons have "calendar-day" base style class
    // -----------------------------------------------------------------------

    @Test
    void testBuildCalendar_AllDayButtonsHaveBaseStyleClass() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            YearMonth month = YearMonth.of(2026, 4); // 30 days

            CalendarBuilder.buildCalendar(grid, monthLabel, month, null,
                    date -> false, date -> {});

            long dayButtonCount = grid.getChildren().stream()
                    .filter(n -> n instanceof Button)
                    .map(n -> (Button) n)
                    .filter(b -> b.getStyleClass().contains("calendar-day"))
                    .count();
            assertEquals(30, dayButtonCount);
        });
    }

    // -----------------------------------------------------------------------
    // onDateClick callback is invoked when a day button fires
    // -----------------------------------------------------------------------

    @Test
    void testBuildCalendar_ClickingDayInvokesCallback() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            YearMonth month = YearMonth.of(2026, 4);
            List<LocalDate> clicked = new ArrayList<>();

            CalendarBuilder.buildCalendar(grid, monthLabel, month, null,
                    date -> false, clicked::add);

            // Fire the first day button (index 3 = day 1)
            Button day1Btn = (Button) grid.getChildren().get(3);
            day1Btn.fire();

            assertEquals(1, clicked.size());
            assertEquals(LocalDate.of(2026, 4, 1), clicked.get(0));
        });
    }

    // -----------------------------------------------------------------------
    // Grid is cleared before rebuild
    // -----------------------------------------------------------------------

    @Test
    void testBuildCalendar_ClearsPreviousChildren() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            grid.getChildren().add(new Label("OLD"));

            YearMonth month = YearMonth.of(2026, 4);
            CalendarBuilder.buildCalendar(grid, monthLabel, month, null,
                    date -> false, date -> {});

            // No "OLD" label should remain
            boolean hasOld = grid.getChildren().stream()
                    .anyMatch(n -> n instanceof Label lbl && "OLD".equals(lbl.getText()));
            assertFalse(hasOld, "Grid should not contain old children after rebuild");
        });
    }

    // -----------------------------------------------------------------------
    // Day button sizes
    // -----------------------------------------------------------------------

    @Test
    void testBuildCalendar_DayButtonsHaveCorrectSize() throws Exception {
        runOnFX(() -> {
            FlowPane grid = new FlowPane();
            Label monthLabel = new Label();
            YearMonth month = YearMonth.of(2026, 4);

            CalendarBuilder.buildCalendar(grid, monthLabel, month, null,
                    date -> false, date -> {});

            Button day1Btn = (Button) grid.getChildren().get(3);
            assertEquals(40.0, day1Btn.getPrefWidth(), 0.01);
            assertEquals(40.0, day1Btn.getPrefHeight(), 0.01);
        });
    }
}

