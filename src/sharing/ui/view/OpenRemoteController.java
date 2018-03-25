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
import sharing.code.netty.client.NettyClient;
import sharing.code.netty.val.NettyConstant;
import sharing.ui.MainApp;
import sharing.ui.model.CheckBoxTableCell;
import sharing.ui.model.ConnTask;
import sharing.ui.model.DescTableRow;

@SuppressWarnings("deprecation")
public class OpenRemoteController {
	private MainAppController mainAppController;
	private ObservableList<DescTableRow> descTableRowData = FXCollections.observableArrayList();
	private ObservableList<ConnTask> taskPool;


	public void setTaskPool(ObservableList<ConnTask> taskPool) {
		this.taskPool = taskPool;
	}

	public void setMainAppController(MainAppController mainAppController) {
		this.mainAppController = mainAppController;
	}

	@FXML
	private TableColumn<DescTableRow, Boolean> commonCol;
	@FXML
	private TableColumn<DescTableRow, Boolean> nuncommonCol;
	@FXML
	private TableColumn<DescTableRow, String> fieldCol;
	@FXML
	private TableColumn<DescTableRow, String> typeCol;
	@FXML
	private TableColumn<DescTableRow, String> nullCol;
	@FXML
	private TableColumn<DescTableRow, String> keyCol;
	@FXML
	private TableColumn<DescTableRow, String> defaultCol;
	@FXML
	private TableColumn<DescTableRow, String> extraCol;
	@FXML
	private TableView<DescTableRow> tableView;
	@FXML
	private ChoiceBox<String> dbChoiceBox;

	@FXML
	private TextField serverAddressTF;
	@FXML
	private TextField dbUrlTF;
	@FXML
	private TextField dbNameTF;
	@FXML
	private TextField dbPortTF;
	@FXML
	private TextField dbUserNameTF;
	@FXML
	private PasswordField dbPasswordTF;

	@FXML
	private Button resetBtn;
	@FXML
	private Button tabOneNextBtn;
	@FXML
	private Button testConnBtn;
	@FXML
	private Button tabTwoPreBtn;
	@FXML
	private Button tabTwoNextBtn;
	@FXML
	private Button tabThreePreBtn;
	@FXML
	private Button tabThreeNextBtn;
	@FXML
	private Button tabFourPreBtn;
	@FXML
	private Button startBtn;

	@FXML
	private TabPane tabPane;
	@FXML
	private Tab tabOne;
	@FXML
	private Tab tabTwo;
	@FXML
	private Tab tabThree;
	@FXML
	private Tab tabFour;

	@FXML
	private TextField primayKeyTF;
	@FXML
	private TextField secondaryKeyTF;


	@FXML
	private void handleResetBtn() {
		dbUrlTF.setText("");
		dbNameTF.setText("");
		dbPortTF.setText("");
		dbUserNameTF.setText("");
		dbPasswordTF.setText("");
	}

	@FXML
	private void initialize() {
		tabOne.setDisable(false);
		tabTwo.setDisable(true);
		tabThree.setDisable(true);
		tabFour.setDisable(true);
		//common column
		commonCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,Boolean>("common"));
		commonCol.setCellFactory(new Callback<TableColumn<DescTableRow, Boolean>, TableCell<DescTableRow, Boolean>>() {
			public TableCell<DescTableRow, Boolean> call(TableColumn<DescTableRow, Boolean> p) {
				return new CheckBoxTableCell<DescTableRow, Boolean>();
			}
		});
		//nuncommon column
		nuncommonCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,Boolean>("nuncommon"));
		nuncommonCol.setCellFactory(new Callback<TableColumn<DescTableRow, Boolean>, TableCell<DescTableRow, Boolean>>() {
			public TableCell<DescTableRow, Boolean> call(TableColumn<DescTableRow, Boolean> p) {
				return new sharing.ui.model.CheckBoxTableCell<DescTableRow, Boolean>();
			}
		});
		//field column
		fieldCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("field"));
		typeCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("type"));
		nullCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("isNull"));
		keyCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("isKey"));
		defaultCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("defaultVal"));
		extraCol.setCellValueFactory(new PropertyValueFactory<DescTableRow,String>("extra"));
		dbChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> obv, String oldvalue, String newvalue) {
				PreparedStatement ps;
				Connection conn = null;
				String dbUser = dbUserNameTF.getText().trim();
				String dbUrl = dbUrlTF.getText().trim();
				String dbPort = dbPortTF.getText().trim();
				String dbName = dbNameTF.getText().trim();
				String dbPasswd = dbPasswordTF.getText().trim();
				try {
					conn = ConnectDB.getConn("jdbc:mysql://" + dbUrl + ":" + dbPort + "/" + dbName, dbUser, dbPasswd);
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
		tableView.setItems(descTableRowData);
	}


	//配置文件选择器
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Key", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/securitykey/"));
	}

	@FXML
	private void handleTabFourPreBtn() {
		tabFour.setDisable(true);
		tabThree.setDisable(false);
		tabPane.getSelectionModel().select(tabThree);
	}

	@FXML
	private void handlePrimaryKeyChoose() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("请选择你的主密钥文件");
		File file = fc.showOpenDialog(mainAppController.getMainApp().getPrimaryState());
		if (file != null) {
			primayKeyTF.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void handleSecondaryKeyChoose() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("请选择你的次密钥文件");
		File file = fc.showOpenDialog(mainAppController.getMainApp().getPrimaryState());
		if (file != null) {
			secondaryKeyTF.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void handleTabThreePreBtn() {
		tabThree.setDisable(true);
		tabTwo.setDisable(false);
		tabPane.getSelectionModel().select(tabTwo);
	}


	@FXML
	private void handleTabThreeNextBtn() {
		tabPane.getSelectionModel().select(tabFour);
		tabThree.setDisable(true);
		tabFour.setDisable(false);
		Connection conn = null;
		String dbUser = dbUserNameTF.getText().trim();
		String dbUrl = dbUrlTF.getText().trim();
		String dbPort = dbPortTF.getText().trim();
		String dbName = dbNameTF.getText().trim();
		String dbPasswd = dbPasswordTF.getText().trim();
		String sql = "show tables";
		try {
			List<String> choiceItems = new ArrayList<String>();
			conn = ConnectDB.getConn("jdbc:mysql://" + dbUrl + ":" + dbPort + "/" + dbName, dbUser, dbPasswd);
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				choiceItems.add(rs.getString(1));
			}
			dbChoiceBox.getItems().clear();
			dbChoiceBox.getItems().addAll(choiceItems);

			if (choiceItems.size() > 0) {
				dbChoiceBox.getSelectionModel().select(choiceItems.get(0));
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
		} catch (ClassNotFoundException | SQLException e) {
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

	@FXML
	private void handleTabOneNextBtn() {
		String url =  dbUrlTF.getText().trim();
		String name = dbNameTF.getText().trim();
		String port = dbPortTF.getText().trim();
		String	user = dbUserNameTF.getText().trim();
		String passwd = dbPasswordTF.getText().trim();
		if (url.equals("") || user.equals("") || name.equals("") || port.equals("")
				||passwd.equals("")) {
			Notifications.create().position(Pos.CENTER).title("还有参数没有填")
			.text("请确保所有参数已经填写后再操作").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else {
			tabOne.setDisable(true);
			tabTwo.setDisable(false);
			tabPane.getSelectionModel().select(tabTwo);
		}
	}

	@FXML
	private void handleTabTwoPreBtn() {
		tabTwo.setDisable(true);
		tabOne.setDisable(false);
		tabPane.getSelectionModel().select(tabOne);
	}

	@FXML
	private void handleTabTwoNextBtn() {
		String serverAddress = serverAddressTF.getText().trim();
		if (serverAddress.equals("")) {
			Notifications.create().position(Pos.CENTER).title("还有参数没有填")
			.text("请确保所有参数已经填写后再操作").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else {
			tabTwo.setDisable(true);
			tabThree.setDisable(false);
			tabPane.getSelectionModel().select(tabThree);
		}
	}

	@FXML
	private void handleBuildClient() {
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

		String url = dbUrlTF.getText().trim();
		String dbname = dbNameTF.getText().trim();
		String port = dbPortTF.getText().trim();
		String user = dbUserNameTF.getText().trim();
		String passwd = dbPasswordTF.getText().trim();
		String table = dbChoiceBox.getSelectionModel().getSelectedItem();
		DataBaseProperty dbp = new DataBaseProperty(url, dbname, port, user, passwd, table, common, nuncommon);

		String primaryKeyPath = primayKeyTF.getText().trim();
		String secondaryKeyPath = secondaryKeyTF.getText().trim();
		File primaryKey = new File(primaryKeyPath);
		File secondaryKey = new File(secondaryKeyPath);
		KeyProperty kp = new KeyProperty(primaryKey, secondaryKey);

		String serverAddress = serverAddressTF.getText().trim();

		NettyClient client = new NettyClient(dbp, kp);
		try {
			client.connect(NettyConstant.PORT, serverAddress);
			taskPool.add(client.getConnTask());

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RemoteConnStatusOverview.fxml"));
			AnchorPane remoteConnStatusOverview = (AnchorPane) loader.load();
			mainAppController.getBorderPane().setCenter(remoteConnStatusOverview);
			RemoteConnStatusController controller = loader.getController();
			controller.setRemoteTaskPool(taskPool);
			Notifications.create().position(Pos.CENTER).title("远程连接成功")
			.text("你可以在远程连接管理中查看服务状态").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();

		} catch (Exception e) {
			Notifications.create().position(Pos.CENTER).title("连接失败")
			.text("连接出现了个错误，请按提示信息进行检查").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
			System.out.println(e);
			e.printStackTrace();
		}
	}

	@FXML
	private void handleTestConn() {
		String url =  dbUrlTF.getText();
		String name = dbNameTF.getText();
		String port = dbPortTF.getText();
		String	user = dbUserNameTF.getText();
		String passwd = dbPasswordTF.getText();
		Connection conn = null;
		if (url.equals("") || user.equals("") || name.equals("") || port.equals("")
				||passwd.equals("")){
			Dialogs.create()
			.title("数据连接测试提醒")
			.masthead("有未填项！")
			.message("请确保所有参数已经填好后再试")
			.showInformation();
		} else {
			try {
				conn = ConnectDB.getConn("jdbc:mysql://" + url + ":" + port + "/" + name, user, passwd);
				Dialogs.create()
				.title("数据连接测试提醒")
				.masthead("测试连接成功！")
				.message("可以连接，可以继续进行后续步骤")
				.showInformation();
			} catch (ClassNotFoundException e) {
				Dialogs.create()
				.title("数据连接测试提醒")
				.masthead("数据库连接驱动程序丢失！")
				.message("程序损坏，请重新安装")
				.showError();
			} catch (SQLException e) {
				Dialogs.create()
				.title("数据连接测试提醒")
				.masthead("数据库连参数错误！")
				.message("请检查后重新再试")
				.showError();
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					Dialogs.create()
					.title("数据连接测试提醒")
					.masthead("操作出现错误！")
					.message("在测试后无法关闭这个短暂的连接,不影响使用")
					.showInformation();
				}
			}
		}
	}
}
