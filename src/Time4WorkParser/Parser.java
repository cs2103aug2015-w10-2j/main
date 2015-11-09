//@@author A0110920Y

package Time4WorkParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.joestelmach.natty.*;

import Time4WorkData.*;

public class Parser {
  
  private static final int POSITION_PARAM_COMMAND = 0;
  private static final int POSITION_FIRST_PARAM_ARGUMENT = 1;
  
  private static final ArrayList<String> VALID_DISPLAY_COMMANDS = 
    new ArrayList<String>(Arrays.asList("archive", "floating", "deadline", "duration", "overdue"));
  
  private static final String REGEX_WHITESPACES = "[\\s,]+";
  
  private static final Logger logger = Logger.getLogger(Parser.class.getName());
  
  private static final com.joestelmach.natty.Parser dateParser = new com.joestelmach.natty.Parser();
  
  public Parser() {
  }
  
  /**
   * @param userInput
   * takes in the user input string and converts it into a command for the logic segment
   * 
   * @return command
   */
  public Command parse(String userInput) {
    Command command;
    ArrayList<String> parameters = splitString(userInput);
    logger.log(Level.INFO, "parsing user input");
    String userCommand = getUserCommand(parameters);
    logger.log(Level.INFO, "user command is: " + userCommand);
    ArrayList<String> arguments = getUserArguments(parameters);
    
    if (userCommand.toUpperCase().equals("ADD")) {
      command = createAddIfValidCommand(arguments);
    } else if (userCommand.toUpperCase().equals("DELETE")) {
      command = createDeleteCommand(arguments);
    } else if (userCommand.toUpperCase().equals("UPDATE")) {
      command = createUpdateIfValidCommand(arguments);
    } else if (userCommand.toUpperCase().equals("SEARCH")) {
      command = createSearchCommand(arguments);
    } else if (userCommand.toUpperCase().equals("UNDO")) {
      command = createUndoCommand();
    } else if (userCommand.toUpperCase().equals("CLEAR")) {
      command = createClearCommand();
    } else if (userCommand.toUpperCase().equals("DONE")) {
      command = createDoneCommand(arguments);
    } else if (userCommand.toUpperCase().equals("STORE")) {
      command = createStoreCommand(arguments);
    } else if (userCommand.toUpperCase().equals("DISPLAY")) {
      command = createDisplayCommand(arguments);
    } else if (userCommand.toUpperCase().equals("EXIT")) {
      command = createExitCommand();
    } else {
      logger.log(Level.WARNING, "invalid command by user");
      command = createInvalidCommand();
    }
    return command;
  }
  
  // ================================================================================
  // Main Parser methods
  // ================================================================================
  
  private Command createAddCommand(ArrayList<String> arguments) {
    Command command;
    Tasks task;
    
    ArrayList<String> combinedDescriptionList = createNewListWithCombinedDescription(arguments);
    
    if (!(combinedDescriptionList == null)) {
      task = createTaskListForAddingOrUpdating(combinedDescriptionList);
      if (!(task == null)) {
        command = new Command("add", task);
      } else {
        command = createInvalidCommand();
      }
    } else {
      command = createInvalidCommand();
    }
    
    return command;
  }
  
  private Command createDeleteCommand(ArrayList<String> arguments) {
    Command command;
    boolean containsDash;
    int argumentsLength = arguments.size();
    ArrayList<Integer> arrayOfDeleteIndexes = new ArrayList<Integer>();
    
    containsDash = checkIfContainsDash(arguments);
    
    if (argumentsLength == 0) {
      command = createInvalidCommand();
    } else if (argumentsLength == 1 && !containsDash) {
      int indexToBeDeleted = Integer.parseInt(arguments.get(0));
      arrayOfDeleteIndexes.add(indexToBeDeleted);
      command = new Command("delete", arrayOfDeleteIndexes);
    } else {
      arrayOfDeleteIndexes = getArrayListForDoneOrDelete(arguments, containsDash, argumentsLength);
      if (arrayOfDeleteIndexes.isEmpty()) {
        command = createInvalidCommand();
      } else {
        command = new Command("delete", arrayOfDeleteIndexes);
      }
    }
    
    return command;
  }
  
  private Command createUpdateCommand(ArrayList<String> arguments) {
    Command command;
    Tasks task;
    int taskID = Integer.parseInt(arguments.get(0));
    
    ArrayList<String> withoutTaskIDArguments = getUserArguments(arguments);
    
    ArrayList<String> combinedDescriptionList = createNewListWithCombinedDescription(withoutTaskIDArguments);
    
    if (!(combinedDescriptionList == null)) {
      task = createTaskListForAddingOrUpdating(combinedDescriptionList);
      if (!(task == null)) {
        task.setTaskID(taskID);
        command = new Command("update", task);
      } else {
        command = createInvalidCommand();
      }
    } else {
      command = createInvalidCommand();
    }
    
    return command;
  }
  
  private Command createDoneCommand(ArrayList<String> arguments) {
    Command command;
    boolean containsDash;
    int argumentsLength = arguments.size();
    ArrayList<Integer> arrayOfMarkDoneIndexes = new ArrayList<Integer>();
    
    containsDash = checkIfContainsDash(arguments);
    
    if (argumentsLength == 0) {
      command = createInvalidCommand();
    } else if (argumentsLength == 1 && !containsDash) {
      int indexToBeMarkedDone = Integer.parseInt(arguments.get(0));
      arrayOfMarkDoneIndexes.add(indexToBeMarkedDone);
      command = new Command("done", arrayOfMarkDoneIndexes);
    } else {
      arrayOfMarkDoneIndexes = getArrayListForDoneOrDelete(arguments, containsDash, argumentsLength);
      command = new Command("done", arrayOfMarkDoneIndexes);
    }
    
    return command;
  }
  
  private Command createSearchCommand(ArrayList<String> arguments) {
    Command command;
    String keyword = String.join(" ", arguments);
    
    command = new Command("search", keyword); 
    
    return command;
  }
  
  private Command createStoreCommand(ArrayList<String> arguments) {
    String storageLocation = String.join(" ", arguments);
    String escapedStorageLocation = storageLocation.replace("\\", "\\\\");
    
    Command command = new Command("store", escapedStorageLocation);
    
    return command;
  }
  
  private Command createDisplayCommand(ArrayList<String> arguments) {
    Command command = null;
    String typeToDisplay = null;
    
    if (arguments.isEmpty()) {
      command = new Command("display", typeToDisplay);
    } else if (VALID_DISPLAY_COMMANDS.contains(arguments.get(0))) {
      typeToDisplay = arguments.get(0);
      command = new Command("display", typeToDisplay);
    } else if (!dateParser.parse(String.join(" ", arguments)).isEmpty()) {
      String firstWord = arguments.get(0).toLowerCase();
      ArrayList<String> timeArray = new ArrayList<String>();
      List<DateGroup> dateGroups = dateParser.parse(String.join(" ", arguments));
      List<Date> dates = dateGroups.get(0).getDates();
      SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");
      command = createDisplayTimeCommand(arguments, firstWord, timeArray, dates, dateFormat);
    } else {
      command = createInvalidCommand();
    }
    return command;
  }
  
  private Command createUndoCommand() {
    Command command = new Command("undo");
    
    return command;
  }
  
  private Command createClearCommand() {
    Command command = new Command("clear");
    
    return command;
  }
  
  private Command createInvalidCommand() {
    Command command = new Command("invalid");
    
    return command;
  }
  
  private Command createExitCommand() {
    Command command = new Command("exit");
    
    return command;
  }
  
  // ================================================================================
  // Initial methods to check if valid add or update
  // ================================================================================
  
  //initial check if a full stop is even in the argument and does not parse the input if not present
  //proceeds to attempt creation of the add command if present
  private Command createAddIfValidCommand(ArrayList<String> arguments) {
    Command command;
    if (findFullStop(arguments)) {
      command = createAddCommand(arguments);
    } else {
      logger.log(Level.WARNING, "full stop not found in user input.");
      command = createInvalidCommand();
    }
    return command;
  }
  
  //initial check if a full stop is even in the argument and stops if not present
  //proceeds to attempt creation of the update command if present
  private Command createUpdateIfValidCommand(ArrayList<String> arguments) {
    Command command;
    if (findFullStop(arguments)) {
      command = createUpdateCommand(arguments);
    } else {
      logger.log(Level.WARNING, "full stop not found in user input.");
      command = createInvalidCommand();
    }
    return command;
  }
  
  // ================================================================================
  // Array creation methods
  // ================================================================================
  
  //takes in the string and splits it into a array list of the split string
  private ArrayList<String> splitString(String arguments) {
    String[] strArray = arguments.trim().split(REGEX_WHITESPACES);
    return new ArrayList<String>(Arrays.asList(strArray));
  }
  
  //takes in the arrayList<String> and gets the first argument, which is the action to be done
  private String getUserCommand(ArrayList<String> parameters) {
    return parameters.get(POSITION_PARAM_COMMAND);
  }
  
  //takes in the arrayList<String> and creates a new arrayList without the command type
  private ArrayList<String> getUserArguments(ArrayList<String> parameters) {
    return new ArrayList<String>(parameters.subList(POSITION_FIRST_PARAM_ARGUMENT,
                                                    parameters.size()));
  }
  
  //splits the user arguments to create the description array for add and update methods
  private ArrayList<String> getDescriptionArray(ArrayList<String> parameters, int end) {
    return new ArrayList<String>(parameters.subList(0, end+1));
  }
  
  //splits the user arguments to create the array of time variables for add and update methods
  private ArrayList<String> getTimeArray(ArrayList<String> parameters, int lastWordIndex) {
    return new ArrayList<String>(parameters.subList(lastWordIndex+1, parameters.size()));
  }
  
  // ================================================================================
  // Description and combined arraylist creation methods for add and update
  // ================================================================================
  
  //takes in the given description array and makes the description string
  private String createDescription(ArrayList<String> parameters,int end) {
    ArrayList<String> description = getDescriptionArray(parameters, end);
    String descriptionString;
    String lastWord = description.get(end);
    if (lastWord.endsWith(".")) {
      description.set(end, lastWord.replaceAll(".$", ""));
      descriptionString = String.join(" ", description);
    } else {
      descriptionString = null;
    }
    return descriptionString;
  }
  
  //makes a new array consisting of the combined description and the time variables array
  private ArrayList<String> createNewListWithCombinedDescription(ArrayList<String> parameters) {
    ArrayList<String> resultArray = new ArrayList<String>();
    int lastWordIndex = findIndexOfLastWord(parameters);
    String description = createDescription(parameters, lastWordIndex);
    if (!(description == null)) {
      ArrayList<String> timeArray = getTimeArray(parameters, lastWordIndex);
      resultArray.add(description);
      resultArray.addAll(timeArray);
    } else {
      resultArray = null;
    }
    return resultArray;
  }
  
  // ================================================================================
  // Task creation methods for add and update
  // ================================================================================
  
  //overall method to call the creation of the task object for add and update commands
  private Tasks createTaskListForAddingOrUpdating(ArrayList<String> arguments) {
    Tasks task = null;
    String descriptionOfTask = arguments.get(0);
    ArrayList<String> timeArray = getTimeArray(arguments, 0);
    int numberOfArguments = timeArray.size();
    
    if (numberOfArguments == 0) {
      task = new FloatingTask(descriptionOfTask);
    } else {
      try {
        for (int i = 0; i < timeArray.size(); i++) {
          Integer.parseInt(timeArray.get(i));
        }
        task = createNoKeywordTask(task, descriptionOfTask, timeArray, numberOfArguments);
      } catch (NumberFormatException e) {
        task = createKeywordTask(task, descriptionOfTask, timeArray);
      }
    }
    return task;
  }
  
  //creates a task that has no given natural language input
  //split according to deadline tasks and duration tasks
  private Tasks createNoKeywordTask(Tasks task, String descriptionOfTask, ArrayList<String> timeArray,
                                    int numberOfArguments) {
    if (numberOfArguments == 1 || numberOfArguments == 2) { //deadline tasks
      task = createDeadlineTask(task, descriptionOfTask, timeArray);
    } else {             //duration tasks
      task = createDurationTask(task, descriptionOfTask, timeArray);
    }
    return task;
  }
  
  //creates a task with natural language input
  //splits the time input and calls the corresponding task creation methods according to number of inputs
  private Tasks createKeywordTask(Tasks task, String descriptionOfTask, ArrayList<String> timeArray) {
    timeArray.set(0, timeArray.get(0).toLowerCase());
    ArrayList<String> formattedTimeArray = new ArrayList<String>();
    createFormattedTimeArray(timeArray, formattedTimeArray);
    if (formattedTimeArray.size() == 2) { //deadline task
      createFormattedTimeArray(timeArray, formattedTimeArray);
      task = createDeadlineTask(task, descriptionOfTask, formattedTimeArray);
    } else if (formattedTimeArray.size() == 4) { //duration task
      createFormattedTimeArray(timeArray, formattedTimeArray);
      task = createDurationTask(task, descriptionOfTask, formattedTimeArray);
    }
    return task;
  }
  
  //method that uses natty date and time parser to create the time array for natural language inputs
  private void createFormattedTimeArray(ArrayList<String> timeArray, ArrayList<String> formattedTimeArray) {
    String timeString = String.join(" ", timeArray);
    List<DateGroup> dateGroups = dateParser.parse(timeString);
    List<Date> dates = dateGroups.get(0).getDates();
    int dateSize = dates.size();
    for (int i = 0; i < dateSize; i++) {
      Date date = dates.get(i);
      SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");
      SimpleDateFormat timeFormat = new SimpleDateFormat("HHmm");
      String formattedDate = dateFormat.format(date);
      String formattedTime;
      if (dateGroups.get(0).isTimeInferred()) {
        formattedTime = "2359";
      } else {
        formattedTime = timeFormat.format(date);
      }
      formattedTimeArray.add(formattedDate);
      formattedTimeArray.add(formattedTime);
    }
  }
  
  //creates a duration task according to whether it is a same day event or over a time period
  private Tasks createDurationTask(Tasks task, String descriptionOfTask, ArrayList<String> timeArray) {
    if (timeArray.size() == 3) {
      String date = timeArray.get(0);
      String startTime = timeArray.get(1);
      String endTime = timeArray.get(2);
      task = createDurationTaskForSameDayEvent(task, descriptionOfTask, date, startTime, endTime);
    } else {
      String startDate = timeArray.get(0);
      String startTime = timeArray.get(1);
      String endDate = timeArray.get(2);
      String endTime = timeArray.get(3);
      task = createDurationTaskForFullInputCommand(task, descriptionOfTask, startDate, startTime, endDate, endTime);
    }
    return task;
  }
  
  //creates a deadline task according to whether a deadline time is specified
  private Tasks createDeadlineTask(Tasks task, String descriptionOfTask, ArrayList<String> timeArray) {
    if (timeArray.size() == 1) {
      String endDate = timeArray.get(0);
      task = createDeadlineTaskForInputWithoutTime(task, descriptionOfTask, endDate);
    } else {
      String endDate = timeArray.get(0);
      String endTime = timeArray.get(1);
      task = createDeadlineTaskForInputWithTime(task, descriptionOfTask, endDate, endTime);
    }
    return task;
  }
  
  //creates a duration task that has all 4 inputs of start date, start time, end date and end time
  private Tasks createDurationTaskForFullInputCommand(Tasks task, String descriptionOfTask, String startDate,
                                                      String startTime, String endDate, String endTime) {
    if (checkValidDate(startDate) && checkValidDate(endDate) && checkValidTime(startTime) && checkValidTime(endTime)) {
      if (startDate.equals(endDate)) { //creates a Duration object using createDurationTaskForSameDayEvent
        task = createDurationTaskForSameDayEvent(task, descriptionOfTask, startDate, startTime, endTime);
      } else if (checkStartDateBeforeEndDate(startDate, endDate)) { //creates a Duration object for (start,start,end,end) if checkStartDateBeforeEndDate true
        Duration durationPeriod = new Duration(startDate, startTime, endDate, endTime);
        task = new DurationTask(descriptionOfTask, durationPeriod);
      } else { //creates a Duration object for (end,end,start,start) if checkStartDateBeforeEndDate false
        Duration durationPeriod = new Duration(endDate, endTime, startDate, startTime);
        task = new DurationTask(descriptionOfTask, durationPeriod);
      }
    }
    return task;
  }
  
  //creates a duration task that falls on the same day, checks if the time is in chronological order
  //flips the time if it is not
  private Tasks createDurationTaskForSameDayEvent(Tasks task, String descriptionOfTask, String date, String startTime, String endTime) {
    if (checkValidDate(date) && checkValidTime(startTime) && checkValidTime(endTime)) {
      if (checkStartTimeBeforeEndTime(startTime, endTime)) {
        Duration durationPeriod = new Duration(date, startTime, date, endTime);
        task = new DurationTask(descriptionOfTask, durationPeriod);
      } else {
        Duration durationPeriod = new Duration(date, endTime, date, startTime);
        task = new DurationTask(descriptionOfTask, durationPeriod);
      }
    }
    return task;
  }
  
  //create a deadline task that have a specified timing
  private Tasks createDeadlineTaskForInputWithTime(Tasks task, String descriptionOfTask, String endDate, String endTime) {
    if (checkValidDate(endDate) && checkValidTime(endTime)) {
      Duration deadline = new Duration(endDate, endTime);
      task = new DeadlineTask(descriptionOfTask, deadline);
    }
    return task;
  }
  
  //creates a deadline task that does not specify timing, defaults to the end of that day at 2359
  private Tasks createDeadlineTaskForInputWithoutTime(Tasks task, String descriptionOfTask, String endDate) {
    if (checkValidDate(endDate)) {
      Duration deadline = new Duration(endDate, "2359");
      task = new DeadlineTask(descriptionOfTask, deadline);
    }
    return task;
  }
  
  // ================================================================================
  // Date and time checking methods
  // ================================================================================
  
  //checks if the given 6 digit integer can be parsed into a valid date
  private boolean checkValidDate(String inputDate) {
    SimpleDateFormat date = new SimpleDateFormat("ddMMyy");
    date.setLenient(false);
    try {
      date.parse(inputDate);
      return true;
    } catch (ParseException e) {
      return false;
    }
  }
  
  //checks if the given time is within the valid time range of 0000 to 2359
  private boolean checkValidTime(String inputTime) {
    int hour = Integer.parseInt(inputTime.substring(0,2));
    int minute = Integer.parseInt(inputTime.substring(2,4));
    
    if ((hour >= 0 && hour <= 23) && (minute >= 0 && minute <= 59)) {
      return true;
    } else {
      return false;
    }
  }
  
  //checks if the given starting date is before the ending date
  private boolean checkStartDateBeforeEndDate(String startDate, String endDate) {
    SimpleDateFormat date = new SimpleDateFormat("ddMMyy");
    Date startingDate = null;
    try {
      startingDate = date.parse(startDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Date endingDate = null;
    try {
      endingDate = date.parse(endDate);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    if (startingDate.before(endingDate)) {
      return true;
    } else {
      return false;
    }
  }
  
  //checks if the given start time is before the end time
  private boolean checkStartTimeBeforeEndTime(String startTime, String endTime) {
    int startHour = Integer.parseInt(startTime.substring(0,2));
    int startMinute = Integer.parseInt(startTime.substring(2,4));
    int endHour = Integer.parseInt(endTime.substring(0,2));
    int endMinute = Integer.parseInt(endTime.substring(2,4));
    
    if (startHour < endHour) {
      return true;
    } else if (startHour > endHour) {
      return false;
    } else {
      if (startMinute < endMinute) {
        return true;
      } else {
        return false;
      }
    }
  }
  
  // ================================================================================
  // Display methods
  // ================================================================================
  
  //overall method to create display for time periods
  private Command createDisplayTimeCommand(ArrayList<String> arguments, String firstWord, ArrayList<String> timeArray,
                                           List<Date> dates, SimpleDateFormat dateFormat) {
    Command command;
    String nullString = null;
    if (firstWord.equals("on")) {
      command = createDisplayOnTime(arguments, timeArray, dates, dateFormat, nullString);
    } else if (firstWord.equals("by")) {
      command = createDisplayByTime(arguments, timeArray, dates, dateFormat, nullString);
    } else {
      try {
        arguments.remove("from");
        arguments.remove("to");
        if (arguments.size() == 1) {
          command = createDisplayOnTimeWithoutKeywordOn(arguments, timeArray, nullString);
        } else if (arguments.size() == 2) {
          command = createDisplayDateRangeNoNaturalLanguage(arguments, timeArray, nullString);
        } else {
          command = createInvalidCommand();
        }
      } catch (NumberFormatException e) {
        command = createDisplayDateRangeNaturalLanguage(timeArray, dates, dateFormat);
      }
    }
    return command;
  }
  
  //creates the command for displaying tasks that falls on the specified date
  private Command createDisplayOnTime(ArrayList<String> arguments, ArrayList<String> timeArray, List<Date> dates,
                                      SimpleDateFormat dateFormat, String nullString) {
    Command command;
    try {
      arguments.remove("on");
      command = createDisplayOnTimeWithoutKeywordOn(arguments, timeArray, nullString);
    } catch (NumberFormatException e) {
      Date date = dates.get(0);
      String formattedDate = dateFormat.format(date);
      timeArray.add(formattedDate);
      command = new Command("display", 1, timeArray);
    }
    return command;
  }
  
  //creates the command for displaying on a date, but input does not specify "on" keyword
  private Command createDisplayOnTimeWithoutKeywordOn(ArrayList<String> arguments, ArrayList<String> timeArray,
                                                      String nullString) {
    Command command;
    String onDate = arguments.get(0);
    Integer.parseInt(onDate);
    if (checkValidDate(onDate)) {
      timeArray.add(arguments.get(0));
      command = new Command("display", 1, timeArray);
    } else {
      command = createInvalidCommand();
    }
    return command;
  }
  
  //creates the command for displaying tasks before a date
  private Command createDisplayByTime(ArrayList<String> arguments, ArrayList<String> timeArray, List<Date> dates,
                                      SimpleDateFormat dateFormat, String nullString) {
    Command command;
    try {
      arguments.remove("by");
      String byDate = arguments.get(0);
      Integer.parseInt(byDate);
      if (checkValidDate(byDate)) {
        timeArray.add(arguments.get(0));
        command = new Command("display", 2, timeArray);
      } else {
        command = createInvalidCommand();
      }
    } catch (NumberFormatException e) {
      Date date = dates.get(0);
      String formattedDate = dateFormat.format(date);
      timeArray.add(formattedDate);
      command = new Command("display", 2, timeArray);
    }
    return command;
  }
  
  //creates command for displaying the date range using natty
  private Command createDisplayDateRangeNaturalLanguage(ArrayList<String> timeArray, List<Date> dates,
                                                        SimpleDateFormat dateFormat) {
    Command command;
    Date fromDate = dates.get(0);
    Date toDate = dates.get(1);
    String formattedStartDate = dateFormat.format(fromDate);
    String formattedToDate = dateFormat.format(toDate);
    timeArray.add(formattedStartDate);      
    timeArray.add(formattedToDate);
    command = new Command("display", 3, timeArray);
    return command;
  }
  
  //creates command for displaying the date range that does not uses natural language
  private Command createDisplayDateRangeNoNaturalLanguage(ArrayList<String> arguments, ArrayList<String> timeArray,
                                                          String nullString) {
    Command command;
    String fromDate = arguments.get(0);
    String toDate = arguments.get(1);
    Integer.parseInt(fromDate);
    Integer.parseInt(toDate);
    if (checkValidDate(fromDate) && checkValidDate(toDate)) {
      if (checkStartDateBeforeEndDate(fromDate, toDate)) {
        timeArray.add(fromDate);
        timeArray.add(toDate);
        command = new Command("display", 3, timeArray);
      } else {
        timeArray.add(toDate);
        timeArray.add(fromDate);
        command = new Command("display", 3, timeArray);
      }
    } else {
      command = createInvalidCommand();
    }
    return command;
  }
  
  // ================================================================================
  // Methods for creating delete and done indexes
  // ================================================================================
  
  //meant to check if the input contains any -(dash) so that we can check if it defines a range
  private boolean checkIfContainsDash(ArrayList<String> arguments) {
    int argumentsLength = arguments.size();
    boolean haveDash = false;
    for (int i = 0; i < argumentsLength; i++) {
      if (arguments.get(i).contains("-")) {
        haveDash = true; 
      } 
    }
    return haveDash;
  }
  
  //creates the list of indexes of items to have action carried out on
  private ArrayList<Integer> getArrayListForDoneOrDelete(ArrayList<String> arguments, boolean containsDash, int argumentsLength) {
    ArrayList<Integer> indexOfItems = new ArrayList<Integer>();
    
    if (argumentsLength > 1 && !containsDash) {
      for (int i = 0; i < argumentsLength; i++) {
        indexOfItems.add(Integer.parseInt(arguments.get(i)));
      }
    } else if (argumentsLength == 1 && containsDash) {
      String[] strArray = arguments.get(0).trim().split("-");
      int startPoint = Integer.parseInt(strArray[0]);
      int endPoint = Integer.parseInt(strArray[1]);
      if (startPoint > endPoint) {
        int temp = startPoint;
        startPoint = endPoint;
        endPoint = temp;
      }
      for (int i = startPoint; i <= endPoint; i++) {
        indexOfItems.add(i);
      }
    } else {
      ;
    }
    return indexOfItems;
  }
  
  // ================================================================================
  // Utility methods
  // ================================================================================
  
  //meant to find the last word of the description in the arrayList, allowing the splitting into the description and time arrays
  private int findIndexOfLastWord (ArrayList<String> parameters) {
    int indexOfLastWord = -1;
    for (int i = 0; i < parameters.size(); i++) {
      if (parameters.get(i).contains(".")) {
        indexOfLastWord = i;
      }      
    }
    return indexOfLastWord;
  }
  
  //meant to check if the argument contains a full stop, allowing verification if valid input
  private boolean findFullStop(ArrayList<String> parameters) {
    int index = findIndexOfLastWord(parameters);
    if (index == -1) {
      return false;
    }
    return true;
  }
  
}
