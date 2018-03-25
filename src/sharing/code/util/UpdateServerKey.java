package sharing.code.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;

import sharing.code.key.GenPublicPrimeP;
import sharing.code.key.PrivateKey;
import sharing.code.key.PrivateKeyFactory;

/**
 * 閿熸枻鎷烽敓鏂ゆ嫹閿熸磥锛氶敓鏂ゆ嫹閿熻妭闈╂嫹閿熼摪鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熺殕锟�
 *
 */
public class UpdateServerKey {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
		String userdir = System.getProperty("user.dir") + "/securitykey/";
		BigInteger P = GenPublicPrimeP.readPrimeP(new File(userdir + "P/p.dat"));
		PrivateKey e1 = PrivateKeyFactory.getInstance(P);
		PrivateKey e2 = PrivateKeyFactory.getInstance(P);
		PrivateKeyFactory.saveKey(new File(userdir + "S/Se1.dat"), e1);
		PrivateKeyFactory.saveKey(new File(userdir + "P/Se2.dat"), e2);
		System.out.println("服务端密钥对更新成功...");
	}

}
