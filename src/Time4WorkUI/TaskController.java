package Time4WorkUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkStorage.Storage;
import Time4WorkStorage.Tasks;
import Time4WorkUI.DateDisplay;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import Time4WorkUI.AutoCompleteComboBoxListener;

public class TaskController {

	private static Logic logic = new Logic();
	// -----------------------------------------
	// FXML variables
	// -----------------------------------------
	@FXML
	private Label taskTitle;
	@FXML
	private VBox inputBox;
	@FXML
	private TextField userCommand;
	@FXML
	private Label feedback;
	@FXML
	private TableView<TaskModel> taskTable;

	@FXML
	private TableColumn<TaskModel, Integer> indexCol;
	@FXML
	private TableColumn<TaskModel, String> descriptionCol;
	@FXML
	private TableColumn<TaskModel, String> toCol;
	@FXML
	private TableColumn<TaskModel, String> fromCol;


	// -----------------------------------------------------
	// Class variables
	// -----------------------------------------------------
	private static ArrayList<Tasks> currentList = new ArrayList<Tasks>();
	private static final Logger logger = Logger.getLogger(TaskController.class.getName());

	/*
	 * upStack and downStack are used to store user input to support using
	 * 'UP'/'DOWN' keycode to obtain previous/post user command
	 */
	private Stack<String> upStack = new Stack<String>();
	private Stack<String> downStack = new Stack<String>();

	// -------------------------------------------------------
	// Message String
	// -------------------------------------------------------
	private static final String PROMPT_USERCOMMAND_TEXT = "Enter command";
	private static final String PROMPT_USERCOMMAND_CLEAR = "";
	private static final String TITLE_TODO_TASK = "TODO TASKS";
	private static final String TITLE_COMPLETED_TASK = "COMPLETED TASKS";


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
		indexCol.getStyleClass().add("align-center");
		toCol.getStyleClass().add("align-center");
		fromCol.getStyleClass().add("align-center");
		taskTitle.getStyleClass().add("taskTitle");

		indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
		descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		fromCol.setCellValueFactory(new PropertyValueFactory<>("startDuration"));
		toCol.setCellValueFactory(new PropertyValueFactory<>("endDuration"));
		taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		initGUI();
		handleUserInput();
	}


	public void handleUserInput() {
		userCommand.setPromptText(PROMPT_USERCOMMAND_TEXT);
		feedback.setText("Commands: add, delete, update, display, search, undo, store");

		userCommand.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				String userInput = userCommand.getText();
				upStack.push(userInput);
				userCommand.setText(PROMPT_USERCOMMAND_CLEAR);
				FeedbackMessage output;
				try {
					output = getOutputFromLogic(userInput);
					currentList = output.getIncompleteTaskList();
					if(userInput.toLowerCase().startsWith("display archive")){
						currentList = output.getCompleteTaskList();
						taskTitle.setText(TITLE_COMPLETED_TASK);
					} else if (userInput.toLowerCase().startsWith("display incomplete")) {
						currentList = output.getIncompleteTaskList();
						taskTitle.setText(TITLE_TODO_TASK);
					}
					taskTable.setItems(getTaskList(currentList));
					feedback.setText(output.getFeedback());
				} catch (Exception e1) {
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

	public ObservableList<TaskModel> getTaskList(ArrayList<Tasks> currentList) {
		ObservableList<TaskModel> taskData = FXCollections.observableArrayList();
		DateDisplay display = new DateDisplay();
		for (int i = 0; i < currentList.size(); i++) {
			Tasks currentTask = currentList.get(i);
			taskData.add(new TaskModel(i + 1, currentTask.getDescription(), display.getStartDuration(currentTask),
					display.getEndDuration(currentTask)));
		}
		return taskData;
	}

	public void initGUI() throws IOException {
		taskTitle.setText(TITLE_TODO_TASK);
		currentList = logic.getIncompleteTaskList();
		taskTable.setItems(getTaskList(currentList));
	}

	public FeedbackMessage getOutputFromLogic(String userCommand) throws Exception {
		return logic.executeCommand(userCommand);
	}

	public static ArrayList<Tasks> getDisplayedList() {
		return currentList;
	}

}
