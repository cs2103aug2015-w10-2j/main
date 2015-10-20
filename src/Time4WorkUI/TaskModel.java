package Time4WorkUI;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaskModel {
	private final IntegerProperty index;
	private final StringProperty description;
	private final StringProperty startDuration;
	private final StringProperty endDuration;

	public TaskModel(int index, String description, String startDuration, String endDuration) {
		this.index = new SimpleIntegerProperty(index);
		this.description = new SimpleStringProperty(description);
		this.startDuration = new SimpleStringProperty(startDuration);
		this.endDuration = new SimpleStringProperty(endDuration);
	}

	public int getIndex() {
		return index.get();
	}

	public void setIndex(int index) {
		this.index.set(index);
	}

	public IntegerProperty indexProperty() {
		return index;
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public String getStartDuration() {
		return startDuration.get();
	}

	public void setStartDuration(String startDuration) {
		this.startDuration.set(startDuration);
	}

	public StringProperty startDurationProperty() {
		return startDuration;
	}

	public String getEndDuration() {
		return endDuration.get();
	}

	public void setEndDuration(String endDuration) {
		this.endDuration.set(endDuration);
	}

	public StringProperty endDurationProperty() {
		return endDuration;
	}
}
