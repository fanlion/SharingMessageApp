package sharing.code.key;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;


/**
 * 閿熸枻鎷烽敓鏂ゆ嫹涓�閿熸枻鎷烽敓鏂ゆ嫹鍏ㄩ敓渚ヨ揪鎷烽敓鏂ゆ嫹閿熸枻鎷�,閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鐭尅鎷疯皳閿熸枻鎷烽敓鏂ゆ嫹涓�閿熸枻鎷�
 *
 */
public class GenPublicPrimeP {
	private static final String ALGORITHM = "RSA"; // 閿熸枻鎷烽敓鏂ゆ嫹RSA閿熷娉曢敓鏂ゆ嫹閿熸枻鎷峰彇閿熸枻鎷蜂竴閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
	private KeyPairGenerator kpg;
	private PrivateKey pk;
	private RSAPrivateCrtKey rsapck;
	private BigInteger P;					//閿熸枻鎷峰叏閿熶茎杈炬嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷锋ā閿熸枻鎷烽敓鏂ゆ嫹

	private GenPublicPrimeP(int sed) {
		try {
			this.kpg = KeyPairGenerator.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		init(1024);
	}

	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹涓�閿熸枻鎷烽敓鏂ゆ嫹鍏ㄩ敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
	 * @param sed - 鎸囬敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
	 * @return - 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
	 */
	public static BigInteger getSecurePrime(int sed) {
		return new GenPublicPrimeP(sed).P;
	}

	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰閿熸枻鎷�
	 * @param sed - 涓�閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
	 */
	private void init(int sed){
		SecureRandom sr = new SecureRandom();
		this.kpg.initialize(sed, sr);
		KeyPair kpair = this.kpg.genKeyPair();
		this.pk = kpair.getPrivate();
		this.rsapck = (RSAPrivateCrtKey) (RSAPrivateKey) this.pk;
		this.P = this.rsapck.getPrimeP();		//妯￠敓鏂ゆ嫹
	}

	/**
	 * 閿熸枻鎷蜂竴閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鎸囬敓鏂ゆ嫹閿熶茎纭锋嫹閿熸枻鎷�
	 * @param file - 鎸囬敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鍕熼敓锟�
	 * @param P - 瑕侀敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
	 */
	public static void savePrimeP(File file, BigInteger P) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file));
			oos.writeObject(P);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 閿熸枻鎷锋寚閿熸枻鎷烽敓鏂ゆ嫹閿熶茎纭锋嫹閿熷彨璁规嫹鍙栦竴閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
	 * @param file - 閿熸枻鎷风朝閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹鍕熼敓鏂ゆ嫹閿燂拷
	 * @return - 瑕侀敓鏂ゆ嫹閿熸枻鎷峰彇閿熶茎杈炬嫹閿熸枻鎷烽敓鏂ゆ嫹
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 */
	public static BigInteger readPrimeP(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		BigInteger p = null;
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		p =  (BigInteger) ois.readObject();
		ois.close();
		return p;

	}
}
