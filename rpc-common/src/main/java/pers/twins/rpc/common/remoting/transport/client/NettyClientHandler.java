package pers.twins.rpc.common.remoting.transport.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.enmus.CompressorType;
import pers.twins.rpc.common.enmus.SerializationType;
import pers.twins.rpc.common.remoting.RpcMessage;
import pers.twins.rpc.common.remoting.RpcResponse;
import pers.twins.rpc.common.remoting.transport.RpcProtocolConstants;

/**
 * @author twins
 * @date 2023-07-17 16:42:18
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final UnprocessedRequestProvider unprocessedRequestProvider;

    public NettyClientHandler(UnprocessedRequestProvider unprocessedRequestProvider) {
        this.unprocessedRequestProvider = unprocessedRequestProvider;
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
                if (messageType == RpcProtocolConstants.TYPE_HEARTBEAT_RESPONSE) {
                    log.info("service heart [{}]", tmp.getData());
                } else if (messageType == RpcProtocolConstants.TYPE_RESPONSE) {
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
                Channel channel = ctx.channel();
                log.info("write idle happen [{}]", channel.remoteAddress());
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationType.KRYO.getCode());
                rpcMessage.setCompress(CompressorType.GZIP.getCode());
                rpcMessage.setMessageType(RpcProtocolConstants.TYPE_HEARTBEAT_REQUEST);
                rpcMessage.setData(RpcProtocolConstants.PING);
                channel.writeAndFlush(rpcMessage)
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception", cause);
        ctx.close();
    }

}
