package altair.task;

/**
 * The base type for every task in the task list.
 *
 * <p>Subclasses inherit the shared description and completion state while
 * customizing the type marker and any scheduling details they display.</p>
 */
public class Task {
    /** The text entered by the user for this task. */
    protected String description;

    /** Whether this task has been marked as done. */
    protected boolean isDone;

    /**
     * Creates an unfinished task.
     *
     * @param description the text describing the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the task description.
     *
     * @return the task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the status symbol used when displaying this task.
     *
     * @return {@code X} for a done task, otherwise a blank symbol.
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
     * Returns the category of this task.
     *
     * @return the task category.
     */
    protected TaskType getTaskType() {
        return TaskType.TODO;
    }

    /**
     * Returns the one-letter marker used for this task type.
     *
     * @return the task type marker.
     */
    protected String getTypeIcon() {
        return getTaskType().getIcon();
    }

    /**
     * Formats this task for the on-disk task list.
     *
     * @return the task type, completion state, and description separated by pipes.
     */
    public String toFileString() {
        return getTypeIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Formats the task for the list and completion messages.
     *
     * @return the task type, completion state, and description.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
