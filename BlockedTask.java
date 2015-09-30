import java.util.ArrayList;

public class BlockedTask extends Tasks{
	
	private ArrayList<Duration> blockedDetails = null;	
	
	public BlockedTask(int taskID, String desc, ArrayList<Duration> duration) {
		super.setTaskID(taskID);
		super.setDescription(desc);
		super.setType(BlockedType);
		setBlockedDetails(duration);
	}
	
	public BlockedTask(String desc, ArrayList<Duration> duration) {
		super.setDescription(desc);
		super.setType(BlockedType);
		setBlockedDetails(duration);
	}

	public ArrayList<Duration> getBlockedDetails() {
		return blockedDetails;
	}

	public void setBlockedDetails(ArrayList<Duration> blockedDetails) {
		this.blockedDetails = blockedDetails;
	}
	
	
}
