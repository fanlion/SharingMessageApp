package sharing.code.fun;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * K���ܺ���
 *
 */
public class K {
	private static SecretKeyFactory kf = null;
	private static PBEParameterSpec ps = null;
	private static Cipher cp = null;

	//��̬��ʼ����
	static {
		try {
			kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
			ps = new PBEParameterSpec(new byte[] {1,2,3,4,5,6,7,8}, 1000);
			cp = Cipher.getInstance("PBEWithMD5AndDES");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}


	/**
	 * ���ܺ���(PBEWithMD5AndDES�㷨)
	 * @param ptext - ����
	 * @param key - ��Կ
	 * @return - ����
	 */
	public static byte[] encrypt(byte[] ptext, char[] key) {
		PBEKeySpec pbks = new PBEKeySpec(key);
		byte[] ctext = null; //����
		try {
			SecretKey k = kf.generateSecret(pbks);
			cp.init(Cipher.ENCRYPT_MODE, k, ps);
			ctext = cp.doFinal(ptext);
		} catch (InvalidKeySpecException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return ctext;
	}

	/**
	 * ���ܺ���(PBEWithMD5AndDES�㷨)
	 * @param ctext - ����
	 * @param key - ��Կ
	 * @return - ����
	 */
	public static byte[] decrypt(byte[] ctext, char[] key) {
		PBEKeySpec pbks = new PBEKeySpec(key);
		byte[] ptext = null; //����
		try {
			SecretKey k = kf.generateSecret(pbks);
			cp.init(Cipher.DECRYPT_MODE, k, ps);
			ptext = cp.doFinal(ctext);
		} catch (InvalidKeySpecException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return ptext;
	}


}
