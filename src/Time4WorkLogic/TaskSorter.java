package Time4WorkLogic;

import java.util.ArrayList;

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
	
	
	public ArrayList<Tasks> sortTask (boolean sortIncomplete) {
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
				if (sortIncomplete = true) {
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
	private ArrayList<Tasks> compareTask (Tasks taskA, Tasks taskB) {
		ArrayList<Tasks> comparedTaskList = new ArrayList<Tasks>();
		ArrayList<Integer> taskDateAndTimeA = getTaskDateAndTimeForCompare(taskA);
		ArrayList<Integer> taskDateAndTimeB = getTaskDateAndTimeForCompare(taskB);
		
		int taskDateA = taskDateAndTimeA.get(0);
		int taskTimeA = taskDateAndTimeA.get(1);
		int taskDateB = taskDateAndTimeB.get(0);
		int taskTimeB = taskDateAndTimeB.get(1);
		
		if (taskDateA < taskDateB || (taskDateA == taskDateB && taskTimeA < taskTimeB)) {
			comparedTaskList.add(taskA);
			comparedTaskList.add(taskB);
		} else {
			comparedTaskList.add(taskB);
			comparedTaskList.add(taskA);
		}
		
		return comparedTaskList;
		
	}
	
	private ArrayList<Integer> getTaskDateAndTimeForCompare (Tasks task) {
		ArrayList<Integer> taskDateAndTime = new ArrayList<Integer>();
		String taskDateInString = "";
		String taskTimeInString = "";

		if (task instanceof DeadlineTask) {
			DeadlineTask classifiedTask = (DeadlineTask) task;
			taskDateInString = classifiedTask.getDate();
			taskTimeInString = classifiedTask.getTime();
		} else if (task instanceof DurationTask) {
			DurationTask classifiedTask = (DurationTask) task;
			taskDateInString = classifiedTask.getStartDate();
			taskTimeInString = classifiedTask.getStartTime();
		}
		
		taskDateInString = new StringBuffer(taskDateInString).reverse().toString();
		
		int taskDate = Integer.parseInt(taskDateInString);
		int taskTime = Integer.parseInt(taskTimeInString);
		
		taskDateAndTime.add(taskDate);
		taskDateAndTime.add(taskTime);
		
		return taskDateAndTime;
	}
}
