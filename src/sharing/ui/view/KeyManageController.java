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
			Notifications.create().position(Pos.CENTER).title("���в���û����")
			.text("��ȷ�����в����Ѿ���д���ٲ���").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else if (!fileKey.equals("") && !valueKey.equals("")) {
			Notifications.create().position(Pos.CENTER).title("��ֻѡ��һ����Կ��ʽ")
			.text("���������һ��������ֵ").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else if (!fileKey.equals("")){
			BigInteger P;
			try {
				P = GenPublicPrimeP.readPrimeP(new File(fileKey));
				PrivateKey key = PrivateKeyFactory.getInstance(P);
				FileChooser fc = new FileChooser();
				configureFileChooser(fc);
				fc.setTitle("��ѡ����Ҫ����ĵط�");
				File file = fc.showSaveDialog(mainAppController.getMainApp().getPrimaryState());
				if (file != null) {
					PrivateKeyFactory.saveKey(file, key);
					Notifications.create().position(Pos.CENTER).title("��Կ���ɳɹ�")
					.text("�����Կ������ " + file + " ·����").darkStyle().showInformation();
				}
			} catch (FileNotFoundException e) {
				Notifications.create().position(Pos.CENTER).title("�ļ�·������")
				.text("��ȷ���ļ�·���Ƿ���ȷ���ٲ���").darkStyle().showInformation();
			} catch (ClassNotFoundException e) {
				Notifications.create().position(Pos.CENTER).title("���ļ�������ȷ�Ĵ������ļ�")
				.text("��ȷ�ϸ��ļ����ͺ����").darkStyle().showInformation();
			} catch (IOException e) {
				Notifications.create().position(Pos.CENTER).title("δ֪����")
				.text("������").darkStyle().showInformation();
			}

		} else if (!valueKey.equals("")) {
			BigInteger P = new BigInteger(valueKey);
			PrivateKey key = PrivateKeyFactory.getInstance(P);
			FileChooser fc = new FileChooser();
			configureFileChooser(fc);
			fc.setTitle("��ѡ����Ҫ����ĵط�");
			File file = fc.showSaveDialog(mainAppController.getMainApp().getPrimaryState());
			if (file != null) {
				System.out.println(file);
				PrivateKeyFactory.saveKey(file, key);
				Notifications.create().position(Pos.CENTER).title("��Կ���ɳɹ�")
				.text("�����Կ������ " + file + " ·����").darkStyle().showInformation();
			}
		}
	}


	@FXML
	private void handleFileKeyTF() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("��ѡ������Կ�ļ�");
		File file = fc.showOpenDialog(mainAppController.getMainApp().getPrimaryState());
		if (file != null) {
			fileKeyTF.setText(file.getAbsolutePath());
		}
	}

	//�����ļ�ѡ����
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Key", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/securitykey/P"));
	}


}
