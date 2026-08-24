import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A simple command-line task manager.
 */
public class Altair {

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
        List<Task> tasks = new ArrayList<>();

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
                    System.out.println("     The following are your tasks");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println("     " + (i + 1) + "." + tasks.get(i));
                    }
                    System.out.println(separator);
                } else if (command.trim().equals("mark") || command.trim().startsWith("mark ")) {
                    markTask(command, tasks, separator);
                } else if (command.trim().equals("unmark") || command.trim().startsWith("unmark ")) {
                    unmarkTask(command, tasks, separator);
                } else if (command.trim().equals("delete") || command.trim().startsWith("delete ")) {
                    deleteTask(command, tasks, separator);
                } else {
                    Task newTask = createTask(command);
                    tasks.add(newTask);
                    printAddedTask(newTask, tasks.size(), separator);
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
        System.out.println("    Copy. Your task has been added:");
        System.out.println("      " + task);
        System.out.println("    Now you have " + taskCount + " tasks in the list.");
        System.out.println(separator);
    }

    /**
     * Marks the task selected by a {@code mark <number>} command as done.
     * Invalid mark commands are reported and do not become new tasks.
     *
     * @param command the command entered by the user
     * @param tasks the current task collection
     * @param separator the line used to separate responses
     * @throws AltairException if the command does not contain a valid task number
     */
    private static void markTask(String command, List<Task> tasks, String separator)
            throws AltairException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            throw new AltairException("Please use: mark <task number>.");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new AltairException("That task number is not in your list.");
            }

            Task task = tasks.get(taskNumber - 1);
            task.markAsDone();
            System.out.println("     Task marked as completed:");
            System.out.println("       " + task);
        } catch (NumberFormatException exception) {
            throw new AltairException("Please use a valid task number.");
        }
        System.out.println(separator);
    }

    /**
     * Marks the task selected by an {@code unmark <number>} command as not done.
     * Invalid unmark commands are reported and do not become new tasks.
     *
     * @param command the command entered by the user
     * @param tasks the current task collection
     * @param separator the line used to separate responses
     * @throws AltairException if the command does not contain a valid task number
     */
    private static void unmarkTask(String command, List<Task> tasks, String separator)
            throws AltairException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            throw new AltairException("Please use: unmark <task number>.");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new AltairException("That task number is not in your list.");
            }

            Task task = tasks.get(taskNumber - 1);
            task.markAsNotDone();
            System.out.println("     OK, I've marked this task as not done yet:");
            System.out.println("       " + task);
        } catch (NumberFormatException exception) {
            throw new AltairException("Please use a valid task number.");
        }
        System.out.println(separator);
    }

    /**
     * Deletes the task selected by a {@code delete <number>} command.
     * Invalid delete commands are reported and leave the task collection unchanged.
     *
     * @param command the command entered by the user
     * @param tasks the current task collection
     * @param separator the line used to separate responses
     * @throws AltairException if the command does not contain a valid task number
     */
    private static void deleteTask(String command, List<Task> tasks, String separator)
            throws AltairException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            throw new AltairException("Please use: delete <task number>.");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new AltairException("That task number is not in your list.");
            }

            Task removedTask = tasks.remove(taskNumber - 1);
            System.out.println("    Noted. I've removed this task:");
            System.out.println("      " + removedTask);
            System.out.println("    Now you have " + tasks.size() + " tasks in the list.");
        } catch (NumberFormatException exception) {
            throw new AltairException("Please use a valid task number.");
        }
        System.out.println(separator);
    }

}
