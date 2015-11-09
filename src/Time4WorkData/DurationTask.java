package Time4WorkData;

/* @@author A0125495Y */

public class DurationTask extends Tasks{
	
	private Duration durationDetails = null;	
	private static final int DurationType = TaskType.DurationType.getTaskType();
	
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
	
	public String getStartTime() {
		return durationDetails.getStartTime();
	}
	
	public void setStartTime(String sTime) {
		this.durationDetails.setStartTime(sTime);
	}
	
	public String getStartDate() {
		return durationDetails.getStartDate();
	}
	
	public void setStartDate(String sDate) {
		this.durationDetails.setStartDate(sDate);
	}

	
	public String getEndTime() {
		return durationDetails.getEndTime();
	}
	
	public void setEndTime(String eTime) {
		this.durationDetails.setEndTime(eTime);
	}
	
	public String getEndDate() {
		return durationDetails.getEndDate();
	}
	
	public void setEndDate(String eDate) {
		this.durationDetails.setEndDate(eDate);
	}

	
	
}
