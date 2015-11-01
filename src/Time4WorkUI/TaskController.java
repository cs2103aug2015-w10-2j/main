package Time4WorkUI;

import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import Time4WorkLogic.FeedbackMessage;
import Time4WorkLogic.Logic;
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

public class controllerTemp {

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
	private TableView<TaskModel> toDoTaskTable = new TableView<TaskModel>();
	@FXML
	private TableView<TaskModel> completedTaskTable = new TableView<TaskModel>();

	@FXML
	private TableColumn<TaskModel, Integer> indexCol;
	@FXML
	private TableColumn<TaskModel, String> descriptionCol;
	@FXML
	private TableColumn<TaskModel, String> toCol;
	@FXML
	private TableColumn<TaskModel, String> fromCol;

	@FXML
	private TableColumn<TaskModel, Integer> indexCol1;
	@FXML
	private TableColumn<TaskModel, String> descriptionCol1;
	@FXML
	private TableColumn<TaskModel, String> toCol1;
	@FXML
	private TableColumn<TaskModel, String> fromCol1;

	@FXML
	private TabPane tabPane = new TabPane();
	@FXML
	private Tab toDoTab;
	@FXML
	private Tab completedTab;


	// -----------------------------------------------------
	// Class variables
	// -----------------------------------------------------
	private ArrayList<Tasks> currentToDoList = new ArrayList<Tasks>();
	private ArrayList<Tasks> currentCompletedList = new ArrayList<Tasks>();

	private ArrayList<Tasks> currentList = new ArrayList<Tasks>();
	SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
	private static final Logger logger = Logger.getLogger(Logic.class.getName());

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
		indexCol.getStyleClass().add("align-center");
		toCol.getStyleClass().add("align-center");
		fromCol.getStyleClass().add("align-center");

		indexCol1.getStyleClass().add("align-center");
		toCol1.getStyleClass().add("align-center");
		fromCol1.getStyleClass().add("align-center");


		indexCol.setCellValueFactory(new PropertyValueFactory<>("index"));
		descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
		fromCol.setCellValueFactory(new PropertyValueFactory<>("startDuration"));
		toCol.setCellValueFactory(new PropertyValueFactory<>("endDuration"));
		toDoTaskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		indexCol1.setCellValueFactory(new PropertyValueFactory<>("index"));
		descriptionCol1.setCellValueFactory(new PropertyValueFactory<>("description"));
		fromCol1.setCellValueFactory(new PropertyValueFactory<>("startDuration"));
		toCol1.setCellValueFactory(new PropertyValueFactory<>("endDuration"));
		completedTaskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


		handleTabSelection();
		initGUI();
		handleUserInput();
	}

	public void handleUserInput() {
		userCommand.setPromptText(PROMPT_USERCOMMAND_TEXT);
		feedback.setText("Commands: add, delete, update, display, search, undo, store");
		userCommand.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ENTER)) {
				String userInput = userCommand.getText();
				
			/**
				tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
					@Override
					public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab arg2) {
						if (userInput.toLowerCase().equals("search complete")) {
							selectionModel.select(completedTab);
						} else if (userInput.toLowerCase().equals("search incomplete")) {
							selectionModel.select(toDoTab);
						}
					}
				});
			*/
				upStack.push(userInput);
				userCommand.setText(PROMPT_USERCOMMAND_CLEAR);
				FeedbackMessage output;
				try {
					output = getOutputFromLogic(userInput);
					currentToDoList = output.getIncompleteTaskList();
					currentCompletedList = output.getCompleteTaskList();

				//	 currentToDoList = logic.getIncompleteTaskFromMytaskList(logic.getFullTaskList());
				//	 currentCompletedList = logic.getCompleteTaskFromMytaskList(logic.getFullTaskList());

					toDoTaskTable.setItems(getTaskList(currentToDoList));
					completedTaskTable.setItems(getTaskList(currentCompletedList));


					toDoTab.setContent(toDoTaskTable);
					completedTab.setContent(completedTaskTable);


					feedback.setText(output.getFeedback());
					handleTabSelection();
					logger.log(Level.INFO, "toDoTab selected::" + toDoTab.isSelected());
					logger.log(Level.INFO, "completedTab selected:: " + completedTab.isSelected());
					logger.log(Level.INFO, "toDoTab selected .." + currentList.size());
					logger.log(Level.INFO, "completedTab selected:: " + currentList.size());
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

	public void initGUI() {
		currentToDoList = logic.getIncompleteTaskFromMytaskList(logic.getFullTaskList());
		currentCompletedList = logic.getCompleteTaskFromMytaskList(logic.getFullTaskList());


		toDoTaskTable.setItems(getTaskList(currentToDoList));
		completedTaskTable.setItems(getTaskList(currentCompletedList));

		toDoTab.setContent(toDoTaskTable);
		completedTab.setContent(completedTaskTable);

	}

	public FeedbackMessage getOutputFromLogic(String userCommand) throws Exception {
		return logic.executeCommand(userCommand);
	}

	public int getTaskIDFromUserInput(int userInputID) {
		Tasks requiredTask = currentList.get(userInputID - 1);
		return requiredTask.getTaskID();
	}

	@FXML
	public void toDoTabSelected(Event e) {
		currentList = currentToDoList;
	}

	@FXML
	public void completedTabSelected(Event e) {
		currentList = currentCompletedList;
	}

	public void handleTabSelection() {
		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab mostRecentlySelectedTab) {
				if (mostRecentlySelectedTab.equals(toDoTab)) {
					currentList = currentToDoList;
				}
				if (mostRecentlySelectedTab.equals(completedTab)) {
					currentList = currentCompletedList;
				}
			}
		});
	}

	public ArrayList<Tasks> getFullList(ArrayList<Tasks> incompletedList, ArrayList<Tasks> completedList){
		ArrayList<Tasks> list = new ArrayList<Tasks>();
		for(int i=0; i<incompletedList.size(); i++){
			Tasks task = incompletedList.get(i);
			list.add(task);
		}

		for(int i=0; i<completedList.size(); i++){
			Tasks task = completedList.get(i);
			list.add(task);
		}
		return list;
	}
/*
	public void handleMousePress(Event e) {
		tabPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
			
			}
		});
	}*/
}
