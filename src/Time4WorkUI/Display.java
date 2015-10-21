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

		String timeDisplay = time.substring(0, 2) + ":" + time.substring(2, 4);
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

		String dateDisplay = date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4);
		String timeDisplay = time.substring(0, 2) + ":" + time.substring(2, 4);
		endDuration += dateDisplay + "  " + timeDisplay;
		return endDuration;
	}

	/*
	 * private void displaySearch(ArrayList<Tasks> taskList) {
	 * ObservableList<TaskModel> taskData = getTaskList(taskList);
	 *
	 * // 1. Wrap the ObservableList in a FilteredList (initially display all //
	 * data). FilteredList<TaskModel> filteredData = new
	 * FilteredList<>(taskData, p -> true);
	 *
	 * // 2. Set the filter Predicate whenever the filter changes.
	 * userCommand.textProperty().addListener((observable, oldValue, newValue)
	 * -> { filteredData.setPredicate(task -> { // If filter text is empty,
	 * display all tasks. if (newValue == null || newValue.isEmpty()) { return
	 * true; }
	 *
	 * // Compare each task content with filter text. String lowerCaseFilter =
	 * newValue.toLowerCase();
	 *
	 * if (task.getStartDuration().toLowerCase().indexOf(lowerCaseFilter) != -1)
	 * { return true; // Filter matches startDuration. } else if
	 * (task.getStartDuration().toLowerCase().indexOf(lowerCaseFilter) != -1) {
	 * return true; // Filter matches startDuration. } else if
	 * (task.getDescription().toLowerCase().indexOf(lowerCaseFilter) != -1) {
	 * return true; // Filter matches description. } return false; // Does not
	 * match. }); });
	 *
	 * // 3. Wrap the FilteredList in a SortedList. SortedList<TaskModel>
	 * sortedData = new SortedList<>(filteredData);
	 *
	 * // 4. Bind the SortedList comparator to the TableView comparator. //
	 * Otherwise, sorting the TableView would have no effect.
	 * sortedData.comparatorProperty().bind(taskTable.comparatorProperty());
	 *
	 * // 5. Add sorted (and filtered) data to the table.
	 * taskTable.setItems(sortedData); }
	 */


}

