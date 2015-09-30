import java.io.IOException;
import java.util.ArrayList;

public class Logic {
	
	private static final String MESSAGE_INVALID_FORMAT = "invalid command: %1$s";
	private static final String EMPTY_COMMAND = "Command is empty!";
	private static final String MESSAGE_ADDED = "Task added successfully!";
	private static final String MESSAGE_SORTED = "Task sorted successfully";
	
	private Storage storage;
	private ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();

	enum COMMAND_TYPE {
		ADD, DELETE, UPDATE, SEARCH, SORT, INVALID, EXIT
	};
	
	public Logic() {
		storage = new Storage();
	}
	
	public FeedbackMessage executeCommand (String userInput) throws IOException {
		String userCommand = userInput;
		
		if(checkIfEmptyString(userCommand)){
			return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, EMPTY_COMMAND));
		}
		
		Time4WorkParser parser = new Time4WorkParser();
		
		Command parsedCommand = parser.parse(userCommand);
		
		Tasks task = parsedCommand.getTask();
		
		switch (COMMAND_TYPE.valueOf(parsedCommand.getCommand())) {
		case ADD:
			return executeAdd(task);
		case DELETE:
		 	return executeDelete(task);
		case SORT:
			return executeSort(task);
	/*  case UNDO:
			return executeUndo();
		case CLEAR:
			return clear();			
		case SEARCH:
			return search(removeFirstWord(userCommand));
	*/ 
		case INVALID:
			return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, "invalid"));
		case EXIT:
			System.exit(0);
		default:
			//throw an error if the command is not recognized
			throw new Error("Unrecognized command type");
		}
	}
	
	private FeedbackMessage executeAdd(Tasks newTask) {
		myTaskList = storage.appendTask(newTask);
		return new FeedbackMessage(MESSAGE_ADDED, myTaskList);
	}

	private FeedbackMessage executeDelete(Tasks task) throws IOException {
		int taskID = task.getTaskID();
		myTaskList = storage.deleteTask(taskID);
		return new FeedbackMessage("");
	}
	
	private FeedbackMessage executeSort(Tasks task) throws IOException {
		myTaskList = storage.getMyTaskList();
		myTaskList.sort(null);
		return new FeedbackMessage(MESSAGE_SORTED, myTaskList);
	}
	
	
	private static boolean checkIfEmptyString(String userCommand) {
		return userCommand.trim().equals("");
	}
	
}