import java.util.ArrayList;
import java.util.Arrays;

public class Time4WorkParser {
  
  private static final int POSITION_PARAM_COMMAND = 0;
  private static final int POSITION_FIRST_PARAM_ARGUMENT = 1;
  
  private static final String REGEX_WHITESPACES = "[\\s,]+";
  
  public Time4WorkParser() {
  }
  
  public Command parse(String userInput) {
    Command command;
    ArrayList<String> parameters = splitString(userInput);
    String userCommand = getUserCommand(parameters);
    ArrayList<String> arguments = getUserArguments(parameters);
    
    if (userCommand.toUpperCase().equals("ADD")){
      command = createAddCommand(arguments);
    } else if (userCommand.toUpperCase().equals("DELETE")) {
      command = createDeleteCommand(arguments);
    } else if (userCommand.toUpperCase().equals("UPDATE")) {
      command = createUpdateCommand(arguments);
    } else if (userCommand.toUpperCase().equals("SEARCH")) {
      command = createSearchCommand(arguments);
    } else if (userCommand.toUpperCase().equals("UNDO")){
      command = createUndoCommand();
    } else if (userCommand.toUpperCase().equals("EXIT")) {
      command = createExitCommand();
    } else {
      command = createInvalidCommand();
    }
    return command;
  }
  
  private ArrayList<String> splitString(String arguments) {
    String[] strArray = arguments.trim().split(REGEX_WHITESPACES);
    return new ArrayList<String>(Arrays.asList(strArray));
  }
  
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
    int indexOfLastWord = 0;
    for(int i = 0; i < parameters.size(); i++){
      if (parameters.get(i).contains(".")){
        indexOfLastWord = i;
      }      
    }
    return indexOfLastWord;
  }
  
  private String createDescription(ArrayList<String> parameters,int end){
    ArrayList<String> description = getDescriptionArray(parameters, end);
    String descriptionString = String.join(" ", description);
    return descriptionString;
  }
  
  private ArrayList<String> createNewListWithCombinedDescription(ArrayList<String> parameters){
    int lastWordIndex = findIndexOfLastWord(parameters);
    String description = createDescription(parameters, lastWordIndex).replace(".", "");
    ArrayList<String> timeArray = getTimeArray(parameters, lastWordIndex);
    ArrayList<String> resultArray = new ArrayList<String>();
    resultArray.add(description);
    resultArray.addAll(timeArray);
    
    return resultArray;
  }
  
  private Tasks createTaskListForAddingOrUpdating(ArrayList<String> arguments) {
    Tasks task;
    int numberOfArguments = arguments.size();
    String descriptionOfTask = arguments.get(0);
    
    if (numberOfArguments == 1) {
      task = new FloatingTask(descriptionOfTask);
    } else if (numberOfArguments == 3) {
      Duration deadline = new Duration(arguments.get(1), arguments.get(2));
      task = new DeadlineTask(descriptionOfTask, deadline);
    } else {
      Duration durationPeriod = new Duration(arguments.get(1), arguments.get(2), arguments.get(3), arguments.get(4));
      task = new DurationTask(descriptionOfTask, durationPeriod);
    }
    
    return task;
  }
  
  private Command createAddCommand(ArrayList<String> arguments) {
    Command command;
    
    ArrayList<String> combinedDescriptionList = createNewListWithCombinedDescription(arguments);
    
    command = new Command("add", createTaskListForAddingOrUpdating(combinedDescriptionList));
    
    return command;
  }
  
  private Command createDeleteCommand(ArrayList<String> arguments) {
    Command command;
    
    int indexToBeDeleted = Integer.parseInt(arguments.get(0));
    
    command = new Command("delete", indexToBeDeleted);
    
    return command;
  }
  
  private Command createUpdateCommand(ArrayList<String> arguments) {
    Command command;
    Tasks task;
    int taskID = Integer.parseInt(arguments.get(0));
    
    ArrayList<String> withoutTaskIDArguments = getUserArguments(arguments);
    
    ArrayList<String> combinedDescriptionList = createNewListWithCombinedDescription(withoutTaskIDArguments);
    
    task = createTaskListForAddingOrUpdating(combinedDescriptionList);
    task.setTaskID(taskID);
    
    command = new Command("update", task);
    
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
  
  private Command createExitCommand(){
    Command command = new Command("exit");
    
    return command;
  }
  
  private Command createInvalidCommand(){
    Command command = new Command("invalid");
    
    return command;
  }
  
}
