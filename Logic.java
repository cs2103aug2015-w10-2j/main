import java.io.IOException;
import java.util.ArrayList;

public class Logic {
    
    private static final String MESSAGE_INVALID_FORMAT = "invalid command: %1$s";
    private static final String EMPTY_COMMAND = "Command is empty!";
    private static final String MESSAGE_ADDED = "Task added successfully!";
    private static final String MESSAGE_UPDATED = "Task updated successfully!";
    private static final String MESSAGE_DELETED = "Task deleted successfully!";
    private static final String MESSAGE_CLEAR = "All task cleared successfully!";
    private static final String MESSAGE_UNDO = "Undo successfully!";
    
    private Storage storage;
    private ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
    
    enum COMMAND_TYPE {
        ADD, READ, UPDATE, DELETE, SEARCH, SORT, INVALID, UNDO, CLEAR, EXIT
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
        Tasks task;
        
        COMMAND_TYPE commandType = determineCommandType(parsedCommand.getCommand());
        
        switch (commandType) {
            case ADD:
                task = parsedCommand.getTask();
                return executeAdd(task);
            case READ:
                return executeRead();
            case UPDATE:
                task = parsedCommand.getTask();
                return executeUpdate(task);
            case DELETE:
                int userInputIndex = parsedCommand.getIndexToBeDeleted();
                return executeDelete(userInputIndex);
            case SORT:
                return executeSort();
            case UNDO:
                return executeUndo();
            case CLEAR:
                return executeClear();
            case SEARCH:
                String searchKeyword = parsedCommand.getSearchKeyword();
                return executeSearch(searchKeyword);
            case INVALID:
                return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, "invalid"));
            case EXIT:
                System.exit(0);
            default:
                //throw an error if the command is not recognized
                throw new Error("Unrecognized command type");
        }
    }
    
    public static COMMAND_TYPE determineCommandType(String commandTypeString) {
        if (commandTypeString == null) {
            throw new Error("command type string cannot be null!");
        }
        
        if (commandTypeString.equalsIgnoreCase("add")) {
            return COMMAND_TYPE.ADD;
        } else if (commandTypeString.equalsIgnoreCase("read")) {
            return COMMAND_TYPE.READ;
        } else if (commandTypeString.equalsIgnoreCase("delete")){
            return COMMAND_TYPE.DELETE;
        } else if (commandTypeString.equalsIgnoreCase("sort")){
            return COMMAND_TYPE.SORT;
        } else if (commandTypeString.equalsIgnoreCase("search")){
            return COMMAND_TYPE.SEARCH;
        } else if (commandTypeString.equalsIgnoreCase("clear")){
            return COMMAND_TYPE.CLEAR;
        } else if (commandTypeString.equalsIgnoreCase("exit")) {
            return COMMAND_TYPE.EXIT;
        } else {
            return COMMAND_TYPE.INVALID;
        }
    }
    
    private FeedbackMessage executeAdd(Tasks newTask) {
        storage.appendTask(newTask);
        return new FeedbackMessage(MESSAGE_ADDED);
    }
    
    private FeedbackMessage executeRead() {
        myTaskList = storage.getMyTaskList();
        String taskListContent = getTaskListContent(myTaskList);
        return new FeedbackMessage(taskListContent);
    }
    
    private FeedbackMessage executeDelete(int userInputIndex) throws IOException {
        int taskID = getTaskIDFromUserInput(userInputIndex);
        storage.deleteTask(taskID);
        return new FeedbackMessage(MESSAGE_DELETED);
    }
    
    private FeedbackMessage executeUpdate(Tasks task) throws IOException {
        int taskID = task.getTaskID();
        storage.UpdateTask(taskID, task);
        return new FeedbackMessage(MESSAGE_UPDATED);
    }
    
    private FeedbackMessage executeSort() throws IOException {
        myTaskList = storage.getMyTaskList();
        myTaskList.sort(null);
        String taskListContent = getTaskListContent(myTaskList);
        return new FeedbackMessage(taskListContent);
    }
    
    private FeedbackMessage executeSearch(String keyword) throws IOException {
        myTaskList = storage.SearchTask(keyword);
        String taskListContent = getTaskListContent(myTaskList);
        return new FeedbackMessage(taskListContent);
    }
    
    private FeedbackMessage executeUndo() {
        return new FeedbackMessage(MESSAGE_UNDO);
    }
    
    private FeedbackMessage executeClear() {
        return new FeedbackMessage(MESSAGE_CLEAR);
    }
    
    private int getTaskIDFromUserInput(int userInput) {
        myTaskList = storage.getMyTaskList();
        Tasks requriedTask = myTaskList.get(userInput);
        int taskID= requriedTask.getTaskID();
        return taskID;
    }
    
    private static String getTaskListContent(ArrayList<Tasks> taskList) {
        String taskListContent = "";
        int taskListSize = taskList.size();
        for (int i = 0; i < taskListSize - 1; i++){
            taskListContent += taskList.get(i).getDescription() + "\n";
        }
        taskListContent += taskList.get(taskListSize - 1);
        return taskListContent;
    }
    
    private static boolean checkIfEmptyString(String userCommand) {
        return userCommand.trim().equals("");
    }
    
}