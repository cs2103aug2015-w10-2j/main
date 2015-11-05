package Time4WorkUI;

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
		if (userInput.toLowerCase().startsWith("display incomplete")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isSearchCommand() {
		if (userInput.toLowerCase().startsWith("s")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isHelpCommand() {
		if (userInput.toLowerCase().startsWith("h")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isAddCommand() {
		if (userInput.toLowerCase().startsWith("a")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isDeleteCommand() {
		if (userInput.toLowerCase().startsWith("de")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isUpdateCommand() {
		if (userInput.toLowerCase().startsWith("up")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isUndoCommand() {
		if (userInput.toLowerCase().startsWith("un")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isMarkAsDoneCommand() {
		if (userInput.toLowerCase().startsWith("do")){
			return true;
		} else {
			return false;
		}
	}

	public boolean isClearCommand() {
		if (userInput.toLowerCase().startsWith("c")){
			return true;
		} else {
			return false;
		}
	}
}
