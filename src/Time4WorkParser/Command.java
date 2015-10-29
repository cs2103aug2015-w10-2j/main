package Time4WorkParser;
import Time4WorkStorage.Tasks;

import java.util.*;

public class Command {
  
  private Tasks task;
  private String command;
  private String searchOrStoragePath;
  private int selectedIndexNumber;
  private ArrayList<Integer> selectedIndexNumbers = new ArrayList<Integer>();
  
  //for adding and updating
  public Command(String command, Tasks task) {
    this.command = command;
    this.task = task;
  }
  
  //for searching and storing
  public Command(String command, String searchOrStoragePath){
   this.command = command;
   this.searchOrStoragePath = searchOrStoragePath;
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
  
  public String getSearchOrStoragePath() {
   return searchOrStoragePath;
  }

  public int getSelectedIndexNumber() {
   return selectedIndexNumber;
  }
  
  public ArrayList<Integer> getSelectedIndexNumbers(){
   return selectedIndexNumbers;
   }
}
