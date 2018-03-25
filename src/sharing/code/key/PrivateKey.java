package sharing.code.key;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 绉侀挜閿熸磥锛岄敓鏂ゆ嫹閿熸嵎鐧告嫹閿熸枻鎷烽敓渚ヨ揪鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷蜂竴閿熸枻鎷风閽�(a,b閿熻鎲嬫嫹閿熸枻鎷烽敓鑺傜》鎷烽敓鏉板拰鏂ゆ嫹閿熸枻鎷�)
 * @author lifan
 */
public class PrivateKey implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private BigInteger a; //閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽挜
	private BigInteger b; //閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽挜
	private BigInteger p; //閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
	private SecureRandom random ;
	private static final String ALGORITHM2 ="SHA1PRNG";

	public String toString() {
		return "a: " + a + "\n" + "b: " + b + "\n" + "p: " + p;

	}

	/**
	 * 閿熸枻鎷烽敓鏉扮尨鎷烽敓鏂ゆ嫹
	 * @param plainttext - 閿熸枻鎷烽敓鏂ゆ嫹
	 * @param key - 閿熸枻鎷烽挜
	 * @return - BigInteger閿熸枻鎷烽敓鏂ゆ嫹
	 */
	public static BigInteger encryp(BigInteger plainttext, PrivateKey key) {
		if (plainttext.compareTo(BigInteger.ZERO) == -1) {
			plainttext = plainttext.negate();
			return plainttext.modPow(key.a, key.p).negate();
		} else {
			return plainttext.modPow(key.a, key.p);
		}
	}

	/**
	 * 閿熸枻鎷烽敓鏉扮尨鎷烽敓鏂ゆ嫹
	 * @param plainttext - 閿熸枻鎷烽敓鏂ゆ嫹
	 * @param key - 閿熸枻鎷烽挜
	 * @return - BigInteger閿熸枻鎷烽敓鏂ゆ嫹
	 */
	public static BigInteger encryp(byte[] plainttext, PrivateKey key) {
		BigInteger temp = new BigInteger(plainttext);
		if (temp.compareTo(BigInteger.ZERO) == -1) {//閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰彇閿熸枻鎷烽敓鏂ゆ嫹閿熻妭纭锋嫹閿熸枻鎷�
			temp = temp.negate(); // 鍙栭敓鏂ゆ嫹
			return temp.modPow(key.a, key.p).negate(); //閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹閿熻緝闈╂嫹閿熸枻鎷�
		} else {
			return temp.modPow(key.a, key.p); //閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹閿熷�熷閿熸枻鎷风洿閿熸帴纭锋嫹閿熸枻鎷�
		}

	}

	/**
	 * 閿熸枻鎷烽敓鏉扮尨鎷烽敓鏂ゆ嫹
	 * @param ciphertext - 閿熸枻鎷烽敓鏂ゆ嫹
	 * @param key - 閿熸枻鎷烽挜
	 * @return - BigInteger閿熸枻鎷烽敓鏂ゆ嫹
	 */
	public static BigInteger decryp(BigInteger ciphertext, PrivateKey key) {
		if (ciphertext.compareTo(BigInteger.ZERO) == -1) { //閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰彇閿熸枻鎷烽敓鏂ゆ嫹閿熻妭纭锋嫹閿熸枻鎷�
			ciphertext = ciphertext.negate(); // 鍙栭敓鏂ゆ嫹
			return ciphertext.modPow(key.b, key.p).negate(); //閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹閿熻緝闈╂嫹閿熸枻鎷�
		} else {
			return ciphertext.modPow(key.b, key.p); //閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹閿熷�熷閿熸枻鎷风洿閿熸帴纭锋嫹閿熸枻鎷�
		}
	}


	//閿熸枻鎷烽敓鏂ゆ嫹绉侀挜
	PrivateKey(BigInteger p) {
		try {
			this.random = SecureRandom.getInstance(ALGORITHM2);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] randomBytes = new byte[64];
		BigInteger tempa = null;
		this.p = p;
		while(true){
			this.random.nextBytes(randomBytes);
			tempa = new BigInteger(randomBytes);
			//閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷锋閿熸枻鎷峰�奸敓鏂ゆ嫹閿熺殕纭锋嫹閿熻娇锟�1閿熸枻鎷峰�奸敓鏂ゆ嫹涓篴閿熸枻鎷烽敓鏂ゆ嫹
			if(((tempa.gcd(p.subtract(BigInteger.ONE))).compareTo(BigInteger.ONE))==0 && tempa.compareTo(p)== -1){
				this.a = tempa;
				break;
			}
		}
		// 閿熸枻鎷穊,b.a = 1(mod p)
		this.b = this.a.modInverse(p.subtract(BigInteger.ONE));
	}


	/**
	 * 閿熸枻鎷烽敓鏂ゆ嫹鏍￠敓鏂ゆ嫹閿熸枻鎷烽挜閿熻鍑ゆ嫹閿熸枻鎷锋寚閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�
	 * @param key - 瑕侀敓鏂ゆ嫹鏍￠敓鏂ゆ嫹閿熸枻鎷烽敓鐨嗭拷
	 * @param p - 鎸囬敓鏂ゆ嫹閿熶茎杈炬嫹閿熸枻鎷烽敓鏂ゆ嫹
	 * @return - 閿熻鍑ゆ嫹閿熸枻鎷穞rue閿熸枻鎷烽敓浠婅繑浼欐嫹false
	 */
	public boolean isCommonPrimePKey(PrivateKey key, BigInteger p) {
		if (key.p.compareTo(p) == 0)
			return true;
		else
			return false;
	}

	public boolean isGenByPrimeP(BigInteger P) {
		if (this.p.equals(p)) {
			return true;
		} else {
			return false;
		}
	}
}
