import java.util.*;

public class Time4WorkCLI {
	private static final String WELCOME_MESSAGE = "Welcome to Time4Work!";
	private static final Scanner scanner = new Scanner(System.in);
	private static Logic cmdHandler = new Logic();
	
	public static void main (String[] args) throws IOException {
		System.out.println(WELCOME_MESSAGE);
		run();
	}
	
	public static void run() throws IOException {
		while (true) {
			String userCommand = scanner.nextLine();
			FeedbackMessage feedback;
            		feedback = cmdHandler.executeCommand(userCommand);
            		showToUser(feedback);
		}
	}

	/**
	 * this method to return the result after user command was run.
	 */
	private static void showToUser(FeedbackMessage feedback) {
		System.out.println(feedback.getFeedback());
	}
}
