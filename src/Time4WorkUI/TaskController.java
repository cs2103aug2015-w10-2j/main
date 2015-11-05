package Time4WorkUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
import Time4WorkStorage.Tasks;
import Time4WorkUI.DateDisplay;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import Time4WorkUI.UserInput;
import Time4WorkUI.FullValueCell;

//@@author: A0112077N
public class TaskController {

	private static Logic logic = new Logic();
	private static UserInput userInput;

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
	private static final String NO_CONTENT_TABLE_MESSAGE = "You have no tasks.";

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 *
	 * Initializes the table columns and sets up initial interface
	 *
	 * @throws Exception
	 */
	@FXML
	private void initialize() throws Exception {
		initColumn();
		initTableView();
		setUpUI();
		handleUserInput();
	}

	public void handleUserInput() {
		userCommand.setPromptText(PROMPT_USERCOMMAND_TEXT);
		feedback.setText("Commands: add, delete, update, display, search, undo, store");

		userCommand.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				userInput = new UserInput(userCommand.getText());
				upStack.push(userInput.getUserInput());
				userCommand.setText(PROMPT_USERCOMMAND_CLEAR);

				FeedbackMessage output;
				try {
					output = getOutputFromLogic(userInput.getUserInput());
					currentList = logic.getIncompleteTaskList();

					//show the list of completed tasks for "display archive" cmd
					if (userInput.isDisplayArchiveCommand()) {
						currentList = output.getCompleteTaskList();
					} else {
						currentList = output.getIncompleteTaskList();
					}

					taskTable.setItems(getObservableTaskList(currentList));
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


	/**
	 * Get the observableList to display in UI
	 * @param currentList
	 * @return taskData
	 */
	public ObservableList<TaskModel> getObservableTaskList(ArrayList<Tasks> currentList) {
		ObservableList<TaskModel> taskData = FXCollections.observableArrayList();
		DateDisplay display = new DateDisplay();
		for (int i = 0; i < currentList.size(); i++) {
			Tasks currentTask = currentList.get(i);
			taskData.add(new TaskModel(i + 1, currentTask.getDescription(), display.getStartDuration(currentTask),
					display.getEndDuration(currentTask)));
		}
		return taskData;
	}

	public void initColumn() {
		// Initialize the columns.
		indexCol.getStyleClass().add("align-center");
		toCol.getStyleClass().add("align-center");
		fromCol.getStyleClass().add("align-center");
		taskTitle.getStyleClass().add("taskTitle");

		indexCol.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.05));
		descriptionCol.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.55));
		fromCol.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.20));
		toCol.prefWidthProperty().bind(taskTable.widthProperty().multiply(0.20));

		indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
		descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		fromCol.setCellValueFactory(new PropertyValueFactory<>("startDuration"));
		toCol.setCellValueFactory(new PropertyValueFactory<>("endDuration"));

		descriptionCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TaskModel,String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<TaskModel, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getDescription());
            }
        });


		 descriptionCol.setCellFactory(new Callback<TableColumn<TaskModel,String>, TableCell<TaskModel,String>>() {
	            @Override
	            public TableCell<TaskModel, String> call(TableColumn<TaskModel, String> param) {
	                return new FullValueCell();
	            }
	        });
	}

	public void initTableView() {
		Label noContentLabel = new Label(NO_CONTENT_TABLE_MESSAGE);
		taskTable.setPlaceholder(noContentLabel);
	}

	public void setUpUI() throws IOException {
		currentList = logic.getIncompleteTaskList();
		taskTable.setItems(getObservableTaskList(currentList));
	}

	public FeedbackMessage getOutputFromLogic(String userCommand) throws Exception {
		return logic.executeCommand(userCommand);
	}

	public static ArrayList<Tasks> getDisplayedList() {
		return currentList;
	}
}
