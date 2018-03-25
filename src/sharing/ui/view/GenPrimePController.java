package sharing.ui.view;

import java.io.File;
import java.math.BigInteger;

import org.controlsfx.control.Notifications;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import sharing.code.key.GenPublicPrimeP;

public class GenPrimePController {
	private MainAppController mainAppController;

	public void setMainAppController(MainAppController mainAppController) {
		this.mainAppController = mainAppController;
	}

	@FXML
	private Button genPrimePBtn;

	@FXML
	private void handleStart() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("请选择你要保存的地方");
		File file = fc.showSaveDialog(mainAppController.getMainApp().getPrimaryState());
		if (file != null) {
			BigInteger P = GenPublicPrimeP.getSecurePrime(1024);
			GenPublicPrimeP.savePrimeP(file, P);
			Notifications.create().position(Pos.CENTER).title("大素数生成成功")
			.text("保存在 " + file + " 路径下").darkStyle().showInformation();
		}
	}

	//配置文件选择器
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Key", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/securitykey/P"));
	}

}
