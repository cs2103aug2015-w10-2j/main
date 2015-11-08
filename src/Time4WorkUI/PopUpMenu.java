package Time4WorkUI;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//@@author: A0112077N
public class PopUpMenu {
	private Stage newStage = new Stage();
	private VBox layout = new VBox();
	private ImageView imageView = new ImageView();
	private Scene scene;
	private String LOCATION_HELP_SHEET = "img/reference.png";
	private String HELP_SHEET_TITLE = "TIME4WORK HELP SHEET";
	public ImageView getImageView() {
		imageView.setImage(new Image(PopUpMenu.class.getResource(LOCATION_HELP_SHEET).toExternalForm()));
		return imageView;
	}

	public void showMenu() {
		newStage.setTitle(HELP_SHEET_TITLE);
		// show the scene.
		layout.getChildren().addAll(createPopup());
		if (layout.getScene() == null) {
		    scene = new Scene(layout);
		    newStage.setScene(scene);
		} else {
		    newStage.setScene(layout.getScene());
		}

		scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ESCAPE) {
					newStage.close();
				}
			}
		});

		newStage.show();
	}

	private VBox createPopup() {
		VBox helpBox = new VBox();
		helpBox.setAlignment(Pos.CENTER);
		helpBox.getChildren().setAll(getImageView());
		return helpBox;
	}
}
