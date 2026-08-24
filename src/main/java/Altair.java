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
                    System.out.println("     " + (i + 1) + ".[" + tasks[i].getStatusIcon()
                            + "] " + tasks[i].getDescription());
                }
                System.out.println(separator);
            } else if (command.trim().equals("mark") || command.trim().startsWith("mark ")) {
                taskCount = markTask(command, tasks, taskCount, separator);
            } else if (command.trim().equals("unmark") || command.trim().startsWith("unmark ")) {
                taskCount = unmarkTask(command, tasks, taskCount, separator);
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = new Task(command);
                taskCount++;
                System.out.println("    added: " + command);
                System.out.println(separator);
            }




        }
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
            System.out.println("       [" + task.getStatusIcon() + "] " + task.getDescription());
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
            System.out.println("       [" + task.getStatusIcon() + "] " + task.getDescription());
        } catch (NumberFormatException exception) {
            System.out.println("    Please use a valid task number.");
        }
        System.out.println(separator);
        return taskCount;
    }

}
