public class Command {
  
  private Tasks task;
  private String command;
  private String searchKeyword;
  private int indexToBeDeleted;
  
  //for adding and updating
  public Command(String command, Tasks task) {
    this.command = command;
    this.task = task;
  }
  
  //for searching
  public Command(String command, String searchKeyword){
	  this.command = command;
	  this.searchKeyword = searchKeyword;
  }
  
  //for deleting
  public Command(String command, int indexToBeDeleted){
	  this.command = command;
	  this.indexToBeDeleted = indexToBeDeleted;
  }
  
  //for exit and invalid commands
  public Command(String command){
	  this.command = command;
  }
  
  public Tasks getTask(){
	  return task;
  }
  
  public String getCommand(){
	  return command;
  }
  
  public String getSearchKeyword() {
	  return searchKeyword;
  }

  public int getIndexToBeDeleted() {
	  return indexToBeDeleted;
  }
  
}
