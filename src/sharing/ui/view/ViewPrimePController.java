package sharing.ui.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigInteger;

import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Duration;

@SuppressWarnings("deprecation")
public class ViewPrimePController {
	private MainAppController mainAppController;

	public void setMainAppController(MainAppController mianAppController) {
		this.mainAppController = mianAppController;
	}

	@FXML
	private TextField pFileTF;
	@FXML
	private Button startBtn;

	@FXML
	private void handlePFileTF() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("请选择你要查看的大素数文件");
		File file = fc.showOpenDialog(mainAppController.getMainApp().getPrimaryState());
		if (file != null) {
			pFileTF.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void handleStartBtn() {
		String pFile = pFileTF.getText().trim();
		if (pFile.equals("")) {
			Notifications.create().position(Pos.CENTER).title("还有参数没有填")
			.text("请确保必要参数已经填写后再操作").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else {
			File file = new File(pFile);
			try {
				ObjectInputStream ooi = new ObjectInputStream(new FileInputStream(file));
				BigInteger p = (BigInteger)ooi.readObject();
				ooi.close();
				Dialogs.create()
				.title("获取的结果")
				.masthead("大素数值为：")
				.message("" + p.intValue())
				.showInformation();
			} catch (FileNotFoundException e) {
				Notifications.create().position(Pos.CENTER).title("文件未找到")
				.text("请确保文件存在后再操作").darkStyle().
				hideAfter(Duration.seconds(3)).showInformation();
			} catch (IOException e) {
				Notifications.create().position(Pos.CENTER).title("文件打开错误")
				.text("请重试!").darkStyle().
				hideAfter(Duration.seconds(3)).showError();
			} catch (ClassNotFoundException | java.lang.ClassCastException e) {
				Notifications.create().position(Pos.CENTER).title("文件打开错误")
				.text("该文件不是大素数文件，请核对后再操作！").darkStyle().
				hideAfter(Duration.seconds(3)).showError();
			}

		}

	}

	//配置文件选择器
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Key", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/securitykey/P"));
	}

}
