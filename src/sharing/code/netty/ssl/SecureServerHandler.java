package sharing.code.netty.ssl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecureServerHandler extends SimpleChannelInboundHandler<String> {

	private static final Logger logger = Logger
			.getLogger(SecureServerHandler.class.getName());

	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		ctx.pipeline().get(SslHandler.class).handshakeFuture()
		.addListener(new GenericFutureListener<Future<Channel>>() {
			@Override
			public void operationComplete(Future<Channel> future)
					throws Exception {
				ctx.writeAndFlush("Welcome to "
						+ InetAddress.getLocalHost().getHostName()
						+ " secure chat service!\n");
				ctx.writeAndFlush("Your session is protected by "
						+ ctx.pipeline().get(SslHandler.class).engine()
						.getSession().getCipherSuite()
						+ " cipher suite.\n");
				System.out.println("服务端的SSL信息：SSL连接完成............");
			}
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		logger.log(Level.WARNING, "Unexpected exception from downstream.",
				cause);
		ctx.close();
	}


	@Override
	protected void messageReceived(ChannelHandlerContext arg0, String arg1)
			throws Exception {
		// TODO Auto-generated method stub
	}
}
