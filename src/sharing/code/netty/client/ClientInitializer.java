package sharing.code.netty.client;

import java.util.concurrent.TimeUnit;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import sharing.code.bean.DataBaseProperty;
import sharing.code.bean.KeyProperty;
import sharing.code.netty.codec.NettyMessageDecoder;
import sharing.code.netty.codec.NettyMessageEncoder;
import sharing.ui.model.ConnTask;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
	private ClientLoginAuthHandler clientLoginAuthHandler;

	public ClientInitializer(DataBaseProperty dbp, KeyProperty kp, ConnTask task) {
		clientLoginAuthHandler = new ClientLoginAuthHandler(dbp, kp, task);
	}


	@Override
	public void initChannel(SocketChannel ch) throws Exception {

		//编解码Handler
		ch.pipeline().addLast("nettyMessageDecoder", new NettyMessageDecoder(1024 * 1024, 4, 4));
		ch.pipeline().addLast("nettyMessageEncoder", new NettyMessageEncoder());
		//链路空闲处理Handler
		ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(6, 6, 6, TimeUnit.MINUTES));
		ch.pipeline().addLast("heartBeatHandler", new ClientHeartBeatHandler());
		//认证处理Handler
		ch.pipeline().addLast("loginAuthHandler", clientLoginAuthHandler);
	}
}
