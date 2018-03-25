package sharing.code.netty.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import javafx.collections.ObservableList;
import sharing.code.bean.DataBaseProperty;
import sharing.code.bean.KeyProperty;
import sharing.code.netty.val.NettyConstant;
import sharing.ui.model.ConnTask;

public class NettyServer {
	public EventLoopGroup bossGroup;
	public EventLoopGroup workerGroup;
	public ServerBootstrap b;
	private ServerInitializer serverInitializer;

	public NettyServer(DataBaseProperty dbp, KeyProperty kp, ObservableList<ConnTask> taskPool) {
		this.serverInitializer = new ServerInitializer(dbp, kp, taskPool);
	}

	public ChannelGroup getChannelPool() {
		return serverInitializer.getChannelPoll();
	}



	public void bind() throws Exception {
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();
		b = new ServerBootstrap();
		// 配置服务端的NIO线程组
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
		.option(ChannelOption.SO_BACKLOG, 5)
		.option(ChannelOption.SO_RCVBUF, 1024 * 32)
		.option(ChannelOption.SO_SNDBUF, 1024 * 32)
		.handler(new LoggingHandler(LogLevel.INFO))
		.childHandler(serverInitializer);

		// 绑定端口，同步等待成功
		//b.bind(NettyConstant.REMOTEIP, NettyConstant.PORT).sync();
		b.bind(NettyConstant.PORT).sync();
		System.out.println("服务器开启成功 : " + (NettyConstant.REMOTEIP + " : " + NettyConstant.PORT));
	}
}
