package Time4WorkLogic;
import java.util.Stack;

import Time4WorkParser.Command;

public class CommandHistory {
    private Stack<Command> reversedCommand;
    private Stack<Command> commandHistory;
    
    
    public CommandHistory() {
        reversedCommand = new Stack<Command>();
        commandHistory = new Stack<Command>();
    }
    
    public Command getLastCommand() {
        return commandHistory.pop();
    }
    
    public Command getLastReversedCommand() {
        return reversedCommand.pop();
    }
    
    public void addCommand(Command recentCommand) {
        commandHistory.add(recentCommand);
    }
    
    public void addReversedCommand(Command recentReversedCommand) {
        reversedCommand.add(recentReversedCommand);
    }
    
    public int getCommandHistorySize() {
        return commandHistory.size();
    }
    
    
    
    public boolean isEmpty() {
        if (commandHistory.isEmpty()){
            return true;
        } else {
            return false;
        }
    }
}
