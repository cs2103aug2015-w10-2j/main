package Time4WorkParser;

import java.util.ArrayList;
import java.util.Arrays;

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
      command = createDisplayCommand();
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
    Tasks task;
    int numberOfArguments = arguments.size();
    assert numberOfArguments <= 5;
    String descriptionOfTask = arguments.get(0);
    
    if (numberOfArguments == 1) {
      task = new FloatingTask(descriptionOfTask);
    } else if (numberOfArguments ==2){
      Duration deadline = new Duration(arguments.get(1), "2359");
      task = new DeadlineTask(descriptionOfTask, deadline);
    } else if (numberOfArguments == 3) {
      Duration deadline = new Duration(arguments.get(1), arguments.get(2));
      task = new DeadlineTask(descriptionOfTask, deadline);
    } else if (numberOfArguments == 4) {
      Duration durationPeriod = new Duration(arguments.get(1), arguments.get(2), arguments.get(1), arguments.get(3));
      task = new DurationTask(descriptionOfTask, durationPeriod);
    } else {
      Duration durationPeriod = new Duration(arguments.get(1), arguments.get(2), arguments.get(3), arguments.get(4));
      task = new DurationTask(descriptionOfTask, durationPeriod);
    }
    return task;
  }
  
  private Command createAddCommand(ArrayList<String> arguments) {
    Command command;
    
    ArrayList<String> combinedDescriptionList = createNewListWithCombinedDescription(arguments);
    
    if (!combinedDescriptionList.equals(null)){
    	command = new Command("add", createTaskListForAddingOrUpdating(combinedDescriptionList));
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
        task.setTaskID(taskID);
        command = new Command("update", task);
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
  
  private Command createDisplayCommand(){
	 Command command = new Command("display");
	    
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