package Time4WorkUI;

//@@author: A0112077N
public class UserInput {
	String userInput;

	public UserInput(String userInput){
		setUserInput(userInput);
	}

	public void setUserInput(String userInput){
		this.userInput = userInput;
	}

	public String getUserInput(){
		return this.userInput;
	}

	public boolean isDisplayArchiveCommand(){
		if (userInput.toLowerCase().startsWith("display archive")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isDisplayInCompleteCommand() {
		if (userInput.toLowerCase().startsWith("display")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isSearchCommand() {
		if (userInput.toLowerCase().startsWith("search")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isHelpCommand() {
		if (userInput.toLowerCase().startsWith("help")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isAddCommand() {
		if (userInput.toLowerCase().startsWith("add")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isDeleteCommand() {
		if (userInput.toLowerCase().startsWith("delete")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isUpdateCommand() {
		if (userInput.toLowerCase().startsWith("update")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isUndoCommand() {
		if (userInput.toLowerCase().startsWith("undo")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isStoreCommand() {
		if (userInput.toLowerCase().startsWith("store")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isClearCommand() {
		if (userInput.toLowerCase().startsWith("clear")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isExitCommand() {
		if (userInput.toLowerCase().startsWith("exit")){
			return true;
		} else {
			return false;
		}
	}
}
