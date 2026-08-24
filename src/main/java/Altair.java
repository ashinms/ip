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
            } else if (taskCount < MAX_TASKS) {
                Task newTask = createTask(command);
                if (newTask != null) {
                    tasks[taskCount] = newTask;
                    taskCount++;
                    printAddedTask(newTask, taskCount, separator);
                }
            }




        }
    }

    /**
     * Creates the task represented by a user command.
     *
     * <p>A plain command remains a convenient shorthand for a ToDo. Typed
     * commands use markers so descriptions and date/time strings may contain
     * spaces.</p>
     *
     * @param command the complete command entered by the user
     * @return the new task, or {@code null} when a typed command is malformed
     */
    private static Task createTask(String command) {
        String trimmed = command.trim();

        if (trimmed.equals("todo") || trimmed.startsWith("todo ")) {
            String description = textAfterCommand(trimmed, "todo");
            if (description.isEmpty()) {
                printUsage("todo <description>");
                return null;
            }
            return new Todo(description);
        }

        if (trimmed.equals("deadline") || trimmed.startsWith("deadline ")) {
            String remainder = textAfterCommand(trimmed, "deadline");
            int byIndex = remainder.indexOf(" /by ");
            if (byIndex <= 0) {
                printUsage("deadline <description> /by <date/time>");
                return null;
            }

            String description = remainder.substring(0, byIndex).trim();
            String by = remainder.substring(byIndex + 5).trim();
            if (description.isEmpty() || by.isEmpty()) {
                printUsage("deadline <description> /by <date/time>");
                return null;
            }
            return new Deadline(description, by);
        }

        if (trimmed.equals("event") || trimmed.startsWith("event ")) {
            String remainder = textAfterCommand(trimmed, "event");
            int fromIndex = remainder.indexOf(" /from ");
            int toIndex = remainder.indexOf(" /to ");
            if (fromIndex <= 0 || toIndex <= fromIndex + 7) {
                printUsage("event <description> /from <date/time> /to <date/time>");
                return null;
            }

            String description = remainder.substring(0, fromIndex).trim();
            String from = remainder.substring(fromIndex + 7, toIndex).trim();
            String to = remainder.substring(toIndex + 5).trim();
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                printUsage("event <description> /from <date/time> /to <date/time>");
                return null;
            }
            return new Event(description, from, to);
        }

        return new Todo(command);
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

    /** Prints the syntax expected for a malformed typed task command. */
    private static void printUsage(String usage) {
        System.out.println("    Please use: " + usage);
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
     */
    private static int markTask(String command, Task[] tasks, int taskCount, String separator) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            System.out.println("    Please use: mark <task number>");
            System.out.println(separator);
            return taskCount;
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > taskCount) {
                System.out.println("    That task number is not in your list.");
                System.out.println(separator);
                return taskCount;
            }

            Task task = tasks[taskNumber - 1];
            task.markAsDone();
            System.out.println("     Nice! I've marked this task as done:");
            System.out.println("       " + task);
        } catch (NumberFormatException exception) {
            System.out.println("    Please use a valid task number.");
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
     */
    private static int unmarkTask(String command, Task[] tasks, int taskCount, String separator) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            System.out.println("    Please use: unmark <task number>");
            System.out.println(separator);
            return taskCount;
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > taskCount) {
                System.out.println("    That task number is not in your list.");
                System.out.println(separator);
                return taskCount;
            }

            Task task = tasks[taskNumber - 1];
            task.markAsNotDone();
            System.out.println("     OK, I've marked this task as not done yet:");
            System.out.println("       " + task);
        } catch (NumberFormatException exception) {
            System.out.println("    Please use a valid task number.");
        }
        System.out.println(separator);
        return taskCount;
    }

}
