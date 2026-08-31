package altair.ui;

import java.util.List;
import java.util.Scanner;

import altair.task.Task;

/**
 * Handles every interaction with the user on the command line: reading
 * typed commands and printing the greeting, the divider lines, task
 * confirmations, and error messages.
 *
 * <p>Keeping all console input and output here means the rest of the program
 * never touches {@code System.in} or {@code System.out} directly, so the
 * wording and layout of the UI can change without touching the task logic.</p>
 */
public class Ui {

    /** The horizontal divider printed between the user's input and each response. */
    private static final String SEPARATOR =
            "____________________________________________________________";

    /** The ASCII-art logo shown once when the program starts. */
    private static final String BANNER = "   _____  .__   __         .__        \n"
            + "  /  _  \\ |  | _/  |______ |__|______ \n"
            + " /  /_\\  \\|  | \\   __\\__  \\|  \\_  __ \\\n"
            + "/    |    \\  |__|  |  / __ \\|  ||  | \\/\n"
            + "\\____|__  /____/|__| (____  /__||__|  \n"
            + "        \\/                \\/          ";

    /** The source of typed commands. */
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reports whether another command line is available to read.
     *
     * @return {@code true} if {@link #readCommand()} can return another line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command line entered by the user.
     *
     * @return the raw text of the next line.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the divider line used to separate responses. */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Prints the start-up greeting, framed by divider lines. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Greetings, I am Altair.");
        System.out.println("How may I help you?");
        System.out.println(SEPARATOR);
    }

    /**
     * Prints an error explanation, indented to line up with other responses.
     *
     * @param message the explanation to show.
     */
    public void showError(String message) {
        System.out.println("    " + message);
    }

    /** Prints the farewell shown in response to the {@code bye} command. */
    public void showGoodbye() {
        System.out.println("    Goodbye. Let me know when you need me again.");
    }

    /**
     * Prints the whole task list, numbered from one.
     *
     * @param tasks the tasks to display, in list order.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println("     The following are your tasks");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Prints the confirmation shown after a new task is added.
     *
     * @param task the task that was added.
     * @param taskCount the number of tasks now in the list.
     */
    public void showAdded(Task task, int taskCount) {
        System.out.println("    Copy. Your task has been added:");
        System.out.println("      " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
    }

    /**
     * Prints the confirmation shown after a task is marked as done.
     *
     * @param task the task that was marked.
     */
    public void showMarked(Task task) {
        System.out.println("     Task marked as completed:");
        System.out.println("       " + task);
    }

    /**
     * Prints the confirmation shown after a task is marked as not done.
     *
     * @param task the task that was unmarked.
     */
    public void showUnmarked(Task task) {
        System.out.println("     OK, I've marked this task as not done yet:");
        System.out.println("       " + task);
    }

    /**
     * Prints the confirmation shown after a task is deleted.
     *
     * @param task the task that was removed.
     * @param taskCount the number of tasks left in the list.
     */
    public void showDeleted(Task task, int taskCount) {
        System.out.println("    Noted. I've removed this task:");
        System.out.println("      " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
    }
}
