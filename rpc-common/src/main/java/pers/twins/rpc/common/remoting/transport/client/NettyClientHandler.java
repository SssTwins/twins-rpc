package pers.twins.rpc.common.remoting.transport.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.RpcMessage;
import pers.twins.rpc.common.remoting.RpcResponse;
import pers.twins.rpc.common.remoting.transport.RpcCodecConstants;

import java.net.InetSocketAddress;

/**
 * @author twins
 * @date 2023-07-17 16:42:18
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final UnprocessedRequestProvider unprocessedRequestProvider;

    private final ChannelProvider channelProvider;

    public NettyClientHandler(ChannelProvider channelProvider) {
        this.unprocessedRequestProvider = SingletonFactory.getInstance(UnprocessedRequestProvider.class);
        this.channelProvider = channelProvider;
    }

    /**
     * Read the message transmitted by the server
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("client receive msg: [{}]", msg);
            if (msg instanceof RpcMessage tmp) {
                byte messageType = tmp.getMessageType();
                if (messageType == RpcCodecConstants.HEARTBEAT_RESPONSE_TYPE) {
                    log.info("heart [{}]", tmp.getData());
                } else if (messageType == RpcCodecConstants.RESPONSE_TYPE) {
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();
                    unprocessedRequestProvider.complete(rpcResponse);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            IdleState state = idleStateEvent.state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = channelProvider.get((InetSocketAddress) ctx.channel().remoteAddress());
                RpcMessage rpcMessage = new RpcMessage();
                // todo rpcMsg fill
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
