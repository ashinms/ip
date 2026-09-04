package altair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
 * A simple task manager that can be driven from the command line or from a
 * JavaFX GUI.
 *
 * <p>The command handling lives in {@link #getResponse(String)}, which takes one
 * line of user input and returns the text to show back. {@link #main(String[])}
 * drives the text UI by feeding typed lines through that method; the GUI classes
 * in {@code altair.gui} call the same method.</p>
 */
public class Altair {

    /** The save file used when the application is started normally. */
    private static final String DEFAULT_STORAGE_PATH = "./data/duke.txt";

    /** The date format accepted in commands. */
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /** Reads the saved task list at start-up and writes it back after changes. */
    private final Storage storage;

    /** The tasks currently held in memory. */
    private final List<Task> tasks;

    /** The message from a failed start-up load, or {@code null} if the load succeeded. */
    private final String loadError;

    /** Set once the user issues a valid {@code bye} command. */
    private boolean isExit;

    /**
     * Creates a task manager backed by the given save file, loading any tasks
     * already stored there.
     *
     * <p>A load failure is not thrown from the constructor: it is remembered in
     * {@link #getLoadError()} so the caller (text UI or GUI) can decide how to
     * report it, and the task list starts empty.</p>
     *
     * @param filePath the location of the save file, e.g. {@code ./data/duke.txt}.
     */
    public Altair(String filePath) {
        this.storage = new Storage(filePath);
        List<Task> loaded;
        String error;
        try {
            loaded = storage.load();
            error = null;
        } catch (AltairException exception) {
            loaded = new ArrayList<>();
            error = exception.getMessage();
        }
        this.tasks = loaded;
        this.loadError = error;
    }

    /**
     * Returns the explanation of a failed start-up load.
     *
     * @return the error message, or {@code null} if the saved tasks loaded cleanly.
     */
    public String getLoadError() {
        return loadError;
    }

    /**
     * Reports whether the user has asked to exit with the {@code bye} command.
     *
     * @return {@code true} once a valid {@code bye} command has been handled.
     */
    public boolean isExit() {
        return isExit;
    }

    /**
     * Returns the greeting shown when the GUI starts.
     *
     * <p>Unlike the text UI greeting this has no divider lines or ASCII banner,
     * which do not suit a chat bubble. A start-up load failure is included so
     * the user still sees it.</p>
     *
     * @return the greeting text.
     */
    public String getGreeting() {
        String greeting = "Greetings, I am Altair.\nHow may I help you?";
        if (loadError != null) {
            return loadError + "\n" + greeting;
        }
        return greeting;
    }

    /**
     * Handles one line of user input and returns the text to show back.
     *
     * <p>An {@link AltairException} raised while handling the command is turned
     * into an error message rather than propagated, so the caller's loop can
     * continue.</p>
     *
     * @param command the complete line entered by the user.
     * @return the response text, without a trailing newline.
     */
    public String getResponse(String command) {
        try {
            CommandType commandType = CommandType.from(command);

            if (commandType == CommandType.BYE) {
                ensureNoArguments(command, "bye");
                isExit = true;
                return Ui.formatGoodbye();
            }

            switch (commandType) {
            case LIST:
                ensureNoArguments(command, "list");
                return Ui.formatTaskList(tasks);
            case FIND:
                return findTasks(command);
            case MARK:
                return markTask(command);
            case UNMARK:
                return unmarkTask(command);
            case DELETE:
                return deleteTask(command);
            default:
                return addTask(command);
            }
        } catch (AltairException exception) {
            return Ui.formatError(exception.getMessage());
        }
    }

    /**
     * Runs the text UI: loads the saved tasks, greets the user, then reads and
     * handles one command per line until {@code bye} or end of input.
     *
     * @param args command-line arguments; not used.
     */
    public static void main(String[] args) {
        Altair altair = new Altair(DEFAULT_STORAGE_PATH);
        Ui ui = new Ui();

        if (altair.getLoadError() != null) {
            ui.showError(altair.getLoadError());
            return;
        }

        ui.showWelcome();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();

            ui.showLine();
            System.out.println(altair.getResponse(command));
            ui.showLine();

            if (altair.isExit()) {
                break;
            }
        }
    }

    /**
     * Creates the task described by a typed command, adds it to the list, and
     * saves the updated list.
     *
     * @param command the complete command entered by the user.
     * @return the confirmation text.
     * @throws AltairException if the command is incomplete or unknown, or the save fails.
     */
    private String addTask(String command) throws AltairException {
        Task newTask = createTask(command);
        tasks.add(newTask);
        try {
            storage.save(tasks);
        } catch (AltairException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        return Ui.formatAdded(newTask, tasks.size());
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
     * Returns the tasks whose description contains the keyword from a
     * {@code find <keyword>} command.
     *
     * @param command the command entered by the user.
     * @return the formatted list of matching tasks.
     * @throws AltairException if the command has no search keyword.
     */
    private String findTasks(String command) throws AltairException {
        String keyword = textAfterCommand(command.trim(), "find");
        if (keyword.isEmpty()) {
            throw new AltairException("Please use: find <keyword>.");
        }

        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.descriptionContains(keyword)) {
                matches.add(task);
            }
        }
        return Ui.formatFoundTasks(matches);
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done and
     * saves the updated list.
     *
     * @param command the command entered by the user.
     * @return the confirmation text.
     * @throws AltairException if the command does not contain a valid task number, or the save fails.
     */
    private String markTask(String command) throws AltairException {
        int taskNumber = parseTaskNumber(command, "mark");
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new AltairException("That task number is not in your list.");
        }

        Task task = tasks.get(taskNumber - 1);
        boolean wasDone = task.getStatusIcon().equals("X");
        task.markAsDone();
        try {
            storage.save(tasks);
        } catch (AltairException exception) {
            if (!wasDone) {
                task.markAsNotDone();
            }
            throw exception;
        }
        return Ui.formatMarked(task);
    }

    /**
     * Marks the task selected by an {@code unmark <number>} command as not done
     * and saves the updated list.
     *
     * @param command the command entered by the user.
     * @return the confirmation text.
     * @throws AltairException if the command does not contain a valid task number, or the save fails.
     */
    private String unmarkTask(String command) throws AltairException {
        int taskNumber = parseTaskNumber(command, "unmark");
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new AltairException("That task number is not in your list.");
        }

        Task task = tasks.get(taskNumber - 1);
        boolean wasDone = task.getStatusIcon().equals("X");
        task.markAsNotDone();
        try {
            storage.save(tasks);
        } catch (AltairException exception) {
            if (wasDone) {
                task.markAsDone();
            }
            throw exception;
        }
        return Ui.formatUnmarked(task);
    }

    /**
     * Deletes the task selected by a {@code delete <number>} command and saves
     * the updated list.
     *
     * @param command the command entered by the user.
     * @return the confirmation text.
     * @throws AltairException if the command does not contain a valid task number, or the save fails.
     */
    private String deleteTask(String command) throws AltairException {
        int taskNumber = parseTaskNumber(command, "delete");
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new AltairException("That task number is not in your list.");
        }

        Task removedTask = tasks.remove(taskNumber - 1);
        try {
            storage.save(tasks);
        } catch (AltairException exception) {
            tasks.add(taskNumber - 1, removedTask);
            throw exception;
        }
        return Ui.formatDeleted(removedTask, tasks.size());
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
