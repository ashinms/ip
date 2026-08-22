import java.util.Scanner;

/**
 * Starts Altair and responds to commands entered by the user.
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
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            System.out.println(separator);

            if (command.trim().equals("bye")) {
                System.out.println("    Goodbye. Let me know when you need me again.");
                System.out.println(separator);
                break;
            }

            System.out.println("    " + command);
            System.out.println(separator);
        }
    }
}
