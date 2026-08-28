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
     * @param description the task description
     * @param by the date by which the task should be completed
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | " + by;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
