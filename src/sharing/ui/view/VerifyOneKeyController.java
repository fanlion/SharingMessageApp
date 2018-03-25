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
		fc.setTitle("��ѡ����Ҫ����У��Ĵ������ļ�");
		File file = fc.showOpenDialog(mainAppController.getMainApp().getPrimaryState());
		if (file != null) {
			pFileTF.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void handleKeyFileTF() {
		FileChooser fc = new FileChooser();
		configureFileChooser(fc);
		fc.setTitle("��ѡ����ҪУ�����Կ�ļ�");
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
			Notifications.create().position(Pos.CENTER).title("���в���û����")
			.text("��ȷ����Ҫ�����Ѿ���д���ٲ���").darkStyle().
			hideAfter(Duration.seconds(3)).showInformation();
		} else {
			if (!pFile.equals("") && !pValue.equals("")) {
				Notifications.create().position(Pos.CENTER).title("������ֻ����һ�ַ�ʽ�ṩ")
				.text("���������һ��������������ֵ").darkStyle().
				hideAfter(Duration.seconds(3)).showInformation();
			} else if (!pFile.equals("")){
				BigInteger P;
				try {
					P = GenPublicPrimeP.readPrimeP(new File(pFile));
					PrivateKey key = PrivateKeyFactory.readKey(new File(keyFile));
					boolean isOk = key.isGenByPrimeP(P);
					if (isOk) {
						Dialogs.create()
						.title("У����")
						.masthead("У�������£�")
						.message("����Կ���ɸ��������ɣ�")
						.showInformation();
					} else {
						Dialogs.create()
						.title("У����")
						.masthead("У�������£�")
						.message("����Կ�����ɸ��������ɣ�")
						.showInformation();
					}
				} catch (FileNotFoundException e) {
					Notifications.create().position(Pos.CENTER).title("�������ļ�·������")
					.text("��ȷ���ļ�·���Ƿ���ȷ���ٲ���").darkStyle().showInformation();
				} catch (ClassNotFoundException e) {
					Notifications.create().position(Pos.CENTER).title("���ļ�������ȷ�Ĵ������ļ�")
					.text("��ȷ�ϸ��ļ����ͺ����").darkStyle().showInformation();
				} catch (IOException e) {
					Notifications.create().position(Pos.CENTER).title("δ֪����")
					.text("������").darkStyle().showInformation();
				}

			} else if (!pValue.equals("")) {

				PrivateKey key = PrivateKeyFactory.readKey(new File(keyFile));
				boolean isOk = key.isGenByPrimeP(new BigInteger(pValue));
				if (isOk) {
					Dialogs.create()
					.title("У����")
					.masthead("У�������£�")
					.message("����Կ���ɸ��������ɣ�")
					.showInformation();
				} else {
					Dialogs.create()
					.title("У����")
					.masthead("У�������£�")
					.message("����Կ�����ɸ��������ɣ�")
					.showInformation();
				}
			}
		}
	}

	//�����ļ�ѡ����
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Key", "*.dat"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir") + "/securitykey/P"));
	}

}
