import java.io.IOException;
import java.util.ArrayList;

public class Logic {
    
    // =========================================================================
    // Feedback massage string
    // =========================================================================
    
    private static final String MESSAGE_INVALID_FORMAT = "invalid command: %1$s";
    private static final String MESSAGE_EMPTY_COMMAND = "Command is empty!";
    private static final String MESSAGE_ADDED = "Task added successfully!";
    private static final String MESSAGE_UPDATED_ = "Task updated %1$s !";
    private static final String MESSAGE_DELETED_ = "Task deleted %1$s !";
    private static final String MESSAGE_SORTED = "Task sorted successfully!";
    private static final String MESSAGE_SEARCH_ = "Task searched %1$s";
    private static final String MESSAGE_CLEAR = "All task cleared successfully!";
    private static final String MESSAGE_UNDO_ = "Undo %1$s!";
    
    // =========================================================================
    // local variables for Logic
    // =========================================================================
    
    private Storage storage;
    private ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
    private CommandHistory commandHistoryList = new CommandHistory();
    private CommandHistory commandTypeList = new CommandHistory();
    
    enum COMMAND_TYPE {
        ADD, UPDATE, DELETE, SEARCH, SORT, INVALID, UNDO, CLEAR, EXIT
    };
    
    // Constructor
    public Logic() {
        storage = new Storage();
    }
    
    // =========================================================================
    // Method to interact with UI and parser
    // =========================================================================
    
    public FeedbackMessage executeCommand (String userInput) throws IOException {
        String userCommand = userInput;
        myTaskList = getCurrentList();
        if(checkIfEmptyString(userCommand)) {
            return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, MESSAGE_EMPTY_COMMAND), myTaskList);
        }
        
        Time4WorkParser parser = new Time4WorkParser();
        
        Command parsedCommand = parser.parse(userCommand);
        Tasks task = parsedCommand.getTask();
        
        COMMAND_TYPE commandType = determineCommandType(parsedCommand.getCommand());
        
        switch (commandType) {
            case ADD:
                return executeAdd(task);
            case UPDATE:
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
                return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, "invalid"), myTaskList);
            case EXIT:
                System.exit(0);
            default:
                return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, "invalid"), myTaskList);
        }
    }
    
    public static COMMAND_TYPE determineCommandType(String commandTypeString) {
        if (commandTypeString.equalsIgnoreCase("add")) {
            return COMMAND_TYPE.ADD;
        } else if (commandTypeString.equalsIgnoreCase("delete")) {
            return COMMAND_TYPE.DELETE;
        } else if (commandTypeString.equalsIgnoreCase("update")) {
            return COMMAND_TYPE.UPDATE;
        } else if (commandTypeString.equalsIgnoreCase("sort")) {
            return COMMAND_TYPE.SORT;
        } else if (commandTypeString.equalsIgnoreCase("undo")) {
            return COMMAND_TYPE.UNDO;
        } else if (commandTypeString.equalsIgnoreCase("search")) {
            return COMMAND_TYPE.SEARCH;
        } else if (commandTypeString.equalsIgnoreCase("clear")) {
            return COMMAND_TYPE.CLEAR;
        } else if (commandTypeString.equalsIgnoreCase("exit")) {
            return COMMAND_TYPE.EXIT;
        } else {
            return COMMAND_TYPE.INVALID;
        }
    }
    
    private static boolean checkIfEmptyString(String userCommand) {
        return userCommand.trim().equals("");
    }
    
    public ArrayList<Tasks> getCurrentList() {
        return storage.readFile();
    }
    
    // =========================================================================
    // Execute Command
    // =========================================================================
    private FeedbackMessage executeAdd(Tasks newTask) {
        storage.appendTask(newTask);
        myTaskList = getCurrentList();
        
        int taskListSize = myTaskList.size();
        int newTaskID = myTaskList.get(taskListSize - 1).getTaskID();
        Command reversedCommand = new Command("delete", newTaskID); //only can store taskID here
        commandHistoryList.addCommand(reversedCommand);
        commandTypeList.addCommandType("add");
        
        return new FeedbackMessage(MESSAGE_ADDED, myTaskList);
    }
    
    private FeedbackMessage executeDelete(int userInputIndex) throws IOException {
        myTaskList = getCurrentList();
        int taskListSize = myTaskList.size();
        
        if (taskListSize < userInputIndex || userInputIndex < 1) {
            return new FeedbackMessage(String.format(MESSAGE_DELETED_, "failed"), myTaskList);
        } else {
            int taskID = getTaskIDFromUserInput(userInputIndex);
            myTaskList = getCurrentList();
            Tasks deletedTask = getTaskFromTaskID(myTaskList, taskID); //store the task to be deleted
            
            storage.deleteTask(taskID);
            myTaskList = getCurrentList();
            
            Command reversedCommand = new Command("add", deletedTask);
            commandHistoryList.addCommand(reversedCommand);
            commandTypeList.addCommandType("delete");
            
            return new FeedbackMessage(String.format(MESSAGE_DELETED_, "successfully"), myTaskList);
        }
        
    }
    
    private FeedbackMessage executeUpdate(Tasks task) throws IOException {
        myTaskList = getCurrentList();
        int taskListSize = myTaskList.size();
        int indexToBeDeleted = task.getTaskID();
        
        if (taskListSize < indexToBeDeleted || indexToBeDeleted < 1) {
            return new FeedbackMessage(String.format(MESSAGE_UPDATED_, "failed"), myTaskList);
        } else {
            int taskIDToBeDeleted = getTaskIDFromUserInput(indexToBeDeleted);
            task.setTaskID(taskIDToBeDeleted);
            
            Tasks taskBeforeUpdated = storage.UpdateTask(taskIDToBeDeleted, task);
            myTaskList = getCurrentList();
            
            Command reversedCommand = new Command("add", taskBeforeUpdated); // add the original one back
            commandHistoryList.addCommand(reversedCommand);
            reversedCommand = new Command("delete", taskIDToBeDeleted); // delete the updated one
            commandHistoryList.addCommand(reversedCommand);
            
            commandTypeList.addCommandType("update");
            
            return new FeedbackMessage(String.format(MESSAGE_UPDATED_, "successfully"), myTaskList);
        }
        
    }
    
    private FeedbackMessage executeSort() throws IOException {
        myTaskList = getCurrentList();
        myTaskList.sort(null);
        return new FeedbackMessage(MESSAGE_SORTED, myTaskList);
    }
    
    private FeedbackMessage executeSearch(String keyword) throws IOException {
        myTaskList = getCurrentList();
        ArrayList<Tasks> searchList = storage.SearchTask(keyword);
        
        if (searchList.size() != 0) {
            return new FeedbackMessage(String.format(MESSAGE_SEARCH_, "successfully"), searchList);
        } else {
            return new FeedbackMessage(String.format(MESSAGE_SEARCH_, "failed"), myTaskList);
        }
    }
    
    private FeedbackMessage executeUndo() throws IOException {
        if (commandTypeList.isEmpty()) {
            return new FeedbackMessage(String.format(MESSAGE_UNDO_, "failed"), myTaskList);
        } else {
            String lastCommandType = commandTypeList.getLastCommandType();
            if (lastCommandType != "update") {
                Command commandToUndo = commandHistoryList.getLastCommand();
                if (lastCommandType == "add") { //undo add operation
                    myTaskList = getCurrentList();
                    int taskIDToBeDeleted = commandToUndo.getIndexToBeDeleted();
                    storage.deleteTask(taskIDToBeDeleted);
                } else { // undo delete operation
                    Tasks task = commandToUndo.getTask();
                    storage.appendTask(task);
                }
            } else {  // undo update operation
                Command commandToUndo = commandHistoryList.getLastCommand();
                int taskIDToBeDeleted = commandToUndo.getIndexToBeDeleted();
                storage.deleteTask(taskIDToBeDeleted);
                commandToUndo = commandHistoryList.getLastCommand();
                storage.appendTask(commandToUndo.getTask());
            }
            
            myTaskList = getCurrentList();
            return new FeedbackMessage(String.format(MESSAGE_UNDO_, "successfully!"), myTaskList);
            
        }
    }
    
    private FeedbackMessage executeClear() {
        myTaskList = getCurrentList();
        return new FeedbackMessage(MESSAGE_CLEAR, myTaskList);
    }
    
    // =========================================================================
    // get task other information from known info
    // =========================================================================
    
    private int getTaskIDFromUserInput(int userInput) {
        myTaskList = getCurrentList();
        Tasks requriedTask = myTaskList.get(userInput - 1);
        int taskID = requriedTask.getTaskID();
        return taskID;
    }
    
    private Tasks getTaskFromTaskID(ArrayList<Tasks> taskList, int taskID) {
        int taskListSize = taskList.size();
        Tasks currentTask = null;
        for (int i = 0; i < taskListSize; i++) {
            currentTask = taskList.get(i);
            if (currentTask.getTaskID() == taskID) {
                break;
            }
        }
        return currentTask;
    }
    
    private int getIndexFromTaskID(int taskID) {
        myTaskList = getCurrentList();
        int index = -1;
        for  (int i = 0; i < myTaskList.size(); i++) {
            if (myTaskList.get(i).getTaskID() == taskID) {
                index = i + 1;
                break;
            }
        }
        return index;
    }
    
}