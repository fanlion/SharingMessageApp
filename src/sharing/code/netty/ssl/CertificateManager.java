package sharing.code.netty.ssl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public class CertificateManager {

	/**
	 * 利用CA证书颁发新的证书（签名）
	 * @param storepass - 存放CA证书的密钥库密码
	 * @param cakeypass - CA证书的私钥
	 * @param alias - CA证书密码
	 * @param caStoreName - 存放密钥库文件的名字
	 * @param cerFile - 待签发的证书
	 * @param newStorePath - 新签发的证书保存的密钥库
	 * @param newStorePass - 新签发证书保存的密钥库的密码
	 */
	public static void publishCer(String storepass, String cakeypass, String alias,
			String caStoreName, String cerPath, String singedCerAlias, String newStorePath, String newStorePass)
					throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
					IOException, UnrecoverableKeyException, InvalidKeyException, NoSuchProviderException, SignatureException {
		FileInputStream in = new FileInputStream(caStoreName);
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(in, storepass.toCharArray());
		//获取CA的证书
		java.security.cert.Certificate c1 = ks.getCertificate(alias);
		//获取CA的私钥
		PrivateKey caprk = (PrivateKey)ks.getKey(alias, cakeypass.toCharArray());
		in.close();

		//从CA的证书中提取签发者信息
		byte[] encod1 = c1.getEncoded();
		X509CertImpl cimpl = new X509CertImpl(encod1);
		X509CertInfo cinfol = (X509CertInfo)cimpl.get(X509CertImpl.NAME + "." + X509CertImpl.INFO);
		X500Name issuer = (X500Name)cinfol.get(X509CertInfo.SUBJECT + "." + CertificateIssuerName.DN_NAME);


		//获取待签发的证书
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		FileInputStream in2 = new FileInputStream(cerPath);
		java.security.cert.Certificate c2 = cf.generateCertificate(in2);
		in2.close();
		byte[] encod2 = c2.getEncoded();
		X509CertImpl cimp2 = new X509CertImpl(encod2);
		X509CertInfo cinfo2 = (X509CertInfo) cimp2.get(X509CertImpl.NAME + "." + X509CertImpl.INFO);

		//设置新证书的有效期
		Date begindate = new Date();
		Date endate = new Date(begindate.getTime() + 3000 * 24 * 60 * 60 *1000L);
		CertificateValidity cv = new CertificateValidity(begindate, endate);
		cinfo2.set(X509CertInfo.VALIDITY, cv);
		//设置新证书的序列号
		int sn = (int)(begindate.getTime() / 1000);
		CertificateSerialNumber csn = new CertificateSerialNumber(sn);
		cinfo2.set(X509CertInfo.SERIAL_NUMBER, csn);
		//设置新证书签发者
		cinfo2.set(X509CertInfo.ISSUER + "." + CertificateIssuerName.DN_NAME, issuer);
		//设置新证书算法
		AlgorithmId algorithm = new AlgorithmId(AlgorithmId.md5WithRSAEncryption_oid);
		cinfo2.set(CertificateAlgorithmId.NAME + "." + CertificateAlgorithmId.ALGORITHM, algorithm);
		//创建证书
		X509CertImpl newcert = new X509CertImpl(cinfo2);

		//签名
		newcert.sign(caprk, "MD5WithRSA");
		System.out.println(newcert);

		//存入密钥库
		ks.setCertificateEntry(singedCerAlias, newcert);
		FileOutputStream out = new FileOutputStream(newStorePath);
		ks.store(out, newStorePass.toCharArray());
		out.close();
	}

	/**
	 * 验证指定路径的证书日期是否合法
	 * @param certPath - 证书路径
	 * @return - true证书日期合法，false证书过期或未生效
	 * @throws IOException
	 */
	public static boolean checkCertValid(String certPath) throws IOException {
		boolean isOk = false;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			//读入证书
			FileInputStream in = new FileInputStream(certPath);
			java.security.cert.Certificate c = cf.generateCertificate(in);
			in.close();

			X509Certificate t = (X509Certificate) c;
			try {
				t.checkValidity();
				isOk = true;
			} catch (CertificateExpiredException e) {
				System.out.println("证书过期...");
				isOk = false;
			} catch (CertificateNotYetValidException e) {
				System.out.println("证书尚未生效...");
				isOk = false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("证书路径下的证书不存在...");
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return isOk;
	}

	/**
	 * 验证某个证书是否由指定的CA签发
	 * @param caCert - CA证书
	 * @param needCheckCert - 需要被验证的证书
	 * @return - true，false
	 */
	public static boolean checkCertSign(String caCert, String needCheckCert) {
		boolean pass = false;
		try {
			//CA的证书
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			FileInputStream in1 = new FileInputStream(caCert);
			java.security.cert.Certificate cac = cf.generateCertificate(in1);
			in1.close();

			//待检测的证书
			FileInputStream in2 = new FileInputStream(needCheckCert);
			java.security.cert.Certificate c = cf.generateCertificate(in2);

			//获取CA公钥
			PublicKey capbk = cac.getPublicKey();
			try {
				c.verify(capbk);
				pass = true;
			} catch(Exception e) {
				pass = false;
				System.out.println(e);
			}
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("指定路径下的证书不存在!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pass;
	}

	public static void main(String[] args) throws IOException {
		//		try {
		//			CertificateManager.publishCer("raspberry", "raspberry", "smca", "C:\\Users\\李繁\\smkeystore", "C:\\Users\\李繁\\smclient.cer", "signedsmclient", "C:\\Users\\李繁\\smkeystore", "raspberry");
		//			System.out.println("client的证书签名完成........");
		//			CertificateManager.publishCer("raspberry", "raspberry", "smca", "C:\\Users\\李繁\\smkeystore", "C:\\Users\\李繁\\smserver.cer", "signedsmserver", "C:\\Users\\李繁\\smkeystore", "raspberry");
		//			System.out.println("server的证书签名完成........");
		//		} catch (UnrecoverableKeyException | InvalidKeyException
		//				| KeyStoreException | NoSuchAlgorithmException
		//				| CertificateException | NoSuchProviderException
		//				| SignatureException | IOException e) {
		//			e.printStackTrace();
		//		}

		boolean flag = CertificateManager.checkCertValid("C:\\Users\\李繁\\signedsmserver.cer");
		//C:\Users\李繁\signedsmserver.cer
		System.out.println(flag);

		boolean pass1 = CertificateManager.checkCertSign("C:\\Users\\李繁\\SharingMessageRootCertificateAuthority2016.cer", "C:\\Users\\李繁\\signedsmserver.cer");
		System.out.println(pass1);
		boolean pass2 = CertificateManager.checkCertSign("C:\\Users\\李繁\\signedsmclient.cer", "C:\\Users\\李繁\\signedsmserver.cer");
		System.out.println(pass2);
	}

}
