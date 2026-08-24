/**
 * The commands that Altair understands at the start of a user input line.
 */
public enum CommandType {
    /** Ends the application. */
    BYE("bye"),

    /** Displays all tasks. */
    LIST("list"),

    /** Marks a task as completed. */
    MARK("mark"),

    /** Marks a task as not completed. */
    UNMARK("unmark"),

    /** Removes a task. */
    DELETE("delete"),

    /** Creates a basic task. */
    TODO("todo"),

    /** Creates a deadline task. */
    DEADLINE("deadline"),

    /** Creates an event task. */
    EVENT("event"),

    /** Represents an input that does not begin with a known command. */
    UNKNOWN("");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Identifies the command at the start of an input line.
     *
     * @param command the complete input line
     * @return the matching command type, or {@link #UNKNOWN}
     */
    public static CommandType from(String command) {
        String trimmed = command.trim();
        for (CommandType type : values()) {
            if (!type.keyword.isEmpty()
                    && (trimmed.equals(type.keyword) || trimmed.startsWith(type.keyword + " "))) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
