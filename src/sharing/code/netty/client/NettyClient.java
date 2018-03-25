package sharing.code.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

import sharing.code.bean.DataBaseProperty;
import sharing.code.bean.KeyProperty;
import sharing.code.netty.val.NettyConstant;
import sharing.ui.model.ConnTask;

public class NettyClient {
	private ClientInitializer clientInitializer;
	public EventLoopGroup group = new NioEventLoopGroup();

	private ConnTask task;

	public ConnTask getConnTask() {
		return this.task;
	}

	public NettyClient(DataBaseProperty dbp, KeyProperty kp) {
		task = new ConnTask();
		clientInitializer = new ClientInitializer(dbp, kp, this.task);
	}

	public void connect(int port, String host) {
		// 配置客户端NIO线程组
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class)
		.option(ChannelOption.TCP_NODELAY, true)
		.option(ChannelOption.SO_RCVBUF, 1024 * 32)
		.option(ChannelOption.SO_SNDBUF, 1024 * 32)
		.handler(clientInitializer);
		// 发起异步连接操作
		b.connect(
				new InetSocketAddress(host, port),
				new InetSocketAddress(NettyConstant.LOCALIP,
						NettyConstant.LOCAL_PORT));
	}
}
