package Time4WorkUI;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

//@@author: A0112077N
public class UserInterface extends Application {
	private static final String LOCATION_MAIN_WINDOW_LAYOUT = "view/Time4Work.fxml";
	private static final String LOCATION_ICON ="img/icon.png";
	private static final String WINDOW_TITLE = "Time4Work";
	private Stage primaryStage;
	private Scene scene;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle(WINDOW_TITLE);
		showWindow();
	}

	public void showWindow() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(UserInterface.class.getResource(LOCATION_MAIN_WINDOW_LAYOUT));
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(LOCATION_ICON)));
			AnchorPane page = (AnchorPane) loader.load();
			scene = new Scene(page);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the main stage.
	 *
	 * @return primaryStage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public Scene getScene(){
		return scene;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
