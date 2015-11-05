package Time4WorkUI;

import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;

public class FullValueCell extends TableCell<TaskModel, String> {
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            this.setText(item);
            this.setTooltip(
                    (empty || item==null) ? null : new Tooltip(item));
        }
}
