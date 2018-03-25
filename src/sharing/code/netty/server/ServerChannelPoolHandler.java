package sharing.code.netty.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 *this class is use to manage all incoming connect
 * @author ¿Ó∑±
 *
 */
public class ServerChannelPoolHandler extends ChannelHandlerAdapter{
	static private ChannelGroup channels  = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	public ChannelGroup getChannels() {
		return channels;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		System.out.println("Active");
		System.out.println(ctx.channel().id());
		channels.add(ctx.channel());
		System.out.println(channels.find(ctx.channel().id()).localAddress());
		System.out.println(channels.find(ctx.channel().id()).remoteAddress());

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		System.out.println("InActive");
		channels.remove(ctx.channel());
	}


}
