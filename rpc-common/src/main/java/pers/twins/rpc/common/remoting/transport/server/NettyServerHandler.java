package pers.twins.rpc.common.remoting.transport.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import pers.twins.rpc.common.enmus.CompressorType;
import pers.twins.rpc.common.enmus.RpcResCode;
import pers.twins.rpc.common.enmus.SerializationType;
import pers.twins.rpc.common.factory.SingletonFactory;
import pers.twins.rpc.common.remoting.RpcMessage;
import pers.twins.rpc.common.remoting.RpcRequest;
import pers.twins.rpc.common.remoting.RpcResponse;
import pers.twins.rpc.common.remoting.service.ServiceProvider;
import pers.twins.rpc.common.remoting.transport.RpcProtocolConstants;

/**
 * @author twins
 * @date 2023-07-17 15:42:24
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;

    private final ServiceProvider serviceProvider;

    NettyServerHandler(ServiceProvider serviceProvider) {
        rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof RpcMessage) {
                log.info("server receive msg: [{}] ", msg);
                byte messageType = ((RpcMessage) msg).getMessageType();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationType.KRYO.getCode());
                rpcMessage.setCompress(CompressorType.GZIP.getCode());
                if (messageType == RpcProtocolConstants.TYPE_HEARTBEAT_REQUEST) {
                    rpcMessage.setMessageType(RpcProtocolConstants.TYPE_HEARTBEAT_RESPONSE);
                    rpcMessage.setData(RpcProtocolConstants.PONG);
                } else {
                    RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                    Object result = rpcRequestHandler.handle(rpcRequest, serviceProvider.getService(rpcRequest.getRpcServiceName()));
                    log.info(String.format("server get result: %s", result.toString()));
                    rpcMessage.setMessageType(RpcProtocolConstants.TYPE_RESPONSE);
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                        rpcMessage.setData(rpcResponse);
                    } else {
                        RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResCode.FAIL);
                        rpcMessage.setData(rpcResponse);
                        log.error("not writable now, message dropped");
                    }
                }
                ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent idleStateEvent) {
            IdleState state = idleStateEvent.state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception", cause);
        ctx.close();
    }
}
