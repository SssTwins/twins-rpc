package pers.twins.rpc.common.remoting.transport.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author twins
 * @date 2023-07-17 17:38:42
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    public NettyDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    public NettyDecoder() {
        //todo change default decode length
        this(1, 1, 1, 1, 1);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //todo use custom decode
        return super.decode(ctx, in);
    }
}
