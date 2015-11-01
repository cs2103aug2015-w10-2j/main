package Time4WorkParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

public class Parser {
  
  private static final int POSITION_PARAM_COMMAND = 0;
  private static final int POSITION_FIRST_PARAM_ARGUMENT = 1;
  
  private ArrayList<String> VALID_DISPLAY_COMMANDS = new ArrayList<String>(Arrays.asList("archive", "floating", "deadline", "duration"));
  
  private static final String KEYWORD_BY = "by";
  private static final String KEYWORD_FROM = "from";
  private static final String KEYWORD_TO = "to";
  
  private static final String REGEX_WHITESPACES = "[\\s,]+";
  
  private static final Logger logger = Logger.getLogger(Parser.class.getName());
  
  public Parser() {
  }
  
  /**
   * @param userInput
   * takes in the user input string and converts it into a command for the logic segment
   * the end of the description is stipulated with a ".", full stop, to indicate the end
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
    
    if (userCommand.toUpperCase().equals("ADD")){
      command = createAddIfValidCommand(arguments);
    } else if (userCommand.toUpperCase().equals("DELETE")) {
      command = createDeleteCommand(arguments);
    } else if (userCommand.toUpperCase().equals("UPDATE")) {
      command = createUpdateIfValidCommand(arguments);
    } else if (userCommand.toUpperCase().equals("SEARCH")) {
      command = createSearchCommand(arguments);
    } else if (userCommand.toUpperCase().equals("UNDO")){
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
  
  private Command createUpdateIfValidCommand(ArrayList<String> arguments) {
    Command command;
    if (findFullStop(arguments)){
      command = createUpdateCommand(arguments);
    } else {
      logger.log(Level.WARNING, "full stop not found in user input.");
      command = createInvalidCommand();
    }
    return command;
  }
  
  private Command createAddIfValidCommand(ArrayList<String> arguments) {
    Command command;
    if (findFullStop(arguments)){
      command = createAddCommand(arguments);
    } else {
      logger.log(Level.WARNING, "full stop not found in user input.");
      command = createInvalidCommand();
    }
    return command;
  }
  
  /**
   * @param arguments
   * takes in the string and splits it into a array list of the split string
   * 
   * @return String[] of the string input
   */
  private ArrayList<String> splitString(String arguments) {
    String[] strArray = arguments.trim().split(REGEX_WHITESPACES);
    return new ArrayList<String>(Arrays.asList(strArray));
  }
  
  /**
   * @param parameters
   * takes in the arraylist<String> and gets the first argument, which is the action to be done
   * 
   * @return String of the action to be done
   */
  private String getUserCommand(ArrayList<String> parameters) {
    return parameters.get(POSITION_PARAM_COMMAND);
  }
  
  private ArrayList<String> getUserArguments(ArrayList<String> parameters) {
    return new ArrayList<String>(parameters.subList(POSITION_FIRST_PARAM_ARGUMENT,
                                                    parameters.size()));
  }
  
  private ArrayList<String> getDescriptionArray(ArrayList<String> parameters, int end){
    return new ArrayList<String>(parameters.subList(0, end+1));
  }
  
  private ArrayList<String> getTimeArray(ArrayList<String> parameters, int lastWordIndex){
    return new ArrayList<String>(parameters.subList(lastWordIndex+1, parameters.size()));
  }
  
  private int findIndexOfLastWord (ArrayList<String> parameters){
    int indexOfLastWord = -1;
    for(int i = 0; i < parameters.size(); i++){
      if (parameters.get(i).contains(".")){
        indexOfLastWord = i;
      }      
    }
    return indexOfLastWord;
  }
  
  private boolean findFullStop(ArrayList<String> parameters) {
    int index = findIndexOfLastWord(parameters);
    if (index == -1){
      return false;
    }
    return true;
  }
  
  private String createDescription(ArrayList<String> parameters,int end){
    ArrayList<String> description = getDescriptionArray(parameters, end);
    String descriptionString;
    String lastWord = description.get(end);
    if(lastWord.endsWith(".")){
      description.set(end, lastWord.replaceAll(".$", ""));
      descriptionString = String.join(" ", description);
    } else {
      descriptionString = null;
    }
    return descriptionString;
  }
  
  private ArrayList<String> createNewListWithCombinedDescription(ArrayList<String> parameters){
    
    ArrayList<String> resultArray = new ArrayList<String>();
    int lastWordIndex = findIndexOfLastWord(parameters);
    String description = createDescription(parameters, lastWordIndex);
    if (!description.equals(null)){
      ArrayList<String> timeArray = getTimeArray(parameters, lastWordIndex);
      resultArray.add(description);
      resultArray.addAll(timeArray);
    } else {
      resultArray = null;
    }
    return resultArray;
  }
  
  private Tasks createTaskListForAddingOrUpdating(ArrayList<String> arguments) {
    Tasks task = null;
    String descriptionOfTask = arguments.get(0);
    ArrayList<String> timeArray = getTimeArray(arguments, 0);
    timeArray.set(0, timeArray.get(0).toLowerCase());
    int numberOfArguments = timeArray.size();
    
    if (numberOfArguments == 0) {
      task = new FloatingTask(descriptionOfTask);
    } else if (timeArray.contains(KEYWORD_BY)) {
      timeArray.remove(KEYWORD_BY);
      task = createDeadlineTask(task, descriptionOfTask, timeArray);
    } else if (timeArray.contains(KEYWORD_FROM) && timeArray.contains(KEYWORD_TO)) {
      timeArray.remove(KEYWORD_FROM);
      timeArray.remove(KEYWORD_TO);
      task = createDurationTask(task, descriptionOfTask, timeArray);
    }
    return task;
  }
  
  private Tasks createDurationTask(Tasks task, String descriptionOfTask, ArrayList<String> timeArray) {
    if (timeArray.size() == 3){
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
  
  private Tasks createDeadlineTask(Tasks task, String descriptionOfTask, ArrayList<String> timeArray) {
    if (timeArray.size() == 1){
      String endDate = timeArray.get(0);
      task = createDeadlineTaskForInputWithoutTime(task, descriptionOfTask, endDate);
    } else {
      String endDate = timeArray.get(0);
      String endTime = timeArray.get(1);
      task = createDeadlineTaskForInputWithTime(task, descriptionOfTask, endDate, endTime);
    }
    return task;
  }
  
  private Tasks createDurationTaskForFullInputCommand(Tasks task, String descriptionOfTask, String startDate,
                                                      String startTime, String endDate, String endTime) {
    if (checkValidDate(startDate) && checkValidDate(endDate) && checkValidTime(startTime) && checkValidTime(endTime)){
      if(startDate.equals(endDate)){ //creates a Duration object using createDurationTaskForSameDayEvent
        task = createDurationTaskForSameDayEvent(task, descriptionOfTask, startDate, startTime, endTime);
      } else if (checkStartDateBeforeEndDate(startDate, endDate)){ //creates a Duration object for (start,start,end,end) if checkStartDateBeforeEndDate true
        Duration durationPeriod = new Duration(startDate, startTime, endDate, endTime);
        task = new DurationTask(descriptionOfTask, durationPeriod);
      } else { //creates a Duration object for (end,end,start,start) if checkStartDateBeforeEndDate false
        Duration durationPeriod = new Duration(endDate, endTime, startDate, startTime);
        task = new DurationTask(descriptionOfTask, durationPeriod);
      }
    }
    return task;
  }
  
  private Tasks createDurationTaskForSameDayEvent(Tasks task, String descriptionOfTask, String date, String startTime, String endTime) {
    if (checkValidDate(date) && checkValidTime(startTime) && checkValidTime(endTime)){
      if (checkStartTimeBeforeEndTime(startTime, endTime)){
        Duration durationPeriod = new Duration(date, startTime, date, endTime);
        task = new DurationTask(descriptionOfTask, durationPeriod);
      } else {
        Duration durationPeriod = new Duration(date, endTime, date, startTime);
        task = new DurationTask(descriptionOfTask, durationPeriod);
      }
    }
    return task;
  }
  
  private Tasks createDeadlineTaskForInputWithTime(Tasks task, String descriptionOfTask, String endDate, String endTime) {
    if(checkValidDate(endDate) && checkValidTime(endTime)){
      Duration deadline = new Duration(endDate, endTime);
      task = new DeadlineTask(descriptionOfTask, deadline);
    }
    return task;
  }
  
  private Tasks createDeadlineTaskForInputWithoutTime(Tasks task, String descriptionOfTask, String endDate) {
    if(checkValidDate(endDate)){
      Duration deadline = new Duration(endDate, "2359");
      task = new DeadlineTask(descriptionOfTask, deadline);
    }
    return task;
  }
  
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
  
  private boolean checkValidTime(String inputTime) {
    int hour = Integer.parseInt(inputTime.substring(0,2));
    int minute = Integer.parseInt(inputTime.substring(2,4));
    
    if ((hour >= 0 && hour <= 23) && (minute >= 0 && minute <= 59)){
      return true;
    } else {
      return false;
    }
  }
  
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
    if (startingDate.before(endingDate)){
      return true;
    } else {
      return false;
    }
  }
  
  private boolean checkStartTimeBeforeEndTime(String startTime, String endTime){
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
  
  private Command createAddCommand(ArrayList<String> arguments) {
    Command command;
    Tasks task;
    
    ArrayList<String> combinedDescriptionList = createNewListWithCombinedDescription(arguments);
    
    if (!combinedDescriptionList.equals(null)){
      task = createTaskListForAddingOrUpdating(combinedDescriptionList);
      if(!task.equals(null)){
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
    
    containsDash = checkIfContainsDash(arguments, argumentsLength);
    
    if (argumentsLength == 1 && !containsDash){
      int indexToBeDeleted = Integer.parseInt(arguments.get(0));
      command = new Command("delete", indexToBeDeleted);
    } else {
      command = new Command("delete", getArrayListForDoneOrDelete(arguments, containsDash, argumentsLength));
    }
    
    return command;
  }
  
  private boolean checkIfContainsDash(ArrayList<String> arguments, int argumentsLength) {
    if (argumentsLength == 1 && arguments.get(0).contains("-")) {
      return true; 
    }
    return false;
  }
  
  private ArrayList<Integer> getArrayListForDoneOrDelete(ArrayList<String> arguments, boolean containsDash, int argumentsLength) {
    ArrayList<Integer> indexOfItems = new ArrayList<Integer>();
    
    if (argumentsLength > 1 && !containsDash) {
      for (int i = 0; i < argumentsLength; i++){
        indexOfItems.add(Integer.parseInt(arguments.get(i)));
      }
    } else {
      String[] strArray = arguments.get(0).trim().split("-");
      int startPoint = Integer.parseInt(strArray[0]);
      int endPoint = Integer.parseInt(strArray[1]);
      if (startPoint > endPoint){
        int temp = startPoint;
        startPoint = endPoint;
        endPoint = temp;
      }
      for (int i = startPoint; i <= endPoint; i++){
        indexOfItems.add(i);
      }
    }
    return indexOfItems;
  }
  
  private Command createUpdateCommand(ArrayList<String> arguments) {
    Command command;
    Tasks task;
    int taskID = Integer.parseInt(arguments.get(0));
    
    ArrayList<String> withoutTaskIDArguments = getUserArguments(arguments);
    
    ArrayList<String> combinedDescriptionList = createNewListWithCombinedDescription(withoutTaskIDArguments);
    
    if (!combinedDescriptionList.equals(null)){
      task = createTaskListForAddingOrUpdating(combinedDescriptionList);
      if (!task.equals(null)){
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
  
  private Command createSearchCommand(ArrayList<String> arguments) {
    Command command;
    String keyword = String.join(" ", arguments);
    
    command = new Command("search", keyword);
    
    return command;
  }
  
  private Command createUndoCommand(){
    Command command = new Command("undo");
    
    return command;
  }
  
  private Command createClearCommand(){
    Command command = new Command("clear");
    
    return command;
  }
  
  private Command createDisplayCommand(ArrayList<String> arguments){
    String typeToDisplay = null;
    
    if (arguments.isEmpty()){
    	;
    } else if (VALID_DISPLAY_COMMANDS.contains(arguments.get(0))) {
    	typeToDisplay = arguments.get(0);
    } else {
    	;
    }
    Command command = new Command("display", typeToDisplay);
    
    return command;
  }
  
  private Command createDoneCommand(ArrayList<String> arguments){
    Command command;
    boolean containsDash;
    int argumentsLength = arguments.size();
    
    containsDash = checkIfContainsDash(arguments, argumentsLength);
    
    if (argumentsLength == 1 && !containsDash){
      int indexToBeMarkedDone = Integer.parseInt(arguments.get(0));
      command = new Command("done", indexToBeMarkedDone);
    } else {
      command = new Command("done", getArrayListForDoneOrDelete(arguments, containsDash, argumentsLength));
    }
    
    return command;
  }
  
  private Command createStoreCommand(ArrayList<String> arguments){
    String storageLocation = String.join(" ", arguments);
    String escapedStorageLocation = storageLocation.replace("\\", "\\\\");
    
    Command command = new Command("store", escapedStorageLocation);
    
    return command;
  }
  
  private Command createExitCommand(){
    Command command = new Command("exit");
    
    return command;
  }
  
  private Command createInvalidCommand(){
    Command command = new Command("invalid");
    
    return command;
  }
  
}
