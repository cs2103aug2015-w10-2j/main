package Time4WorkStorage;

/* @@author A0125495Y */

public class FloatingTask extends Tasks{
	
	private static final int FloatingType = TaskType.FloatingType.getTaskType();
	
	public FloatingTask(int taskID, String desc) {
		super.setTaskID(taskID);
		super.setDescription(desc);
		super.setType(FloatingType);
	}
	
	public FloatingTask(String desc) {
		super.setDescription(desc);
		super.setType(FloatingType);
	}

}
