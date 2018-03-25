package sharing.code.netty.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import javafx.collections.ObservableList;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLEngine;

import sharing.code.netty.struct.Header;
import sharing.code.netty.struct.NettyMessage;
import sharing.code.netty.val.MessageType;
import sharing.ui.model.ConnTask;
import sharing.code.bean.DataBaseProperty;
import sharing.code.bean.KeyProperty;
import sharing.code.bean.RemoteTask;
import sharing.code.netty.ssl.CertificateManager;
import sharing.code.netty.ssl.SecureSslContextFactory;

public class ServerLoginAuthHandler extends ChannelHandlerAdapter {

	private ServerSharingMessageHandler serverSharingMessageHandler;
	private ObservableList<ConnTask> taskPool;
	private Map<String, RemoteTask> taskMap = new HashMap<String, RemoteTask>();

	public ServerLoginAuthHandler(DataBaseProperty dbp, KeyProperty kp, ObservableList<ConnTask> taskPool) {
		this.taskPool = taskPool;
		serverSharingMessageHandler = new ServerSharingMessageHandler(dbp, kp, taskMap);
	}



	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
		super.close(ctx, promise);
		channels.remove(ctx.channel());
	}


	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		channels.remove(ctx.channel());
	}


	private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<String, Boolean>();
	private ChannelGroup channels  = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	public ChannelGroup getChannelPoll() {
		return this.channels;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ConnTask connTask = new ConnTask();
		String sessionId = ctx.channel().id().asShortText();
		String address = ctx.channel().remoteAddress().toString();
		connTask.setSessionIdProperty(sessionId);
		connTask.setLocalAddressProperty(address);
		taskPool.add(connTask);

		RemoteTask remoteTask = new RemoteTask();
		channels.add(ctx.channel());
		//从指定的路径读取证书并构造登陆报文发送给对方
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		FileInputStream in = new FileInputStream("C:\\Users\\李繁\\signedsmserver.cer");
		java.security.cert.Certificate cert = cf.generateCertificate(in);
		//把自己的证书发送给对方
		ctx.writeAndFlush(buildLoginReq(cert));

		remoteTask.addObserver(connTask);
		if (ctx.channel().isActive()) {
			remoteTask.setStatus("正常");
		} else {
			remoteTask.setStatus("异常");
		}
		remoteTask.setProcess("初始化");
		taskMap.put(sessionId, remoteTask);

	}


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		String sessionId = ctx.channel().id().asShortText();
		RemoteTask task = (RemoteTask) taskMap.get(sessionId);
		NettyMessage message = (NettyMessage) msg;

		// 如果是握手请求消息，处理，其它消息透传
		if (message.getHeader() != null
				&& message.getHeader().getType() == MessageType.LOGIN_REQ.value()) {

			Certificate cert = (Certificate)message.getBody();
			task.setCerInfo(cert.toString());
			task.setProcess("接受到证书");
			//验证证书是否由指定CA签名
			String userdir = System.getProperty("user.dir") + "/securitycert/";
			boolean checkSign = CertificateManager.checkCertSign(
					userdir + "SharingMessageRootCertificateAuthority2016.cer",
					userdir + "signedsmserver.cer");
			//验证证书是否过期或未生效
			boolean checkValid = CertificateManager.checkCertValid(
					userdir + "signedsmserver.cer");

			if (checkSign && checkValid) {//通过验证
				task.setProcess("身份合法");
				//配置SSL
				SSLEngine engine = SecureSslContextFactory.getServerContext(
						userdir + "smkeystore",
						userdir + "smkeystore")
						.createSSLEngine();
				engine.setUseClientMode(false);
				engine.setNeedClientAuth(true);
				//建立SSL连接
				ctx.pipeline().addFirst("sslHandler", new SslHandler(engine));
				//接受业务请求
				ctx.pipeline().addLast(serverSharingMessageHandler);

				NettyMessage loginResp = buildLoginResp((byte) 1);
				ctx.writeAndFlush(loginResp);
			}else{
				task.setProcess("身份不合法");
				NettyMessage loginResp = buildLoginResp((byte)-1);
				ctx.writeAndFlush(loginResp);
				ctx.close();
				task.setStatus("断开连接");
			}
		} else if(message != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()){
			byte loginResult = (byte) message.getBody();
			if (loginResult != (byte) 1) {
				//对方拒绝连接则关闭连接
				task.setProcess("对方拒绝连接");
				ctx.close();
				task.setStatus("断开连接");
			} else {
				task.setProcess("请求成功");
			}
		}
		else {
			ctx.fireChannelRead(msg);
		}
	}

	private NettyMessage buildLoginResp(byte result) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_RESP.value());
		message.setHeader(header);
		message.setBody(result);
		return message;
	}

	private NettyMessage buildLoginReq(Certificate cert) {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_REQ.value());
		message.setHeader(header);
		message.setBody(cert);
		return message;
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		nodeCheck.remove(ctx.channel().remoteAddress().toString());// 删除缓存
		ctx.close();
		ctx.fireExceptionCaught(cause);
	}

}
