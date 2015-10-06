import java.io.IOException;
import java.util.ArrayList;

public class Logic {
    
    private static final String MESSAGE_INVALID_FORMAT = "invalid command: %1$s";
    private static final String EMPTY_COMMAND = "Command is empty!";
    private static final String MESSAGE_ADDED = "Task added successfully!";
    private static final String MESSAGE_UPDATED = "Task updated successfully!";
    private static final String MESSAGE_DELETED = "Task deleted successfully!";
    private static final String MESSAGE_SORTED = "Task sorted successfully!";
    private static final String MESSAGE_SEARCH = "Task searched %1$s";
    private static final String MESSAGE_CLEAR = "All task cleared successfully!";
    private static final String MESSAGE_UNDO = "Undo successfully!";
    
    private Storage storage;
    private ArrayList<Tasks> myTaskList = new ArrayList<Tasks>();
    private CommandHistory commandHistoryList = new CommandHistory();
    private CommandHistory commandTypeList = new CommandHistory();
    
    enum COMMAND_TYPE {
        ADD, READ, UPDATE, DELETE, SEARCH, SORT, INVALID, UNDO, CLEAR, EXIT
    };
    
    public Logic() {
        storage = new Storage();
    }
    
    public FeedbackMessage executeCommand (String userInput) throws IOException {
        String userCommand = userInput;
        myTaskList = getCurrentList();
        String taskListContent = getTaskListContent(myTaskList);
        
        if(checkIfEmptyString(userCommand)){
            return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, EMPTY_COMMAND), myTaskList);
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
        } else if (commandTypeString.equalsIgnoreCase("read")) {
            return COMMAND_TYPE.READ;
        } else if (commandTypeString.equalsIgnoreCase("delete")) {
            return COMMAND_TYPE.DELETE;
        } else if (commandTypeString.equalsIgnoreCase("update")) {
            return COMMAND_TYPE.UPDATE;
        } else if (commandTypeString.equalsIgnoreCase("sort")) {
            return COMMAND_TYPE.SORT;
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
    
    private FeedbackMessage executeAdd(Tasks newTask) {
        storage.appendTask(newTask);
        myTaskList = getCurrentList();
        String taskListContent = getTaskListContent(myTaskList);
        
        int taskListSize = myTaskList.size();
        int newTaskID = myTaskList.get(taskListSize - 1).getTaskID();
        Command reversedCommand = new Command("delete", newTaskID);
        commandHistoryList.addCommand(reversedCommand);
        commandTypeList.addCommandType("add");
        
        return new FeedbackMessage(MESSAGE_ADDED, myTaskList);
    }
    
    private FeedbackMessage executeRead() {
        myTaskList = getCurrentList();
        String taskListContent = getTaskListContent(myTaskList);
        return new FeedbackMessage(MESSAGE_ADDED, myTaskList);
    }
    
    private FeedbackMessage executeDelete(int userInputIndex) throws IOException {
        int taskID = getTaskIDFromUserInput(userInputIndex);
        myTaskList = getCurrentList();
        Tasks deletedTask = getTaskFromTaskID(myTaskList, taskID); //store the task to be deleted
        
        storage.deleteTask(taskID);
        myTaskList = getCurrentList();
        String taskListContent = getTaskListContent(myTaskList);
        
        Command reversedCommand = new Command("add", deletedTask);
        commandHistoryList.addCommand(reversedCommand);
        commandTypeList.addCommandType("delete");
        
        return new FeedbackMessage(MESSAGE_DELETED, myTaskList);
    }
    
    private FeedbackMessage executeUpdate(Tasks task) throws IOException {
        int taskID = task.getTaskID();
        Tasks taskBeforeUpdated = storage.UpdateTask(taskID, task);
        
        myTaskList = getCurrentList();
        String taskListContent = getTaskListContent(myTaskList);
        
        Command reversedCommand = new Command("delete", taskID);
        commandHistoryList.addCommand(reversedCommand);
        reversedCommand = new Command("add", taskBeforeUpdated);
        commandHistoryList.addCommand(reversedCommand);
        
        commandTypeList.addCommandType("update");
        
        return new FeedbackMessage(MESSAGE_UPDATED, myTaskList);
    }
    
    private FeedbackMessage executeSort() throws IOException {
        myTaskList = getCurrentList();
        myTaskList.sort(null);
        String taskListContent = getTaskListContent(myTaskList);
        return new FeedbackMessage(MESSAGE_SORTED, myTaskList);
    }
    
    private FeedbackMessage executeSearch(String keyword) throws IOException {
        myTaskList = getCurrentList();
        ArrayList<Tasks> searchList = storage.SearchTask(keyword);
        
        if (searchList.size() != 0) {
            String taskListContent = getTaskListContent(searchList);
            return new FeedbackMessage(String.format(MESSAGE_SEARCH, "successfully"), searchList);
        } else {
            String taskListContent = getTaskListContent(myTaskList);
            return new FeedbackMessage(String.format(MESSAGE_SEARCH, "successfully"), myTaskList);
        }
    }
    
    private FeedbackMessage executeUndo() throws IOException {
        String lastCommandType = commandTypeList.getLastCommandType();
        if (lastCommandType != "update") {
            Command commandToUndo = commandHistoryList.getLastCommand();
            if (commandToUndo.getCommand() == "delete") {
                storage.appendTask(commandToUndo.getTask());
            } else {
                myTaskList = getCurrentList();
                int taskIDToBeDeleted = commandToUndo.getIndexToBeDeleted();
                int indexToBeDeleted = getIndexFromTaskID(taskIDToBeDeleted);
                storage.deleteTask(indexToBeDeleted);
            }
        } else {
            Command commandToUndo = commandHistoryList.getLastCommand();
            int taskIDToBeDeleted = commandToUndo.getIndexToBeDeleted();
            int indexToBeDeleted = getIndexFromTaskID(taskIDToBeDeleted);
            storage.deleteTask(indexToBeDeleted);
            commandToUndo = commandHistoryList.getLastCommand();
            storage.appendTask(commandToUndo.getTask());
        }
        
        myTaskList = getCurrentList();
        String taskListContent = getTaskListContent(myTaskList);
        return new FeedbackMessage(MESSAGE_UNDO, myTaskList);
    }
    
    private FeedbackMessage executeClear() {
        myTaskList = getCurrentList();
        String taskListContent = getTaskListContent(myTaskList);
        return new FeedbackMessage(MESSAGE_CLEAR, myTaskList);
    }
    
    private int getTaskIDFromUserInput(int userInput) {
        myTaskList = getCurrentList();
        Tasks requriedTask = myTaskList.get(userInput - 1);
        int taskID = requriedTask.getTaskID();
        return taskID;
    }
    
    private String getTaskListContent(ArrayList<Tasks> taskList) {
        String taskListContent = "";
        int taskListSize = taskList.size();
        for (int i = 0; i < taskListSize - 1; i++) {
            taskListContent += taskList.get(i).getDescription() + "\n";
        }
        if (taskListSize > 1) {
            taskListContent += taskList.get(taskListSize - 1);
        }
        
        return taskListContent;
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
        myTaskList = storage.readFile();
        int index = -1;
        for  (int i = 0; i < myTaskList.size(); i++) {
            if (myTaskList.get(i).getTaskID() == taskID) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    private static boolean checkIfEmptyString(String userCommand) {
        return userCommand.trim().equals("");
    }
    
    public ArrayList<Tasks> getCurrentList(){
        return storage.readFile();
    }
}