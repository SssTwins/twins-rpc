package pers.twins.rpc.common.remoting.transport.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author twins
 * @date 2023-07-17 15:42:24
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
