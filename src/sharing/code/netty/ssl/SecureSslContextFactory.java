package sharing.code.netty.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public final class SecureSslContextFactory {

	private static final String PROTOCOL = "TLS";
	private static SSLContext SERVER_CONTEXT;
	private static SSLContext CLIENT_CONTEXT;

	public static SSLContext getServerContext() {
		return SERVER_CONTEXT;
	}

	public static SSLContext getServerContext(String pkPath,  String caPath) {
		if (SERVER_CONTEXT == null) {
			InputStream in = null;
			InputStream tIN = null;
			try {
				//读取公钥
				KeyManagerFactory kmf = null;
				if (pkPath != null) {
					KeyStore ks = KeyStore.getInstance("JKS");
					in = new FileInputStream(pkPath);
					ks.load(in, "raspberry".toCharArray());
					kmf = KeyManagerFactory.getInstance("SunX509");
					kmf.init(ks, "raspberry".toCharArray());
				}

				//设置信任证书库
				TrustManagerFactory tf = null;
				if (caPath != null) {
					KeyStore tks = KeyStore.getInstance("JKS");
					tIN = new FileInputStream(caPath);
					tks.load(tIN, "raspberry".toCharArray());
					tf = TrustManagerFactory.getInstance("SunX509");
					tf.init(tks);
				}
				SERVER_CONTEXT = SSLContext.getInstance(PROTOCOL);
				SERVER_CONTEXT.init(kmf.getKeyManagers(), tf.getTrustManagers(), null);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Error(
						"Failed to initialize the server-side SSLContext", e);
			} finally {
				if (in != null)
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				in = null;
				if (tIN != null)
					try {
						tIN.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				tIN = null;
			}
		}
		return SERVER_CONTEXT;
	}

	public static SSLContext getClientContext() {
		return CLIENT_CONTEXT;
	}

	public static SSLContext getClientContext(String pkPath,
			String caPath) {
		if (CLIENT_CONTEXT == null) {
			InputStream in = null;
			InputStream tIN = null;
			try {
				KeyManagerFactory kmf = null;
				if (pkPath != null) {
					KeyStore ks = KeyStore.getInstance("JKS");
					in = new FileInputStream(pkPath);
					ks.load(in, "raspberry".toCharArray());
					kmf = KeyManagerFactory.getInstance("SunX509");
					kmf.init(ks, "raspberry".toCharArray());
				}

				TrustManagerFactory tf = null;
				if (caPath != null) {
					KeyStore tks = KeyStore.getInstance("JKS");
					tIN = new FileInputStream(caPath);
					tks.load(tIN, "raspberry".toCharArray());
					tf = TrustManagerFactory.getInstance("SunX509");
					tf.init(tks);
				}
				CLIENT_CONTEXT = SSLContext.getInstance(PROTOCOL);
				CLIENT_CONTEXT.init(kmf.getKeyManagers(), tf.getTrustManagers(), null);
			} catch (Exception e) {
				e.printStackTrace();
				throw new Error(
						"Failed to initialize the client-side SSLContext", e);
			} finally {
				if (in != null)
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				in = null;
			}
		}
		return CLIENT_CONTEXT;
	}
}
