package sharing.code.key;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

/**
 * 锟斤拷钥锟斤拷锟斤拷锟洁，锟斤拷锟节伙拷锟揭伙拷锟斤拷锟皆渴碉拷锟斤拷锟矫匡拷位锟饺★拷锟斤拷锟皆匡拷锟斤拷锟斤拷锟斤拷锟斤拷
 * @author lifan
 *
 */
public class PrivateKeyFactory {

	/**
	 * 锟斤拷钥锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟节革拷锟斤拷指锟斤拷锟侥达拷锟斤拷锟斤拷锟斤拷锟斤拷一锟斤拷锟斤拷钥
	 * @param p - 指锟斤拷锟侥达拷锟斤拷锟斤拷
	 * @return - PrivateKey实锟斤拷
	 */
	public static PrivateKey getInstance(BigInteger p) {
		return new PrivateKey(p);
	}

	/**
	 * 锟斤拷锟斤拷钥锟斤拷锟斤拷锟节憋拷锟斤拷
	 * @param file - 指锟斤拷要锟斤拷锟斤拷锟轿伙拷锟�
	 * @param key - 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟皆�
	 */
	public static void saveKey(File file, PrivateKey key) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(key);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e);
		}
	}

	/**
	 * 锟斤拷指锟斤拷锟侥硷拷锟叫讹拷取锟斤拷钥
	 * @param file - 锟斤拷锟斤拷锟皆匡拷锟斤拷募锟�
	 * @return - 锟斤拷钥
	 */
	public static PrivateKey readKey(File file) {
		PrivateKey key = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			key = (PrivateKey)ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return key;
	}
}
