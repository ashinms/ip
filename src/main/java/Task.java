/**
 * The base type for every task in the task list.
 *
 * <p>Subclasses inherit the shared description and completion state while
 * customizing the type marker and any date/time details they display.</p>
 */
public class Task {
    /** The text entered by the user for this task. */
    protected String description;

    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /**
     * Creates an unfinished task.
     *
     * @param description the text describing the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the status symbol used when displaying this task.
     *
     * @return {@code X} for a done task, otherwise a blank symbol
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the one-letter marker used for this task type.
     *
     * @return {@code T} for a basic task
     */
    protected String getTypeIcon() {
        return "T";
    }

    /**
     * Formats the task for the list and completion messages.
     *
     * @return the task type, completion state, and description
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
