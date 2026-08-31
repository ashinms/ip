package altair.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task that must be completed before a specified date.
 */
public class Deadline extends Task {

    /** The date by which this task should be completed. */
    protected LocalDate by;

    /** The format used when showing dates to the user. */
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    /**
     * Creates an unfinished deadline.
     *
     * @param description the task description.
     * @param by the date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link TaskType#DEADLINE}
     */
    @Override
    protected TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Appends the due date, so the saved line is {@code D | done | description | yyyy-MM-dd}.</p>
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + by;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Adds the due date in {@code MMM dd yyyy} form, e.g. {@code (by: Oct 15 2025)}.</p>
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
