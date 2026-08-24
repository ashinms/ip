/**
 * The fixed categories of tasks supported by Altair.
 */
public enum TaskType {
    /** A basic task without date or time details. */
    TODO("T"),

    /** A task with a completion date or time. */
    DEADLINE("D"),

    /** A task with start and end date or time details. */
    EVENT("E");

    private final String icon;

    TaskType(String icon) {
        this.icon = icon;
    }

    /**
     * Returns the one-letter marker used when displaying this task type.
     *
     * @return the task type marker
     */
    public String getIcon() {
        return icon;
    }
}
