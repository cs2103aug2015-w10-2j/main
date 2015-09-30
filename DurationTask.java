
public class DurationTask extends Tasks{
	
	private Duration durationDetails = null;	
	
	public DurationTask(int taskID, String desc, Duration duration) {
		super.setTaskID(taskID);
		super.setDescription(desc);
		super.setType(DurationType);
		setDurationDetails(duration);
	}
	
	public DurationTask(String desc, Duration duration) {
		super.setDescription(desc);
		super.setType(DurationType);
		setDurationDetails(duration);
	}

	public Duration getDurationDetails() {
		return durationDetails;
	}

	public void setDurationDetails(Duration durationDetails) {
		this.durationDetails = durationDetails;
	}
	
	
}
