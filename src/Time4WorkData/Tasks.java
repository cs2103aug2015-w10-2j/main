package Time4WorkData;

/* @@author A0125495Y */

public abstract class Tasks {
	private int taskID;
	private String description = "";		 
	private boolean completed = false;
	private int type = 0;
	
	public enum TaskType {
		DeadlineType(1), DurationType(2), FloatingType(3);
		private int myType = 0;
		
		private TaskType(int thisType) {
			this.myType = thisType;
		}
		
		public int getTaskType() {
	        return myType;
	    }
	}
	
	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	
	
}
