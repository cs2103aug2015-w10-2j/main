//@@author A0110920Y

package Time4WorkParser;
import Time4WorkStorage.Tasks;

import java.util.*;

public class Command {
  
  private Tasks task;
  private String command;
  private String storeSearchAndDisplayStrings;
  private ArrayList<Integer> selectedIndexNumbers = new ArrayList<Integer>();
  private boolean isDateSearch;
  private int displayType;
  private ArrayList<String> timeArray = new ArrayList<String>();
  
  //for adding and updating
  public Command(String command, Tasks task) {
    this.command = command;
    this.task = task;
  }
  
  //for storing, searching and non-time display functions
  public Command(String command, String storeSearchAndDisplayStrings) {
    this.command = command;
    this.storeSearchAndDisplayStrings = storeSearchAndDisplayStrings;
  }
  
  //for deleting tasks or marking tasks as done
  public Command(String command, ArrayList<Integer> selectedIndexNumbers) {
    this.command = command;
    this.selectedIndexNumbers = selectedIndexNumbers;
  }
  
  //for undo, clear, exit and invalid commands
  public Command(String command) {
    this.command = command;
  }
  
  //for time display functions
  public Command(String command, int displayType, ArrayList<String> timeArray) {
    this.command = command;
    this.displayType = displayType;
    this.timeArray = timeArray;
  }
  
  public Tasks getTask() {
    return task;
  }
  
  public String getCommand() {
    return command;
  }
  
  public String getStoreSearchAndDisplayStrings() {
    return storeSearchAndDisplayStrings;
  }
  
  public boolean getIsDateSearch() {
    return isDateSearch;
  }
  
  public ArrayList<Integer> getSelectedIndexNumbers() {
    return selectedIndexNumbers;
  }
  
  public int getDisplayType() {
    return displayType;
  }
  
  public ArrayList<String> getTimeArray() {
    return timeArray;
  }
  
}
