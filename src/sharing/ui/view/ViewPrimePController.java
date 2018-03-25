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
		fc.setTitle("��ѡ����Ҫ�鿴�Ĵ������ļ�");
		File file = fc.showOpenDialog(mainAppController.getMainApp().getPrimaryState());
		if (file != null) {
			pFileTF.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void handleStartBtn() {
		String pFile = pFileTF.getText().trim();
		if (pFile.equals("")) {
			Notifications.create().position(Pos.CENTER).title("���в���û����")
			.text("��ȷ����Ҫ�����Ѿ���д���ٲ���").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else {
			File file = new File(pFile);
			try {
				ObjectInputStream ooi = new ObjectInputStream(new FileInputStream(file));
				BigInteger p = (BigInteger)ooi.readObject();
				ooi.close();
				Dialogs.create()
				.title("��ȡ�Ľ��")
				.masthead("������ֵΪ��")
				.message("" + p.intValue())
				.showInformation();
			} catch (FileNotFoundException e) {
				Notifications.create().position(Pos.CENTER).title("�ļ�δ�ҵ�")
				.text("��ȷ���ļ����ں��ٲ���").darkStyle().
				hideAfter(Duration.seconds(3)).showInformation();
			} catch (IOException e) {
				Notifications.create().position(Pos.CENTER).title("�ļ��򿪴���")
				.text("������!").darkStyle().
				hideAfter(Duration.seconds(3)).showError();
			} catch (ClassNotFoundException | java.lang.ClassCastException e) {
				Notifications.create().position(Pos.CENTER).title("�ļ��򿪴���")
				.text("���ļ����Ǵ������ļ�����˶Ժ��ٲ�����").darkStyle().
				hideAfter(Duration.seconds(3)).showError();
			}

		}

	}

	//�����ļ�ѡ����
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Key", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/securitykey/P"));
	}

}
