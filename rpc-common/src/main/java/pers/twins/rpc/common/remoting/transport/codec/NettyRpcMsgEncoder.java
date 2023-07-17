package pers.twins.rpc.common.remoting.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import pers.twins.rpc.common.remoting.RpcMessage;

/**
 * @author twins
 * @date 2023-07-17 17:39:41
 */
public class NettyRpcMsgEncoder extends MessageToByteEncoder<RpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) throws Exception {
        //todo encode
    }
}
