import java.util.ArrayList;

public class FeedbackMessage {
	
	private String feedback;
	private ArrayList<Tasks> taskList;

	public FeedbackMessage(String feedback) {
		this.feedback = feedback;
	}
	
	public FeedbackMessage(String feedback, ArrayList<Tasks> task) {
		this.feedback = feedback;
		this.taskList = task;
	}
	
	
	public String getFeedback() {
		return this.feedback;
	}
	
	public ArrayList<Tasks> getTask() {
		return this.taskList;
	}
	
	
}
