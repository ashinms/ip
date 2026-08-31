package altair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import altair.command.CommandType;
import altair.storage.Storage;
import altair.task.Deadline;
import altair.task.Event;
import altair.task.Task;
import altair.task.Todo;
import altair.ui.Ui;

/**
 * A simple command-line task manager.
 */
public class Altair {

    /** Handles reading the saved task list at start-up and writing it after changes. */
    private static final Storage STORAGE = new Storage("./data/duke.txt");

    /** Handles all reading from and printing to the console. */
    private static final Ui UI = new Ui();

    /** The date format accepted in commands. */
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /** This class only holds static members, so it is never instantiated. */
    private Altair() {
    }

    /**
     * Runs the task manager: loads the saved tasks, greets the user, then reads
     * and handles one command per line until {@code bye} or end of input.
     *
     * <p>Any {@link AltairException} raised while handling a command is shown to
     * the user and the loop continues; a failure to load the saved tasks at
     * start-up is reported and ends the program.</p>
     *
     * @param args command-line arguments; not used.
     */
    public static void main(String[] args) {
        List<Task> tasks;
        try {
            tasks = STORAGE.load();
        } catch (AltairException exception) {
            UI.showError(exception.getMessage());
            return;
        }

        UI.showWelcome();

        while (UI.hasNextCommand()) {
            String command = UI.readCommand();

            UI.showLine();

            try {
                CommandType commandType = CommandType.from(command);

                if (commandType == CommandType.BYE) {
                    ensureNoArguments(command, "bye");
                    UI.showGoodbye();
                    break;
                }

                if (commandType == CommandType.LIST) {
                    ensureNoArguments(command, "list");
                    UI.showTaskList(tasks);
                } else if (commandType == CommandType.MARK) {
                    markTask(command, tasks);
                } else if (commandType == CommandType.UNMARK) {
                    unmarkTask(command, tasks);
                } else if (commandType == CommandType.DELETE) {
                    deleteTask(command, tasks);
                } else {
                    Task newTask = createTask(command);
                    tasks.add(newTask);
                    try {
                        STORAGE.save(tasks);
                    } catch (AltairException exception) {
                        tasks.remove(tasks.size() - 1);
                        throw exception;
                    }
                    UI.showAdded(newTask, tasks.size());
                }
            } catch (AltairException exception) {
                UI.showError(exception.getMessage());
            } finally {
                UI.showLine();
            }
        }
    }

    /**
     * Creates the task represented by a user command.
     *
     * <p>Typed commands use markers so descriptions and date strings may
     * contain spaces.</p>
     *
     * @param command the complete command entered by the user.
     * @return the new task.
     * @throws AltairException if the command is incomplete or unknown.
     */
    private static Task createTask(String command) throws AltairException {
        String trimmed = command == null ? "" : command.trim();

        switch (CommandType.from(trimmed)) {
        case TODO: {
            String description = textAfterCommand(trimmed, "todo");
            if (description.isEmpty()) {
                throw new AltairException("I'm afraid the description of a todo cannot be empty.");
            }
            validateStorableText(description);
            return new Todo(description);
        }

        case DEADLINE: {
            String remainder = textAfterCommand(trimmed, "deadline");
            String[] words = splitWords(remainder);
            int byIndex = findMarker(words, "/by", 0);
            if (words.length == 0 || byIndex == 0) {
                throw new AltairException("I'm afraid the description of a deadline cannot be empty.");
            }
            if (byIndex < 0) {
                throw new AltairException("A deadline needs a date after /by.");
            }

            String description = joinWords(words, 0, byIndex);
            String byText = joinWords(words, byIndex + 1, words.length);
            if (description.isEmpty()) {
                throw new AltairException("I'm afraid the description of a deadline cannot be empty.");
            }
            if (byText.isEmpty()) {
                throw new AltairException("A deadline needs a date after /by.");
            }
            validateStorableText(description);
            validateStorableText(byText);
            return new Deadline(description, parseDate(byText, "A deadline date"));
        }

        case EVENT: {
            String remainder = textAfterCommand(trimmed, "event");
            String[] words = splitWords(remainder);
            int fromIndex = findMarker(words, "/from", 0);
            int toIndex = findMarker(words, "/to", fromIndex < 0 ? 0 : fromIndex + 1);
            if (words.length == 0 || fromIndex == 0) {
                throw new AltairException("I'm afraid the description of an event cannot be empty.");
            }
            if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex + 1) {
                throw new AltairException("An event needs /from and /to dates.");
            }

            String description = joinWords(words, 0, fromIndex);
            String fromText = joinWords(words, fromIndex + 1, toIndex);
            String toText = joinWords(words, toIndex + 1, words.length);
            if (description.isEmpty()) {
                throw new AltairException("I'm afraid the description of an event cannot be empty.");
            }
            if (fromText.isEmpty() || toText.isEmpty()) {
                throw new AltairException("An event needs /from and /to dates.");
            }
            validateStorableText(description);
            validateStorableText(fromText);
            validateStorableText(toText);
            return new Event(description, parseDate(fromText, "An event start date"),
                    parseDate(toText, "An event end date"));
        }

        default:
            throw new AltairException("I do not understand your command. Try again, perhaps?");
        }
    }

    /**
     * Returns the part of a command after its command word.
     *
     * @param command the trimmed command.
     * @param commandWord the command word to remove.
     * @return the remaining text.
     */
    private static String textAfterCommand(String command, String commandWord) {
        return command.substring(commandWord.length()).trim();
    }

    /** Splits a command remainder into non-empty whitespace-delimited words. */
    private static String[] splitWords(String text) {
        String trimmed = text == null ? "" : text.trim();
        return trimmed.isEmpty() ? new String[0] : trimmed.split("\\s+");
    }

    /** Finds a case-insensitive marker token from a given word index onward. */
    private static int findMarker(String[] words, String marker, int startIndex) {
        for (int i = Math.max(0, startIndex); i < words.length; i++) {
            if (words[i].toLowerCase(Locale.ROOT).equals(marker)) {
                return i;
            }
        }
        return -1;
    }

    /** Joins a range of command words with single spaces. */
    private static String joinWords(String[] words, int startIndex, int endIndex) {
        if (startIndex >= endIndex) {
            return "";
        }
        return String.join(" ", Arrays.copyOfRange(words, startIndex, endIndex));
    }

    /** Parses a user-supplied ISO date and reports a helpful command error. */
    private static LocalDate parseDate(String text, String dateDescription) throws AltairException {
        try {
            return LocalDate.parse(text, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new AltairException(dateDescription + " must use yyyy-MM-dd format.");
        }
    }

    /** Rejects the file delimiter in user data so saved rows stay parseable. */
    private static void validateStorableText(String text) throws AltairException {
        if (text.contains("|")) {
            throw new AltairException("Task details cannot contain the '|' character.");
        }
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     * Invalid mark commands are reported and do not become new tasks.
     *
     * @param command the command entered by the user.
     * @param tasks the current task collection.
     * @throws AltairException if the command does not contain a valid task number.
     */
    private static void markTask(String command, List<Task> tasks)
            throws AltairException {
        int taskNumber = parseTaskNumber(command, "mark");
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new AltairException("That task number is not in your list.");
        }

        Task task = tasks.get(taskNumber - 1);
        boolean wasDone = task.getStatusIcon().equals("X");
        task.markAsDone();
        try {
            STORAGE.save(tasks);
        } catch (AltairException exception) {
            if (!wasDone) {
                task.markAsNotDone();
            }
            throw exception;
        }
        UI.showMarked(task);
    }

    /**
     * Marks the task selected by an {@code unmark <number>} command as not done.
     * Invalid unmark commands are reported and do not become new tasks.
     *
     * @param command the command entered by the user.
     * @param tasks the current task collection.
     * @throws AltairException if the command does not contain a valid task number.
     */
    private static void unmarkTask(String command, List<Task> tasks)
            throws AltairException {
        int taskNumber = parseTaskNumber(command, "unmark");
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new AltairException("That task number is not in your list.");
        }

        Task task = tasks.get(taskNumber - 1);
        boolean wasDone = task.getStatusIcon().equals("X");
        task.markAsNotDone();
        try {
            STORAGE.save(tasks);
        } catch (AltairException exception) {
            if (wasDone) {
                task.markAsDone();
            }
            throw exception;
        }
        UI.showUnmarked(task);
    }

    /**
     * Deletes the task selected by a {@code delete <number>} command.
     * Invalid delete commands are reported and leave the task collection unchanged.
     *
     * @param command the command entered by the user.
     * @param tasks the current task collection.
     * @throws AltairException if the command does not contain a valid task number.
     */
    private static void deleteTask(String command, List<Task> tasks)
            throws AltairException {
        int taskNumber = parseTaskNumber(command, "delete");
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new AltairException("That task number is not in your list.");
        }

        Task removedTask = tasks.remove(taskNumber - 1);
        try {
            STORAGE.save(tasks);
        } catch (AltairException exception) {
            tasks.add(taskNumber - 1, removedTask);
            throw exception;
        }
        UI.showDeleted(removedTask, tasks.size());
    }

    /** Parses the single positive integer used by mark, unmark, and delete. */
    private static int parseTaskNumber(String command, String operation) throws AltairException {
        String[] parts = splitWords(command);
        if (parts.length != 2) {
            throw new AltairException("Please use: " + operation + " <task number>.");
        }
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new AltairException("Please use a valid task number.");
        }
    }

    /** Rejects arguments on commands that only have a bare command form. */
    private static void ensureNoArguments(String command, String operation) throws AltairException {
        if (splitWords(command).length != 1) {
            throw new AltairException("Please use: " + operation + ".");
        }
    }

}
