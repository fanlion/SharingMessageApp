package sharing.ui.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;

import org.controlsfx.control.Notifications;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import sharing.code.key.GenPublicPrimeP;
import sharing.code.key.PrivateKey;
import sharing.code.key.PrivateKeyFactory;

public class KeyManageController {
	private MainAppController mainAppController;

	public void setMainAppController(MainAppController mainAppController) {
		this.mainAppController = mainAppController;
	}

	@FXML
	private TextField fileKeyTF;
	@FXML
	private TextField valueKeyTF;
	@FXML
	private Button genKeyBtn;

	@FXML
	private void handleGenKeyBtn() {
		String fileKey = fileKeyTF.getText().trim();
		String valueKey = valueKeyTF.getText().trim();
		//if all is null
		if (fileKey.equals("") && valueKey.equals("")) {
			Notifications.create().position(Pos.CENTER).title("还有参数没有填")
			.text("请确保所有参数已经填写后再操作").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else if (!fileKey.equals("") && !valueKey.equals("")) {
			Notifications.create().position(Pos.CENTER).title("请只选择一种密钥方式")
			.text("请清除其中一个输入框的值").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else if (!fileKey.equals("")){
			BigInteger P;
			try {
				P = GenPublicPrimeP.readPrimeP(new File(fileKey));
				PrivateKey key = PrivateKeyFactory.getInstance(P);
				FileChooser fc = new FileChooser();
				configureFileChooser(fc);
				fc.setTitle("请选择你要保存的地方");
				File file = fc.showSaveDialog(mainAppController.getMainApp().getPrimaryState());
				if (file != null) {
					PrivateKeyFactory.saveKey(file, key);
					Notifications.create().position(Pos.CENTER).title("密钥生成成功")
					.text("你的密钥保存在 " + file + " 路径下").darkStyle().showInformation();
				}
			} catch (FileNotFoundException e) {
				Notifications.create().position(Pos.CENTER).title("文件路径错误")
				.text("请确认文件路径是否正确后再操作").darkStyle().showInformation();
			} catch (ClassNotFoundException e) {
				Notifications.create().position(Pos.CENTER).title("该文件不是正确的大素数文件")
				.text("请确认该文件类型后操作").darkStyle().showInformation();
			} catch (IOException e) {
				Notifications.create().position(Pos.CENTER).title("未知错误")
				.text("请重试").darkStyle().showInformation();
			}

		} else if (!valueKey.equals("")) {
			BigInteger P = new BigInteger(valueKey);
			PrivateKey key = PrivateKeyFactory.getInstance(P);
			FileChooser fc = new FileChooser();
			configureFileChooser(fc);
			fc.setTitle("请选择你要保存的地方");
			File file = fc.showSaveDialog(mainAppController.getMainApp().getPrimaryState());
			if (file != null) {
				System.out.println(file);
				PrivateKeyFactory.saveKey(file, key);
				Notifications.create().position(Pos.CENTER).title("密钥生成成功")
				.text("你的密钥保存在 " + file + " 路径下").darkStyle().showInformation();
			}
		}
	}


	@FXML
	private void handleFileKeyTF() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("请选择你密钥文件");
		File file = fc.showOpenDialog(mainAppController.getMainApp().getPrimaryState());
		if (file != null) {
			fileKeyTF.setText(file.getAbsolutePath());
		}
	}

	//配置文件选择器
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Key", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/securitykey/P"));
	}


}
