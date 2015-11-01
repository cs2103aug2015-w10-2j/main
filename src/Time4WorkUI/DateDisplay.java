package Time4WorkUI;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateDisplay {
	private static final Logic logic = new Logic();
	private static final Logger logger = Logger.getLogger(Logic.class.getName());

	public DateDisplay(){
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

        startDuration += dateFormatter(date, time);
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

        endDuration += dateFormatter(date, time);
        return endDuration;
    }

	public String dateFormatter(String dateInString, String timeInString){
		String dateFormatted = "";
		String timeFormatted = "";
		SimpleDateFormat prevformatterDate = new SimpleDateFormat("ddMMyy");
		SimpleDateFormat prevformatterTime = new SimpleDateFormat("HHmm");
		SimpleDateFormat posformatterDate = new SimpleDateFormat("dd MMM yy,");
		SimpleDateFormat posformatterTime = new SimpleDateFormat("HH:mm");
		try{
			if(!dateInString.equals("")){
				Date date = prevformatterDate.parse(dateInString);
				dateFormatted = posformatterDate.format(date);
			} else {
				dateFormatted = "";
			}
			if(!timeInString.equals("")){
				Date time = prevformatterTime.parse(timeInString);
				timeFormatted = posformatterTime.format(time);
			} else {
				timeFormatted = "";
			}
		} catch(ParseException e){
			e.printStackTrace();
		}
		return dateFormatted + " " + timeFormatted;
	}

	public Date dateDurationFormatter(String dateDurationInString){
		Date dateFormatted = null;
		SimpleDateFormat prevformatter = new SimpleDateFormat("dd MMM yy, HH:mm");
		try{
			dateFormatted = prevformatter.parse(dateDurationInString);
		} catch(ParseException e){
			e.printStackTrace();
		}
		return dateFormatted;
	}
}
