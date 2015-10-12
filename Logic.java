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
    private ArrayList<Tasks> fullTaskList = new ArrayList<Tasks>();
    private CommandHistory commandHistoryList = new CommandHistory();
    private CommandHistory commandTypeList = new CommandHistory();
    
    
    enum COMMAND_TYPE {
        ADD, UPDATE, DELETE, SEARCH, SORT, INVALID, UNDO, CLEAR, EXIT
    };
    
    // Constructor
    public Logic() {
        try {
            storage = new Storage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    // =========================================================================
    // Method to interact with UI and parser
    // =========================================================================
    
    public FeedbackMessage executeCommand (String userInput) throws IOException {
        String userCommand = userInput;
        fullTaskList = getFullTaskList();
        
        if (myTaskList.isEmpty()) {
            myTaskList = fullTaskList;
        }
        
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
    
    private static COMMAND_TYPE determineCommandType(String commandTypeString) {
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
    
    
    public ArrayList<Tasks> getFullTaskList() {
        try {
            return storage.readFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fullTaskList;
    }
    
    // =========================================================================
    // Execute Command
    // =========================================================================
    private FeedbackMessage executeAdd(Tasks newTask) throws IOException {
        storage.appendTask(newTask);
        myTaskList = getFullTaskList();
        
        // Set up for undo operation
        int taskListSize = myTaskList.size();
        int newTaskID = myTaskList.get(taskListSize - 1).getTaskID();
        Command reversedCommand = new Command("delete", newTaskID); //only can store taskID here
        commandHistoryList.addCommand(reversedCommand);
        commandTypeList.addCommandType("add");
        
        return new FeedbackMessage(MESSAGE_ADDED, myTaskList);
    }
    
    private FeedbackMessage executeDelete(int userInputIndex) throws IOException {
        int taskListSize = myTaskList.size();
        
        if (taskListSize < userInputIndex || userInputIndex < 1) {
            return new FeedbackMessage(String.format(MESSAGE_DELETED_, "failed"), myTaskList);
        } else {
            int taskID = getTaskIDFromUserInput(userInputIndex);
            Tasks deletedTask = getTaskFromTaskID(myTaskList, taskID); //store the task to be deleted
            
            try {
                storage.deleteTask(taskID);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            myTaskList.remove(userInputIndex - 1);
            
            // Set up for undo operation
            Command reversedCommand = new Command("add", deletedTask);
            commandHistoryList.addCommand(reversedCommand);
            commandTypeList.addCommandType("delete");
            
            return new FeedbackMessage(String.format(MESSAGE_DELETED_, "successfully"), myTaskList);
        }
        
    }
    
    private FeedbackMessage executeUpdate(Tasks task) throws IOException {
        
        int taskListSize = myTaskList.size();
        int indexToBeDeleted = task.getTaskID();   // get index to be deleted by retrieving taskID
        
        if (taskListSize < indexToBeDeleted || indexToBeDeleted < 1) {
            return new FeedbackMessage(String.format(MESSAGE_UPDATED_, "failed"), myTaskList);
        } else {
            int taskIDToBeDeleted = getTaskIDFromUserInput(indexToBeDeleted);
            task.setTaskID(taskIDToBeDeleted);
            
            Tasks taskBeforeUpdated = null;
            try {
                taskBeforeUpdated = storage.UpdateTask(taskIDToBeDeleted, task);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            myTaskList.remove(indexToBeDeleted - 1);
            myTaskList.add(task);
            
            // Set up for undo operation
            Command reversedCommand = new Command("add", taskBeforeUpdated); // add the original one back
            commandHistoryList.addCommand(reversedCommand);
            reversedCommand = new Command("delete", taskIDToBeDeleted); // delete the updated one
            commandHistoryList.addCommand(reversedCommand);
            commandTypeList.addCommandType("update");
            
            return new FeedbackMessage(String.format(MESSAGE_UPDATED_, "successfully"), myTaskList);
        }
        
    }
    
    private FeedbackMessage executeSort() throws IOException {
        myTaskList.sort(null);
        return new FeedbackMessage(MESSAGE_SORTED, myTaskList);
    }
    
    private FeedbackMessage executeSearch(String keyword) throws IOException {
        ArrayList<Tasks> searchList = storage.SearchTask(keyword);
        
        if (searchList.size() != 0) {
            myTaskList = searchList;
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
                    int taskIDToBeDeleted = commandToUndo.getIndexToBeDeleted();
                    try {
                        storage.deleteTask(taskIDToBeDeleted);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    int indexInPreviouslist = getIndexFromTaskID(myTaskList, taskIDToBeDeleted);
                    myTaskList.remove(indexInPreviouslist);
                } else { // undo delete operation
                    Tasks task = commandToUndo.getTask();
                    storage.appendTask(task);
                    myTaskList.add(task);
                }
            } else {  // undo update operation
                Command commandToUndo = commandHistoryList.getLastCommand();
                int taskIDToBeDeleted = commandToUndo.getIndexToBeDeleted();
                try {
                    storage.deleteTask(taskIDToBeDeleted);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                int indexInPreviousList = getIndexFromTaskID(myTaskList, taskIDToBeDeleted);
                myTaskList.remove(indexInPreviousList);
                
                commandToUndo = commandHistoryList.getLastCommand();
                storage.appendTask(commandToUndo.getTask());
                myTaskList.add(commandToUndo.getTask());
            }
            
            return new FeedbackMessage(String.format(MESSAGE_UNDO_, "successfully!"), myTaskList);
            
        }
    }
    
    private FeedbackMessage executeClear() {
        return new FeedbackMessage(MESSAGE_CLEAR, myTaskList);
    }
    
    // =========================================================================
    // get task other information from known info
    // =========================================================================
    
    private int getTaskIDFromUserInput(int userInput) {
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
    
    private int getIndexFromTaskID(ArrayList<Tasks> taskList, int taskID) {
        myTaskList = taskList;
        int index = -1;
        for  (int i = 0; i < myTaskList.size(); i++) {
            if (myTaskList.get(i).getTaskID() == taskID) {
                index = i;
                break;
            }
        }
        return index;
    }
    
}