package sharing.code.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;

import sharing.code.key.GenPublicPrimeP;
import sharing.code.key.PrivateKey;
import sharing.code.key.PrivateKeyFactory;

/**
 * �����ࣺ���ڸ��¿ͻ��˵�������Կ
 */
public class UpdateClientKey {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
		String userdir = System.getProperty("user.dir") + "/securitykey/";
		BigInteger P = GenPublicPrimeP.readPrimeP(new File(userdir + "P/p.dat"));
		PrivateKey e1 = PrivateKeyFactory.getInstance(P);
		PrivateKey e2 = PrivateKeyFactory.getInstance(P);
		PrivateKeyFactory.saveKey(new File(userdir + "R/Re1.dat"), e1);
		PrivateKeyFactory.saveKey(new File(userdir + "P/Re2.dat"), e2);
		System.out.println("�ͻ�����Կ���³ɹ�...");
	}
}
