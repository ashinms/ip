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
    private static final String DEFAULT_STORAGE_PATH = "./data/altair.txt";

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

    /** Whether the most recent {@link #getResponse(String)} call returned an error. */
    private boolean hasError;

    /**
     * A newly created task awaiting the user's y/n duplicate confirmation, or
     * {@code null} when no confirmation is pending. Never persisted: the task
     * has not been added yet.
     */
    private Task pendingDuplicateTask;

    /**
     * Creates a task manager backed by the given save file, loading any tasks
     * already stored there.
     *
     * <p>A load failure is not thrown from the constructor: it is remembered in
     * {@link #getLoadError()} so the caller (text UI or GUI) can decide how to
     * report it, and the task list starts empty.</p>
     *
     * @param filePath the location of the save file, e.g. {@code ./data/altair.txt}.
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
     * Reports whether the most recent {@link #getResponse(String)} call
     * returned an error message.
     *
     * @return {@code true} if the last response was an error.
     */
    public boolean hasError() {
        return hasError;
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
        String greeting = "Greetings. Altair, at your service. How may I be of assistance?";
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
        hasError = false;
        try {
            if (pendingDuplicateTask != null) {
                String resolution = resolvePendingDuplicate(command);
                if (resolution != null) {
                    return resolution;
                }
            }

            CommandType commandType = CommandType.from(command);
            // from() falls back to UNKNOWN, so the switch below never sees null.
            assert commandType != null : "CommandType.from returns UNKNOWN, never null";

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
            hasError = true;
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
     * Creates the task described by a typed command. If it duplicates a task
     * already in the list, the add is held back pending a y/n confirmation
     * instead of being added immediately.
     *
     * @param command the complete command entered by the user.
     * @return the confirmation text, or a duplicate-confirmation prompt.
     * @throws AltairException if the command is incomplete or unknown, or the save fails.
     */
    private String addTask(String command) throws AltairException {
        Task newTask = createTask(command);
        Task duplicate = findDuplicate(newTask);
        if (duplicate != null) {
            pendingDuplicateTask = newTask;
            return Ui.formatDuplicateWarning(duplicate);
        }
        return commitTask(newTask);
    }

    /**
     * Finds the first existing task that a new task would duplicate.
     *
     * @param newTask the task about to be added.
     * @return the first matching existing task, or {@code null} if none match.
     */
    private Task findDuplicate(Task newTask) {
        for (Task task : tasks) {
            if (newTask.isDuplicateOf(task)) {
                return task;
            }
        }
        return null;
    }

    /**
     * Adds a task to the list and saves the updated list.
     *
     * @param newTask the task to add.
     * @return the confirmation text.
     * @throws AltairException if the save fails.
     */
    private String commitTask(Task newTask) throws AltairException {
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
     * Resolves a pending duplicate-task confirmation using the next line the
     * user types.
     *
     * <p>A {@code y}/{@code yes} answer adds and saves the pending task; a
     * {@code n}/{@code no} answer discards it. Anything else is treated as the
     * user ignoring the prompt: the pending task is silently dropped so the
     * caller can process the same input as an ordinary command instead.</p>
     *
     * @param command the line typed while a confirmation was pending.
     * @return the response for a yes/no answer, or {@code null} if the input
     *     was neither and should be handled as a new command.
     * @throws AltairException if confirming the add fails to save.
     */
    private String resolvePendingDuplicate(String command) throws AltairException {
        String answer = command == null ? "" : command.trim().toLowerCase(Locale.ROOT);
        if (answer.equals("y") || answer.equals("yes")) {
            Task confirmedTask = pendingDuplicateTask;
            pendingDuplicateTask = null;
            return commitTask(confirmedTask);
        }
        if (answer.equals("n") || answer.equals("no")) {
            pendingDuplicateTask = null;
            return Ui.formatDuplicateDeclined();
        }
        pendingDuplicateTask = null;
        return null;
    }

    /**
     * Creates the task represented by a user command by dispatching to the
     * helper for its command type.
     *
     * @param command the complete command entered by the user.
     * @return the new task.
     * @throws AltairException if the command is incomplete or unknown.
     */
    private static Task createTask(String command) throws AltairException {
        String trimmed = command == null ? "" : command.trim();

        switch (CommandType.from(trimmed)) {
        case TODO:
            return createTodo(trimmed);
        case DEADLINE:
            return createDeadline(trimmed);
        case EVENT:
            return createEvent(trimmed);
        default:
            throw new AltairException("I do not understand your command. Try again, perhaps?");
        }
    }

    /**
     * Creates the {@link Todo} described by a {@code todo <description>} command.
     *
     * @param command the trimmed command entered by the user.
     * @return the new todo.
     * @throws AltairException if the description is missing or contains '|'.
     */
    private static Todo createTodo(String command) throws AltairException {
        String description = textAfterCommand(command, "todo");
        if (description.isEmpty()) {
            throw new AltairException("The description of a todo cannot be empty.");
        }
        validateStorableText(description);
        return new Todo(description);
    }

    /**
     * Creates the {@link Deadline} described by a
     * {@code deadline <description> /by <date>} command.
     *
     * <p>Markers let the description and date contain spaces.</p>
     *
     * @param command the trimmed command entered by the user.
     * @return the new deadline.
     * @throws AltairException if the description or date is missing or malformed, or a field contains '|'.
     */
    private static Deadline createDeadline(String command) throws AltairException {
        String remainder = textAfterCommand(command, "deadline");
        String[] words = splitWords(remainder);
        int byIndex = findMarker(words, "/by", 0);
        if (words.length == 0 || byIndex == 0) {
            throw new AltairException("The description of a deadline cannot be empty.");
        }
        if (byIndex < 0) {
            throw new AltairException("A deadline needs a date after /by.");
        }

        String description = joinWords(words, 0, byIndex);
        String byText = joinWords(words, byIndex + 1, words.length);
        if (description.isEmpty()) {
            throw new AltairException("The description of a deadline cannot be empty.");
        }
        if (byText.isEmpty()) {
            throw new AltairException("A deadline needs a date after /by.");
        }
        validateStorableText(description);
        validateStorableText(byText);
        return new Deadline(description, parseDate(byText, "A deadline date"));
    }

    /**
     * Creates the {@link Event} described by an
     * {@code event <description> /from <date> /to <date>} command.
     *
     * <p>Markers let the description and dates contain spaces.</p>
     *
     * @param command the trimmed command entered by the user.
     * @return the new event.
     * @throws AltairException if the description or a date is missing or malformed, a field contains '|',
     *     or the start date is after the end date.
     */
    private static Event createEvent(String command) throws AltairException {
        String remainder = textAfterCommand(command, "event");
        String[] words = splitWords(remainder);
        int fromIndex = findMarker(words, "/from", 0);
        int toIndex = findMarker(words, "/to", fromIndex < 0 ? 0 : fromIndex + 1);
        if (words.length == 0 || fromIndex == 0) {
            throw new AltairException("The description of an event cannot be empty.");
        }
        if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex + 1) {
            throw new AltairException("An event needs /from and /to dates.");
        }

        String description = joinWords(words, 0, fromIndex);
        String fromText = joinWords(words, fromIndex + 1, toIndex);
        String toText = joinWords(words, toIndex + 1, words.length);
        if (description.isEmpty()) {
            throw new AltairException("The description of an event cannot be empty.");
        }
        if (fromText.isEmpty() || toText.isEmpty()) {
            throw new AltairException("An event needs /from and /to dates.");
        }
        validateStorableText(description);
        validateStorableText(fromText);
        validateStorableText(toText);

        LocalDate fromDate = parseDate(fromText, "An event start date");
        LocalDate toDate = parseDate(toText, "An event end date");
        if (fromDate.isAfter(toDate)) {
            throw new AltairException("An event's start date cannot be after its end date.");
        }
        return new Event(description, fromDate, toDate);
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

        List<Task> matches = tasks.stream()
                .filter(task -> task.descriptionContains(keyword))
                .toList();
        return Ui.formatFoundTasks(matches);
    }

    /**
     * Parses and range-checks the task number shared by the mark, unmark, and
     * delete commands.
     *
     * @param command the command entered by the user.
     * @param operation the command word, used in the usage hint on error.
     * @return the zero-based index of the referenced task in {@link #tasks}.
     * @throws AltairException if there is no valid number or it is out of range.
     */
    private int resolveTaskIndex(String command, String operation) throws AltairException {
        int taskNumber = parseTaskNumber(command, operation);
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new AltairException("That task number is not in your list.");
        }
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "the range check above guarantees taskNumber is a valid 1-based index";
        return taskNumber - 1;
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
        int index = resolveTaskIndex(command, "mark");

        Task task = tasks.get(index);
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
        int index = resolveTaskIndex(command, "unmark");

        Task task = tasks.get(index);
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
        int index = resolveTaskIndex(command, "delete");

        Task removedTask = tasks.remove(index);
        try {
            storage.save(tasks);
        } catch (AltairException exception) {
            tasks.add(index, removedTask);
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
