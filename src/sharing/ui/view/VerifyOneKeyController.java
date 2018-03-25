package sharing.ui.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;

import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.Dialogs;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import sharing.code.key.GenPublicPrimeP;
import sharing.code.key.PrivateKey;
import sharing.code.key.PrivateKeyFactory;

@SuppressWarnings("deprecation")
public class VerifyOneKeyController {
	private MainAppController mainAppController;
	public void setMainAppController(MainAppController mainAppController) {
		this.mainAppController = mainAppController;
	}

	@FXML
	private TextField pFileTF;
	@FXML
	private TextField pValueTF;
	@FXML
	private TextField keyFileTF;
	@FXML
	private Button startBtn;

	@FXML
	private void handlePFileTF() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("请选择你要用于校验的大素数文件");
		File file = fc.showOpenDialog(mainAppController.getMainApp().getPrimaryState());
		if (file != null) {
			pFileTF.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void handleKeyFileTF() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("请选择你要校验的密钥文件");
		File file = fc.showOpenDialog(mainAppController.getMainApp().getPrimaryState());
		if (file != null) {
			keyFileTF.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void handleStartBtn() {
		String pFile = pFileTF.getText().trim();
		String pValue = pValueTF.getText().trim();
		String keyFile = keyFileTF.getText().trim();
		if (keyFile.equals("") || (pValue.equals("") && pFile.equals(""))) {
			Notifications.create().position(Pos.CENTER).title("还有参数没有填")
			.text("请确保必要参数已经填写后再操作").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else {
			if (!pFile.equals("") && !pValue.equals("")) {
				Notifications.create().position(Pos.CENTER).title("大素数只能以一种方式提供")
				.text("请清除其中一个大素数输入框的值").darkStyle().
				hideAfter(Duration.seconds(3)).showInformation();
			} else if (!pFile.equals("")){
				BigInteger P;
				try {
					P = GenPublicPrimeP.readPrimeP(new File(pFile));
					PrivateKey key = PrivateKeyFactory.readKey(new File(keyFile));
					boolean isOk = key.isGenByPrimeP(P);
					if (isOk) {
						Dialogs.create()
						.title("校验结果")
						.masthead("校验结果如下：")
						.message("该密钥是由该素数生成！")
						.showInformation();
					} else {
						Dialogs.create()
						.title("校验结果")
						.masthead("校验结果如下：")
						.message("该密钥不是由该素数生成！")
						.showInformation();
					}
				} catch (FileNotFoundException e) {
					Notifications.create().position(Pos.CENTER).title("大素数文件路径错误")
					.text("请确认文件路径是否正确后再操作").darkStyle().showInformation();
				} catch (ClassNotFoundException e) {
					Notifications.create().position(Pos.CENTER).title("该文件不是正确的大素数文件")
					.text("请确认该文件类型后操作").darkStyle().showInformation();
				} catch (IOException e) {
					Notifications.create().position(Pos.CENTER).title("未知错误")
					.text("请重试").darkStyle().showInformation();
				}

			} else if (!pValue.equals("")) {

				PrivateKey key = PrivateKeyFactory.readKey(new File(keyFile));
				boolean isOk = key.isGenByPrimeP(new BigInteger(pValue));
				if (isOk) {
					Dialogs.create()
					.title("校验结果")
					.masthead("校验结果如下：")
					.message("该密钥是由该素数生成！")
					.showInformation();
				} else {
					Dialogs.create()
					.title("校验结果")
					.masthead("校验结果如下：")
					.message("该密钥不是由该素数生成！")
					.showInformation();
				}
			}
		}
	}

	//配置文件选择器
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Key", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/securitykey/P"));
	}

}
