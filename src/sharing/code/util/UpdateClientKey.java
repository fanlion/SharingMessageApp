package sharing.code.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;

import sharing.code.key.GenPublicPrimeP;
import sharing.code.key.PrivateKey;
import sharing.code.key.PrivateKeyFactory;

/**
 * ï¿½ï¿½ï¿½ï¿½ï¿½à£ºï¿½ï¿½ï¿½Ú¸ï¿½ï¿½Â¿Í»ï¿½ï¿½Ëµï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½Ô¿
 */
public class UpdateClientKey {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
		String userdir = System.getProperty("user.dir") + "/securitykey/";
		BigInteger P = GenPublicPrimeP.readPrimeP(new File(userdir + "P/p.dat"));
		PrivateKey e1 = PrivateKeyFactory.getInstance(P);
		PrivateKey e2 = PrivateKeyFactory.getInstance(P);
		PrivateKeyFactory.saveKey(new File(userdir + "R/Re1.dat"), e1);
		PrivateKeyFactory.saveKey(new File(userdir + "P/Re2.dat"), e2);
		System.out.println("¿Í»§¶ËÃÜÔ¿¸üÐÂ³É¹¦...");
	}
}
