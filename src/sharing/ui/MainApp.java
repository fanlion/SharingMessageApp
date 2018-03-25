package sharing.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import sharing.ui.model.ConnTask;
import sharing.ui.view.MainAppController;

public class MainApp extends Application {
	private Stage primaryStage;
	private BorderPane rootLayout;
	private static ObservableList<ConnTask> remoteTaskPool = FXCollections.observableArrayList();
	private static ObservableList<ConnTask> nativeTaskPool = FXCollections.observableArrayList();


	public ObservableList<ConnTask> getRemoteTaskPool() {
		return remoteTaskPool;
	}

	public ObservableList<ConnTask> getNativeTaskPool() {
		return nativeTaskPool;
	}



	public static void main(String[] args) {
		launch(args);
	}



	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("数据库数据共享");
		this.primaryStage.centerOnScreen();
		this.primaryStage.setResizable(false);
		initRootLayout();
		showWelcomeOverview();
	}


	public void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainApp.fxml"));
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout);
			MainAppController controller = loader.getController();
			// Give the controller access to the main app
			controller.setMainApp(this);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//展示欢迎页面
	public void showWelcomeOverview() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource("view/WelcomeOverview.fxml"));
		try {
			AnchorPane welcomeOverview = (AnchorPane) loader.load();
			rootLayout.setCenter(welcomeOverview);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public Stage getPrimaryState() {
		return primaryStage;
	}

}




