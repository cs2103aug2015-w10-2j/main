package Time4WorkUI;

import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        
        if (time.length() < 3) {
            time += "00";
        }
        
        startDuration += dateFormatter(date + " " + time);
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
        
        endDuration += dateFormatter(date + " " + time);
        return endDuration;
    }
    
    public String dateFormatter(String dateInString){
        String dateFormatted = "";
        SimpleDateFormat prevformatter = new SimpleDateFormat("ddMMyy HHmm");
        SimpleDateFormat posformatter = new SimpleDateFormat("dd MMM yy, HH:mm");
        
        try{
            Date date = prevformatter.parse(dateInString);
            dateFormatted = posformatter.format(date);
        } catch(ParseException e){
            e.printStackTrace();
        }
        return dateFormatted;
    }
}

