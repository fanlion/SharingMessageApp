package sharing.code.util;

import java.io.File;
import java.math.BigInteger;

import sharing.code.key.GenPublicPrimeP;

/**
 * �����ࣺ���ڸ��¹���������
 *
 */
public class UpdatePrimeP {

	public static void main(String[] args) {
		String userdir = System.getProperty("user.dir") + "/securitykey/";
		BigInteger P = GenPublicPrimeP.getSecurePrime(1024);
		GenPublicPrimeP.savePrimeP(new File(userdir + "P/p.dat"), P);
		System.out.println("���������³ɹ�...");
		System.out.println("��������ֵΪ: " + P.intValue());
	}

}
