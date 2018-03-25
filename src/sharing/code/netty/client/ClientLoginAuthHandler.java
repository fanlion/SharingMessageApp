package sharing.code.netty.client;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;

import sharing.code.netty.struct.Header;
import sharing.code.netty.struct.NettyMessage;
import sharing.code.netty.val.MessageType;
import sharing.ui.model.ConnTask;
import sharing.code.bean.DataBaseProperty;
import sharing.code.bean.KeyProperty;
import sharing.code.bean.RemoteTask;
import sharing.code.netty.ssl.CertificateManager;
import sharing.code.netty.ssl.SecureSslContextFactory;

public class ClientLoginAuthHandler extends ChannelHandlerAdapter {
	private ClientSharingMessageHandler clientSharingMessageHandler;
	private ConnTask connTask;
	private RemoteTask task;



	public ClientLoginAuthHandler(DataBaseProperty dbp, KeyProperty kp, ConnTask ConnTask) {
		this.connTask = ConnTask;
		this.task = new RemoteTask();
		clientSharingMessageHandler = new ClientSharingMessageHandler(dbp, kp, this.task);

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		String sessionId = ctx.channel().id().asShortText();
		String address = ctx.channel().remoteAddress().toString();
		connTask.setSessionIdProperty(sessionId);
		connTask.setLocalAddressProperty(address);
		//从指定的路径读取证书并构造登陆报文发送给对方
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		FileInputStream in = new FileInputStream("C:\\Users\\李繁\\signedsmclient.cer");
		java.security.cert.Certificate cert = cf.generateCertificate(in);
		//把自己的证书发送给对方
		ctx.writeAndFlush(buildLoginReq(cert));
		task.addObserver(this.connTask);
		if (ctx.channel().isActive()) {
			task.setStatus("正常");
		} else {
			task.setStatus("异常");
		}

		task.setProcess("初始化");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		NettyMessage message = (NettyMessage) msg;
		// 如果是握手应答消息，需要判断是否认证成功
		if (message.getHeader() != null
				&& message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
			byte loginResult = (byte) message.getBody();
			if (loginResult != (byte) 1) {
				task.setProcess("对方拒绝连接，握手失败");
				// 握手失败，关闭连接
				ctx.close();
			} else {
				task.setProcess("握手成功，发送W请求");
				//向对方发送请求发送W的报文
				ctx.writeAndFlush(buildSendWReq());
			}
			//如果接受的是对方的登陆请求报文，则验证对方身份
		} else if (message.getHeader() != null
				&& message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {
			Certificate cert = (Certificate)message.getBody();
			task.setProcess("收到对方的证书信息");
			task.setCerInfo(cert.toString());

			//添加到被观察者中
			ChannelFuture future = ctx.channel().newSucceededFuture();
			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture arg0) throws Exception {
					System.out.println("对方的身份信息如下：");
					System.out.println(cert.toString());
				}
			});

			//验证证书是否由指定CA签名
			String userdir = System.getProperty("user.dir") + "/securitycert/";
			boolean checkSign = CertificateManager.checkCertSign(
					userdir+ "SharingMessageRootCertificateAuthority2016.cer",
					userdir+ "signedsmserver.cer");
			//验证证书是否过期或未生效
			boolean checkValid = CertificateManager.checkCertValid(
					userdir+ "signedsmserver.cer");
			System.out.println("checkSign: " + checkSign);
			System.out.println("checkValid: " + checkValid);
			//通过验证
			if (checkSign && checkValid) {
				task.setProcess("对方通过验证");
				//				int i=JOptionPane.showConfirmDialog(null, "该证书合法，是否信任该证书","来自客户端的提示:", JOptionPane.YES_NO_OPTION);
				//				if(i==JOptionPane.OK_OPTION){
				//配置SSL
				SSLEngine engine = SecureSslContextFactory.getClientContext(
						userdir +"smkeystore",
						userdir + "smkeystore")
						.createSSLEngine();
				engine.setUseClientMode(true);
				//添加SSL连接Handler
				ctx.pipeline().addFirst("sslHandler", new SslHandler(engine));
				//添加业务处理Handler
				ctx.pipeline().addLast("clientSharingMessageHandler", clientSharingMessageHandler);
				NettyMessage loginResp = buildLoginResp((byte) 1);
				ctx.writeAndFlush(loginResp);
			}else{
				NettyMessage loginResp = buildLoginResp((byte)-1);
				ctx.writeAndFlush(loginResp);
				ctx.close();
			}
			//			} else { //拒绝服务
			//				task.setProcess("对方证书不合法，拒绝提供服务");
			//				NettyMessage loginResp = buildLoginResp((byte) -1);
			//				ctx.writeAndFlush(loginResp);
			//				ctx.close();
			//			}

		} else
			ctx.fireChannelRead(msg);
	}

	/**
	 * 构造登陆报文
	 * @param - 要发送的证书
	 * @return - 登陆报文
	 */
	private NettyMessage buildLoginReq(Certificate cert) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_REQ.value());
		message.setHeader(header);
		message.setBody(cert);
		return message;
	}

	/**
	 * 构造请求对方发送W数据报文
	 * @return - 报文
	 */
	private NettyMessage buildSendWReq() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.Y_REQ.value());
		message.setHeader(header);
		return message;
	}

	private NettyMessage buildLoginResp(byte type) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_RESP.value());
		message.setHeader(header);
		message.setBody(type);
		return message;
	}


	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.fireExceptionCaught(cause);
	}
}
