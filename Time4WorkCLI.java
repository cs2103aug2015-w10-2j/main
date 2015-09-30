import java.util.*;
import UserInterface;
import Logic;
import FeedbackMessage;
import Tasks;
import DeadLines;

public class Time4WorkCLI implements UserInterface {
	private static final String WELCOME_MESSAGE = "Welcome to Time4Work!";
	private static final Scanner scanner = new Scanner(System.in);
	private Logic cmdHandler = new Logic();

	@Override
	public void run() {
		System.out.println(WELCOME_MESSAGE);
		while (true) {
			String userCommand = scanner.nextLine();
			FeedbackMessage feedback = cmdHandler.executeCommand(userCommand);
			showToUser(feedback);
		}
	}

	/**
	 * this method to return the result after user command was run.
	 */
	private void showToUser(FeedbackMessage feedback) {
		System.out.println(feedback.getFeedback());
		System.out.println(getContent(feedback.getTask()));
	}

	private static String displayTask(Tasks task) {
		String description = task.getDescription();
		/*
		int type = task.getType(); //1 = deadline task, 2 = duration, 3 = blocked, 4 = floating
		DeadLines deadlines = task.getDueDetails(); 
		String startTime = deadlines.getStartTime(); 
		String endTime = deadlines.getEndTime();
		String startDate = deadlines.getStartDate();
		String endDate = deadlines.getEndDate();
		int deadlinesType= deadlines.getType(); //1 = deadline task, 2 = duration
		*/
		String taskItem = description + "";
		return taskItem;
	}
	
	public static String getContent(ArrayList<Tasks> taskList) {
		int index;
		String taskToDisplay = new String();

		for (int i = 0; i < taskList.size(); i++) {
			String taskItem = displayTask(taskList.get(i));
			index = i + 1;
			taskToDisplay += (index + ". " + taskItem +"\n");
		}
		return taskToDisplay;
	}
}
