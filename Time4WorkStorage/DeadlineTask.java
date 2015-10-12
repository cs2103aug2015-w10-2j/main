package Time4WorkStorage;


public class DeadlineTask extends Tasks{
	
	private Duration durationDetails = null;	
	
	public DeadlineTask(int taskID, String desc, Duration duration) {
		super.setTaskID(taskID);
		super.setDescription(desc);
		super.setType(DeadlineType);
		setDurationDetails(duration);
	}
	
	public DeadlineTask(String desc, Duration duration) {
		super.setDescription(desc);
		super.setType(DeadlineType);
		setDurationDetails(duration);
	}

	public Duration getDurationDetails() {
		return durationDetails;
	}

	public void setDurationDetails(Duration durationDetails) {
		this.durationDetails = durationDetails;
	}
	
	
}
