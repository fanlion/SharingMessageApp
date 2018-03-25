package sharing.code.netty.server;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

import sharing.code.netty.struct.Header;
import sharing.code.netty.struct.NettyMessage;
import sharing.code.netty.val.MessageType;

/**
 * 处理链路空闲事件，发送心跳包和接受心跳包
 * 判断连接是否正常
 * @author 李繁
 *
 */
public class ServerHeartBeatHandler extends ChannelHandlerAdapter {
	//连续发送心跳包并未接受到请求的次数
	private int heart_beat_req_count = 0;
	//断连的阈值
	private static int DISCONNET_THRESHOLD = 10;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		NettyMessage message = (NettyMessage) msg;
		//处理对方回应心跳信息（Pong）
		if (message.getHeader() != null
				&& message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {
			//收到对方发送的心跳回复信息了，将count置零
			heart_beat_req_count = 0;
			System.out.println("我收到你的心跳应答包了");
		} else if (message.getHeader() != null
				&&message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
			NettyMessage respMsg = buildHeatBeatResp();
			//向对方发送一个心跳应答信息
			ctx.writeAndFlush(respMsg);
			System.out.println("我收到你的心跳请求包了，现在我将发送应答包");
		} else
			ctx.fireChannelRead(msg);
	}

	//对信道空闲事件进行监听，当信道空闲时向对方发送心跳信息
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		//若是空闲事件,向对方发送一个心跳请求信息（ping）
		if (evt instanceof IdleStateEvent) {
			//连续发送了DISCONNET_THRESHOLD次都没有应答则关闭连接
			System.out.println("已经发送心跳包个数" + heart_beat_req_count);
			if (heart_beat_req_count > DISCONNET_THRESHOLD) {
				ctx.close();
			} else {
				NettyMessage message = buildHeatBeatReq();
				ctx.writeAndFlush(message);
				//心跳发送次数加一
				heart_beat_req_count++;
			}

		} else {
			//否则把事件传递到下一个Handler处理
			ctx.fireUserEventTriggered(evt);
		}

		super.userEventTriggered(ctx, evt);
	}

	//构造心跳应答信息
	private NettyMessage buildHeatBeatResp() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.HEARTBEAT_RESP.value());
		message.setHeader(header);
		return message;
	}

	//构造心跳请求信息
	private NettyMessage buildHeatBeatReq() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.HEARTBEAT_REQ.value());
		message.setHeader(header);
		return message;
	}

}
