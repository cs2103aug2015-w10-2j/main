package Time4WorkUI;

import java.util.ArrayList;
import java.util.Stack;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkStorage.Tasks;
import Time4WorkUI.Display;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

public class TaskController {

	private static Logic logic = new Logic();

	// -----------------------------------------
	// FXML variables
	// -----------------------------------------
	
	@FXML
	private VBox inputBox;
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

	// -----------------------------------------------------
	// Class variables
	// -----------------------------------------------------

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
		taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		initTaskList();
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

	public ObservableList<TaskModel> getTaskList(ArrayList<Tasks> currentList) {
		ObservableList<TaskModel> taskData = FXCollections.observableArrayList();
		Display display = new Display();
		for (int i = 0; i < currentList.size(); i++) {
			taskData.add(new TaskModel(i + 1, currentList.get(i).getDescription(), display.getStartDuration(currentList.get(i)),
					display.getEndDuration(currentList.get(i))));
		}
		return taskData;
	}

	public void initTaskList() {
		taskTable.setItems(getTaskList(logic.getFullTaskList()));
	}
	
	public FeedbackMessage getOutputFromLogic(String userCommand) throws Exception {
		return logic.executeCommand(userCommand);
	}
}
