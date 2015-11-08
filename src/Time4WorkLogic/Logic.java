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
    private static final String MESSAGE_DELETED_ = "Task %1$s deleted %2$s !";
    private static final String MESSAGE_UPDATED_ = "Task updated %1$s !";
    private static final String MESSAGE_DISPLAY_TASKTYPE_SUCCESSFULLY = "Here are all %1$s tasks";
    private static final String MESSAGE_DISPLAY_TASKDATE_SUCCESSFULLY = "Here are all tasks %1$s";
    private static final String MESSAGE_DISPLAY_FAILED_ = "Display failed: %1$s";
    private static final String MESSAGE_SEARCH_ = "Task searched %1$s";
    private static final String MESSAGE_SORT_FAILED = "Sort task failed: display without sorting";
    private static final String MESSAGE_DONE_ = "Mark task %1$s as done %2$s";
    private static final String MESSAGE_CLEAR_ = "All task cleared %1$s!";
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
    public Logic() {
        logger.log(Level.INFO, LOGIC_INIT);
        try {
            storage = Storage.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            case DELETE :
                if (userInputIndexes.isEmpty()) {
                    int userInputIndex = parsedCommand.getSelectedIndexNumber();
                    userInputIndexes.add(userInputIndex);
                }
                return executeDelete(userInputIndexes);
            case UPDATE :
                return executeUpdate(task);
            case UNDO :
                return executeUndo();
            case DISPLAY:
                int displayDateType = parsedCommand.getDisplayType();
                if (displayDateType == 1) { //display tasks on the specific one date
                    ArrayList<String> displayTime = parsedCommand.getTimeArray();
                    String displayDate = displayTime.get(0);
                    return executeDisplayOnDate(displayDate);
                } else if (displayDateType == 2) { //display all tasks due by one date
                    ArrayList<String> displayTime = parsedCommand.getTimeArray();
                    String beforeDate = displayTime.get(0);
                    return executeDisplayBeforeDate(beforeDate);
                } else if (displayDateType == 3) { // display tasks from one date to another date
                    ArrayList<String> displayTime = parsedCommand.getTimeArray();
                    String fromDate = displayTime.get(0);
                    String toDate = displayTime.get(1);
                    return executeDisplayFromToDates(fromDate, toDate);
                } else if (parsedCommand.getStoreSearchAndDisplayStrings() == "overdue") {
                    return executeDisplayOverdue();
                } else { // display by task type instead of dates
                    String displayType;
                    if (parsedCommand.getStoreSearchAndDisplayStrings() != null) {
                        displayType = parsedCommand.getStoreSearchAndDisplayStrings();
                    } else {
                        displayType = "incomplete";
                    }
                    return executeDisplay(displayType);
                }
            case SEARCH :
                String searchKeyword = parsedCommand.getStoreSearchAndDisplayStrings();
                return executeSearchDescription(searchKeyword);
            case STORE:
                String storagePath = parsedCommand.getStoreSearchAndDisplayStrings();
                return executeCreatePath(storagePath);
            case DONE:
                if (userInputIndexes.isEmpty()) {
                    int userInputIndex = parsedCommand.getSelectedIndexNumber();
                    userInputIndexes.add(userInputIndex);
                }
                return executeMarkTaskAsDone(userInputIndexes);
            case INVALID :
                return new FeedbackMessage(String.format(MESSAGE_INVALID_FORMAT, "command is invalid"),
                                           completeList, incompleteList);
            case CLEAR :
                return executeClear();
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
    
    public ArrayList<Tasks> getCompleteTaskList() {
        fullTaskList = getFullTaskList();
        completeList = myFilter.searchCompleted(fullTaskList);
        try {
            completeList = sortCompleteTask(completeList);
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return completeList;
    }
    
    public ArrayList<Tasks> getIncompleteTaskList() {
        fullTaskList = getFullTaskList();
        incompleteList = myFilter.searchNotCompleted(fullTaskList);
        try {
            incompleteList = sortIncompleteTask(incompleteList);
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return incompleteList;
    }
    
    // =========================================================================
    // Execute Command
    // =========================================================================
    public FeedbackMessage executeAdd(Tasks newTask) {
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
        ArrayList<Integer> taskIDList = new ArrayList<Integer>();
        taskIDList.add(newTaskID);
        Command reversedCommand = new Command("delete", taskIDList); //only can store taskID here
        reversedCommandHistory.addReversedCommand(reversedCommand);
        commandHistory.addCommand(new Command("add", newTask));
        
        try {
            completeList = sortCompleteTask(completeList);
            incompleteList = sortIncompleteTask(incompleteList);
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
        }
        
        logger.log(Level.INFO, "end of processing add command");
        return new FeedbackMessage(String.format(MESSAGE_ADDED_, "successfully"),
                                   completeList, incompleteList);
    }
    
    public FeedbackMessage executeDelete(ArrayList<Integer> userInputIndexes) {
        logger.log(Level.INFO, "start processing delete command");
        
        int taskListSize = incompleteList.size();
        int numToBeDeleted = userInputIndexes.size();
        int deletedTaskNum = 0;
        
        String indexDeletedSuccessfully = "";
        String indexDeletedFailed = "";
        
        ArrayList<Integer> arrangedIndexes = new ArrayList<Integer>();
        ArrayList<Integer> validTaskID = new ArrayList<Integer>();
        
        //delete tasks from the largest index number to avoid index out of range
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
                Tasks deletedTask = getTaskFromTaskID(incompleteList, taskID);
                
                validTaskID.add(taskID);
                incompleteList.remove(userInputIndex - 1);
                
                // Set up for undo operation
                Command reversedCommand = new Command("add", deletedTask);
                reversedCommandHistory.addReversedCommand(reversedCommand);
                
                deletedTaskNum++;
                indexDeletedSuccessfully = String.valueOf(userInputIndex + " ") + indexDeletedSuccessfully;
                logger.log(Level.INFO, "end of processing delete command");
            }
        }
        
        try {
            storage.deleteTask(validTaskID);
        } catch (InterruptedException | IOException e) {
            logger.log(Level.WARNING, "delete error");
            return new FeedbackMessage(String.format(MESSAGE_DELETED_, indexDeletedFailed, "failed: index out of range"),
                                       completeList, incompleteList);
        }
        
        try {
            completeList = sortCompleteTask(completeList);
            incompleteList = sortIncompleteTask(incompleteList);
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
        }
        
        if (indexDeletedSuccessfully.isEmpty()) {
            return new FeedbackMessage(String.format(MESSAGE_DELETED_, indexDeletedFailed, "failed: index out of range"),
                                       completeList, incompleteList);
        } else {
            ArrayList<Integer> deletedTaskNumList = new ArrayList<Integer>();
            deletedTaskNumList.add(deletedTaskNum);
            commandHistory.addCommand(new Command("delete", deletedTaskNumList));
            return new FeedbackMessage(String.format(MESSAGE_DELETED_, indexDeletedSuccessfully, "successfully"),
                                       completeList, incompleteList);
        }
        
    }
    
    public FeedbackMessage executeUpdate(Tasks task) {
        logger.log(Level.INFO, "start processing update command");
        int taskListSize = incompleteList.size();
        int indexToBeDeleted = task.getTaskID();   // get index to be deleted by retrieving taskID
        
        if (taskListSize < indexToBeDeleted || indexToBeDeleted < 1) {
            return new FeedbackMessage(String.format(MESSAGE_UPDATED_, "failed: no such task number"),
                                       completeList, incompleteList);
        } else {
            int taskIDToBeDeleted = getTaskIDFromUserInput(indexToBeDeleted);
            task.setTaskID(taskIDToBeDeleted);
            
            Tasks taskBeforeUpdated = null;
            try {
                taskBeforeUpdated = storage.UpdateTask(taskIDToBeDeleted, task);
            } catch (Exception e) {
                logger.log(Level.WARNING, "update error");
                return new FeedbackMessage(String.format(MESSAGE_UPDATED_, "failed"),
                                           completeList, incompleteList);
            }
            incompleteList.remove(indexToBeDeleted - 1);
            incompleteList.add(task);
            
            // Set up for undo operation
            Command reversedCommand = new Command("add", taskBeforeUpdated); // add the original one back
            reversedCommandHistory.addReversedCommand(reversedCommand);
            
            ArrayList<Integer> taskIDToBeDeletedList = new ArrayList<Integer>();
            taskIDToBeDeletedList.add(taskIDToBeDeleted);
            reversedCommand = new Command("delete", taskIDToBeDeletedList); // delete the updated one
            reversedCommandHistory.addReversedCommand(reversedCommand);
            commandHistory.addCommand(new Command("update", task));
            
            try {
                completeList = sortCompleteTask(completeList);
                incompleteList = sortIncompleteTask(incompleteList);
            } catch (IOException | ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
            }
            
            logger.log(Level.INFO, "end of processing update command");
            return new FeedbackMessage(String.format(MESSAGE_UPDATED_, "successfully"),
                                       completeList, incompleteList);
        }
        
    }
    
    public FeedbackMessage executeUndo() {
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
                ArrayList<Integer> taskIDToBeDeletedList = commandToUndo.getSelectedIndexNumbers();
                int taskIDToBeDeleted = taskIDToBeDeletedList.get(0);
                taskIDToBeDeletedList.add(taskIDToBeDeleted);
                try {
                    storage.deleteTask(taskIDToBeDeletedList);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "undo error");
                    return new FeedbackMessage(String.format(MESSAGE_UNDO_, "failed"),
                                               completeList, incompleteList);
                }
                int indexInPreviouslist = getIndexFromTaskID(incompleteList, taskIDToBeDeleted);
                incompleteList.remove(indexInPreviouslist);
                undoSuccessfully = true;
            } else if (lastCommandType == "delete"){ //undo delete task
                int undoDeleteNum = lastCommand.getSelectedIndexNumbers().get(0);
                for (int i = 0; i < undoDeleteNum; i++) {
                    Command commandToUndo = reversedCommandHistory.getLastReversedCommand();
                    Tasks task = commandToUndo.getTask();
                    try {
                        storage.appendTask(task);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return new FeedbackMessage(String.format(MESSAGE_UNDO_, "failed"),
                                                   completeList, incompleteList);
                    }
                    incompleteList.add(task);
                }
                undoSuccessfully = true;
            } else if (lastCommandType == "update"){  // undo update operation
                Command commandToUndo = reversedCommandHistory.getLastReversedCommand();
                int taskIDToBeDeleted = commandToUndo.getSelectedIndexNumbers().get(0);
                ArrayList<Integer> taskIDToBeDeletedList = new ArrayList<Integer>();
                taskIDToBeDeletedList.add(taskIDToBeDeleted);
                try {
                    storage.deleteTask(taskIDToBeDeletedList);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "undo error");
                    return new FeedbackMessage(String.format(MESSAGE_UNDO_, "failed"),
                                               completeList, incompleteList);
                }
                int indexInPreviousList = getIndexFromTaskID(incompleteList, taskIDToBeDeleted);
                incompleteList.remove(indexInPreviousList);
                
                commandToUndo = reversedCommandHistory.getLastReversedCommand();
                try {
                    storage.appendTask(commandToUndo.getTask());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return new FeedbackMessage(String.format(MESSAGE_UNDO_, "failed"),
                                               completeList, incompleteList);
                }
                incompleteList.add(commandToUndo.getTask());
                undoSuccessfully = true;
            } else if (lastCommandType == "clear") {  // undo clear operation
                int clearedTaskNum = lastCommand.getSelectedIndexNumbers().get(0);
                for (int i = 0; i < clearedTaskNum; i++) {
                    Command commandToUndo = reversedCommandHistory.getLastReversedCommand();
                    Tasks task = commandToUndo.getTask();
                    try {
                        storage.appendTask(task);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return new FeedbackMessage(String.format(MESSAGE_UNDO_, "failed"),
                                                   completeList, incompleteList);
                    }
                    if (task.isCompleted()) {
                        completeList.add(task);
                    } else {
                        incompleteList.add(task);
                    }
                }
                undoSuccessfully = true;
            } else if (lastCommandType == "done") {  //undo done operation
                int undoMarkedNum = lastCommand.getSelectedIndexNumbers().get(0);
                for (int i = 0; i < undoMarkedNum; i++) {
                    Command commandToUndo = reversedCommandHistory.getLastReversedCommand();
                    Tasks task = commandToUndo.getTask();
                    int taskID = task.getTaskID();
                    ArrayList<Integer> taskIDList = new ArrayList<Integer>();
                    taskIDList.add(taskID);
                    try {
                        storage.SetIncompleted(taskIDList);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return new FeedbackMessage(String.format(MESSAGE_UNDO_, "failed"),
                                                   completeList, incompleteList);
                    }
                    incompleteList.add(task);
                    int taskIndexInCompleteList = getIndexFromTaskID(completeList, taskID);
                    completeList.remove(taskIndexInCompleteList);
                }
                undoSuccessfully = true;
            }
            
        }
        
        try {
            completeList = sortCompleteTask(completeList);
            incompleteList = sortIncompleteTask(incompleteList);
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
        }
        
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
    
    public FeedbackMessage executeDisplayOnDate(String keyDate) {
        logger.log(Level.INFO, "start processing display task on one day command");
        ArrayList<Tasks> searchList = new ArrayList<Tasks>();
        
        incompleteList = getIncompleteTaskList();
        searchList = myFilter.searchDate(incompleteList, keyDate);
        
        if (searchList.size() != 0) {
            incompleteList = searchList;
            try {
                completeList = sortCompleteTask(completeList);
                incompleteList = sortIncompleteTask(incompleteList);
            } catch (IOException | ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
            }
            
            logger.log(Level.INFO, "end of processing display task on one day command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_TASKDATE_SUCCESSFULLY, ("on " + keyDate)),
                                       completeList, incompleteList);
        } else {
            logger.log(Level.INFO, "end of processing display task on one day command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_FAILED_, "No such task!"), completeList, incompleteList);
        }
    }
    
    public FeedbackMessage executeDisplayFromToDates(String fromDate, String toDate) {
        logger.log(Level.INFO, "start processing display task between two dates command");
        ArrayList<Tasks> searchList = new ArrayList<Tasks>();
        
        incompleteList = getIncompleteTaskList();
        try {
            searchList = myFilter.searchBetweenDates(incompleteList, fromDate, toDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_FAILED_, "cannot search display type"), completeList, incompleteList);
        }
        
        if (searchList.size() != 0) {
            incompleteList = searchList;
            try {
                completeList = sortCompleteTask(completeList);
                incompleteList = sortIncompleteTask(incompleteList);
            } catch (IOException | ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
            }
            
            logger.log(Level.INFO, "end of processing display task between two dates command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_TASKDATE_SUCCESSFULLY,
                                                     ("from " + fromDate + " to " + toDate)),
                                       completeList, incompleteList);
        } else {
            logger.log(Level.INFO, "end of processing display task between two dates command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_FAILED_, "No such task!"), completeList, incompleteList);
        }
    }
    
    public FeedbackMessage executeDisplayBeforeDate(String beforeDate) {
        logger.log(Level.INFO, "start processing display task before one day command");
        ArrayList<Tasks> searchList = new ArrayList<Tasks>();
        
        incompleteList = getIncompleteTaskList();
        try {
            searchList = myFilter.searchBeforeDate(incompleteList, beforeDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_FAILED_, "cannot search display type"), completeList, incompleteList);
        }
        
        if (searchList.size() != 0) {
            incompleteList = searchList;
            try {
                completeList = sortCompleteTask(completeList);
                incompleteList = sortIncompleteTask(incompleteList);
            } catch (IOException | ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
            }
            
            logger.log(Level.INFO, "end of processing display task before one day command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_TASKDATE_SUCCESSFULLY, ("before " + beforeDate)),
                                       completeList, incompleteList);
        } else {
            logger.log(Level.INFO, "end of processing display task before one day command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_FAILED_, "No such task!"), completeList, incompleteList);
        }
    }
    
    public FeedbackMessage executeDisplayOverdue() {
        logger.log(Level.INFO, "start processing display task overdue command");
        ArrayList<Tasks> searchList = new ArrayList<Tasks>();
        
        incompleteList = getIncompleteTaskList();
        try {
            searchList = myFilter.searchOverDue(incompleteList);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_FAILED_, "cannot search display type"), completeList, incompleteList);
        }
        if (searchList.size() != 0) {
            incompleteList = searchList;
            try {
                completeList = sortCompleteTask(completeList);
                incompleteList = sortIncompleteTask(incompleteList);
            } catch (IOException | ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
            }
            
            logger.log(Level.INFO, "end of processing display task overdue command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_TASKDATE_SUCCESSFULLY, "overdued"),
                                       completeList, incompleteList);
        } else {
            logger.log(Level.INFO, "end of processing display task overdue command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_FAILED_, "No such task!"), completeList, incompleteList);
        }
    }
    
    
    public FeedbackMessage executeDisplay(String displayType) {
        logger.log(Level.INFO, "start processing display command");
        
        ArrayList<Tasks> displayList = new ArrayList<Tasks>();
        fullTaskList = getFullTaskList();
        
        ArrayList<Tasks> fullIncompleteTask = new ArrayList<Tasks>();
        fullIncompleteTask = getIncompleteTaskList();
        
        
        if (displayType.equals("archive")) {
            logger.log(Level.INFO, "start processing display archive");
            completeList = getCompleteTaskList();
            displayList = myFilter.searchCompleted(fullTaskList);
        } else if (displayType.equals("incomplete")) {
            logger.log(Level.INFO, "start processing display incomplete");
            displayList = myFilter.searchNotCompleted(fullIncompleteTask);
        } else if (displayType.equals("deadline")) {
            logger.log(Level.INFO, "start processing display deadline tasks");
            displayList = myFilter.searchDeadline(fullIncompleteTask);
        } else if (displayType.equals("duration")) {
            logger.log(Level.INFO, "start processing display duration tasks");
            displayList = myFilter.searchDuration(fullIncompleteTask);
        } else if (displayType.equals("floating")) {
            logger.log(Level.INFO, "start processing display floating tasks");
            displayList = myFilter.searchFloating(fullIncompleteTask);
        }
        
        
        if (displayList.size() != 0) {
            incompleteList = displayList;
            try {
                completeList = sortCompleteTask(completeList);
                incompleteList = sortIncompleteTask(incompleteList);
            } catch (IOException | ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
            }
            
            logger.log(Level.INFO, "end of processing display command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_TASKTYPE_SUCCESSFULLY, displayType),
                                       completeList, incompleteList);
        } else {
            logger.log(Level.INFO, "end of processing display command");
            return new FeedbackMessage(String.format(MESSAGE_DISPLAY_FAILED_, "No such task!"), completeList, incompleteList);
        }
    }
    
    public FeedbackMessage executeSearchDescription(String keyword) {
        logger.log(Level.INFO, "start processing search command");
        ArrayList<Tasks> searchList = new ArrayList<Tasks>();
        incompleteList = getIncompleteTaskList();
        
        searchList = myFilter.searchDescription(incompleteList, keyword);
        
        
        if (searchList.size() != 0) {
            incompleteList = searchList;
            try {
                completeList = sortCompleteTask(completeList);
                incompleteList = sortIncompleteTask(incompleteList);
            } catch (IOException | ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
            }
            
            logger.log(Level.INFO, "end of processing search command");
            return new FeedbackMessage(String.format(MESSAGE_SEARCH_, "successfully"),
                                       completeList, incompleteList);
        } else {
            logger.log(Level.INFO, "end of processing search command");
            return new FeedbackMessage(String.format(MESSAGE_SEARCH_, "failed: no such task"),
                                       completeList, incompleteList);
        }
    }
    
    public FeedbackMessage executeCreatePath(String storagePath) {
        logger.log(Level.INFO, " start processing create path");
        try {
            storage.setCustomPath(storagePath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new FeedbackMessage(String.format(MESSAGE_CREATE_PATH_, "failed"),
                                       completeList, incompleteList);
        }
        isFirstCommand = true;
        fullTaskList = getFullTaskList();
        
        completeList = getCompleteTaskList();
        incompleteList = getIncompleteTaskList();
        
        try {
            completeList = sortCompleteTask(completeList);
            incompleteList = sortIncompleteTask(incompleteList);
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
        }
        logger.log(Level.INFO, " end of processing create path");
        return new FeedbackMessage(String.format(MESSAGE_CREATE_PATH_, "successfully"),
                                   completeList, incompleteList);
        
    }
    
    public FeedbackMessage executeMarkTaskAsDone(ArrayList<Integer> userInputIndexes) {
        logger.log(Level.INFO, "start processing mark command");
        
        int taskListSize = incompleteList.size();
        int numToBeMarked = userInputIndexes.size();
        int markedTaskNum = 0;
        
        String indexMarkedSuccessfully = "";
        String indexMarkedFailed = "";
        
        ArrayList<Integer> arrangedIndexes = new ArrayList<Integer>();
        ArrayList<Integer> validTaskID = new ArrayList<Integer>();
        
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
                
                validTaskID.add(taskID);
                
                incompleteList.remove(userInputIndex - 1);
                completeList.add(markedTask);
                // Set up for undo operation
                Command reversedCommand = new Command("done", markedTask);
                reversedCommandHistory.addReversedCommand(reversedCommand);
                
                markedTaskNum++;
                indexMarkedSuccessfully = String.valueOf(userInputIndex + " ") + indexMarkedSuccessfully;
                logger.log(Level.INFO, "end of processing mark command");
            }
        }
        
        try {
            storage.SetCompleted(validTaskID);
        } catch (Exception e) {
            logger.log(Level.WARNING, "mark error");
            return new FeedbackMessage(String.format(MESSAGE_DONE_, indexMarkedFailed, "failed !"),
                                       completeList, incompleteList);
        }
        
        try {
            completeList = sortCompleteTask(completeList);
            incompleteList = sortIncompleteTask(incompleteList);
        } catch (IOException | ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new FeedbackMessage(MESSAGE_SORT_FAILED, completeList, incompleteList);
        }
        
        if (indexMarkedSuccessfully.isEmpty()) {
            return new FeedbackMessage(String.format(MESSAGE_DONE_, indexMarkedFailed, "failed !"),
                                       completeList, incompleteList);
        } else {
            ArrayList<Integer> markedTaskNumList = new ArrayList<Integer>();
            markedTaskNumList.add(markedTaskNum);
            commandHistory.addCommand(new Command("done", markedTaskNumList));
            return new FeedbackMessage(String.format(MESSAGE_DONE_, indexMarkedSuccessfully, "successfully !"),
                                       completeList, incompleteList);
        }
        
    }
    
    public FeedbackMessage executeClear() {
        logger.log(Level.INFO, "start processing clear command");
        ArrayList<Tasks> previousTaskList = null;
        try {
            previousTaskList = storage.ClearAll();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new FeedbackMessage(String.format(MESSAGE_CLEAR_, "failed"), completeList, incompleteList);
        }
        int previousTaskListSize = previousTaskList.size();
        
        ArrayList<Integer> sizeInArrayForm = new ArrayList<Integer>();
        sizeInArrayForm.add(previousTaskListSize);
        commandHistory.addCommand(new Command("clear", sizeInArrayForm));
        
        Tasks taskInPreviousTaskList;
        for (int i = previousTaskListSize - 1; i >= 0; i--) {
            taskInPreviousTaskList = previousTaskList.get(i);
            Command reversedCommand = new Command("add", taskInPreviousTaskList);
            reversedCommandHistory.addReversedCommand(reversedCommand);
        }
        
        completeList.clear();
        incompleteList.clear();
        logger.log(Level.INFO, "end of processing clear command");
        return new FeedbackMessage(String.format(MESSAGE_CLEAR_, "successfully"), completeList, incompleteList);
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