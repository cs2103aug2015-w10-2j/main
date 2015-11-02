package Time4WorkStorage;

/* @@author A0125495Y */

public class DeadlineTask extends Tasks{
	
	private Duration durationDetails = null;
	private static final int DeadlineType = TaskType.DeadlineType.getTaskType();
	
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
	
	public String getTime() {
		return durationDetails.getEndTime();
	}
	
	public void setTime(String eTime) {
		this.durationDetails.setEndTime(eTime);
	}
	
	public String getDate() {
		return durationDetails.getEndDate();
	}
	
	public void setDate(String eDate) {
		this.durationDetails.setEndDate(eDate);
	}

	public Duration getDurationDetails() {
		return durationDetails;
	}

	public void setDurationDetails(Duration durationDetails) {
		this.durationDetails = durationDetails;
	}
	
	
}
