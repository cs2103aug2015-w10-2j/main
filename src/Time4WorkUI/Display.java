package Time4WorkUI;

import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

public class Display {

	public Display(){

	}

	public String getStartDuration(Tasks task) {
		Duration taskDuration = new Duration("", "", "", "");
		String startDuration = "";
		if (task instanceof DeadlineTask) {
			// DeadlineTask classifiedTask = (DeadlineTask) task;
			// taskDuration = classifiedTask.getDurationDetails();
			return "";
		} else if (task instanceof DurationTask) {
			DurationTask classifiedTask = (DurationTask) task;
			taskDuration = classifiedTask.getDurationDetails();
		} else if (task instanceof FloatingTask) {
			return "";
		}
		String date = taskDuration.getStartDate();
		String time = taskDuration.getStartTime();
		String dateDisplay = date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4);

		if (time.length() < 3) {
			time += "00";
		}

		String dateDisplay = dateFormatter(date);
		startDuration += dateDisplay + "  " + timeDisplay;
		return startDuration;
	}

	public String getEndDuration(Tasks task) {
		Duration taskDuration = new Duration("", "", "", "");
		String endDuration = "";
		if (task instanceof DeadlineTask) {
			DeadlineTask classifiedTask = (DeadlineTask) task;
			taskDuration = classifiedTask.getDurationDetails();
		} else if (task instanceof DurationTask) {
			DurationTask classifiedTask = (DurationTask) task;
			taskDuration = classifiedTask.getDurationDetails();
		} else if (task instanceof FloatingTask) {
			return "";
		}
		String date = taskDuration.getEndDate();
		String time = taskDuration.getEndTime();

		if (time.length() < 3) {
			time += "00";
		}

		String dateDisplay = dateFormatter(date);
		String timeDisplay = time.substring(0, 2) + ":" + time.substring(2, 4);
		endDuration += dateDisplay + "  " + timeDisplay;
		return endDuration;
	}

	public String dateFormatter(String dateInString){
		String dateFormatted = "";
		SimpleDateFormat prevformatter = new SimpleDateFormat("ddMMyy");
		SimpleDateFormat posformatter = new SimpleDateFormat("dd MMM yy");

		try{
			Date date = prevformatter.parse(dateInString);
			dateFormatted = posformatter.format(date);
			logger.log(Level.INFO, dateFormatted);
		} catch(ParseException e){
			e.printStackTrace();
		}
		return dateFormatted;
	}
}

