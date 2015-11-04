package Time4WorkLogic;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import Time4WorkParser.Command;
import Time4WorkParser.Parser;
import Time4WorkStorage.FilterTask;
import Time4WorkStorage.Storage;
import Time4WorkStorage.Tasks;

//@@author A0133894W
public class Logic {
    
    // =========================================================================
    // Feedback massage string
    // =========================================================================
    
    private static final String MESSAGE_INVALID_FORMAT = "invalid command: %1$s";
    private static final String MESSAGE_EMPTY_COMMAND = "Command is empty!";
    private static final String MESSAGE_ADDED_ = "Task added %1$s !";
    private static final String MESSAGE_UPDATED_ = "Task updated %1$s !";
    private static final String MESSAGE_DELETED_ = "Task %1$s deleted %2$s !";
    private static final String MESSAGE_DISPLAY = "Here are all the %1$s tasks";
    private static final String MESSAGE_SEARCH_ = "Task searched %1$s";
    private static final String MESSAGE_DONE_ = "Mark task %1$s as done %2$s";
    private static final String MESSAGE_CLEAR = "All task cleared successfully!";
    private static final String MESSAGE_UNDO_ = "Undo %1$s!";
    private static final String MESSAGE_CREATE_PATH_ = "Personalize storage path %1$s !";
    
    private static final String LOGIC_INIT = "Logic initialized";
    private static final Logger logger = Logger.getLogger(Logic.class.getName());
    
    // =========================================================================
    // local variables for Logic
    // =========================================================================
    
    private boolean isFirstCommand = true;
    private Storage storage;
    private FilterTask myFilter = new FilterTask();
    private ArrayList<Tasks> fullTaskList = new ArrayList<Tasks>();
    private ArrayList<Tasks> completeList = new ArrayList<Tasks>();
    private ArrayList<Tasks> incompleteList = new ArrayList<Tasks>();
    private CommandHistory commandHistory = new CommandHistory();
    private CommandHistory reversedCommandHistory = new CommandHistory();
    
    
    enum COMMAND_TYPE {
        ADD, UPDATE, DELETE, SEARCH, DISPLAY, STORE, INVALID, UNDO, DONE, CLEAR, EXIT
    };
    
    // Constructor
    //@@author A0133894W
    public Logic() {
        try {
            storage = Storage.getInstance();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        logger.log(Level.INFO, LOGIC_INIT);
    }
    
    // =========================================================================
    // Method to interact with UI and parser
    // =========================================================================
    
    public FeedbackMessage executeCommand (String userInput) throws Exception {
        logger.log(Level.INFO, "start processing user input");
        String userCommand = userInput;
        fullTaskList = getFullTaskList();
        
        if (isFirstCommand) {
            completeList = getCompleteTaskList();
            incompleteList = getIncompleteTaskList();
            
        }
        isFirstCommand = false;
        
        if(checkIfEmptyString(userCommand)) {
            return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, MESSAGE_EMPTY_COMMAND),
                                       completeList, incompleteList);
        }
        
        Parser parser = new Parser();
        
        Command parsedCommand = null;
        
        try{
            parsedCommand = parser.parse(userCommand);
        } catch (Exception NumberFormatException) {
            return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, "invalid input"),
                                       completeList, incompleteList);
        }
        
        
        Tasks task = parsedCommand.getTask();
        
        ArrayList<Integer> userInputIndexes = new ArrayList<Integer>();
        
        userInputIndexes = parsedCommand.getSelectedIndexNumbers();
        
        COMMAND_TYPE commandType = determineCommandType(parsedCommand.getCommand());
        
        switch (commandType) {
            case ADD :
                return executeAdd(task);
            case UPDATE :
                return executeUpdate(task);
            case DELETE :
                if (userInputIndexes.isEmpty()) {
                    int userInputIndex = parsedCommand.getSelectedIndexNumber();
                    userInputIndexes.add(userInputIndex);
                }
                return executeDelete(userInputIndexes);
            case UNDO :
                return executeUndo();
            case CLEAR :
                return executeClear();
            case SEARCH :
                String searchKeyword = parsedCommand.getSearchKeyword();
                if (parsedCommand.getIsDateSearch() == false) {
                    return executeSearchDescription(searchKeyword);
                } else {
                    return executeSearchDate(searchKeyword);
                }
                
            case DISPLAY:
                String displayType;
                if (parsedCommand.getDisplayTypeOrStoragePath() != null) {
                    displayType = parsedCommand.getDisplayTypeOrStoragePath();
                } else {
                    displayType = "incomplete";
                }
                return executeDisplay(displayType);
            case DONE:
                if (userInputIndexes.isEmpty()) {
                    int userInputIndex = parsedCommand.getSelectedIndexNumber();
                    userInputIndexes.add(userInputIndex);
                }
                return executeMarkTaskAsDone(userInputIndexes);
            case STORE:
                String storagePath = parsedCommand.getDisplayTypeOrStoragePath();
                return executeCreatePath(storagePath);
            case INVALID :
                return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, "command is invalid"),
                                           completeList, incompleteList);
            case EXIT :
                logger.log(Level.INFO, "exit");
                System.exit(0);
            default :
                return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, "command does not exist"),
                                           completeList, incompleteList);
        }
    }
    
    private static COMMAND_TYPE determineCommandType(String commandTypeString) {
        if (commandTypeString.equalsIgnoreCase("add")) {
            return COMMAND_TYPE.ADD;
        } else if (commandTypeString.equalsIgnoreCase("delete")) {
            return COMMAND_TYPE.DELETE;
        } else if (commandTypeString.equalsIgnoreCase("update")) {
            return COMMAND_TYPE.UPDATE;
        } else if (commandTypeString.equalsIgnoreCase("undo")) {
            return COMMAND_TYPE.UNDO;
        } else if (commandTypeString.equalsIgnoreCase("display")) {
            return (COMMAND_TYPE.DISPLAY);
        } else if (commandTypeString.equalsIgnoreCase("search")) {
            return COMMAND_TYPE.SEARCH;
        } else if (commandTypeString.equalsIgnoreCase("store")) {
            return COMMAND_TYPE.STORE;
        } else if (commandTypeString.equalsIgnoreCase("done")) {
            return COMMAND_TYPE.DONE;
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
    
    // =========================================================================
    // Useful method for UI to get task list
    // =========================================================================
    
    public ArrayList<Tasks> getCompleteTaskList() throws IOException, ParseException {
        fullTaskList = getFullTaskList();
        completeList = myFilter.searchCompleted(fullTaskList);
        completeList = sortCompleteTask(completeList);
        return completeList;
    }
    
    public ArrayList<Tasks> getIncompleteTaskList() throws IOException, ParseException {
        fullTaskList = getFullTaskList();
        incompleteList = myFilter.searchNotCompleted(fullTaskList);
        incompleteList = sortIncompleteTask(incompleteList);
        return incompleteList;
    }
    // =========================================================================
    // Execute Command
    // =========================================================================
    public FeedbackMessage executeAdd(Tasks newTask) throws IOException, ParseException {
        try{
            logger.log(Level.INFO, "start processing add command");
            storage.appendTask(newTask);
        } catch (IOException e) {
            logger.log(Level.WARNING, "add error");
            return new FeedbackMessage(String.format(MESSAGE_ADDED_, "failed"),
                                       completeList, incompleteList);
        }
        incompleteList.add(newTask);
        
        // Set up for undo operation
        int taskListSize = incompleteList.size();
        int newTaskID = incompleteList.get(taskListSize - 1).getTaskID();
        Command reversedCommand = new Command("delete", newTaskID); //only can store taskID here
        reversedCommandHistory.addReversedCommand(reversedCommand);
        commandHistory.addCommand(new Command("add", newTask));
        
        completeList = sortCompleteTask(completeList);
        incompleteList = sortIncompleteTask(incompleteList);
        logger.log(Level.INFO, "end of processing add command");
        return new FeedbackMessage(String.format(MESSAGE_ADDED_, "successfully"),
                                   completeList, incompleteList);
    }
    
    public FeedbackMessage executeDelete(ArrayList<Integer> userInputIndexes) throws IOException, ParseException {
        logger.log(Level.INFO, "start processing delete command");
        
        int taskListSize = incompleteList.size();
        int numToBeDeleted = userInputIndexes.size();
        int deletedTaskNum = 0;
        
        String indexDeletedSuccessfully = "";
        String indexDeletedFailed = "";
        
        ArrayList<Integer> arrangedIndexes = new ArrayList<Integer>();
        
        userInputIndexes.sort(null);
        for (int i = numToBeDeleted - 1; i >= 0; i--) {
            arrangedIndexes.add(userInputIndexes.get(i));
        }
        
        for (int i = 0; i < numToBeDeleted; i++) {
            int userInputIndex = arrangedIndexes.get(i);
            if (taskListSize < userInputIndex || userInputIndex < 1) {
                indexDeletedFailed += String.valueOf(userInputIndex + " ");
            } else {
                int taskID = getTaskIDFromUserInput(userInputIndex);
                Tasks deletedTask = getTaskFromTaskID(incompleteList, taskID); //store the task to be deleted
                
                try {
                    storage.deleteTask(taskID);
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "delete error");
                    e.printStackTrace();
                }
                incompleteList.remove(userInputIndex - 1);
                
                // Set up for undo operation
                Command reversedCommand = new Command("add", deletedTask);
                reversedCommandHistory.addReversedCommand(reversedCommand);
                
                deletedTaskNum++;
                indexDeletedSuccessfully = String.valueOf(userInputIndex + " ") + indexDeletedSuccessfully;
                logger.log(Level.INFO, "end of processing delete command");
            }
        }
        
        completeList = sortCompleteTask(completeList);
        incompleteList = sortIncompleteTask(incompleteList);
        if (indexDeletedSuccessfully.isEmpty()) {
            return new FeedbackMessage(String.format(MESSAGE_DELETED_, indexDeletedFailed, "failed: index out of range"),
                                       completeList, incompleteList);
        } else {
            commandHistory.addCommand(new Command("delete", deletedTaskNum));
            return new FeedbackMessage(String.format(MESSAGE_DELETED_, indexDeletedSuccessfully, "successfully"),
                                       completeList, incompleteList);
        }
        
    }
    public FeedbackMessage executeMarkTaskAsDone(ArrayList<Integer> userInputIndexes) throws Exception {
        logger.log(Level.INFO, "start processing mark command");
        
        int taskListSize = incompleteList.size();
        int numToBeMarked = userInputIndexes.size();
        int markedTaskNum = 0;
        
        String indexMarkedSuccessfully = "";
        String indexMarkedFailed = "";
        
        ArrayList<Integer> arrangedIndexes = new ArrayList<Integer>();
        
        userInputIndexes.sort(null);
        for (int i = numToBeMarked - 1; i >= 0; i--) {
            arrangedIndexes.add(userInputIndexes.get(i));
        }
        
        for (int i = 0; i < numToBeMarked; i++) {
            int userInputIndex = arrangedIndexes.get(i);
            if (taskListSize < userInputIndex || userInputIndex < 1) {
                indexMarkedFailed += String.valueOf(userInputIndex + " ");
            } else {
                int taskID = getTaskIDFromUserInput(userInputIndex);
                Tasks markedTask = getTaskFromTaskID(incompleteList, taskID); //store the task to be deleted
                
                try {
                    storage.SetCompleted(taskID);
                    incompleteList.remove(userInputIndex - 1);
                    completeList.add(markedTask);
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "mark error");
                    e.printStackTrace();
                }
                
                // Set up for undo operation
                Command reversedCommand = new Command("done", markedTask);
                reversedCommandHistory.addReversedCommand(reversedCommand);
                
                markedTaskNum++;
                indexMarkedSuccessfully = String.valueOf(userInputIndex + " ") + indexMarkedSuccessfully;
                logger.log(Level.INFO, "end of processing mark command");
            }
        }
        
        completeList = sortCompleteTask(completeList);
        incompleteList = sortIncompleteTask(incompleteList);
        if (indexMarkedSuccessfully.isEmpty()) {
            return new FeedbackMessage(String.format(MESSAGE_DONE_, indexMarkedFailed, "failed !"),
                                       completeList, incompleteList);
        } else {
            commandHistory.addCommand(new Command("done", markedTaskNum));
            return new FeedbackMessage(String.format(MESSAGE_DONE_, indexMarkedSuccessfully, "successfully !"),
                                       completeList, incompleteList);
        }
        
    }
    
    public FeedbackMessage executeUpdate(Tasks task) throws IOException, ParseException {
        logger.log(Level.INFO, "start processing update command");
        int taskListSize = incompleteList.size();
        int indexToBeDeleted = task.getTaskID();   // get index to be deleted by retrieving taskID
        
        if (taskListSize < indexToBeDeleted || indexToBeDeleted < 1) {
            return new FeedbackMessage(String.format(MESSAGE_UPDATED_, "failed: index out of range"),
                                       completeList, incompleteList);
        } else {
            int taskIDToBeDeleted = getTaskIDFromUserInput(indexToBeDeleted);
            task.setTaskID(taskIDToBeDeleted);
            
            Tasks taskBeforeUpdated = null;
            try {
                taskBeforeUpdated = storage.UpdateTask(taskIDToBeDeleted, task);
            } catch (Exception e) {
                logger.log(Level.WARNING, "update error");
                e.printStackTrace();
            }
            incompleteList.remove(indexToBeDeleted - 1);
            incompleteList.add(task);
            
            // Set up for undo operation
            Command reversedCommand = new Command("add", taskBeforeUpdated); // add the original one back
            reversedCommandHistory.addReversedCommand(reversedCommand);
            reversedCommand = new Command("delete", taskIDToBeDeleted); // delete the updated one
            reversedCommandHistory.addReversedCommand(reversedCommand);
            commandHistory.addCommand(new Command("update", task));
            
            completeList = sortCompleteTask(completeList);
            incompleteList = sortIncompleteTask(incompleteList);
            logger.log(Level.INFO, "end of processing update command");
            return new FeedbackMessage(String.format(MESSAGE_UPDATED_, "successfully"),
                                       completeList, incompleteList);
        }
        
    }
    
    
    public FeedbackMessage executeSearchDescription(String keyword) throws IOException, ParseException {
        logger.log(Level.INFO, "start processing search command");
        ArrayList<Tasks> searchList = new ArrayList<Tasks>();
        incompleteList = getIncompleteTaskList();
        
        searchList = myFilter.searchDescription(incompleteList, keyword);
        
        
        if (searchList.size() != 0) {
            incompleteList = searchList;
            completeList = sortCompleteTask(completeList);
            incompleteList = sortIncompleteTask(incompleteList);
            logger.log(Level.INFO, "end of processing search command");
            return new FeedbackMessage(String.format(MESSAGE_SEARCH_, "successfully"),
                                       completeList, incompleteList);
        } else {
            logger.log(Level.INFO, "end of processing search command");
            return new FeedbackMessage(String.format(MESSAGE_SEARCH_, "failed: no such task"),
                                       completeList, incompleteList);
        }
    }
    
    public FeedbackMessage executeSearchDate(String keyword) throws IOException, ParseException {
        logger.log(Level.INFO, "start processing search command");
        ArrayList<Tasks> searchList = new ArrayList<Tasks>();
        incompleteList = getIncompleteTaskList();
        searchList = myFilter.searchDescription(incompleteList, keyword);
        
        
        if (searchList.size() != 0) {
            incompleteList = searchList;
            completeList = sortCompleteTask(completeList);
            incompleteList = sortIncompleteTask(incompleteList);
            logger.log(Level.INFO, "end of processing search command");
            return new FeedbackMessage(String.format(MESSAGE_SEARCH_, "successfully"),
                                       completeList, incompleteList);
        } else {
            logger.log(Level.INFO, "end of processing search command");
            return new FeedbackMessage(String.format(MESSAGE_SEARCH_, "failed: no such task"),
                                       completeList, incompleteList);
        }
    }
    
    public FeedbackMessage executeUndo() throws Exception {
        logger.log(Level.INFO, "start processing undo command");
        boolean undoSuccessfully = false;
        if (commandHistory.isEmpty()) {
            logger.log(Level.INFO, "end of processing undo command");
            return new FeedbackMessage(String.format(MESSAGE_UNDO_, "failed: no more command for undo"),
                                       completeList, incompleteList);
        } else {
            Command lastCommand = commandHistory.getLastCommand();
            String lastCommandType = lastCommand.getCommand();
            if (lastCommandType == "add") { //undo add operation
                Command commandToUndo = reversedCommandHistory.getLastReversedCommand();
                int taskIDToBeDeleted = commandToUndo.getSelectedIndexNumber();
                try {
                    storage.deleteTask(taskIDToBeDeleted);
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "undo error");
                    e.printStackTrace();
                }
                int indexInPreviouslist = getIndexFromTaskID(incompleteList, taskIDToBeDeleted);
                incompleteList.remove(indexInPreviouslist);
                undoSuccessfully = true;
            } else if (lastCommandType == "delete"){ //undo delete task
                int undoDeleteNum = lastCommand.getSelectedIndexNumber();
                for (int i = 0; i < undoDeleteNum; i++) {
                    Command commandToUndo = reversedCommandHistory.getLastReversedCommand();
                    Tasks task = commandToUndo.getTask();
                    storage.appendTask(task);
                    incompleteList.add(task);
                }
                undoSuccessfully = true;
            } else if (lastCommandType == "update"){  // undo update operation
                Command commandToUndo = reversedCommandHistory.getLastReversedCommand();
                int taskIDToBeDeleted = commandToUndo.getSelectedIndexNumber();
                try {
                    storage.deleteTask(taskIDToBeDeleted);
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "undo error");
                    e.printStackTrace();
                }
                int indexInPreviousList = getIndexFromTaskID(incompleteList, taskIDToBeDeleted);
                incompleteList.remove(indexInPreviousList);
                
                commandToUndo = reversedCommandHistory.getLastReversedCommand();
                storage.appendTask(commandToUndo.getTask());
                incompleteList.add(commandToUndo.getTask());
                undoSuccessfully = true;
            } else if (lastCommandType == "clear") {  // undo clear operation
                int clearedTaskNum = lastCommand.getSelectedIndexNumber();
                for (int i = 0; i < clearedTaskNum; i++) {
                    Command commandToUndo = reversedCommandHistory.getLastReversedCommand();
                    Tasks task = commandToUndo.getTask();
                    storage.appendTask(task);
                    if (task.isCompleted()) {
                        completeList.add(task);
                    } else {
                        incompleteList.add(task);
                    }
                }
                undoSuccessfully = true;
            } else if (lastCommandType == "done") {  //undo done operation
                int undoMarkedNum = lastCommand.getSelectedIndexNumber();
                for (int i = 0; i < undoMarkedNum; i++) {
                    Command commandToUndo = reversedCommandHistory.getLastReversedCommand();
                    Tasks task = commandToUndo.getTask();
                    int taskID = task.getTaskID();
                    storage.SetIncompleted(taskID);
                    incompleteList.add(task);
                    int taskIndexInCompleteList = getIndexFromTaskID(completeList, taskID);
                    completeList.remove(taskIndexInCompleteList);
                }
                undoSuccessfully = true;
            }
            
        }
        
        completeList = sortCompleteTask(completeList);
        incompleteList = sortIncompleteTask(incompleteList);
        if (undoSuccessfully) {
            logger.log(Level.INFO, "end of processing undo command");
            return new FeedbackMessage(String.format(MESSAGE_UNDO_, "successfully!"),
                                       completeList, incompleteList);
        } else {
            logger.log(Level.WARNING, "end of processing undo command: previous command cannot undo");
            return new FeedbackMessage(String.format(MESSAGE_UNDO_, "failed!"),
                                       completeList, incompleteList);
        }
        
    }
    
    public FeedbackMessage executeDisplay(String displayType) throws Exception {
        logger.log(Level.INFO, "start processing display command");
        
        ArrayList<Tasks> displayList = new ArrayList<Tasks>();
        fullTaskList = getFullTaskList();
        
        ArrayList<Tasks> fullIncompleteTask = new ArrayList<Tasks>();
        fullIncompleteTask = getIncompleteTaskList();
        
        
        if (displayType.equals("archive")) {
            completeList = getCompleteTaskList();
            
            displayList = myFilter.searchCompleted(fullTaskList);
        } else if (displayType.equals("incomplete")) {
            displayList = myFilter.searchNotCompleted(fullIncompleteTask);
        } else if (displayType.equals("deadline")) {
            displayList = myFilter.searchDeadline(fullIncompleteTask);
        } else if (displayType.equals("duration")) {
            displayList = myFilter.searchDuration(fullIncompleteTask);
        } else if (displayType.equals("floating")) {
            displayList = myFilter.searchFloating(fullIncompleteTask);
            
        }
        
        
        if (displayList.size() != 0) {
            incompleteList = displayList;
            completeList = sortCompleteTask(completeList);
            incompleteList = sortIncompleteTask(incompleteList);
            logger.log(Level.INFO, "end of processing search command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY, displayType),
                                       completeList, incompleteList);
        } else {
            logger.log(Level.INFO, "end of processing search command");
            return new FeedbackMessage(String.format(MESSAGE_SEARCH_, "failed: no such task"),
                                       completeList, incompleteList);
        }
    }
    
    
    public FeedbackMessage executeClear() throws Exception {
        logger.log(Level.INFO, "start processing clear command");
        ArrayList<Tasks> previousTaskList = storage.ClearAll();
        int previousTaskListSize = previousTaskList.size();
        
        commandHistory.addCommand(new Command("clear", previousTaskListSize));
        
        Tasks taskInPreviousTaskList;
        for (int i = previousTaskListSize - 1; i >= 0; i--) {
            taskInPreviousTaskList = previousTaskList.get(i);
            Command reversedCommand = new Command("add", taskInPreviousTaskList);
            reversedCommandHistory.addReversedCommand(reversedCommand);
        }
        
        completeList.clear();
        incompleteList.clear();
        logger.log(Level.INFO, "end of processing clear command");
        return new FeedbackMessage(MESSAGE_CLEAR, completeList, incompleteList);
    }
    
    public FeedbackMessage executeCreatePath(String storagePath) throws IOException, ParseException {
        storage.setCustomPath(storagePath);
        isFirstCommand = true;
        fullTaskList = getFullTaskList();
        completeList = getCompleteTaskList();
        incompleteList = getIncompleteTaskList();
        completeList = sortCompleteTask(completeList);
        incompleteList = sortIncompleteTask(incompleteList);
        return new FeedbackMessage(String.format(MESSAGE_CREATE_PATH_, "successfully"),
                                   completeList, incompleteList);
        
    }
    
    // =========================================================================
    // get task's other information from known info
    // =========================================================================
    
    private int getTaskIDFromUserInput(int userInput) {
        Tasks requriedTask = incompleteList.get(userInput - 1);
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
        int index = -1;
        for  (int i = 0; i < taskList.size(); i++) {
            boolean isSameTaskID = checkIsSameTaskID(taskList.get(i), taskID);
            if (isSameTaskID) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    private boolean checkIsSameTaskID (Tasks task, int taskID) {
        boolean isSameTaskID = false;
        if (task.getTaskID() == taskID) {
            isSameTaskID = true;
        }
        return isSameTaskID;
    }
    
    private ArrayList<Tasks> getFullTaskList() {
        try {
            fullTaskList = storage.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullTaskList;
    }
    
    private ArrayList<Tasks> sortIncompleteTask(ArrayList<Tasks> taskList) throws IOException, ParseException {
        logger.log(Level.INFO, "start processing incomplete sort");
        TaskSorter taskSorter = new TaskSorter(taskList);
        taskList = taskSorter.sortTask(true);
        logger.log(Level.INFO, "end of processing incomplete sort");
        return incompleteList;
    }
    
    private ArrayList<Tasks> sortCompleteTask(ArrayList<Tasks> taskList) throws IOException, ParseException {
        logger.log(Level.INFO, "start processing complete sort");
        TaskSorter taskSorter = new TaskSorter(taskList);
        taskList = taskSorter.sortTask(false);
        logger.log(Level.INFO, "end of processing complete sort");
        return taskList;
    }
    
    
}