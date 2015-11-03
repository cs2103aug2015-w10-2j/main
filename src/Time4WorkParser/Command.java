package Time4WorkParser;
import Time4WorkStorage.Tasks;

import java.util.*;

public class Command {
  
  private Tasks task;
  private String command;
  private String displayOrStoragePath;
  private int selectedIndexNumber;
  private ArrayList<Integer> selectedIndexNumbers = new ArrayList<Integer>();
  private boolean isDateSearch;
  private String searchKeyword;
  
  //for adding and updating
  public Command(String command, Tasks task) {
    this.command = command;
    this.task = task;
  }
  
  //for storing and alternate display functions
  public Command(String command, String displayOrStoragePath){
   this.command = command;
   this.displayOrStoragePath = displayOrStoragePath;
  }
  
  //search command
  public Command(String command, boolean isDateSearch, String searchKeyword){
	this.command = command;
	this.isDateSearch = isDateSearch;
	this.searchKeyword = searchKeyword;
  }
  
  //for deleting 1 task or marking 1 task as done
  public Command(String command, int selectedIndexNumber){
   this.command = command;
   this.selectedIndexNumber = selectedIndexNumber;
  }
  
  //for deleting multiple tasks or marking multiple tasks as done
  public Command(String command, ArrayList<Integer> selectedIndexNumbers){
   this.command = command;
   this.selectedIndexNumbers = selectedIndexNumbers;
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
  
  public String getDisplayTypeOrStoragePath() {
   return displayOrStoragePath;
  }

  public boolean getIsDateSearch() {
	  return isDateSearch;
  }
  
  public String getSearchKeyword() {
	  return searchKeyword;
  }
  
  public int getSelectedIndexNumber() {
   return selectedIndexNumber;
  }
  
  public ArrayList<Integer> getSelectedIndexNumbers(){
   return selectedIndexNumbers;
   }
}
