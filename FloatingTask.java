
public class FloatingTask extends Tasks{
	
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
