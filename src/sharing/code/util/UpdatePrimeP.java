package sharing.code.util;

import java.io.File;
import java.math.BigInteger;

import sharing.code.key.GenPublicPrimeP;

/**
 * ï¿½ï¿½ï¿½ï¿½ï¿½à£ºï¿½ï¿½ï¿½Ú¸ï¿½ï¿½Â¹ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½
 *
 */
public class UpdatePrimeP {

	public static void main(String[] args) {
		String userdir = System.getProperty("user.dir") + "/securitykey/";
		BigInteger P = GenPublicPrimeP.getSecurePrime(1024);
		GenPublicPrimeP.savePrimeP(new File(userdir + "P/p.dat"), P);
		System.out.println("´óËØÊý¸üÐÂ³É¹¦...");
		System.out.println("´óËØÊýµÄÖµÎª: " + P.intValue());
	}

}
