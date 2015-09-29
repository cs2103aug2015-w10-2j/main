import java.util.ArrayList;

public class Tasks {
	
	private int taskID;
	private String description = "";		
	private DeadLines dueDetails = null;					//for events with duration or just end date
	private ArrayList<DeadLines> blockedDetails = null;		//arraylist of deadLines for blocking
	private boolean completed = false;						//true complete, false not completed 
															//floating tasks, dueDetails and blockedDetails are both null
	private int type = 0;									//1 = deadline task, 2 = duration, 3 = blocked, 4 = floating
	private final int DeadlineType = 1;
	private final int DurationType = 2;
	private final int BlockedType = 3;
	private final int FloatingType = 4;
	
	//floating task
	public Tasks(String desc) {
		setDescription(desc);
		setType(FloatingType);
	}
	
	//deadline/duration tasks
	public Tasks(String desc, DeadLines dueDeadlines) {
		setDescription(desc);
		setDueDetails(dueDeadlines);
		setType(dueDetails.getType());	//DeadLines class will return if duration or deadline
	}
	
	//blocked tasks
	public Tasks(String desc, ArrayList<DeadLines> blockedDeadlines) {
		setDescription(desc);
		setBlockedDetails(blockedDeadlines);
		setType(BlockedType);
	}
	
	//for deleting
	public Tasks(int taskID) {
		setTaskID(taskID);
	}
	
	//for searching
	public Tasks(String desc) {
		setDescription(desc);
	}
	
	//select a blocked timeslot as final, position starts from 1 (arraylist[0])
	//returns true if selection was successful, false = task was not blocked task or invalid selection index
	public boolean selectBlockedSlot(int position) {
		
		boolean successful = false;
		
		//ensure task was a blocked task and input selection is valid
		if(getType() != 3 && position <= getBlockedDetails().size() && position >= 1) {
			
			//retrieve selected deadline, clear blocked details, set into dueDetails, change type
			setDueDetails(getBlockedDetails().get(position-1));
			setBlockedDetails(null);
			setType(dueDetails.getType());
			successful = true;
		}
		
		return successful;
	}
		
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public DeadLines getDueDetails() {
		return dueDetails;
	}
	public void setDueDetails(DeadLines dueDetails) {
		this.dueDetails = dueDetails;
	}
	public ArrayList<DeadLines> getBlockedDetails() {
		return blockedDetails;
	}
	public void setBlockedDetails(ArrayList<DeadLines> blockedDetails) {
		this.blockedDetails = blockedDetails;
	}
	public boolean isCompleted() {
		return completed;
	}
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int ID) {
		taskID = ID;
	}
}
