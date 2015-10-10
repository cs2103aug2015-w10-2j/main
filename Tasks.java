public abstract class Tasks {
	private int taskID;
	private String description = "";		 
	private boolean completed = false;
	private int type = 0;
	
	protected final int DeadlineType = 1;
	protected final int DurationType = 2;
	protected final int BlockedType = 3;
	protected final int FloatingType = 4;
	
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
