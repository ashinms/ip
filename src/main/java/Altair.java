import java.util.Scanner;

/**
 * A simple command-line task manager.
 */
public class Altair {

    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        String separator = "____________________________________________________________";
        String banner = "   _____  .__   __         .__        \n"
                + "  /  _  \\ |  | _/  |______ |__|______ \n"
                + " /  /_\\  \\|  | \\   __\\__  \\|  \\_  __ \\\n"
                + "/    |    \\  |__|  |  / __ \\|  ||  | \\/\n"
                + "\\____|__  /____/|__| (____  /__||__|  \n"
                + "        \\/                \\/          ";

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Greetings, I am Altair.");
        System.out.println("How may I help you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        Task[] tasks = new Task[MAX_TASKS];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println(separator);

            try {
                if (command.trim().equals("bye")) {
                    System.out.println("    Goodbye. Let me know when you need me again.");
                    System.out.println(separator);
                    break;
                }

                if (command.trim().equals("list")) {
                    System.out.println("     Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println("     " + (i + 1) + "." + tasks[i]);
                    }
                    System.out.println(separator);
                } else if (command.trim().equals("mark") || command.trim().startsWith("mark ")) {
                    taskCount = markTask(command, tasks, taskCount, separator);
                } else if (command.trim().equals("unmark") || command.trim().startsWith("unmark ")) {
                    taskCount = unmarkTask(command, tasks, taskCount, separator);
                } else {
                    Task newTask = createTask(command);
                    if (taskCount >= MAX_TASKS) {
                        throw new AltairException("Your task list is full. Please remove a task before adding another.");
                    }
                    tasks[taskCount] = newTask;
                    taskCount++;
                    printAddedTask(newTask, taskCount, separator);
                }
            } catch (AltairException exception) {
                System.out.println("    " + exception.getMessage());
                System.out.println(separator);
            }
        }
    }

    /**
     * Creates the task represented by a user command.
     *
     * <p>Typed commands use markers so descriptions and date/time strings may
     * contain spaces.</p>
     *
     * @param command the complete command entered by the user
     * @return the new task
     * @throws AltairException if the command is incomplete or unknown
     */
    private static Task createTask(String command) throws AltairException {
        String trimmed = command.trim();

        if (trimmed.equals("todo") || trimmed.startsWith("todo ")) {
            String description = textAfterCommand(trimmed, "todo");
            if (description.isEmpty()) {
                throw new AltairException("I'm afraid the description of a todo cannot be empty.");
            }
            return new Todo(description);
        }

        if (trimmed.equals("deadline") || trimmed.startsWith("deadline ")) {
            String remainder = textAfterCommand(trimmed, "deadline");
            int byIndex = remainder.indexOf(" /by ");
            if (remainder.isEmpty() || byIndex == 0 || remainder.startsWith("/by ") || remainder.equals("/by")) {
                throw new AltairException("I'm afraid the description of a deadline cannot be empty.");
            }
            if (byIndex < 0) {
                throw new AltairException("A deadline needs a date or time after /by.");
            }

            String description = remainder.substring(0, byIndex).trim();
            String by = remainder.substring(byIndex + 5).trim();
            if (description.isEmpty()) {
                throw new AltairException("I'm afraid the description of a deadline cannot be empty.");
            }
            if (by.isEmpty()) {
                throw new AltairException("A deadline needs a date or time after /by.");
            }
            return new Deadline(description, by);
        }

        if (trimmed.equals("event") || trimmed.startsWith("event ")) {
            String remainder = textAfterCommand(trimmed, "event");
            int fromIndex = remainder.indexOf(" /from ");
            int toIndex = remainder.indexOf(" /to ");
            if (remainder.isEmpty() || fromIndex == 0
                    || remainder.startsWith("/from ") || remainder.equals("/from")) {
                throw new AltairException("I'm afraid the description of an event cannot be empty.");
            }
            if (fromIndex < 0 || toIndex < 0 || toIndex <= fromIndex + 7) {
                throw new AltairException("An event needs /from and /to date or time details.");
            }

            String description = remainder.substring(0, fromIndex).trim();
            String from = remainder.substring(fromIndex + 7, toIndex).trim();
            String to = remainder.substring(toIndex + 5).trim();
            if (description.isEmpty()) {
                throw new AltairException("I'm afraid the description of an event cannot be empty.");
            }
            if (from.isEmpty() || to.isEmpty()) {
                throw new AltairException("An event needs /from and /to date or time details.");
            }
            return new Event(description, from, to);
        }

        throw new AltairException("I do not understand your command. Try again, perhaps?");
    }

    /**
     * Returns the part of a command after its command word.
     *
     * @param command the trimmed command
     * @param commandWord the command word to remove
     * @return the remaining text
     */
    private static String textAfterCommand(String command, String commandWord) {
        return command.substring(commandWord.length()).trim();
    }

    /** Prints the common confirmation shown after adding any task type. */
    private static void printAddedTask(Task task, int taskCount, String separator) {
        System.out.println("    Got it. I've added this task:");
        System.out.println("      " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
        System.out.println(separator);
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     * Invalid mark commands are reported and do not become new tasks.
     *
     * @param command the command entered by the user
     * @param tasks the current task array
     * @param taskCount the number of tasks currently stored
     * @param separator the line used to separate responses
     * @return the unchanged task count
     * @throws AltairException if the command does not contain a valid task number
     */
    private static int markTask(String command, Task[] tasks, int taskCount, String separator)
            throws AltairException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            throw new AltairException("Please use: mark <task number>.");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new AltairException("That task number is not in your list.");
            }

            Task task = tasks[taskNumber - 1];
            task.markAsDone();
            System.out.println("     Nice! I've marked this task as done:");
            System.out.println("       " + task);
        } catch (NumberFormatException exception) {
            throw new AltairException("Please use a valid task number.");
        }
        System.out.println(separator);
        return taskCount;
    }

    /**
     * Marks the task selected by an {@code unmark <number>} command as not done.
     * Invalid unmark commands are reported and do not become new tasks.
     *
     * @param command the command entered by the user
     * @param tasks the current task array
     * @param taskCount the number of tasks currently stored
     * @param separator the line used to separate responses
     * @return the unchanged task count
     * @throws AltairException if the command does not contain a valid task number
     */
    private static int unmarkTask(String command, Task[] tasks, int taskCount, String separator)
            throws AltairException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            throw new AltairException("Please use: unmark <task number>.");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new AltairException("That task number is not in your list.");
            }

            Task task = tasks[taskNumber - 1];
            task.markAsNotDone();
            System.out.println("     OK, I've marked this task as not done yet:");
            System.out.println("       " + task);
        } catch (NumberFormatException exception) {
            throw new AltairException("Please use a valid task number.");
        }
        System.out.println(separator);
        return taskCount;
    }

}
