package Time4WorkLogic;
import java.util.ArrayList;

import Time4WorkStorage.Tasks;

public class FeedbackMessage {
    private String feedback;
    private ArrayList<Tasks> completeList;
    private ArrayList<Tasks> incompleteList;
    
    //@@author A0133894W
    public FeedbackMessage(String feedback, ArrayList<Tasks> completeList,
                           ArrayList<Tasks> incompleteList) {
        this.feedback = feedback;
        this.completeList = completeList;
        this.incompleteList = incompleteList;
    }
    
    public String getFeedback() {
        return this.feedback;
    }
    
    public ArrayList<Tasks> getCompleteTaskList() {
        return this.completeList;
    }
    
    public ArrayList<Tasks> getIncompleteTaskList() {
        return this.incompleteList;
    }
    
}
