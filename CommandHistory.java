import java.util.Stack;

public class CommandHistory {
	private Stack<Command> commandHistory;
	private Stack<String> commandTypeList;
	
	public CommandHistory() {
		commandHistory = new Stack<Command>();
		commandTypeList = new Stack<String>();
	}
	
	public Command getLastCommand() {
		return commandHistory.pop();
	}
	
	public String getLastCommandType() {
		return commandTypeList.pop();
	}
	
	public void addCommand(Command recentCommand) {
		commandHistory.add(recentCommand);
	}
	
	public void addCommandType(String recentCommandType) {
		commandTypeList.add(recentCommandType);
	}
	
	public int getCommandHistorySize() {
		return commandTypeList.size();
	}
	
	public boolean isEmpty() {
		if (commandTypeList.isEmpty()){
			return true;
		} else {
			return false;
		}
	}
}
