package Time4WorkUI;

import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkStorage.DeadlineTask;
import Time4WorkStorage.Duration;
import Time4WorkStorage.DurationTask;
import Time4WorkStorage.FloatingTask;
import Time4WorkStorage.Tasks;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;

public class TaskController {
	@FXML
	private TextField userCommand;
	@FXML
	private Label feedback;
	@FXML
	private TableView<TaskModel> taskTable = new TableView<TaskModel>();
	@FXML
	private TableColumn<TaskModel, Integer> indexCol;
	@FXML
	private TableColumn<TaskModel, String> descriptionCol;
	@FXML
	private TableColumn<TaskModel, String> fromCol;
	@FXML
	private TableColumn<TaskModel, String> toCol;

	/* upStack and downStack are used to store user input to support
	 * using 'UP'/'DOWN' keycode to obtain previous/post user command
	 */
	private Stack<String> upStack = new Stack<String>();
	private Stack<String> downStack = new Stack<String>();

	private static Logic logic = new Logic();
	private static final Logger logger = Logger.getLogger(Logic.class.getName());
	private final String PROMPT_USERCOMMAND_TEXT = "Enter command";
	private final String PROMPT_USERCOMMAND_CLEAR = "";

	public void run() {
		initTaskList();
		userCommand.setPromptText(PROMPT_USERCOMMAND_TEXT);
		userCommand.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				String userInput = userCommand.getText();
				upStack.push(userInput);
				userCommand.setText(PROMPT_USERCOMMAND_CLEAR);
				FeedbackMessage output;
				try {
					output = getOutputFromLogic(userInput);
					ArrayList<Tasks> currentList = output.getTaskList();
					taskTable.setItems(getTaskList(currentList));
					feedback.setText(output.getFeedback());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			if (e.getCode().equals(KeyCode.UP)) {
				String currentCommand = upStack.pop();
				userCommand.setText(currentCommand);
				downStack.push(currentCommand);
			} else if (e.getCode().equals(KeyCode.DOWN)) {
				String currentCommand = downStack.pop();
				userCommand.setText(currentCommand);
				upStack.push(currentCommand);
			}

		});
	}

	public void initTaskList() {
		taskTable.setItems(getTaskList(logic.getFullTaskList()));
	}

	public ObservableList<TaskModel> getTaskList(ArrayList<Tasks> currentList) {
		ObservableList<TaskModel> taskData = FXCollections.observableArrayList();
		for (int i = 0; i < currentList.size(); i++) {
			taskData.add(new TaskModel(i + 1, currentList.get(i).getDescription(), getStartDuration(currentList.get(i)),
					getEndDuration(currentList.get(i))));
			logger.log(Level.INFO, "taskData updated.");
		}
		return taskData;
	}

	public FeedbackMessage getOutputFromLogic(String userCommand) throws Exception {
		return logic.executeCommand(userCommand);
	}

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 *
	 * Initializes the table columns and sets up sorting and filtering.
	 *
	 * @throws Exception
	 */
	@FXML
	private void initialize() throws Exception {
		// Initialize the columns.
		indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
		descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		fromCol.setCellValueFactory(new PropertyValueFactory<>("startDuration"));
		toCol.setCellValueFactory(new PropertyValueFactory<>("endDuration"));
		run();

	}

	private static String getStartDuration(Tasks task) {
		Duration taskDuration = new Duration("", "", "", "");
		String startDuration = "";
		if (task instanceof DeadlineTask) {
		//	DeadlineTask classifiedTask = (DeadlineTask) task;
		//	taskDuration = classifiedTask.getDurationDetails();
			return "";
		} else if (task instanceof DurationTask) {
			DurationTask classifiedTask = (DurationTask) task;
			taskDuration = classifiedTask.getDurationDetails();
		} else if (task instanceof FloatingTask) {
			return "";
		}
		String date = taskDuration.getEndDate();
		String time = taskDuration.getEndTime();
		String dateDisplay = date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4);

		if (time.length() < 3 ){
			time+="00";
		}

		String timeDisplay = time.substring(0, 2) + ":" + time.substring(2, 4);
		startDuration += dateDisplay + "  " + timeDisplay;
		return startDuration;
	}

	private static String getEndDuration(Tasks task) {
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

		if (time.length() < 3 ){
			time+="00";
		}

		String dateDisplay = date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4);
		String timeDisplay = time.substring(0, 2) + ":" + time.substring(2, 4);
		endDuration += dateDisplay + "  " + timeDisplay;
		return endDuration;
	}
}
