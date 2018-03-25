package sharing.ui.view;

import java.io.IOException;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import io.netty.util.concurrent.Future;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import sharing.code.netty.server.NettyServer;
import sharing.ui.model.CerButtonTableCell;
import sharing.ui.model.ConnTask;

@SuppressWarnings("deprecation")
public class NativeServerStatusController {

	private NettyServer server;
	private	MainAppController mainAppController;

	private ObservableList<ConnTask> nativeTaskPool;


	@FXML
	private TableColumn<ConnTask, String> sessionIdTC;
	@FXML
	private TableColumn<ConnTask, String> addressTC;
	@FXML
	private TableColumn<ConnTask, String> statusTC;
	@FXML
	private TableColumn<ConnTask, String> processTC;
	@FXML
	private TableView<ConnTask> tableView;

	public void setNativeTaskPool(ObservableList<ConnTask> nativeTaskPool) {
		this.nativeTaskPool = nativeTaskPool;
		tableView.setItems(this.nativeTaskPool);
	}

	@FXML
	public void initialize() {
		updatServerStatus(this.server);
		//Set cell factory for cells that allow editing
		sessionIdTC.setCellValueFactory(new PropertyValueFactory<ConnTask,String>("sessionId"));
		addressTC.setCellValueFactory(new PropertyValueFactory<ConnTask,String>("localAddress"));
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


	@FXML
	private Label serverStatusLabel;
	@FXML
	private Button startServerBtn;
	@FXML
	private Button stopServerBtn;


	public void setServer(NettyServer server) {
		this.server = server;
	}

	public void setMainAppController(MainAppController mainAppController) {
		this.mainAppController = mainAppController;
	}


	//this method is support for other controller change the Label status
	public void updatServerStatus(NettyServer server) {
		this.server = server;
		if (server == null) {
			serverStatusLabel.setText("服务未开启");
			serverStatusLabel.setTextFill(Color.RED);
			startServerBtn.setDisable(false);
			stopServerBtn.setDisable(true);
		} else if (server.workerGroup.isShutdown() && server.bossGroup.isShutdown()) {
			startServerBtn.setDisable(false);
			stopServerBtn.setDisable(true);
			serverStatusLabel.setText("服务未开启");
			serverStatusLabel.setTextFill(Color.RED);
		} else {
			startServerBtn.setDisable(true);
			stopServerBtn.setDisable(false);
			serverStatusLabel.setText("服务开启");
			serverStatusLabel.setTextFill(Color.GREEN);
		}
	}

	@FXML
	private void handleStartServerBtn() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(NativeServerStatusController.class.getResource("OpenNativeOverview.fxml"));
		try {
			AnchorPane view = (AnchorPane) loader.load();
			OpenNativeController controller = loader.getController();
			controller.setMainApp(mainAppController.getMainApp());
			controller.setMainAppController(mainAppController);
			mainAppController.getBorderPane().setCenter(view);
			mainAppController.getOpenButton().setDisable(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleStopServerBtn() {
		Action action = Dialogs.create()
				.title("重要提示！")
				.masthead("您在关闭服务程序，是否要继续？")
				.message("关闭服务或许导致正在进行的任务失败")
				.showConfirm();

		if (action == Dialog.ACTION_YES) {
			Future<?> bossGroupfuture = server.bossGroup.shutdownGracefully();
			Future<?> workfuture = server.workerGroup.shutdownGracefully();
			Notifications.create().position(Pos.CENTER).title("关闭服务提醒")
			.text("服务即将关闭，其关闭时间取决于机器，请耐心等待").darkStyle().
			hideAfter(Duration.seconds(5)).showInformation();
			//update server's status until all above future is closed
			while(!(bossGroupfuture.isSuccess() && workfuture.isSuccess())) {
				updatServerStatus(null);
			}
		}
	}

}
