package sharing.ui.model;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CerButtonTableCell extends TableCell<ConnTask, Boolean> {
	final Button button = new Button("÷§ È");

	public CerButtonTableCell(final TableView<ConnTask> tableView) {
		button.setAlignment(Pos.CENTER);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				int selectdIndex = getTableRow().getIndex();
				ConnTask task = (ConnTask) tableView.getItems().get(selectdIndex);
				String cerInfo = task.cerInfoProperty().get();
				Stage cerDialog = new Stage();
				cerDialog.initModality(Modality.WINDOW_MODAL);

				Label label = new Label(cerInfo);
				AnchorPane pane = new AnchorPane(label);
				Scene cerScene = new Scene(pane);
				cerDialog.setScene(cerScene);
				cerDialog.show();
			}

		});
	}

	 @Override
     protected void updateItem(Boolean t, boolean empty) {
         super.updateItem(t, empty);
         if (!empty) {
             setGraphic(button);
         }
     }
}
