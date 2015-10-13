package Time4WorkLogic;
import java.util.ArrayList;

import Time4WorkStorage.Tasks;

public class FeedbackMessage {
    private String feedback;
    private ArrayList<Tasks> taskList;
    
    public FeedbackMessage(String feedback, ArrayList<Tasks> taskList) {
        this.feedback = feedback;
        this.taskList = taskList;
    }
    
    public String getFeedback() {
        return this.feedback;
    }
    
    public ArrayList<Tasks> getTaskList() {
        return this.taskList;
    }
    
}
