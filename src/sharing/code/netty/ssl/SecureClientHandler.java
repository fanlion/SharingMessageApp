package sharing.code.netty.ssl;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SecureClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = Logger.getLogger(
            SecureClientHandler.class.getName());

    @Override
    public void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
    	System.out.println("�ͻ��˵�SSL��Ϣ��SSL�������....");
        System.err.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
