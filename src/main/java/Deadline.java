/**
 * A task that must be completed before a specified date or time.
 */
public class Deadline extends Task {

    /** The date or time by which this task should be completed. */
    protected String by;

    /**
     * Creates an unfinished deadline.
     *
     * @param description the task description
     * @param by the date or time string supplied by the user
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
