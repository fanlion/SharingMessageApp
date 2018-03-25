package sharing.ui.view;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.Dialogs;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import sharing.code.bean.DataBaseProperty;
import sharing.code.bean.KeyProperty;
import sharing.code.jdbc.ConnectDB;
import sharing.code.netty.server.NettyServer;
import sharing.ui.MainApp;
import sharing.ui.model.CheckBoxTableCell;
import sharing.ui.model.ConnTask;
import sharing.ui.model.DescTableRow;

@SuppressWarnings("deprecation")
public class OpenNativeController {
	private	ObservableList<ConnTask> taskPool;

	public void setTaskPool(ObservableList<ConnTask> taskPool) {
		this.taskPool = taskPool;
	}

	private String dbUrl;
	private String dbUser;
	private String dbPort;
	private String dbPasswd;
	private String dbName;
	private ObservableList<DescTableRow> descTableRowData = FXCollections.observableArrayList();
	private MainApp mainApp;
	private MainAppController mainAppController;

	//this controller build a NettyServer, and pass it to MainAppController
	private NettyServer server = null;

	//give server's access to MainAppController
	public void updateServerStatus() {
		mainAppController.setServer(server);
	}

	@FXML
	private Tab stepOneTab;
	@FXML
	private Tab stepTwoTab;
	@FXML
	private Tab stepThreeTab;
	@FXML
	private TextField dbUrlTextField;
	@FXML
	private TextField dbUserNameTextField;
	@FXML
	private TextField dbPortTextField;
	@FXML
	private PasswordField dbPasswordTextField;
	@FXML
	private TextField dbNameTextField;

	@FXML
	private Button resetBtn;
	@FXML
	private Button connTestBtn;
	@FXML
	private Button tabOneNextBtn;
	@FXML
	private Button tabTwoPreBtn;
	@FXML
	private Button tabTwoNextBtn;
	@FXML
	private Button tabThreePreBtn;
	@FXML
	private Button tabThreeStartBtn;
	@FXML
	private TabPane tabPane;
	@FXML
	private ChoiceBox<String> tabTwoChoiceBox;

	@FXML
	private TableColumn<DescTableRow, Boolean> commonTabTwoTableCol;
	@FXML
	private TableColumn<DescTableRow, Boolean> nuncommonTabTwoTableCol;
	@FXML
	private TableColumn<DescTableRow, String> fieldTabTwoTableCol;
	@FXML
	private TableColumn<DescTableRow, String> typeTabTwoTableCol;
	@FXML
	private TableColumn<DescTableRow, String> nullTabTwoTableCol;
	@FXML
	private TableColumn<DescTableRow, String> keyTabTwoTableCol;
	@FXML
	private TableColumn<DescTableRow, String> defaultTabTwoTableCol;
	@FXML
	private TableColumn<DescTableRow, String> extraTabTwoTableCol;
	@FXML
	private TableView<DescTableRow> tableViewTabTwo;
	@FXML
	private TextField primayKeyTextField;
	@FXML
	private TextField secondaryKeyTextField;
	@FXML
	public void initialize() {
		stepOneTab.setDisable(false);
		stepTwoTab.setDisable(true);
		stepThreeTab.setDisable(true);

		//common column
		commonTabTwoTableCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,Boolean>("common"));
		commonTabTwoTableCol.setCellFactory(new Callback<TableColumn<DescTableRow, Boolean>, TableCell<DescTableRow, Boolean>>() {
			public TableCell<DescTableRow, Boolean> call(TableColumn<DescTableRow, Boolean> p) {
				return new CheckBoxTableCell<DescTableRow, Boolean>();
			}
		});
		//nuncommon column
		nuncommonTabTwoTableCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,Boolean>("nuncommon"));
		nuncommonTabTwoTableCol.setCellFactory(new Callback<TableColumn<DescTableRow, Boolean>, TableCell<DescTableRow, Boolean>>() {
			public TableCell<DescTableRow, Boolean> call(TableColumn<DescTableRow, Boolean> p) {
				return new sharing.ui.model.CheckBoxTableCell<DescTableRow, Boolean>();
			}
		});
		//field column
		fieldTabTwoTableCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("field"));
		typeTabTwoTableCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("type"));
		nullTabTwoTableCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("isNull"));
		keyTabTwoTableCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("isKey"));
		defaultTabTwoTableCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("defaultVal"));
		extraTabTwoTableCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("extra"));

		//Ϊ�������ѡ�������¼�
		tabTwoChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> obv, String oldvalue, String newvalue) {
				PreparedStatement ps;
				Connection conn = null;
				try {
					String url = "jdbc:mysql://" + dbUrl + ":" + dbPort + "/" + dbName;
					conn = ConnectDB.getConn(url, dbUser, dbPasswd);
					if (newvalue == null) {
						ps = conn.prepareStatement("desc" + " " + oldvalue);
					} else {
						ps = conn.prepareStatement("desc" + " " + newvalue);
					}

					ResultSet rs = ps.executeQuery();
					descTableRowData.clear();
					while (rs.next()) {
						String field = rs.getString("Field");
						String type = rs.getString("Type");
						String isNull = rs.getString("Null");
						String key = rs.getString("Key");
						String defaultVal = rs.getString("Default");
						String extra = rs.getString("Extra");
						descTableRowData.add(new DescTableRow(false, false, field, type, isNull, key, defaultVal, extra));
					}
				} catch (SQLException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (conn != null) {
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		tableViewTabTwo.setItems(descTableRowData);
	}

	@FXML
	private void handleTabOneNextBtn() {
		dbUrl = dbUrlTextField.getText().trim();
		dbUser = dbUserNameTextField.getText().trim();
		dbPasswd = dbPasswordTextField.getText().trim();
		dbPort = dbPortTextField.getText().trim();
		dbName = dbNameTextField.getText().trim();
		if (dbUrl.equals("") || dbUser.equals("") ||
				dbPasswd.equals("") || dbName.equals("")) {
			Notifications.create().position(Pos.CENTER).title("���в���û����")
			.text("��ȷ�����в����Ѿ���д���ٲ���").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else {
			Connection conn =testConnToDB(false);
			stepOneTab.setDisable(true);
			stepTwoTab.setDisable(false);
			tabPane.getSelectionModel().select(stepTwoTab);
			String sql = "show tables";
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				List<String> choiceItems = new ArrayList<String>();
				while(rs.next()) {
					choiceItems.add(rs.getString(1));
				}
				tabTwoChoiceBox.getItems().clear();
				tabTwoChoiceBox.getItems().addAll(choiceItems);
				if (choiceItems.size() > 0) {
					tabTwoChoiceBox.getSelectionModel().select(choiceItems.get(0));
					ps = conn.prepareStatement("desc " + choiceItems.get(0));
					rs = ps.executeQuery();
					descTableRowData.clear();
					while (rs.next()) {
						String field = rs.getString("Field");
						String type = rs.getString("Type");
						String isNull = rs.getString("Null");
						String key = rs.getString("Key");
						String defaultVal = rs.getString("Default");
						String extra = rs.getString("Extra");
						descTableRowData.add(new DescTableRow(false, false, field, type, isNull, key, defaultVal, extra));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (conn != null)
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
	}

	@FXML
	private void handleTabTwoPreBtn() {
		stepOneTab.setDisable(false);
		stepTwoTab.setDisable(true);
		tabPane.getSelectionModel().select(stepOneTab);
	}

	@FXML
	private void handleTabTwoNextBtn() {
		Iterator<DescTableRow> it = descTableRowData.iterator();
		String commonField = null;
		List<String> nuncommonField = new ArrayList<String>();
		while (it.hasNext()) {
			DescTableRow dr = it.next();
			if (dr.getCommon()) {
				commonField = dr.getField();
			}
			if (dr.getNuncommon()) {
				nuncommonField.add(dr.getField());
			}
		}

		if (commonField == null || nuncommonField.isEmpty()) {
			Notifications.create().position(Pos.CENTER).title("����δ��д�Ĳ�����")
			.text("ȷ��ѡȡһ��������������������һ���ǹ���������").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else {
			stepTwoTab.setDisable(true);
			stepThreeTab.setDisable(false);
			tabPane.getSelectionModel().select(stepThreeTab);
		}


	}

	@FXML
	private void handleTabThreePreBtn() {
		stepThreeTab.setDisable(true);
		stepTwoTab.setDisable(false);
		tabPane.getSelectionModel().select(stepTwoTab);
	}

	@FXML
	//handle the start NettyServer button
	private void handleBuildServer() {
		Iterator<DescTableRow> it = descTableRowData.iterator();
		String common = null;
		List<String> nuncommon = new ArrayList<String>();
		while (it.hasNext()) {
			DescTableRow dr = it.next();
			if (dr.getCommon()) {
				common = dr.getField();
			}
			if (dr.getNuncommon()) {
				nuncommon.add(dr.getField());
			}
		}

		String url = dbUrlTextField.getText().trim();
		String dbname = dbNameTextField.getText().trim();
		String port = dbPortTextField.getText().trim();
		String user = dbUserNameTextField.getText().trim();
		String passwd = dbPasswordTextField.getText().trim();
		String table =  tabTwoChoiceBox.getSelectionModel().getSelectedItem();

		DataBaseProperty dbp = new DataBaseProperty(url, dbname, port, user, passwd, table, common, nuncommon);

		String primaryKeyPath = primayKeyTextField.getText().trim();
		String secondaryKeyPath = secondaryKeyTextField.getText().trim();
		File primaryKey = new File(primaryKeyPath);
		File secondaryKey = new File(secondaryKeyPath);
		KeyProperty kp = new KeyProperty(primaryKey, secondaryKey);

		this.server = new NettyServer(dbp, kp, taskPool);
		try {
			server.bind();
			//update server status to MainAppController
			updateServerStatus();
			Notifications.create().position(Pos.CENTER).title("���������ɹ�")
			.text("������ڲ鿴���ط���˵��в鿴Ŀǰ����״̬").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
			//disable openNativeButton in MainApp.fxml
			mainAppController.getOpenButton().setDisable(true);

			//redirect to NativeServerStatusOverview.fxml
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(OpenNativeController.class.getResource("NativeServerStatusOverview.fxml"));
			AnchorPane  view = (AnchorPane) loader.load();
			NativeServerStatusController controller = loader.getController();
			controller.setServer(server);
			controller.setMainAppController(mainAppController);
			controller.updatServerStatus(server);
			controller.setNativeTaskPool(taskPool);
			mainAppController.getBorderPane().setCenter(view);
		} catch (Exception e) {
			Dialogs.create()
			.title("���������쳣")
			.masthead("���������쳣")
			.lightweight()
			.message("��ȷ�����в����Ѿ���ú�����")
			.showExceptionInNewWindow(e);
			e.printStackTrace();
		}
	}



	@FXML
	//��ղ���һ�е�����
	private void handleRestBtn() {
		dbUrlTextField.setText("");
		dbUserNameTextField.setText("");
		dbPasswordTextField.setText("");
		dbNameTextField.setText("");
		dbPortTextField.setText("");
	}

	@FXML
	private void handlePrimaryKeyChoose() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("��ѡ���������Կ�ļ�");
		File file = fc.showOpenDialog(mainApp.getPrimaryState());
		if (file != null)
			primayKeyTextField.setText(file.getAbsolutePath());
	}

	@FXML
	private void handleSecondaryKeyChoose() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("��ѡ����Ĵ���Կ�ļ�");
		File file = fc.showOpenDialog(mainApp.getPrimaryState());
		if (file != null)
			secondaryKeyTextField.setText(file.toString());
	}

	//�����ļ�ѡ����
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Key", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/securitykey/"));
	}

	@FXML
	//����������ݿ����Ӱ�ť
	private void handleConnTestBtn() {
		//�������ݿ����Ӳ�������ʾ��Ϣ
		testConnToDB(true);
	}


	private Connection testConnToDB(boolean needCloseConn) {
		String url = dbUrlTextField.getText().trim();
		String user = dbUserNameTextField.getText().trim();
		String port = dbPortTextField.getText().trim();
		String passwd = dbPasswordTextField.getText().trim();
		String name = dbNameTextField.getText().trim();
		Connection conn = null;
		if (url == null || url.equals("") || user == null
				|| user.equals("") || passwd == null
				|| passwd.equals("") || name == null || name.equals("")) {
			Dialogs.create()
			.title("�������Ӳ�������")
			.masthead("��δ���")
			.message("��ȷ�����в����Ѿ���ú�����")
			.showInformation();
		} else {
			try {
				conn = ConnectDB.getConn("jdbc:mysql://" + url + ":" + port + "/" + name, user, passwd);
				Dialogs.create()
				.title("�������Ӳ�������")
				.masthead("�������ӳɹ���")
				.message("�������ӣ����Լ������к�������")
				.showInformation();
			} catch (ClassNotFoundException e) {
				Dialogs.create()
				.title("�������Ӳ�������")
				.masthead("���ݿ�������������ʧ��")
				.message("�����𻵣������°�װ")
				.showError();
			} catch (SQLException e) {
				Dialogs.create()
				.title("�������Ӳ�������")
				.masthead("���ݿ�����������")
				.message("�������������")
				.showError();
			}
			if (needCloseConn) {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					Dialogs.create()
					.title("�������Ӳ�������")
					.masthead("�������ִ���")
					.message("�ڲ��Ժ��޷��ر�������ݵ�����,��Ӱ��ʹ��")
					.showInformation();
				}
			}
		}
		return conn;
	}

	//give MainApp access for this controller
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	//give MainAppcontroller access for this controller
	public void setMainAppController(MainAppController mainAppController) {
		this.mainAppController = mainAppController;
	}
}
