import java.util.ArrayList;
import java.util.Arrays;

public class Time4WorkParser {
    
    private static final int POSITION_PARAM_COMMAND = 0;
    private static final int POSITION_FIRST_PARAM_ARGUMENT = 1;

    private static final String REGEX_WHITESPACES = "[\\s,]+";

    public Time4WorkParser() {
    }
    
    enum COMMANDS {
      ADD, DELETE, UPDATE, SEARCH, EXIT
    };
    
    public Command parse(String userInput) {
      Command command;
      ArrayList<String> parameters = splitString(userInput);
      String userCommand = getUserCommand(parameters);
      ArrayList<String> arguments = getUserArguments(parameters);

      switch (COMMANDS.valueOf(userCommand.toUpperCase())) {
        
        case ADD :
          command = createAddCommand(arguments);
          break; 
          
        case DELETE :
          command = createDeleteCommand(arguments);
          break;
          
        case UPDATE :
          command = createUpdateCommand(arguments);
          break;
          
        case SEARCH : 
          command = createSearchCommand(arguments);
          break;
          
        case EXIT :
          command = createExitCommand();
          break;
          
        default :
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

    private Tasks createTaskListForAddingOrUpdating(ArrayList<String> arguments) {
      Tasks task;
      DeadLines taskDeadLine;
      int numberOfArguments = arguments.size();
      
      if (numberOfArguments == 1) {
        task = new Tasks(arguments.get(0));
      } else if (numberOfArguments == 3){
        taskDeadLine = new DeadLines(arguments.get(1), arguments.get(2));
        task = new Tasks(arguments.get(0), taskDeadLine);
      } else {
        taskDeadLine = new DeadLines(arguments.get(1), arguments.get(2), arguments.get(3), arguments.get(4));  
        task = new Tasks(arguments.get(0), taskDeadLine);
      }
      
      return task;
    }

    private Command createAddCommand(ArrayList<String> arguments) {
      Command command;
      
      command = new Command("add", createTaskListForAddingOrUpdating(arguments));
      
      return command;
    }
    
    private Command createDeleteCommand(ArrayList<String> arguments) {
      Command command;
      Tasks task;
      
      task = new Tasks(Integer.parseInt(arguments.get(0)));
      command = new Command("delete", task);
      
      return command;
    }
    
    private Command createUpdateCommand(ArrayList<String> arguments) {
      Command command;
      Tasks task;
      int taskID = Integer.parseInt(arguments.get(0));
      
      task = createTaskListForAddingOrUpdating(getUserArguments(arguments));
      task.setTaskID(taskID);
      
      command = new Command("update", task);
      
      return command;
    }
    
    private Command createSearchCommand(ArrayList<String> arguments) {
      Command command;
      Tasks task;
      
      task = new Tasks(arguments.get(0));
      command = new Command("search", task);
      
      return command;
    }

    private Command createExitCommand(){
     Tasks task = new Tasks();
     Command command = new Command("exit", task);
     
     return command;
    }
    
    private Command createInvalidCommand(){
     Tasks task = new Tasks();
     Command command = new Command("invalid", task);
     
     return command;
    }
}
