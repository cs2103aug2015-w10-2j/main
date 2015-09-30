public class Command {
  
  private Tasks task;
  private String command;
  
  public Command(String command, Tasks task) {
    this.command = command;
    this.task = task;
  }
  
  public Tasks getTask(){
	  return task;
  }
  
  public String getCommand(){
	  return command;
  }
}
