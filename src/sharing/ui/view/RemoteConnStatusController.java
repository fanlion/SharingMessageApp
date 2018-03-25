package sharing.ui.view;



import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import sharing.ui.model.CerButtonTableCell;
import sharing.ui.model.ConnTask;

public class RemoteConnStatusController {
	private ObservableList<ConnTask> remoteTaskPool;

	public void setRemoteTaskPool(ObservableList<ConnTask> remoteTaskPool) {
		this.remoteTaskPool = remoteTaskPool;
		tableView.setItems(remoteTaskPool);
	}

	@FXML
	private TableColumn<ConnTask, String> sessionIdTC;
	@FXML
	private TableColumn<ConnTask, String> localAddressTC;
	@FXML
	private TableColumn<ConnTask, String> statusTC;
	@FXML
	private TableColumn<ConnTask, String> processTC;
	@FXML
	private TableView<ConnTask> tableView;


	@FXML
	public void initialize() {
		sessionIdTC.setCellValueFactory(new PropertyValueFactory<ConnTask,String>("sessionId"));
		localAddressTC.setCellValueFactory(new PropertyValueFactory<ConnTask,String>("localAddress"));
		statusTC.setCellValueFactory(new PropertyValueFactory<ConnTask,String>("status"));
		processTC.setCellValueFactory(new PropertyValueFactory<ConnTask,String>("process"));

		TableColumn<ConnTask, Boolean> cerInfoTC = new TableColumn<ConnTask, Boolean>("查看证书");
		cerInfoTC.setSortable(false);
		cerInfoTC.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<ConnTask, Boolean>, ObservableValue<Boolean>>() {
					@Override
					public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<ConnTask, Boolean> p) {
						return new SimpleBooleanProperty(p.getValue() != null);
					}
				});

		cerInfoTC.setCellFactory(new Callback<TableColumn<ConnTask, Boolean>, TableCell<ConnTask, Boolean>>() {
			@Override
			public TableCell<ConnTask, Boolean> call(TableColumn<ConnTask, Boolean> p) {
				return new CerButtonTableCell(tableView);
			}
		});
		tableView.getColumns().add(cerInfoTC);
	}
}
