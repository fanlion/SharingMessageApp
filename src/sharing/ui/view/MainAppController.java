package sharing.ui.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import sharing.code.netty.server.NettyServer;
import sharing.ui.MainApp;

public class MainAppController {
	private MainApp mainApp;

	public MainApp getMainApp() {
		return mainApp;
	}

	//this server is from OpenNativeController.server
	private NettyServer server;

	public void setServer(NettyServer server) {
		this.server = server;
	}

	@FXML
	private BorderPane borderPane;
	@FXML
	private Button openButton;
	@FXML
	private Button showButton;
	@FXML
	private TitledPane nativeTitledPane;
	@FXML
	private TitledPane remoteTitlePane;
	@FXML
	private TitledPane keyTitlePane;
	@FXML
	private Button openRemoteOverviewBtn;
	@FXML
	private Button openRemoteStatusBtn;

	@FXML
	private Button openKeyManageBtn;

	@FXML
	private Button OpenVerifyOneKeyBtn;
	@FXML
	private Button openGenPrimePBtn;
	@FXML
	private Button viewPrimePBtn;

	//give openButton's access to other controller
	public Button getOpenButton() {
		return openButton;
	}

	//give borderPane's access to other controller
	public BorderPane getBorderPane() {
		return borderPane;
	}

	public MainAppController() {
	}

	@FXML
	private void initialize() {

	}

	@FXML
	private void handleViewPrimeP() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/ViewPrimePOverview.fxml"));
		try {
			AnchorPane viewPrimePOverview = (AnchorPane) loader.load();
			borderPane.setCenter(viewPrimePOverview);
			ViewPrimePController controller = loader.getController();
			controller.setMainAppController(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleOpenGenPrimeP() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/GenPrimePOverview.fxml"));
		try {
			AnchorPane genPrimePOverview = (AnchorPane) loader.load();
			borderPane.setCenter(genPrimePOverview);
			GenPrimePController controller = loader.getController();
			controller.setMainAppController(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleOpenVerifyOneKey() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/VerifyOenKeyOverview.fxml"));
		try {
			AnchorPane verifyOneKeyOverview = (AnchorPane) loader.load();
			borderPane.setCenter(verifyOneKeyOverview);
			VerifyOneKeyController controller = loader.getController();
			controller.setMainAppController(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleOpenKeyMananger() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/KeyManageOverview.fxml"));
		try {
			AnchorPane keyManageOverview = (AnchorPane) loader.load();
			borderPane.setCenter(keyManageOverview);
			KeyManageController controller = loader.getController();
			controller.setMainAppController(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleTitledPane() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/WelcomeOverview.fxml"));
		try {
			AnchorPane welcomeOverview = (AnchorPane) loader.load();
			borderPane.setCenter(welcomeOverview);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@FXML
	private void handleOpenButton() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/OpenNativeOverview.fxml"));
		try {
			AnchorPane openNativeOverview = (AnchorPane) loader.load();
			// Give the controller access to the main app
			OpenNativeController controller = loader.getController();
			controller.setMainApp(this.mainApp);
			controller.setMainAppController(this);
			controller.setTaskPool(mainApp.getNativeTaskPool());
			borderPane.setCenter(openNativeOverview);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleShowServerStatus() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/NativeServerStatusOverview.fxml"));
		try {
			AnchorPane showServerStatusOverview = (AnchorPane) loader.load();
			borderPane.setCenter(showServerStatusOverview);
			NativeServerStatusController controller = loader.getController();
			//give the server status to NativeServerStatusController
			controller.setMainAppController(this);
			//update the server status
			controller.updatServerStatus(server);
			controller.setNativeTaskPool(mainApp.getNativeTaskPool());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleRemoteConnStatus() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/RemoteConnStatusOverview.fxml"));
		try {
			AnchorPane remoteConnStatusOverview = (AnchorPane) loader.load();
			borderPane.setCenter(remoteConnStatusOverview);
			RemoteConnStatusController controller = loader.getController();
			controller.setRemoteTaskPool(mainApp.getRemoteTaskPool());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void handleOpenRemote() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainAppController.class.getResource("OpenRemoteOverview.fxml"));
		try {
			AnchorPane openRemoteOverview = loader.load();
			borderPane.setCenter(openRemoteOverview);
			OpenRemoteController controller = loader.getController();
			controller.setTaskPool(mainApp.getRemoteTaskPool());
			controller.setMainAppController(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private void handleCloseButton() {

	}

	@FXML
	private void handleShowButton() {

	}


}
