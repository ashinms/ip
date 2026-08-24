/**
 * A task with a start date/time and an end date/time.
 */
public class Event extends Task {

    /** The date or time string at which the event starts. */
    protected String from;

    /** The date or time string at which the event ends. */
    protected String to;

    /**
     * Creates an unfinished event.
     *
     * @param description the event description
     * @param from the start date or time string supplied by the user
     * @param to the end date or time string supplied by the user
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected TaskType getTaskType() {
        return TaskType.EVENT;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
