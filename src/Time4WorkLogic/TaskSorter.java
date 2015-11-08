package Time4WorkLogic;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;

//@@author A0133894W
public class TaskSorter {
    private ArrayList<Tasks> taskList;
    int DeadlineType = 1;
    int DurationType = 2;
    int FloatingType = 3;
    
    //Constructor
    public TaskSorter (ArrayList<Tasks> taskList) {
        this.taskList = taskList;
    }
    
    
    public ArrayList<Tasks> sortTask (boolean isIncomplete) throws ParseException {
        ArrayList<Tasks> floatingTaskList = new ArrayList<Tasks>();
        ArrayList<Tasks> tempTaskList = new ArrayList<Tasks>();
        int taskListSize = taskList.size();
        
        for (int i = 0; i < taskListSize; i++) {
            tempTaskList.add(taskList.get(i));
        }
        for (int i = 0; i < taskListSize; i++) {
            Tasks currTask = tempTaskList.get(i);
            if (currTask instanceof FloatingTask) {
                floatingTaskList.add(currTask);
                taskList.remove(currTask);
            }
        }
        
        taskListSize = taskList.size();
        for (int i = taskListSize - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                ArrayList<Tasks> comparedTaskList = compareTask(taskList.get(j),
                                                                taskList.get(j+1));
                if (isIncomplete == true) {
                    if (comparedTaskList.get(0) == taskList.get(j+1)) {
                        Tasks temp = taskList.get(j);
                        taskList.set(j, taskList.get(j+1));
                        taskList.set(j+1, temp);
                    }
                } else {
                    if (comparedTaskList.get(0) == taskList.get(j)) {
                        Tasks temp = taskList.get(j);
                        taskList.set(j, taskList.get(j+1));
                        taskList.set(j+1, temp);
                    }
                }
                
            }
        }
        
        taskList.addAll(floatingTaskList);
        return taskList;
    }
    
    /*
     * compare two task and return an ArrayList of task.
     * The first one will be the earlier task.
     */
    private ArrayList<Tasks> compareTask (Tasks taskA, Tasks taskB) throws ParseException {
        ArrayList<Tasks> comparedTaskList = new ArrayList<Tasks>();
        ArrayList<Date> taskDateAndTimeA = getTaskDateAndTimeForCompare(taskA);
        ArrayList<Date> taskDateAndTimeB = getTaskDateAndTimeForCompare(taskB);
        
        Date taskDateA = taskDateAndTimeA.get(0);
        Date taskTimeA = taskDateAndTimeA.get(1);
        Date taskDateB = taskDateAndTimeB.get(0);
        Date taskTimeB = taskDateAndTimeB.get(1);
        
        if (taskDateA.before(taskDateB) || (taskDateA.equals(taskDateB) && taskTimeA.before(taskTimeB))) {
            comparedTaskList.add(taskA);
            comparedTaskList.add(taskB);
        } else {
            comparedTaskList.add(taskB);
            comparedTaskList.add(taskA);
        }
        
        return comparedTaskList;
        
    }
    
    private ArrayList<Date> getTaskDateAndTimeForCompare (Tasks task) throws ParseException {
        ArrayList<Date> taskDateAndTime = new ArrayList<Date>();
        String taskDateInString = "";
        String taskTimeInString = "";
        Date taskDate = null;
        Date taskTime = null;
        
        DateFormat dateFormat = new SimpleDateFormat("ddMMyy");
        DateFormat timeFormat = new SimpleDateFormat("HHmm");
        if (task instanceof DeadlineTask) {
            DeadlineTask classifiedTask = (DeadlineTask) task;
            taskDateInString = classifiedTask.getDate();
            taskTimeInString = classifiedTask.getTime();
        } else if (task instanceof DurationTask) {
            DurationTask classifiedTask = (DurationTask) task;
            taskDateInString = classifiedTask.getStartDate();
            taskTimeInString = classifiedTask.getStartTime();
        }
        
        taskDate = dateFormat.parse(taskDateInString);
        taskTime = timeFormat.parse(taskTimeInString);
        
        taskDateAndTime.add(taskDate);
        taskDateAndTime.add(taskTime);
        
        return taskDateAndTime;
    }
}
